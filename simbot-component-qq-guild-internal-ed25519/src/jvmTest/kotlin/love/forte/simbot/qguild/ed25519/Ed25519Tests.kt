package love.forte.simbot.qguild.ed25519

import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.crypto.signers.Ed25519Signer
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.BeforeAll
import java.security.Security
import kotlin.test.*


/**
 *
 * @author ForteScarlet
 */
class Ed25519Tests {
    companion object {
        // 下面这些都是官方文档里提供的用于测试的数据，不包含敏感信息。

        private const val ASSERT_PUBLIC_HEX = "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"

        // 似乎 private bytes = seed.bytes + public.bytes
        private const val ASSERT_PRIVATE_HEX =
            "6e614f43306f635145337368574c41666666564c42317268595047376e614f43" +
                    "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"

        private const val SEED = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"

        private const val SECRET = "DG5g3B4j9X2KOErG"
        private const val PLAIN_TOKEN = "Arq0D5A61EgUu4OxUvOp"
        private const val TS = "1725442341"
        // val body = """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}"""

        private const val SIGNATURE =
            "87befc99c42c651b3aac0278e71ada338433ae26fcb24307bdc5ad38c1adc2d01bcfcadc0842edac85e85205028a1132afe09280305f13aa6909ffc2d652c706"

        @JvmStatic
        @BeforeAll
        fun addBouncyCastle() {
            assertNull(Security.getProvider("BC"))
            Security.addProvider(BouncyCastleProvider())
        }
    }

    /**
     * 使用 bouncy castle 生成 ed25519 的公私钥对。
     */
    @Test
    fun testBouncyCastleEd25519KeyPairGeneration() {
        val seedBytes = SEED.toByteArray()
        assertEquals("BC", Security.getProvider("BC").name)

        // 2. 生成Bouncy Castle的Ed25519私钥和公钥参数
        val privateKeyParams = Ed25519PrivateKeyParameters(seedBytes, 0)
        val publicKeyParams = privateKeyParams.generatePublicKey()

        // 3. 提取公钥字节并转换为十六进制
        val publicKeyBytes = publicKeyParams.encoded

        // 4. 构造私钥字节（种子 + 公钥）
        val privateKeyCombined = ByteArray(seedBytes.size + publicKeyBytes.size)

        seedBytes.copyInto(privateKeyCombined, destinationOffset = 0)
        publicKeyBytes.copyInto(privateKeyCombined, destinationOffset = seedBytes.size)

        verifyKeyPair(publicKeyBytes, privateKeyCombined)
    }

    /**
     * 使用 bouncy castle 进行 ed25519 的签名。
     */
    @OptIn(ExperimentalStdlibApi::class, InternalEd25519Api::class)
    @Test
    fun testBouncyCastleEd25519Sign() {
        val seed = SECRET.paddingEd25519Seed()
        val seedBytes = seed.toByteArray()

        // 2. 生成Bouncy Castle的Ed25519私钥和公钥参数
        val privateKey = Ed25519PrivateKeyParameters(seedBytes, 0)
        // val publicKey = privateKey.generatePublicKey()

        val msgBytes = "$TS$PLAIN_TOKEN".toByteArray()

        val signer = Ed25519Signer()
        signer.init(true, privateKey)
        signer.update(msgBytes, 0, msgBytes.size)
        val signature = signer.generateSignature()

        assertEquals(
            SIGNATURE,
            signature.toHexString()
        )
    }

    /**
     * 使用 bouncy castle 进行 ed25519 的验证。
     */
    @OptIn(InternalEd25519Api::class, ExperimentalStdlibApi::class)
    // @Test // TODO 官方文档的签名验证部分跑不通，但是自己生成一个又没意义
    fun testBouncyCastleEd25519Verify() {
        val sig =
            "865ad13a61752ca65e26bde6676459cd36cf1be609375b37bd62af366e1dc25a8dc789ba7f14e017ada3d554c671a911bfdf075ba54835b23391d509579ed002"
                .hexToByteArray()

        val body = "{\"op\":0,\"d\":{},\"t\":\"GATEWAY_EVENT_NAME\"}"
        val secret = "naOC0ocQE3shWLAfffVLB1rhYPG7"
        val timestamp = "1725442341"

        val msgBytes = "$timestamp$body".toByteArray()

        val seed = secret.paddingEd25519Seed()
        val seedBytes = seed.toByteArray()

        // 2. 生成Bouncy Castle的Ed25519私钥和公钥参数
        val privateKey = Ed25519PrivateKeyParameters(seedBytes, 0)
        val publicKey = privateKey.generatePublicKey()

        val signer = Ed25519Signer()
        signer.init(false, publicKey)
        signer.update(msgBytes, 0, msgBytes.size)

        assertTrue(
            signer.verifySignature(sig),
            "signature verify failed"
        )
    }

    @Test
    fun testEd25519JavaGenerate() {
        val seedBytes = SEED.toByteArray()

        val privateKey = EdDSAPrivateKey(
            EdDSAPrivateKeySpec(
                seedBytes,
                EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519)
            )
        )

        val pub = privateKey.abyte
        // seed + pub
        val pri = ByteArray(seedBytes.size + pub.size)

        seedBytes.copyInto(pri, destinationOffset = 0)
        pub.copyInto(pri, destinationOffset = seedBytes.size)

        verifyKeyPair(pub, pri)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun verifyKeyPair(publicKeyBytes: ByteArray, privateKeyBytes: ByteArray) {
        assertContentEquals(
            ASSERT_PUBLIC_HEX.hexToByteArray(),
            publicKeyBytes,
            "public key not equals"
        )

        assertContentEquals(
            ASSERT_PRIVATE_HEX.hexToByteArray(),
            privateKeyBytes,
            "private key not equals"
        )
    }
}
