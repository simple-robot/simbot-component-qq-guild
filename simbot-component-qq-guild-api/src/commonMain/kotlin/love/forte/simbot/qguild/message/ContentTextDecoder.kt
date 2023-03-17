/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.qguild.message


private const val S_LT: String = "<"
private const val S_GT: String = ">"
private const val S_AMP: String = "&"

private val sLtEscape: String = Regex.escapeReplacement(S_LT)
private val sGtEscape: String = Regex.escapeReplacement(S_GT)
private val sAmpEscape: String = Regex.escapeReplacement(S_AMP)

private const val E_LT: String = "&lt;"
private const val E_GT: String = "&gt;"
private const val E_AMP: String = "&amp;"

private val eLtEscape: String = Regex.escapeReplacement(E_LT)
private val eGtEscape: String = Regex.escapeReplacement(E_GT)
private val eAmpEscape: String = Regex.escapeReplacement(E_AMP)

/**
 * QQ频道中[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)解码器。
 *
 * 用于将**转义后**字符解码为**源字符**。
 *
 * | **源字符** | **转义后** |
 * |----------|----------|
 * | `&` <- | `&amp;` |
 * | `<` <- | `&lt;` |
 * | `>` <- | `&gt;` |
 */
public object ContentTextDecoder {
    private val replaceRegex = Regex("$eLtEscape|$eGtEscape|$eAmpEscape")

    /**
     * 将 [format] 中的被转义的内容解码为源字符
     */
    public fun decode(format: String): String {
        return replaceRegex.replace(format) {
            when (val v = it.value) {
                E_LT -> S_LT
                E_GT -> S_GT
                E_AMP -> S_AMP
                else -> v
            }
        }
    }
}

/**
 * QQ频道中[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)编码器。
 *
 * 用于将**源字符**编码为**转义后**字符。
 *
 * | **源字符** | **转义后** |
 * |----------|----------|
 * | `&` -> | `&amp;` |
 * | `<` -> | `&lt;` |
 * | `>` -> | `&gt;` |
 */
public object ContentTextEncoder {
    private val replaceRegex = Regex("$sLtEscape|$sGtEscape|$sAmpEscape")

    /**
     * 将 [text] 中的源字符转义
     */
    public fun encode(text: String): String {
        return replaceRegex.replace(text) {
            when (val v = it.value) {
                S_LT -> E_LT
                S_GT -> E_GT
                S_AMP -> E_AMP
                else -> v
            }
        }
    }
}
