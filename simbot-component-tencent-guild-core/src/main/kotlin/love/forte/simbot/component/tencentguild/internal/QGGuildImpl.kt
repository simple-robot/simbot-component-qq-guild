/*
 * Copyright (c) 2022-2023. ForteScarlet.
 *
 * This file is part of simbot-component-tencent-guild.
 *
 * simbot-component-tencent-guild is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-tencent-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-tencent-guild. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.QGChannel
import love.forte.simbot.component.tencentguild.util.requestBy
import love.forte.simbot.literal
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.TencentApiException
import love.forte.simbot.qguild.api.apipermission.ApiPermissions
import love.forte.simbot.qguild.api.apipermission.GetApiPermissionListApi
import love.forte.simbot.qguild.api.apipermission.hasAuth
import love.forte.simbot.qguild.api.channel.GetChannelApi
import love.forte.simbot.qguild.api.channel.GetGuildChannelListApi
import love.forte.simbot.qguild.api.member.GetGuildMemberListApi
import love.forte.simbot.qguild.api.member.GetMemberApi
import love.forte.simbot.qguild.api.member.createFlow
import love.forte.simbot.qguild.api.role.GetGuildRoleListApi
import love.forte.simbot.qguild.event.*
import love.forte.simbot.qguild.isNotFound
import love.forte.simbot.qguild.model.*
import love.forte.simbot.toTimestamp
import love.forte.simbot.utils.item.Items
import love.forte.simbot.utils.item.Items.Companion.asItems
import love.forte.simbot.utils.item.Items.Companion.emptyItems
import love.forte.simbot.utils.item.effectedFlowItems
import love.forte.simbot.utils.item.flowItems
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import love.forte.simbot.qguild.model.Guild as QGSourceGuild


/**
 *
 * @author ForteScarlet
 */
