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

package test

import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.TencentAudioControl
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class AudioTest {

    @Test
    fun test1() {

        val audioControl = TencentAudioControl(
            audioUrl = "url",
            text = "text",
            status = TencentAudioControl.Status.START,
        )

        println(audioControl)

        val jsonStr = Json.encodeToString(TencentAudioControl.serializer(), audioControl)
        println(jsonStr)

        val audio = Json.decodeFromString(TencentAudioControl.serializer(), jsonStr)

        println(audio == audioControl)

    }

}