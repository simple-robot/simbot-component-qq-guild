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