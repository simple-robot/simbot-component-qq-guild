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

package test;

import io.ktor.client.HttpClient;
import kotlinx.serialization.json.Json;
import love.forte.simbot.tencentguild.TencentGuildInfo;
import love.forte.simbot.tencentguild.api.ApiRequestUtil;
import love.forte.simbot.tencentguild.api.guild.GetBotGuildListApi;

import java.util.List;

/**
 * @author ForteScarlet
 */
public class Api4JTest {

    public void run() {
        final HttpClient client = ApiRequestUtil.newHttpClient();
        final Json decoder = ApiRequestUtil.newJson((builder) -> {
            builder.setLenient(true);
            builder.setIgnoreUnknownKeys(true);
        });

        // 构建请求
        final GetBotGuildListApi api = new GetBotGuildListApi(null, null, 10);

        // 使用 ApiRequestUtil.doRequest
        final List<? extends TencentGuildInfo> infoList = ApiRequestUtil.doRequest(
                api,
                client,
                "https://sandbox.api.sgroup.qq.com",
                "token",
                decoder
        );

        for (TencentGuildInfo info : infoList) {
            System.out.println(info);
            System.out.println(info.getName());
            System.out.println(info.getOwnerId());
        }
    }

}
