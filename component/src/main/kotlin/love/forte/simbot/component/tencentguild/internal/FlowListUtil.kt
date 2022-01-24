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

package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.FlowCollector


public suspend inline fun <reified R> FlowCollector<R>.flowForLimiter(
    skip: Int,
    limit: Int,
    maxIfZero: Boolean = true,
    listGetter: () -> List<R>,
) {

    var remainingSkip = skip
    var remainingAmount = if (maxIfZero && limit <= 0) Int.MAX_VALUE else limit

    while (remainingAmount > 0) {
        val list = listGetter()
        if (list.isEmpty()) {
            break
        }
        if (remainingSkip > 0) {
            val size = list.size
            if (size == remainingSkip) {
                remainingSkip = 0
                continue
            }
            if (size < remainingSkip) {
                remainingSkip -= size
                continue
            }
            // size > skip
            // 10 - 2 = 8
            // (0, 1)~ 2 .. 10
            val lastSkip = remainingSkip
            // val last = size - lastSkip
            remainingSkip = 0

            for (i in lastSkip until size) {
                emit(list[i])
                remainingAmount -= 1
                if (remainingAmount <= 0) {
                    break
                }
            }

        } else {
            for (i in list) {
                emit(i)
                remainingAmount -= 1
                if (remainingAmount <= 0) {
                    break
                }
            }
        }

    }
}


public suspend inline fun <reified R> SequenceScope<R>.sequenceForLimiter(
    skip: Int,
    limit: Int,
    maxIfZero: Boolean = true,
    listGetter: () -> List<R>,
) {
    var remainingSkip = skip
    var remainingAmount = if (maxIfZero && limit <= 0) Int.MAX_VALUE else limit

    while (remainingAmount > 0) {
        val list = listGetter()
        if (list.isEmpty()) {
            break
        }
        if (remainingSkip > 0) {
            val size = list.size
            if (size == remainingSkip) {
                remainingSkip = 0
                continue
            }
            if (size < remainingSkip) {
                remainingSkip -= size
                continue
            }
            // size > skip
            // 10 - 2 = 8
            // (0, 1)~ 2 .. 10
            val lastSkip = remainingSkip
            // val last = size - lastSkip
            remainingSkip = 0

            for (i in lastSkip until size) {
                yield(list[i])
                remainingAmount -= 1
                if (remainingAmount <= 0) {
                    break
                }
            }

        } else {
            for (i in list) {
                yield(i)
                remainingAmount -= 1
                if (remainingAmount <= 0) {
                    break
                }
            }
        }

    }
}