internal class QGGuildImpl private constructor(
    private val baseBot: QGBotImpl,
    override val source: QGSourceGuild,
    private val job: CompletableJob,
    override val coroutineContext: CoroutineContext,
) : love.forte.simbot.component.tencentguild.QGGuild {

    internal val logger =
        LoggerFactory.getLogger("love.forte.simbot.component.tencentguild.internal.QGGuildImpl[${source.id}]")


    internal fun update(guild: QGSourceGuild): QGGuildImpl {
        return QGGuildImpl(
            baseBot, guild, job, coroutineContext,
        ).also {
            it.permissions = permissions
            it.internalMembers = internalMembers
            it.internalChannels = internalChannels
            it.internalChannelCategories = internalChannelCategories
            it.ownerInternal = ownerInternal
            // caches
            it.internalChannelInfoCache = internalChannelInfoCache
            it.internalMemberInfoCache = internalMemberInfoCache
            // skip init
            it.init.set(true)
        }
    }

    @Volatile
    override lateinit var permissions: ApiPermissions

    private suspend fun initPermissions() {
        this.permissions = GetApiPermissionListApi.create(source.id).requestBy(baseBot)
    }

    private val permissionsRefreshLock = Mutex()

    override suspend fun refreshPermissions(evaluationInternalCache: Boolean): ApiPermissions =
        permissionsRefreshLock.withLock {
            val newPermissions = GetApiPermissionListApi.create(source.id).requestBy(baseBot).also {
                this.permissions = it
            }

            if (evaluationInternalCache) {
                if (internalMembers == null) {
                    trySyncMemberList()
                }
                if (internalChannels == null || internalChannelCategories == null) {
                    trySyncChannels()
                }
            }

            newPermissions
        }

    override val id: ID = source.id.ID
    override val ownerId: ID = source.ownerId.ID

    override val maximumChannel: Int get() = -1
    override val createTime: Timestamp get() = source.joinedAt.toTimestamp()
    override val currentMember: Int get() = source.memberCount
    override val description: String get() = source.description
    override val icon: String get() = source.icon
    override val maximumMember: Int get() = source.maxMembers
    override val name: String get() = source.name

    private data class CacheData<V>(val value: V, val time: Long)


    //region channel cache
    /**
     * 内部 channel 缓存器，用于减缓API的调用。
     * 当前bot订阅的guild相关事件时可用。
     */
    private var internalChannelInfoCache: ConcurrentHashMap<String, CacheData<SimpleChannel>>? = null

    /**
     * 当 [internalChannelInfoCache] 可用时优先使用缓存。
     */
    internal suspend fun getChannelWithCache(id: String): SimpleChannel {
        suspend fun get() = GetChannelApi.create(id).requestBy(baseBot)
        val cache = internalChannelInfoCache ?: return get()

        return cache[id]?.value ?: kotlin.run {
            get().also(::updateChannelCache)
        }
    }

    internal fun removeChannelCache(id: String): SimpleChannel? {
        return internalChannelInfoCache?.remove(id)?.value
    }

    internal fun updateChannelCache(channel: SimpleChannel): SimpleChannel? {
        val cache = internalChannelInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.computeIfPresent(channel.id) { _, curr ->
            if (curr.time > now) curr else CacheData(channel, now)
        }?.value
    }

    internal fun updateChannelCache(channel: EventChannel): SimpleChannel? {
        val cache = internalChannelInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.computeIfPresent(channel.id) { _, curr ->
            if (curr.time > now) curr else {
                CacheData(curr.value.copy(
                    guildId = channel.guildId,
                    name = channel.name,
                    type = channel.type,
                    subType = channel.subType,
                    ownerId = channel.ownerId,
                ), now)
            }
        }?.value
    }
    //endregion

    //region member cache
    private var internalMemberInfoCache: ConcurrentHashMap<String, CacheData<SimpleMember>>? = null

    /**
     * 当 [internalMemberInfoCache] 可用时优先使用缓存。
     */
    internal suspend fun getMemberWithCache(id: String): SimpleMember {
        suspend fun get() = GetMemberApi.create(source.id, id).requestBy(baseBot)
        val cache = internalMemberInfoCache ?: return get()

        return cache[id]?.value ?: kotlin.run {
            get().also(::updateMemberCache)
        }
    }

    internal fun removeMemberCache(id: String): SimpleMember? {
        return internalMemberInfoCache?.remove(id)?.value
    }

    internal fun updateMemberCache(member: SimpleMember): SimpleMember? {
        val cache = internalMemberInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.computeIfPresent(member.user.id) { _, curr ->
            if (curr.time > now) curr else CacheData(member, now)
        }?.value
    }
    internal fun updateMemberCache(member: EventMember): SimpleMember? {
        val cache = internalMemberInfoCache ?: return null
        val now = System.currentTimeMillis()
        return cache.computeIfPresent(member.user.id) { _, curr ->
            if (curr.time > now) curr else {
                CacheData(curr.value.copy(
                    user = member.user,
                    nick = member.nick,
                    roles = member.roles,
                    joinedAt = member.joinedAt,
                ), now)
            }
        }?.value
    }
    //endregion

    private var internalMembers: ConcurrentHashMap<String, QGMemberImpl>? = null
    private var internalChannels: ConcurrentHashMap<String, QGChannelImpl>? = null
    private var internalChannelCategories: ConcurrentHashMap<String, QGChannelCategoryImpl>? = null
    private lateinit var ownerInternal: QGMemberImpl

    private val hasMemberListApiPermission = permissions hasAuth GetGuildMemberListApi
    private val hasChannelListApiPermission = permissions hasAuth GetGuildChannelListApi

    override lateinit var bot: QGGuildBotImpl
        private set

    override fun toString(): String {
        return "QGGuildImpl(bot=$baseBot, guild=$source)"
    }

    override suspend fun owner(): QGMemberImpl = ownerInternal

    private val init = AtomicBoolean(false)

    /**
     * 同步数据，包括成员信息和频道列表信息, 以及 [bot] 和 [ownerInternal] 的初始化。
     * 必须在对象实例后执行一次进行初始化，否则内部属性信息将会为空。
     *
     * *Note: 成员列表的获取暂时不支持（只支持私域）*
     *
     * @param onlyFirst 如果为 true, 则只有当从未初始化过的时候才会进行初始化
     */
    internal suspend fun initData(onlyFirst: Boolean = false) {
        if (!onlyFirst || init.compareAndSet(false, true)) {
            initCache()
            initPermissions()
            syncData()
        }
    }

    private fun initCache() {
        if (EventIntents.Guilds.intents in baseBot.source.configuration.intents) {
            internalChannelInfoCache = ConcurrentHashMap()
        }
        if (EventIntents.GuildMembers.intents in baseBot.source.configuration.intents) {
            internalMemberInfoCache = ConcurrentHashMap()
        }
    }

    /**
     * 同步数据，包括成员信息和频道列表信息, 以及 [bot] 和 [ownerInternal] 的初始化。
     * 必须在对象实例后执行一次进行初始化，否则内部属性信息将会为空。
     *
     * *Note: 成员列表的获取暂时不支持（只支持私域）*
     */
    private suspend fun syncData() {
        kotlin.runCatching { syncMembers() }.onFailure { e ->
            logger.error("Sync members data for guild $this failed.", e)
        }

        kotlin.runCatching { trySyncChannels() }.onFailure { e ->
            logger.error("Sync channels data for guild $this failed.", e)
        }
    }

    override val members: Items<QGMemberImpl>
        get() = internalMembers?.values?.asItems()
            ?: (if (hasMemberListApiPermission) queryMembers() else emptyItems())

    @OptIn(FlowPreview::class)
    private fun queryMembers(): Items<QGMemberImpl> = flowItems { (limit, offset, batch) ->
        // 批次
        val batchLimit = batch.takeIf { it > 0 } ?: GetGuildMemberListApi.MAX_LIMIT
        val flow =
            GetGuildMemberListApi.createFlow(source.id, batchLimit) { requestBy(baseBot) }.flatMapConcat { it.asFlow() }
                .let {
                    if (offset > 0) it.drop(offset) else it
                }.let {
                    if (limit > 0) it.take(limit) else it
                }

        emitAll(flow.map { m -> QGMemberImpl(baseBot, m, this@QGGuildImpl) })
    }

    override suspend fun member(id: ID): QGMemberImpl? = member(id.literal)

    /**
     * 如果 [internalMembers] 不为null，寻找缓存，否则直接查询
     */
    private suspend fun member(id: String): QGMemberImpl? {
        return internalMembers?.get(id) ?: queryMember(id)
    }

    private suspend fun queryMember(id: String): QGMemberImpl? {
        val member = try {
            GetMemberApi.create(source.id, id).requestBy(baseBot)
        } catch (apiEx: TencentApiException) {
            if (apiEx.value == 404) null else throw apiEx
        }

        return member?.let { info -> QGMemberImpl(baseBot, info, this) }
    }

    override val roles: Items<QGRoleImpl>
        get() = bot.effectedFlowItems {
            GetGuildRoleListApi.create(source.id).requestBy(baseBot).roles.forEach { info ->
                val roleImpl = QGRoleImpl(baseBot, info)
                emit(roleImpl)
            }
        }


    override val currentChannel: Int get() = internalChannels?.size ?: -1

    override val channels: Items<QGChannelImpl>
        get() = internalChannels?.values?.asItems()
            ?: (if (hasChannelListApiPermission) queryChannels() else emptyItems())

    /**
     * 通过API实时查询channels列表
     */
    private fun queryChannels(): Items<QGChannelImpl> = effectedFlowItems {
        val channelListGroupedByCategories = GetGuildChannelListApi
            .create(source.id)
            .requestBy(baseBot)
            .asSequence()
            .onEach { updateChannelCache(it) } // update caches
            .groupBy { it.type.isCategory }

        val channels = channelListGroupedByCategories[false]
        // if empty, just return.
            ?: return@effectedFlowItems
        val categories = channelListGroupedByCategories[true]?.associateBy { it.id } ?: emptyMap()

        val flow = channels
            .asFlow()
            .filter { info ->
                val categoryId = info.parentId

                // 没有对应的分组?
                if (!categories.containsKey(categoryId)) {
                    logger.warn(
                        "Cannot find category(id={}) for channel({}). This is an expected problem and please report this log to issues: https://github.com/simple-robot/simbot-component-tencent-guild/issues/new/choose?labels=%E7%BC%BA%E9%99%B7",
                        categoryId,
                        info
                    )
                    return@filter false
                }

                true
            }.map { info ->
                val categoryInfo = categories[info.parentId]!!
                val category = QGChannelCategoryImpl(baseBot, categoryInfo, this@QGGuildImpl)
                QGChannelImpl(baseBot, info, this@QGGuildImpl, category)
            }

        emitAll(flow)
    }

    override suspend fun channel(id: ID): QGChannel? {
        internalChannels?.also {
            return it[id.literal]
        }

        // by api
        val channelInfo = try {
            getChannelWithCache(source.id)
        } catch (apiEx: TencentApiException) {
            if (apiEx.value == 404) null else throw apiEx
        } ?: return null


        // 没有找到 ch 的 ct
        val categoryId = channelInfo.parentId
        val category = category(categoryId)
            ?: throw NoSuchElementException("No category(id=$categoryId) found for channel(id=$id)")

        return QGChannelImpl(baseBot, channelInfo, this, category)
    }

    override val categories: Items<QGChannelCategoryImpl>
        get() = internalChannelCategories?.values?.asItems()
            ?: (if (hasChannelListApiPermission) queryCategories() else emptyItems())

    private fun queryCategories(): Items<QGChannelCategoryImpl> = effectedFlowItems {
        val flow = GetGuildChannelListApi.create(source.id).requestBy(baseBot).asFlow()
            .filter { it.type.isCategory }
            .map { info ->
                QGChannelCategoryImpl(baseBot, info, this@QGGuildImpl)
            }

        emitAll(flow)
    }

    override suspend fun category(id: ID): QGChannelCategoryImpl? = category(id.literal)

    private suspend fun category(id: String): QGChannelCategoryImpl? {
        internalChannelCategories?.also {
            return it[id]
        }

        val info = try {
            getChannelWithCache(id)
//            GetChannelApi.create(id).requestBy(baseBot)
        } catch (apiEx: TencentApiException) {
            if (apiEx.isNotFound) null else throw apiEx
        }

        if (info != null && info.type != ChannelType.CATEGORY) {
            throw IllegalStateException("The type of channel(id=$id) is not category, but ${info.type}(${info.type.value})")
        }

        return info?.let { QGChannelCategoryImpl(baseBot, it, this) }
    }

    override suspend fun mute(duration: Duration): Boolean = false

    /**
     * 同步成员列表、owner、bot的信息。
     */
    private suspend fun syncMembers() {
        trySyncMemberList()
        syncBot()
        syncOwner()
    }

    private suspend fun trySyncMemberList() {
        if (hasMemberListApiPermission && EventIntents.GuildMembers.intents in baseBot.source.configuration.intents) {
            // 如果支持成员列表，且订阅了成员变更事件，查询并缓存用户列表。
            val guildId = source.id
            val members = ConcurrentHashMap<String, SimpleMember>()
            var after: String? = null

            /*
                > 1. 在每次翻页的过程中，可能会返回上一次请求已经返回过的member信息，需要调用方自己根据user id来进行去重。
                > 2. 每次返回的member数量与limit不一定完全相等。翻页请使用最后一个member的user id作为下一次请求的after参数，直到回包为空，拉取结束。
             */

            do {
                val memberList = GetGuildMemberListApi.create(guildId, after = after).requestBy(bot)
                memberList.associateByTo(members) { m -> m.user.id }
                after = memberList.lastOrNull()?.user?.id
            } while (memberList.isNotEmpty())


            this.internalMembers =
                members.mapValuesTo(ConcurrentHashMap()) { (_, member) -> QGMemberImpl(baseBot, member, this) }
        }
    }

    /**
     * 从成员中寻找bot所代表的对象
     */
    private suspend fun syncBot() {
        val member = member(baseBot.id)!!
        val guildBot = baseBot.asMember(member)
        logger.debug("Sync guild bot: {}", guildBot)
        bot = guildBot
    }

    /**
     * 从成员中寻找owner
     */
    private suspend fun syncOwner() {
        val ownerId = source.ownerId
        val owner = member(ownerId) ?: run {
            val ownerInfo = GetMemberApi.create(source.id, ownerId).requestBy(baseBot)
            QGMemberImpl(baseBot, ownerInfo, this)
        }

        logger.debug("Sync guild owner: {}", owner)
        ownerInternal = owner
    }


    private suspend fun trySyncChannels() {
        // 拥有API权限，且订阅了guild相关事件
        if (hasChannelListApiPermission && EventIntents.Guilds.intents in baseBot.source.configuration.intents) {
            val channelList = GetGuildChannelListApi.create(source.id).requestBy(baseBot)

            val groupByCategory = channelList
                .asSequence()
                .onEach { updateChannelCache(it) }
                .groupBy { it.type.isCategory }

            val categories = groupByCategory[true] ?: emptyList()
            val channels = groupByCategory[false] ?: emptyList()

            logger.debug(
                "Sync channel list for guild(id={}, name={}), categories: {}, channels: {}",
                id, name,
                categories.size,
                channels.size,
            )

            val internalCategories: ConcurrentHashMap<String, QGChannelCategoryImpl> = categories
                .associateBy { it.id }
                .mapValuesTo(ConcurrentHashMap()) { (_, info) ->
                    QGChannelCategoryImpl(baseBot, info, this)
                }

            val internalChannels: ConcurrentHashMap<String, QGChannelImpl> = channels
                .asSequence()
                .filter { info ->
                    val categoryId = info.parentId

                    // 没有对应的分组?
                    if (!internalCategories.containsKey(categoryId)) {
                        logger.warn(
                            "Cannot find category(id={}) for channel({}). This is an expected problem and please report this log to issues: https://github.com/simple-robot/simbot-component-tencent-guild/issues/new/choose?labels=%E7%BC%BA%E9%99%B7",
                            categoryId,
                            info
                        )
                        return@filter false
                    }

                    true
                }
                .associateBy { it.id }
                .mapValuesTo(ConcurrentHashMap()) { (_, info) ->
                    val category = internalCategories[info.parentId]!!
                    QGChannelImpl(baseBot, info, this, category)
                }

            this.internalChannelCategories = internalCategories
            this.internalChannels = internalChannels
        } else {
            if (!hasChannelListApiPermission) {
                logger.debug(
                    "Bot(id={}) does not have access to the channel list in guild(id={}), skip sync.",
                    baseBot.id,
                    source.id
                )
            } else {
                logger.debug(
                    "bot(id={}) is not listening to Guild related events (see EventIntents.Guilds, intents={}), skip sync.",
                    baseBot.id,
                    EventIntents.Guilds.intents
                )
            }
        }
    }

    internal suspend fun resolveChannel(channel: EventChannel): ChannelOrCategory {
        val cacheChannel = updateChannelCache(channel)
        val channelId = channel.id

        if (channel.type.isCategory) {
            val internalChannelCategories = internalChannelCategories
            return if (internalChannelCategories == null) {
                // not support, query and init
                val info = cacheChannel ?: getChannelWithCache(channelId)
                val category = QGChannelCategoryImpl(baseBot, info, this)

                ChannelOrCategory(category)
            } else {
                // ok
                val category = internalChannelCategories[channelId] ?: kotlin.run {
                    val info = cacheChannel ?: getChannelWithCache(channelId)
                    internalChannelCategories.computeIfAbsent(channelId) { QGChannelCategoryImpl(baseBot, info, this) }
                }

                ChannelOrCategory(category)
            }
        } else {
            val internalChannels = internalChannels
            return if (internalChannels == null) {
                // not support
                val info = cacheChannel ?: getChannelWithCache(channelId)
                val category = category(info.parentId)
                    ?: throw NoSuchElementException("No category(id=${info.parentId}) found for channel(id=$id)")

                val cl = QGChannelImpl(baseBot, info, this, category)

                ChannelOrCategory(cl)
            } else {
                // ok
                val cl = internalChannels[channelId] ?: kotlin.run {
                    val info = cacheChannel ?: getChannelWithCache(channelId)
                    val category = category(info.parentId)
                        ?: throw NoSuchElementException("No category(id=${info.parentId}) found for channel(id=$id)")

                    internalChannels.computeIfAbsent(channelId) { QGChannelImpl(baseBot, info, this, category) }
                }

                ChannelOrCategory(cl)
            }
        }

    }

    //region internal events
    internal suspend fun emitChannelCreate(event: ChannelCreate): ChannelOrCategory {
        return resolveChannel(event.data)
    }

    internal suspend fun emitChannelUpdate(event: ChannelUpdate): ChannelOrCategory {
        return resolveChannel(event.data)
    }

    internal fun emitChannelDelete(event: ChannelDelete): ChannelOrCategory? {
        val channel = event.data
        val internalChannelCategories = internalChannelCategories
        val internalChannels = internalChannels
        return when {
            channel.type.isCategory && internalChannelCategories != null -> {
                internalChannelCategories.remove(channel.id)?.let { ChannelOrCategory(it) }
            }

            channel.type != ChannelType.CATEGORY && internalChannels != null -> {
                internalChannels.remove(channel.id)?.let { ChannelOrCategory(it) }
            }

            else -> null
        }
    }
    //endregion

    companion object {
        internal suspend fun qgGuild(
            bot: QGBotImpl,
            guild: QGSourceGuild
        ): QGGuildImpl {
            return qgGuildWithoutInit(bot, guild).also { it.initData() }
        }

        internal fun qgGuildWithoutInit(
            bot: QGBotImpl,
            guild: QGSourceGuild,
        ): QGGuildImpl {
            // permissions
            val job: CompletableJob = SupervisorJob(bot.coroutineContext[Job])
            val coroutineContext: CoroutineContext = bot.coroutineContext + job

            return QGGuildImpl(bot, guild, job, coroutineContext)
        }
    }
}


/**
 * 一个 [value] 只可能是 [QGChannelImpl] 或 [QGChannelCategoryImpl] 的内联类型。
 */
@JvmInline
internal value class ChannelOrCategory private constructor(private val value: Any) {
    constructor(channel: QGChannelImpl) : this(channel as Any)
    constructor(category: QGChannelCategoryImpl) : this(category as Any)

    val channelOrNull: QGChannelImpl? get() = value as? QGChannelImpl
    val categoryOrNull: QGChannelCategoryImpl? get() = value as? QGChannelCategoryImpl

    val channel: QGChannelImpl get() = value as QGChannelImpl
    val category: QGChannelCategoryImpl get() = value as QGChannelCategoryImpl

    val isChannel: Boolean get() = value is QGChannelImpl
    val isCategory: Boolean get() = value is QGChannelCategoryImpl

}
