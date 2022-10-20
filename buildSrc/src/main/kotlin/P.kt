/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

/**
 * Project versions.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object P {
    object Simbot {
        const val GROUP = "love.forte.simbot"
        const val BOOT_GROUP = "love.forte.simbot.boot"
        
        val version = Version(
            "3", 0, 0,
            status = VersionStatus.beta(3, null, null),
            isSnapshot = isSnapshot()
        )
        
        val isSnapshot: Boolean get() = version.isSnapshot
        val VERSION get() = version.fullVersion(true) // = if (SNAPSHOT) "$REAL_VERSION-SNAPSHOT" else REAL_VERSION
        
        /**
         * 返回结果：`love.forte.simbot:simbot-$name:$VERSION`
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun simbot(name: String): String {
            return "$GROUP:simbot-$name:$VERSION"
        }
        
        /**
         * 返回结果：`love.forte.simbot.boot:simboot-$name:$VERSION`
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun simboot(name: String): String {
            return "$BOOT_GROUP:simboot-$name:$VERSION"
        }
        
    }
    
    object ComponentTencentGuild {
        val isSnapshot get() = Simbot.isSnapshot
        val version = Version(
            major = "${Simbot.version.major}.${Simbot.version.minor}",
            minor = 0, patch = 0,
            status = VersionStatus.alpha(2, null),
            isSnapshot = Simbot.isSnapshot
        )
        const val GROUP = "${Simbot.GROUP}.component"
        const val DESCRIPTION = "Simple Robot框架下针对QQ频道的组件实现"
        
        val VERSION: String get() = version.fullVersion(true)
    }
    
    
}

fun isSnapshot(): Boolean {
    val property = System.getProperty("simbot.snapshot")?.toBoolean() ?: false
    val env = System.getenv(Env.IS_SNAPSHOT)?.toBoolean() ?: false
    println("Is snapshot from System.property:  $property")
    println("Is snapshot from System.env:       $env")
    
    return property || env
}
