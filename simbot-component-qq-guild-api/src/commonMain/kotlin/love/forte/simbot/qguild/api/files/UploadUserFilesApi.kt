/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.qguild.api.files

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.api.PostQQGuildApi
import love.forte.simbot.qguild.api.SimplePostApiDescription
import love.forte.simbot.qguild.model.MessageMedia
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic


/**
 * [富媒体消息-单聊](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/message/send-receive/rich-media.html#用于单聊)
 *
 * @author ForteScarlet
 */
public class UploadUserFilesApi private constructor(
    openid: String,
    private val _body: Body
) : PostQQGuildApi<MessageMedia>() {
    public companion object Factory : SimplePostApiDescription(
        "/v2/users/{openid}/files"
    ) {
        public const val FILE_TYPE_IMAGE: Int = 1
        public const val FILE_TYPE_VIDEO: Int = 2
        public const val FILE_TYPE_AUDIO: Int = 3
        public const val FILE_TYPE_FILE: Int = 4

        /**
         * Create [UploadUserFilesApi].
         *
         * @param openid QQ 用户的 openid，可在各类事件中获得。
         */
        @JvmStatic
        public fun create(openid: String, body: Body): UploadUserFilesApi =
            UploadUserFilesApi(openid, body)

        /**
         * Create [UploadUserFilesApi].
         *
         * @param openid QQ 用户的 openid，可在各类事件中获得。
         * @param fileType 媒体类型：1 图片，2 视频，3 语音，4 文件（暂不开放）
         * 资源格式要求:
         * 图片：png/jpg，视频：mp4，语音：silk
         * @param url 需要发送媒体资源的url
         * @param srvSendMsg 设置 true 会直接发送消息到目标端，且会占用主动消息频次
         */
        @JvmStatic
        @JvmOverloads
        public fun create(
            openid: String,
            fileType: Int,
            url: String,
            srvSendMsg: Boolean = false
        ): UploadUserFilesApi =
            create(
                openid,
                Body(
                    fileType = fileType,
                    url = url,
                    srvSendMsg = srvSendMsg
                )
            )

    }

    override val resultDeserializationStrategy: DeserializationStrategy<MessageMedia>
        get() = MessageMedia.serializer()

    override val path: Array<String> = arrayOf("v2", "users", openid, "files")

    override fun createBody(): Any = _body

    /**
     * @property fileType 媒体类型：1 图片，2 视频，3 语音，4 文件（暂不开放）
     * 资源格式要求:
     * 图片：png/jpg，视频：mp4，语音：silk
     * @property url 需要发送媒体资源的url
     * @property srvSendMsg 设置 true 会直接发送消息到目标端，且会占用主动消息频次
     */
    @Serializable
    public data class Body(
        @SerialName("file_type")
        val fileType: Int,
        val url: String,
        @SerialName("srv_send_msg")
        val srvSendMsg: Boolean,
    )
}
