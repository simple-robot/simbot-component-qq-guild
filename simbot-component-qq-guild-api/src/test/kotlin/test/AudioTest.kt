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

package test

import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.TencentAudioControl
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
