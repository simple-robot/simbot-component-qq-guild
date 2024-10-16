import java.security.KeyPairGenerator
import java.security.SecureRandom
import java.security.Signature
import java.security.spec.NamedParameterSpec
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue


/**
 *
 * @author ForteScarlet
 */
class Ed25519Tests4J {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun a() {
        "110 97 79 67 48 111 99 81 69 51 115 104 87 76 65 102 102 102 86 76 66 49 114 104 89 80 71 55 110 97 79 67 215 195 98 254 120 174 248 31 242 50 135 180 147 98 139 93 176 42 60 79 227 11 33 94 77 25 96 155 93 118 103 58"
            .split(" ")
            .map { it.toUByte() }
            .map { it.toByte() }
            .toByteArray()
            .toHexString().also {
                println(it)
            }
    }


    // https://openjdk.org/jeps/339
    // https://howtodoinjava.com/java15/java-eddsa-example/
    @OptIn(ExperimentalStdlibApi::class)
    // @Test
    fun ed25519KeyGenTest() {
        val secret = "naOC0ocQE3shWLAfffVLB1rhYPG7"
        val seed = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"

        println(System.getProperty("java.version"))

        val kpg = KeyPairGenerator.getInstance("Ed25519")
        kpg.initialize(NamedParameterSpec.ED25519, SecureRandom(seed.toByteArray()))

        val kp = kpg.generateKeyPair()

        assertContentEquals(
            ASSERT_PUBLIC_HEX.hexToByteArray(),
            kp.public.encoded,
        )

        assertContentEquals(
            ASSERT_PRIVATE_HEX.hexToByteArray(),
            kp.private.encoded,
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    // @Test TODO
    fun ed25519VerifyTest() {
        val secret = "naOC0ocQE3shWLAfffVLB1rhYPG7"
        val seed   = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"
        val body = """{"op":0,"d":{},"t":"GATEWAY_EVENT_NAME"}"""
        val timestamp = "1725442341"
        val sig = "865ad13a61752ca65e26bde6676459cd36cf1be609375b37bd62af366e1dc25a8dc789ba7f14e017ada3d554c671a911bfdf075ba54835b23391d509579ed002"

        // 获取 HTTP Header 中 X-Signature-Ed25519 的值进行 hec (十六进制解码)操作后的得到 Signature 并进行校验
        val kpg = KeyPairGenerator.getInstance("Ed25519")
        kpg.initialize(NamedParameterSpec.ED25519, SecureRandom(seed.toByteArray()))
        val kp = kpg.generateKeyPair()
        // 获取 HTTP Header 中 X-Signature-Timestamp 的和 HTTP Body 的值按照 timestamp+body 顺序进行组合成签名体msg

        // algorithm is pure Ed25519
        val signature = Signature.getInstance("Ed25519")
        signature.initVerify(kp.public)
        signature.update("$timestamp$body".toByteArray())
        assertTrue(signature.verify(sig.hexToByteArray()))
//        signature.initSign(kp.private)
//        signature.initSign(kp.private)
//        signature.update("$timestamp$body".toByteArray())
//        val s: ByteArray = signature.sign()
//        s.toHexString()


        // 根据公钥、Signature、签名体调用 Ed25519 算法进行验证


    }

}

private const val ASSERT_PUBLIC_HEX = "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
private const val ASSERT_PRIVATE_HEX =
    "6e614f43306f635145337368574c41666666564c42317268595047376e614f43d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
