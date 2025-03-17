/*
 * Copyright (c) 2025. ForteScarlet.
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

package love.forte.simbot.qguild.ed25519

import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api
import kotlin.jvm.JvmInline


/**
 *
 * @author ForteScarlet
 */
@InternalEd25519Api
public interface Ed25519KeyPair {
    public val privateKey: Ed25519PrivateKey
    public val publicKey: Ed25519PublicKey
}

@InternalEd25519Api
public operator fun Ed25519KeyPair.component1(): Ed25519PrivateKey = privateKey

@InternalEd25519Api
public operator fun Ed25519KeyPair.component2(): Ed25519PublicKey = publicKey

/**
 * Ed25519 private key.
 */
@InternalEd25519Api
public interface Ed25519PrivateKey {
    /**
     * 仅包含 seed 数据的 32 位字节私钥
     */
    public val bytes: ByteArray

    /**
     * seed + public key 的 64 位字节私钥
     */
    public val bytes64: ByteArray

    /**
     * Sign data with this private key.
     */
    @Throws(Exception::class)
    public fun sign(data: ByteArray): Signature
}

/**
 * Ed25519 public key.
 */
@InternalEd25519Api
public interface Ed25519PublicKey {
    public val bytes: ByteArray

    /**
     * Verify the signature.
     */
    @Throws(Exception::class)
    public fun verify(signature: Signature, data: ByteArray): Boolean
}

@JvmInline
@InternalEd25519Api
public value class Signature(public val data: ByteArray)

/**
 * Repeat to `length` == 32
 */
@InternalEd25519Api
public fun String.paddingEd25519Seed(): String {
    return when {
        length == 32 || isEmpty() -> this
        length > 32 -> substring(32)
        else -> {
            buildString(32) {
                append(this@paddingEd25519Seed)
                while (length < 32) {
                    append(
                        this@paddingEd25519Seed,
                        0,
                        kotlin.math.min(32 - length, this@paddingEd25519Seed.length)
                    )
                }
            }

        }
    }
}
