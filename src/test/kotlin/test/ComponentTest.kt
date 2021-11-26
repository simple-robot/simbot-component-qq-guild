package test

import love.forte.simbot.Components
import love.forte.simbot.tencentguild.TencentGuildApi
import org.junit.Test

/**
 *
 * @author ForteScarlet
 */
class ComponentTest {

    @Test
    fun test1() {
        println(Components.find(TencentGuildApi.COMPONENT_ID))


    }


}