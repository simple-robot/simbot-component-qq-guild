/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.tencentguild.api


/**
 *
 * 支持结果缓存的 [TencentApi] 实现，
 * 会通过一定的策略对目标 [TencentApi] 的结果进行缓存，并在 [doRequest] 中进行获取并跳过请求环节。
 *
 *
 * @author ForteScarlet
 */
public sealed class TencentCacheableApi<R> : TencentApi<R>() {

    /**
     * 内部存在的缓存结果。如果为null则代表缓存不存在或已失效。
     */
    public abstract val cachedValue: R?

    /**
     * 缓存模式。
     */
    public sealed class CacheType {

        /**
         * TODO
         */
        public class Lazy(public val lazyThreadSafetyMode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED)


    }

}



// Type
// Kind
// Mode
