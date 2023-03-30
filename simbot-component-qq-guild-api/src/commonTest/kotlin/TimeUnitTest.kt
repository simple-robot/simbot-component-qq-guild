package test

import love.forte.simbot.qguild.time.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals


class TimeUnitTest {

    @Test
    fun timeUnitTest() {
        assertEquals(TimeUnit.DAYS.toSeconds(30), TimeUnit.HOURS.toSeconds(30 * 24))
    }

}
