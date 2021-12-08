package test

import kotlinx.serialization.json.Json
import love.forte.simbot.tencentguild.TencentMessage
import love.forte.simbot.tencentguild.api.message.ArkMessageTemplates
import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class MessageTemplateTest {

    @Test
    fun test() {
        val t23 = ArkMessageTemplates.TextLinkList(
            desc = "desc",
            prompt = "prompt",
            list = listOf(
                ArkMessageTemplates.TextLinkList.Desc("DESC"),
                ArkMessageTemplates.TextLinkList.Desc("DESC", "https://sss")
            )
        )
        println(Json { prettyPrint = true }.encodeToString(TencentMessage.Ark.serializer(), t23.ark))


    }
}