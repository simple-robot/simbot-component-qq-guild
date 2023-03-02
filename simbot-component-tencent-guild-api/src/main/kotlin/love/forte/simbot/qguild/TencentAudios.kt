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

package love.forte.simbot.qguild

import kotlinx.serialization.*
import love.forte.simbot.*
import love.forte.simbot.qguild.internal.*


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
