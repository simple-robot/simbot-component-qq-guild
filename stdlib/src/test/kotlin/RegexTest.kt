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