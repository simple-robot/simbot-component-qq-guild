package love.forte.simbot.qguild.ed25519

import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull


/**
 *
 * @author ForteScarlet
 */
class GenTest {
    companion object {
        private const val ASSERT_PUBLIC_HEX = "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"

        // 似乎 private bytes = seed.bytes + public.bytes
        private const val ASSERT_PRIVATE_HEX =
            "6e614f43306f635145337368574c41666666564c42317268595047376e614f43" +
                    "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"

        private const val SEED = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"
    }

    @Test
    fun testBouncyCastleEd25519() {
        val seedBytes = SEED.toByteArray()

        assertNull(Security.getProvider("BC"))
        Security.addProvider(BouncyCastleProvider())
        assertEquals("BC", Security.getProvider("BC").name)

        // 2. 生成Bouncy Castle的Ed25519私钥和公钥参数
        val privateKeyParams = Ed25519PrivateKeyParameters(seedBytes, 0)
        val publicKeyParams = privateKeyParams.generatePublicKey()

        // 3. 提取公钥字节并转换为十六进制
        val publicKeyBytes = publicKeyParams.encoded

        // 4. 构造私钥字节（种子 + 公钥）
        val privateKeyCombined = ByteArray(seedBytes.size + publicKeyBytes.size)

//        System.arraycopy(seedBytes, 0, privateKeyCombined, 0, seedBytes.size)
//        System.arraycopy(publicKeyBytes, 0, privateKeyCombined, seedBytes.size, publicKeyBytes.size)

        seedBytes.copyInto(privateKeyCombined, destinationOffset = 0)
        publicKeyBytes.copyInto(privateKeyCombined, destinationOffset = seedBytes.size)

        verifyKeyPair(publicKeyBytes, privateKeyCombined)
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
