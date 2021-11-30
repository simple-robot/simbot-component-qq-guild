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
