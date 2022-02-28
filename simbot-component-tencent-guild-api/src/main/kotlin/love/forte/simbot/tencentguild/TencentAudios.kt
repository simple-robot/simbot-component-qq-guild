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

package love.forte.simbot.tencentguild

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.tencentguild.internal.*


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