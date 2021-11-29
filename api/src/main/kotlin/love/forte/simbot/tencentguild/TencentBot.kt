package love.forte.simbot.tencentguild


/**
 *
 * 一个 [TencentBot] 标准接口，其只提供接口定义，而实现则在 `core` 模块中。
 *
 * [TencentBot] 不考虑实现 simple-robot-api 中的 [love.forte.simbot.Bot] 接口，由对组件进行实现，此处的 [TencentBot] 仅定义对于一个频道机器人的最基本的信息。
 *
 * @author ForteScarlet
 */
public interface TencentBot {

    /**
     * 当前bot的 [Ticket].
     */
    public val ticket: Ticket


    /**
     * [票据](https://bot.q.qq.com/wiki/develop/api/#%E7%A5%A8%E6%8D%AE)
     */
    public interface Ticket {
        /**
         * 用于识别一个机器人的 id
         */
        public val appId: String

        /**
         * 用于在 oauth 场景进行请求签名的密钥，在一些描述中也叫做 app_secret
         */
        public val appKey: String

        /**
         * 机器人token，用于以机器人身份调用 openapi，格式为 ${app_id}.${random_str}
         */
        public val token: String
    }


    /**
     * 启动当前BOT。只有 [start] 了之后，[clients] 中才会出现数据，否则可能会抛出异常。
     *
     * @return 当且仅当启动了并且成功了才会得到true。
     */
    public suspend fun start(): Boolean


    /**
     * 终止当前BOT。
     *
     * @return 当且仅当此BOT未关闭且关闭成功才会得到true。
     */
    public suspend fun cancel(): Boolean



    /**
     * 挂起直到此bot被 [cancel]. 如果已经 [cancel], 则不会挂起。
     */
    public suspend fun join()


    /**
     * 此bot所需要的全部分片数量。
     * 这并不代表此bot中所**包含**的分片数量，而是代表你通过 /gateway/bot 或者根据你自己的判断而决定的最终的所有分片数量。
     *
     * 如果你想知道当前bot中包含了那几个分片，启动后通过 [clients] 中的 [shared][Client.shared] 可以得到。
     *
     */
    public val totalShared: Int


    /**
     * 此Bot中包含的连接。
     *
     * 如果此一个bot存在多个分片，那么这可能包
     *
     */
    public val clients: List<Client>


    /**
     * Bot的连接信息。一般来讲代表一个ws连接。
     */
    public interface Client {

        /**
         * 此连接所属的 [TencentBot]
         */
        public val bot: TencentBot

        /**
         * 此连接对应的分片信息。
         */
        public val shared: Shared

        /**
         * 事件推送所携带的 `s`.
         */
        public val s: Long

    }


}