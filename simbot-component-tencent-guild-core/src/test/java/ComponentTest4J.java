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

import kotlin.Unit;
import love.forte.simbot.Identifies;
import love.forte.simbot.action.ReplySupport;
import love.forte.simbot.component.tencentguild.TencentGuildBotManager;
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration;
import love.forte.simbot.core.event.CoreListenerManager;
import love.forte.simbot.core.event.CoreListenerManagerConfiguration;
import love.forte.simbot.core.event.CoreListenerUtil;
import love.forte.simbot.definition.Group;
import love.forte.simbot.event.EventListener;
import love.forte.simbot.event.EventProcessingResult;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.At;
import love.forte.simbot.message.Message;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.Text;
import love.forte.simbot.tencentguild.EventSignals;

import java.util.concurrent.Future;

/**
 * @author ForteScarlet
 */
public class ComponentTest4J {
    public static void main(String[] args) {
        String appId = "";
        String appKey = "";
        String token = "";

        final CoreListenerManagerConfiguration config = new CoreListenerManagerConfiguration();

        final CoreListenerManager manager = CoreListenerManager.newInstance(config);

        final TencentGuildBotManagerConfiguration tcgConfig = new TencentGuildBotManagerConfiguration(manager);
        tcgConfig.setBotConfigure((c, id, key, t) -> {
            c.intentsForShardFactoryAsInt((s) -> EventSignals.AtMessages.getIntentsValue());
            return Unit.INSTANCE;
        });

        final TencentGuildBotManager botManager = TencentGuildBotManager.newInstance(tcgConfig);

        // 构建监听函数实例
        final EventListener listener = CoreListenerUtil.newCoreListener(
                GroupMessageEvent.Key,      // 需要监听的事件类型（的伴生Key）
                Identifies.ID(123), // 可选重载参数，ID, 默认UUID
                false,                      // 可选重载参数，blockNext, 默认false
                false,                      // 可选重载参数，isAsync, 默认false
                (context, event) -> {

                    System.out.println(event);

                    final Group group = event.getGroup();

                    // 发送消息
                    group.sendBlocking(Text.of("hello").plus(" World"));

                    // 如果可以直接回复消息，回复
                    if (event instanceof ReplySupport) {
                        // 构建消息（链）
                        Message message = Messages.getMessages(
                                Text.of("你好"),
                                new At(Identifies.ID(123456)),
                                new At(event.getAuthor().getId()) // at发消息的人
                        );
                        ((ReplySupport) event).replyBlocking(message);
                    }


                    // 返回点儿什么, 或者直接null
                    return null;
                });

        // 注册监听函数
        manager.register(listener);

        botManager.register(appId, appKey, token, c -> {

            System.out.println(c.getClientPropertiesFactory());

            return null;
        });


        GroupMessageEvent event = null; // 假如你有一个事件。实际上这里并不能是null

        // 异步的进行事件处理。Java中更推荐这种推送方式
        final Future<EventProcessingResult> resultFuture = manager.pushAsync(event);


        // 阻塞的处理事件。不太推荐
        final EventProcessingResult result = manager.pushBlocking(event);

        // 推荐的组合方式：先检测是否可处理，再推送事件并异步处理
        if (manager.isProcessable(GroupMessageEvent.Key)) {
            manager.pushAsync(event);
        }

    }
}
