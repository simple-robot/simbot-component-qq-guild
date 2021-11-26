package love.forte.simbot.tencentguild

import io.ktor.http.*
import kotlinx.serialization.modules.SerializersModule
import love.forte.simbot.tencentguild.internal.*

/**
 *
 * https://bot.q.qq.com/wiki/develop/api/openapi/guild/model.html
 */
public object TencentGuildApi {
    public val URL: Url = Url("https://api.sgroup.qq.com/")
    public val SANDBOX_URL: Url = Url("https://sandbox.api.sgroup.qq.com")
    public val serializersModule: SerializersModule = SerializersModule {
        polymorphicDefault(TencentGuildInfo::class) {
            TencentGuildInfoImpl.serializer()
        }
        polymorphicDefault(TencentUserInfo::class) {
            TencentUserInfoImpl.serializer()
        }
        polymorphicDefault(TencentRoleInfo::class) {
            TencentRoleInfoImpl.serializer()
        }
        polymorphicDefault(TencentMemberInfo::class) {
            TencentMemberInfoImpl.serializer()
        }
        polymorphicDefault(TencentChannelInfo::class) {
            TencentChannelInfoImpl.serializer()
        }
    }
}



