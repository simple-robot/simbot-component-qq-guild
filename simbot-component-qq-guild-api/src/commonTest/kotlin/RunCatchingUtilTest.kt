package test

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.utils.runCatchingCancellable
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RunCatchingUtilTest {
    @Test
    fun cancellationIsRethrown() = runTest {
        val cancellation = CancellationException("cancelled")

        val thrown = assertFailsWith<CancellationException> {
            runCatchingCancellable<Unit> { throw cancellation }
        }

        assertSame(cancellation, thrown)
    }

    @Test
    fun regularFailureIsCaptured() = runTest {
        val failure = IllegalStateException("failed")

        val result = runCatchingCancellable<Unit> { throw failure }

        assertTrue(result.isFailure)
        assertSame(failure, result.exceptionOrNull())
    }
}
