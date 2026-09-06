/*
 * Copyright (c) 2024-2026. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.model.Message

/**
 * [单聊消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#单聊消息)
 *
 * 触发场景	用户在单聊发送消息给机器人
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.C2C_MESSAGE_CREATE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.C2C_MESSAGE_CREATE_TYPE)
public data class C2CMessageCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: Data,
) : Signal.Dispatch() {

    /**
     * The data of [C2CMessageCreate.data].
     *
     * @property id 平台方消息ID，可以用于被动消息发送
     * @property author 发送者
     * @property content 文本消息内容
     * @property timestamp 消息生产时间（RFC3339）
     * @property messageType 消息类型
     * @property messageScene 消息场景；自定义菜单开关操作的设置结果位于 [MessageScene.ext] 中
     * @property attachments 富媒体文件附件，文件类型："图片，语音，视频，文件"
     * `{"content_type": "", "filename": "", "height": "", "width": "", "size": "", "url": ""}`
     *
     * TODO: 官方单聊消息事件还声明了 `ark_data` 和 `msg_elements`；待抽象对应 ARK 与消息元素模型后补齐。
     */
    @Serializable
    public data class Data(
        public val id: String,
        public val author: Author,
        public val content: String,
        public val timestamp: String,
        public val attachments: List<Message.Attachment> = emptyList(),
        /**
         * 消息类型。
         *
         * @since 4.7.0
         */
        @SerialName("message_type")
        public val messageType: Int? = null,
        /**
         * 消息场景信息。
         *
         * @since 4.7.0
         */
        @SerialName("message_scene")
        public val messageScene: MessageScene? = null,
    ) {
        // TODO: 升级 Kotlin 并可用 `@IntroducedAt` 后，使用它替代下面为构造函数和 copy 保留的 ABI 兼容入口。
        /**
         * 保留 4.7.0 前的 JVM 构造函数签名。
         *
         * @since 4.7.0
         */
        @Deprecated(message = "用于二进制兼容的构造函数，不应直接使用", level = DeprecationLevel.HIDDEN)
        public constructor(
            id: String,
            author: Author,
            content: String,
            timestamp: String,
            attachments: List<Message.Attachment> = emptyList(),
        ) : this(
            id = id,
            author = author,
            content = content,
            timestamp = timestamp,
            attachments = attachments,
            messageType = null,
            messageScene = null,
        )

        /**
         * 保留 4.7.0 前的 JVM [copy] 函数签名。
         *
         * @since 4.7.0
         */
        @Deprecated(message = "用于二进制兼容的函数，不应直接使用", level = DeprecationLevel.HIDDEN)
        public fun copy(
            id: String = this.id,
            author: Author = this.author,
            content: String = this.content,
            timestamp: String = this.timestamp,
            attachments: List<Message.Attachment> = this.attachments,
        ): Data = copy(
            id = id,
            author = author,
            content = content,
            timestamp = timestamp,
            attachments = attachments,
            messageType = messageType,
            messageScene = messageScene,
        )
    }

    /**
     * C2C 消息的场景信息。
     *
     * [ext] 中的元素是 `key=value` 形式的扩展信息。自定义菜单的开关操作会在此处携带开关设置结果。
     *
     * @since 4.7.0
     */
    @Serializable
    public class MessageScene(
        /** 消息来源。 */
        public val source: String? = null,
        /** 场景扩展信息。 */
        public val ext: List<String> = emptyList(),
    )

    /**
     * The [Data.author].
     *
     * TODO: 官方单聊消息事件还声明了 `id`、`username`、`bot`、`union_openid`、`union_user_account`、
     * `member_openid` 与 `member_role`；待确认跨 C2C/群聊的统一作者模型后补齐。
     */
    @Serializable
    public data class Author(
        @SerialName("user_openid")
        val userOpenid: String,
    )
}

/**
 * 群聊消息内容体
 *
 * @since 4.3.0
 */
@Serializable
public sealed interface GroupMessageData {

    /**
     * 平台方消息 ID，可以用于被动消息发送
     */
    public val id: String

    /**
     * 发送者
     */
    public val author: GroupMessageAuthor

    /**
     * 消息内容
     */
    public val content: String

