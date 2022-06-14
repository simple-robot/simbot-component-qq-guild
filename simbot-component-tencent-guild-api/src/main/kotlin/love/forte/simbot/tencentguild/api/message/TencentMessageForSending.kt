/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

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
    public var content: String? = null,

    /**
     * MessageEmbed	embed 消息，一种特殊的 ark
     */
    public var embed: TencentMessage.Embed? = null,

    /**
     * ark消息对象	ark 消息
     */
    public var ark: TencentMessage.Ark? = null,

    /**
     * 图片url地址
     */
    public var image: String? = null,

    /**
     * 要回复的消息id(Message.id), 在 CREATE_MESSAGE 事件中获取。带了 msg_id 视为被动回复消息，否则视为主动推送消息
     */
    @SerialName("msg_id")
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    public var msgId: ID? = null,
)

