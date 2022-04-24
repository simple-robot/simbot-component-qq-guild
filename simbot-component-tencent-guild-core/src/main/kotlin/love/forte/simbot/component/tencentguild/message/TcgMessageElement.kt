package love.forte.simbot.component.tencentguild.message

import love.forte.simbot.message.Message


/**
 *
 * 腾讯QQ频道下所有的组件特殊消息的统一类型。
 *
 * 所有腾讯QQ频道的组件消息类型，也就是 [TcgMessageElement] 的实现类型，其开头都会以 `Tcg` 开头，例如 [TcgReplyTo].
 *
 * @author ForteScarlet
 */
public interface TcgMessageElement<out E : TcgMessageElement<E>> : Message.Element<E>