    /**
     * 消息生产时间（RFC3339）
     */
    public val timestamp: String

    /**
     * 群聊的 openid
     */
    public val groupOpenid: String

    /**
     * 富媒体文件附件，文件类型："图片，语音，视频，文件"
     * `{"content_type": "", "filename": "", "height": "", "width": "", "size": "", "url": ""}`
     */
    public val attachments: List<Message.Attachment>

}

/**
 * 群聊消息体内的发信人信息。
 * @since 4.3.0
 */
@Serializable
public sealed interface GroupMessageAuthor {
    /**
     * 用户在本群的 member_openid
     */
    public val memberOpenid: String

    /**
     * 消息发送者在群内的身份，枚举值：owner、admin、member
     */
    public val memberRole: GroupMessageAuthorRole

    /**
     * 是否是机器人
     */
    public val bot: Boolean

}

/**
 * 群聊消息体内的发信人身份。
 * @since 4.3.0
 */
@Serializable
public enum class GroupMessageAuthorRole {
    @SerialName("owner")
    OWNER,

    @SerialName("admin")
    ADMIN,

    @SerialName("member")
    MEMBER
}

/**
 * [群聊@机器人](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#群聊-机器人)
 *
 * 触发场景	用户在群聊@机器人发送消息
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_AT_MESSAGE_CREATE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_AT_MESSAGE_CREATE_TYPE)
public data class GroupAtMessageCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: Data,
) : Signal.Dispatch() {
    /**
     * The data of [GroupAtMessageCreate.data]
     */
    @Serializable
    public data class Data(
        override val id: String,
        override val author: Author,
        override val content: String,
        override val timestamp: String,
        @SerialName("group_openid")
        override val groupOpenid: String,
        override val attachments: List<Message.Attachment> = emptyList(),
    ) : GroupMessageData

    /**
     * The [Data.author]
     */
    @Serializable
    public data class Author(
        @SerialName("member_openid")
        override val memberOpenid: String,
        @SerialName("member_role")
        override val memberRole: GroupMessageAuthorRole = GroupMessageAuthorRole.MEMBER,
        override val bot: Boolean = false,
    ) : GroupMessageAuthor
}


/**
 * [群聊全量消息](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/event.html#群聊全量消息)
 *
 * > 说明：当群主设定允许该机器人接收群内全部消息时，机器人可接收到群内所有成员在群内的发言消息。
 *
 * ### 事件示例
 *
 * ```JSON5
 * // Websocket
 * {
 *   "author": {
 *       "member_openid": "E4F4AEA33253A2797FB897C50B81D7ED"
 *   },
 *   "content": " 123",
 *   "group_openid": "C9F778FE6ADF9D1D1DBE395BF744A33A",
 *   "id": "ROBOT1.0_eBIyWnxpmSu6uLQ7u7fU0eGloKGYg4eEa737vRyKnMCgyZjKi7JLYkQ9B0VapbiY",
 *   "timestamp": "2023-11-06T13:37:18+08:00"
 * }
 * ```
 */
@Serializable
@SerialName(EventIntents.GroupAndC2CEvent.GROUP_MESSAGE_CREATE_TYPE)
@DispatchTypeName(EventIntents.GroupAndC2CEvent.GROUP_MESSAGE_CREATE_TYPE)
public data class GroupMessageCreate(
    override val id: String? = null,
    override val s: Long = DEFAULT_SEQ,
    @SerialName("d")
    override val data: Data,
) : Signal.Dispatch() {

    /**
     * The data of [GroupMessageCreate.data]
     */
    @Serializable
    public data class Data(
        override val id: String,
        override val author: Author,
        override val content: String,
        override val timestamp: String,
        @SerialName("group_openid")
        override val groupOpenid: String,
        override val attachments: List<Message.Attachment> = emptyList(),
    ) : GroupMessageData

    /**
     * The [Data.author]
     */
    @Serializable
    public data class Author(
        @SerialName("member_openid")
        override val memberOpenid: String,
        @SerialName("member_role")
        override val memberRole: GroupMessageAuthorRole = GroupMessageAuthorRole.MEMBER,
        override val bot: Boolean = false,
    ) : GroupMessageAuthor
}
