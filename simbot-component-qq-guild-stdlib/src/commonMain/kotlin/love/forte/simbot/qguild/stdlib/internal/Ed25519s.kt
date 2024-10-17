/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.stdlib.internal

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.signature.InvalidSignatureException
import com.ionspin.kotlin.crypto.signature.Signature
import com.ionspin.kotlin.crypto.signature.SignatureKeyPair
import com.ionspin.kotlin.crypto.signature.crypto_sign_BYTES
import love.forte.simbot.common.atomic.atomic
import love.forte.simbot.logger.LoggerFactory
import kotlin.jvm.JvmInline

@JvmInline
@OptIn(ExperimentalUnsignedTypes::class)
internal value class Ed25519Keypair(private val keyPair: SignatureKeyPair) {
    val publicKey: Ed25519PublicKey
        get() = Ed25519PublicKey(keyPair.publicKey)

    val privateKey: Ed25519PrivateKey
        get() = Ed25519PrivateKey(keyPair.secretKey)
}

@JvmInline
@OptIn(ExperimentalUnsignedTypes::class)
internal value class Ed25519PrivateKey(private val key: UByteArray) {
    fun sign(msg: ByteArray): SignResult =
        SignResult(Signature.sign(msg.asUByteArray(), key))

    @JvmInline
    internal value class SignResult(private val signature: UByteArray) {
        fun signatureUBytes(): UByteArray =
            signature.copyOf(crypto_sign_BYTES)

        fun signatureBytes(): ByteArray =
            signatureUBytes().asByteArray()

        fun plainUBytes(): UByteArray =
            signature.copyOfRange(crypto_sign_BYTES, signature.size)

        fun plainBytes(): ByteArray =
            plainUBytes().asByteArray()
    }
}

@JvmInline
@OptIn(ExperimentalUnsignedTypes::class)
internal value class Ed25519PublicKey(private val key: UByteArray) {
    fun verify(signature: ByteArray, message: ByteArray): Boolean {
        return try {
            Signature.verifyDetached(
                signature.asUByteArray(),
                message.asUByteArray(),
                key
            )
            true
        } catch (ise: InvalidSignatureException) {
            false
        }

    }
}

@OptIn(ExperimentalUnsignedTypes::class)
internal fun genEd25519Keypair(seed: ByteArray): Ed25519Keypair {
    initialed

    val pk = Signature.seedKeypair(seed.asUByteArray())
    return Ed25519Keypair(pk)
}

private val Ed25519Logger = LoggerFactory.getLogger("love.forte.simbot.qguild.stdlib.internal.Ed25519")

private val initialed: Unit by lazy(
    mode = LazyThreadSafetyMode.SYNCHRONIZED
) {
    if (!LibsodiumInitializer.isInitialized()) {
        Ed25519Logger.info("LibsodiumInitializer is not initialed yet, initializing...")
        val done = atomic(false)
        LibsodiumInitializer.initializeWithCallback {
            Ed25519Logger.info("LibsodiumInitializer initialized in callback")
            done.value = true
        }

        @Suppress("ControlFlowWithEmptyBody")
        while (!done.value) {
        }

        Ed25519Logger.info("LibsodiumInitializer initialized")
    }

    Unit
}
