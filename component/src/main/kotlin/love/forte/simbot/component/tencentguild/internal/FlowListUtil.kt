package love.forte.simbot.component.tencentguild.internal

import kotlinx.coroutines.flow.FlowCollector


public suspend inline fun <reified R> FlowCollector<R>.flowForLimiter(
    skip: Int,
    limit: Int,
    listGetter: () -> List<R>,
) {

    var remainingSkip = skip
    var remainingAmount = limit

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
    listGetter: () -> List<R>,
) {
    var remainingSkip = skip
    var remainingAmount = limit

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
