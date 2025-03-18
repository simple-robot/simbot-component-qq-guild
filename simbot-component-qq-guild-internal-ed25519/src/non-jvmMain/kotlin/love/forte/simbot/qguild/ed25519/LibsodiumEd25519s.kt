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

// TODO


@InternalEd25519Api
public class LibsodiumEddsaEd25519KeyPair(
    seed: ByteArray
) : Ed25519KeyPair {
    override val privateKey: Ed25519PrivateKey
        get() = TODO("Not yet implemented")

    override val publicKey: Ed25519PublicKey
        get() = TODO("Not yet implemented")
}

/**
 * Initialed libsodium by lazy.
 */
internal expect val initialLibsodium: Unit

private val initialed: Unit by lazy(
    mode = LazyThreadSafetyMode.SYNCHRONIZED
) {
    if (!LibsodiumInitializer.isInitialized()) {
        ed25519sLogger.info("LibsodiumInitializer is not initialed yet, initializing...")
        val done = atomic(false)
        LibsodiumInitializer.initializeWithCallback {
            ed25519sLogger.info("LibsodiumInitializer initialized in callback")
            done.value = true
        }

        @Suppress("ControlFlowWithEmptyBody")
        while (!done.value) {
        }

        ed25519sLogger.info("LibsodiumInitializer initialized")
    }

    Unit
}
