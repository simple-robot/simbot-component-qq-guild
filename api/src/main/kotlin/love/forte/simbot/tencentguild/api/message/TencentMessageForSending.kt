package love.forte.simbot.tencentguild.api.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.TencentMessage


/**
 * 用于发送的普通消息.
 *
 * _content, embed, ark, image 至少需要有一个字段，否则无法下发消息。_
 */
@Serializable
public data class TencentMessageForSending @JvmOverloads constructor(
    /**
     * 消息内容，文本内容，支持内嵌格式
     */
    public val content: String? = null,

    /**
     * MessageEmbed	embed 消息，一种特殊的 ark
     */
    public val embed: TencentMessage.Embed? = null,

    /**
     * ark消息对象	ark 消息
     */
    public val ark: TencentMessage.Ark? = null,

    /**
     * 图片url地址
     */
    public val image: String? = null,

    /**
     * 要回复的消息id(Message.id), 在 CREATE_MESSAGE 事件中获取。带了 msg_id 视为被动回复消息，否则视为主动推送消息
     */
    @SerialName("msg_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public val msgId: ID? = null,
)