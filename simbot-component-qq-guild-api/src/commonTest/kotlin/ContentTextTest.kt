package test

import love.forte.simbot.qguild.message.ContentTextDecoder
import love.forte.simbot.qguild.message.ContentTextEncoder
import kotlin.test.Test
import kotlin.test.assertEquals

class ContentTextTest {

    @Test
    fun encodeTest() {
        assertEquals("123", ContentTextEncoder.encode("123"))
        assertEquals("&lt;@!user_id&gt;", ContentTextEncoder.encode("<@!user_id>"))
        assertEquals("&lt;&amp;&gt;", ContentTextEncoder.encode("<&>"))
        assertEquals("&amp;&lt;&gt;&amp;", ContentTextEncoder.encode("&<>&"))
        assertEquals("&amp;amp;", ContentTextEncoder.encode("&amp;"))
    }

    @Test
    fun decodeTest() {
        assertEquals("123", ContentTextDecoder.decode("123"))
        assertEquals("<@!user_id>", ContentTextDecoder.decode("&lt;@!user_id&gt;"))
        assertEquals("<&>", ContentTextDecoder.decode("&lt;&amp;&gt;"))
        assertEquals("&<>&", ContentTextDecoder.decode("&amp;&lt;&gt;&amp;"))
        assertEquals("&amp;", ContentTextDecoder.decode("&amp;amp;"))
    }


}
