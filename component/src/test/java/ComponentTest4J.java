import kotlin.Unit;
import love.forte.simbot.component.tencentguild.TencentGuildBotManager;
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration;
import love.forte.simbot.core.event.CoreEventManagerConfiguration;
import love.forte.simbot.core.event.CoreListenerManager;
import love.forte.simbot.core.event.CoreListenerUtil;
import love.forte.simbot.definition.Channel;
import love.forte.simbot.event.ChannelMessageEvent;
import love.forte.simbot.event.EventListener;
import love.forte.simbot.message.Text;
import love.forte.simbot.tencentguild.EventSignals;

/**
 * @author ForteScarlet
 */
public class ComponentTest4J {
    public static void main(String[] args) {
        String appId = "";
        String appKey = "";
        String token = "";

        final CoreEventManagerConfiguration config = new CoreEventManagerConfiguration();

        final CoreListenerManager manager = CoreListenerManager.newInstance(config);

        final TencentGuildBotManagerConfiguration tcgConfig = new TencentGuildBotManagerConfiguration(manager);
        tcgConfig.setBotConfigure((c, id, key, t) -> {
            c.intentsForShardFactoryAsInt((s) -> EventSignals.AtMessages.getIntentsValue());
            return Unit.INSTANCE;
        });

        final TencentGuildBotManager botManager = TencentGuildBotManager.newInstance(tcgConfig);

        final EventListener listener = CoreListenerUtil.newCoreListener(ChannelMessageEvent.Key, (context, event) -> {
            System.out.println(event);

            final Channel channel = event.getChannel();
            channel.sendBlocking(Text.getText("hello").plus(" World"));


            return null;
        });

        manager.register(listener);

        botManager.register(appId, appKey, token, c -> {

            System.out.println(c.getClientPropertiesFactory())
            ;

            return null;
        });

    }
}
