//import diglol.crypto.Ed25519
//import io.ktor.utils.io.core.*
//import kotlinx.coroutines.test.runTest
//import kotlin.test.Test
//import kotlin.test.assertContentEquals
//
//
///**
// *
// * @author ForteScarlet
// */
//class Ed25519Tests {
//
//    /**
//     * https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/sign.html
//     *
//     * 验证签名过程:
//     * 根据开发者平台的 Bot Secret 值进行repeat操作得到签名32字节的 seed ,
//     * 根据 seed 调用 Ed25519 算法生成32字节公钥
//     */
//    @OptIn(ExperimentalStdlibApi::class)
//    // @Test
//    fun ed25519Test() = runTest {
//        val secret = "naOC0ocQE3shWLAfffVLB1rhYPG7"
//        val seed = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"
//        val pair = Ed25519.generateKeyPair(seed.toByteArray())
//        println("publicKey: " + pair.publicKey.joinToString { it.toUByte().toString() })
//        println("privateKey: " + pair.privateKey.joinToString { it.toUByte().toString() })
//
//        assertContentEquals(
//            "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
//                .hexToByteArray(),
//            pair.publicKey,
//        )
//
//        assertContentEquals(
//            "6e614f43306f635145337368574c41666666564c42317268595047376e614f43d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
//                .hexToByteArray(),
//            pair.privateKey,
//        )
//    }
//
//}
