import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class RegexTest {

    @Test
    fun test() {
        val regex = Regex("<@!(?<uid>\\d+)>|<#!(?<cid>\\d+)>")

        val text = "<@!15568778634248196340> aaa <@!123123123123><#!666666666> abcd"

        var lastTextIndex = 0

        regex.findAll(text).forEach {
            if (it.range.first != lastTextIndex) {
                println("text:  '${text.substring(lastTextIndex until  it.range.first)}'")

            }
            lastTextIndex = it.range.last + 1
            println("regex: '${it.value}'")
        }
        if (lastTextIndex != text.length) {
            println("text:  '${text.substring(lastTextIndex)}'")
        }



    }

}