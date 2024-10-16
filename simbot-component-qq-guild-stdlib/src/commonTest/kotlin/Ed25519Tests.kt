import io.github.andreypfau.curve25519.ed25519.Ed25519
import io.ktor.utils.io.core.*
import love.forte.simbot.qguild.stdlib.internal.paddingEd25519Seed
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 *
 * @author ForteScarlet
 */
class Ed25519TestsCommon {

    /**
     * https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/sign.html
     *
     * 验证签名过程:
     * 根据开发者平台的 Bot Secret 值进行repeat操作得到签名32字节的 seed ,
     * 根据 seed 调用 Ed25519 算法生成32字节公钥
     */
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun ed25519KeyGenTest() {
        // val secret = "naOC0ocQE3shWLAfffVLB1rhYPG7"
        val seed = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"

        val pk = Ed25519.keyFromSeed(seed.toByteArray())

        assertContentEquals(
            ASSERT_PUBLIC_HEX.hexToByteArray(),
            pk.publicKey().toByteArray()
        )

        assertContentEquals(
            ASSERT_PRIVATE_HEX.hexToByteArray(),
            pk.toByteArray()
        )
    }

    @Test
    fun ed25519SeedPadding() {
        assertEquals(
            "DG5g3B4j9X2KOErGDG5g3B4j9X2KOErG",
            "DG5g3B4j9X2KOErG".paddingEd25519Seed()
        )
        assertEquals(
            "DG5g3B4j9X2KOErGDG5g3B4j9X2KOErG",
            "DG5g3B4j9X2KOErGDG5g3B4j9X2KOErGDG5g3B4j9X2KOErGDG5g3B4j9X2KOErG".paddingEd25519Seed()
        )
    }

    // is OK
    @OptIn(ExperimentalStdlibApi::class)
    private fun a() {
        val secret = "DG5g3B4j9X2KOErG"
        val seed   = secret.paddingEd25519Seed()
        val body = """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}"""
        val timestamp = "1725442341"
        val sig =
            "865ad13a61752ca65e26bde6676459cd36cf1be609375b37bd62af366e1dc25a8dc789ba7f14e017ada3d554c671a911bfdf075ba54835b23391d509579ed002"


        val sigBytes = sig.hexToByteArray()

        assertEquals(Ed25519.SIGNATURE_SIZE_BYTES, sigBytes.size)
        assertEquals(0u, sigBytes[63].toUByte().and(224u))

        val pk = Ed25519.keyFromSeed(seed.toByteArray())
        val msg = "$timestamp$body"

        println(pk.sign(msg.toByteArray()).toHexString())
        println(pk.sign(msg.toByteArray()).toHexString())

        assertTrue { pk.publicKey().verify(msg.toByteArray(), sigBytes) }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun ed25519VerifyTest() {
        // val appId = "11111111"
        val secret = "DG5g3B4j9X2KOErG"
        val seed   = secret.paddingEd25519Seed()
        val plainToken = "Arq0D5A61EgUu4OxUvOp"
        val ts = "1725442341"
        // val body = """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}"""

        val pk = Ed25519.keyFromSeed(seed.toByteArray())
        val msg = "$ts$plainToken"

        val signature = pk.sign(msg.toByteArray())

        assertEquals(
            "87befc99c42c651b3aac0278e71ada338433ae26fcb24307bdc5ad38c1adc2d01bcfcadc0842edac85e85205028a1132afe09280305f13aa6909ffc2d652c706",
            signature.toHexString()
        )
    }

    companion object {
        private const val ASSERT_PUBLIC_HEX = "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
        private const val ASSERT_PRIVATE_HEX =
            "6e614f43306f635145337368574c41666666564c42317268595047376e614f43d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
    }
}

