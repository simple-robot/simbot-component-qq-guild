package love.forte.simbot.component.tencentguild.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.component.tencentguild.internal.MessageParsers
import love.forte.simbot.message.Message
import love.forte.simbot.message.Messages
import love.forte.simbot.message.ReceivedMessageContent
import love.forte.simbot.tencentguild.TencentMessage

/**
 *
 * @author ForteScarlet
 */
public class TencentReceiveMessageContent(sourceMessage: TencentMessage) : ReceivedMessageContent() {

    override val metadata: Metadata = Metadata(
        sourceMessage.id,
        sourceMessage.channelId,
        sourceMessage.guildId,
        sourceMessage.timestamp
    )

    override val messages: Messages = MessageParsers.parse(sourceMessage)


    override fun toString(): String {
        return "TencentReceiveMessageContent(metadata=$metadata)"
    }
    override fun hashCode(): Int = metadata.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is TencentReceiveMessageContent) return false
        return metadata == other.metadata
    }

    /**
     * Tencent频道中的消息元数据，存储消息的id，消息频道的id，消息子频道的id和消息创建的时间。
     *
     */
    @SerialName("tcg.m.meta")
    @Serializable
    public data class Metadata(
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        override val id: ID,
        @SerialName("channel_id")
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        public val channelId: ID,
        @SerialName("guild_id")
        @Serializable(ID.AsCharSequenceIDSerializer::class)
        public val guildId: ID,
        @SerialName("create_time")
        public val createTime: Timestamp
    ) : Message.Metadata() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as Metadata

            if (id != other.id) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + id.hashCode()
            return result
        }
    }

}