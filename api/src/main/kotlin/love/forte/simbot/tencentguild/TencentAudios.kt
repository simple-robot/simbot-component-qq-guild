/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.tencentguild

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID
import love.forte.simbot.tencentguild.internal.TencentAudioActionImpl


/**
 * [语音对象](https://bot.q.qq.com/wiki/develop/api/openapi/audio/model.html)
 */
@Serializable
public data class TencentAudioControl(
    /**
     * 音频数据的url status为0时传
     */
    @SerialName("audio_url")
    public val audioUrl: String?,

    /**
     * 状态文本（比如：简单爱-周杰伦），可选，status为0时传，其他操作不传
     */
    public val text: String?,

    /**
     * 播放状态
     */
    public val status: Status

) {

    @Serializable
    public enum class Status {
        /**
         * 开始播放操作
         */
        @SerialName("0")
        START,

        /**
         * 暂停播放操作
         */
        @SerialName("1")
        PAUSE,

        /**
         * 继续播放操作
         */
        @SerialName("2")
        RESUME,

        /**
         * 停止播放操作
         */
        @SerialName("3")
        STOP,
        ;

        public companion object {
            public fun byCode(code: Int): Status {
                return when (code) {
                    0 -> START
                    1 -> PAUSE
                    2 -> RESUME
                    3 -> STOP
                    else -> throw NoSuchElementException("code: $code")
                }
            }
        }
    }
}


/**
 * https://bot.q.qq.com/wiki/develop/api/openapi/audio/model.html#audioaction
 */
public interface TencentAudioAction {
    public val guildId: ID
    public val channelId: ID

    /**
     * 音频数据的url status为0时传
     */
    public val audioUrl: String

    /**
     * 状态文本（比如：简单爱-周杰伦），可选，status为0时传，其他操作不传
     */
    public val text: String

    public companion object {
        internal val serializer: KSerializer<out TencentAudioAction> = TencentAudioActionImpl.serializer()
    }
}