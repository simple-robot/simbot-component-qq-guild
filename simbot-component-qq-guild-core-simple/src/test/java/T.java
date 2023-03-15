import love.forte.simbot.ID;
import love.forte.simbot.Identifies;
import love.forte.simbot.application.Application;
import love.forte.simbot.application.BotManagers;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.bot.BotManager;
import love.forte.simbot.definition.Friend;
import love.forte.simbot.definition.Group;
import love.forte.simbot.definition.GroupMember;
import love.forte.simbot.event.FriendMessageEvent;
import love.forte.simbot.event.GroupMessageEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @author ForteScarlet
 */
public class T {


    private final Application application; // 通过依赖注入获取 Application

    public T(Application application) {
        this.application = application;
    }

    // 某个不是监听函数的方法，比如某个定时任务调用的地方
    public void example() {
        // 获取bot管理器
        BotManagers botManagers = application.getBotManagers();
        // 获取你需要的bot管理器。假如只使用了一个组件，那可能只有一个结果
        // 如果需要精确寻找，循环并判断类型
        // for (BotManager<?> botManager : botManagers) {
        //     if (botManager instanceof MiraiBotManager) {
        //
        //     }
        //
        // }
        // 此处获取第一个botManager.
        // 此操作有隐患，仅作演示用，实际应用中要避免元素不存在的问题。
        BotManager<?> botManager = botManagers.get(0); // maybe throw IndexOutOfBoundsException
        // 得到你需要的bot，通过id，或者遍历。
        // 此处仅作演示，获取第一个bot
        Bot firstBot = botManager.all().get(0);
        Bot bot = firstBot.getBot();
        // 寻找你需要的群
        Group group = bot.getGroup(Identifies.ID(123));
        if (group == null) {
            // group不存在会得到null
            throw new NullPointerException("group is null");
        }
        // 寻找你需要的群成员
        GroupMember member = group.getMember(Identifies.ID(456));
        member.getId();
    }

    public void example(FriendMessageEvent event) {
        // 得到bot
        Bot bot = event.getBot();
        // 得到群
        Group group = bot.getGroup(Identifies.ID(123));
        if (group == null) {
            // group不存在会得到null
            throw new NullPointerException("group is null");
        }
        // 得到成员
        GroupMember member = group.getMember(Identifies.ID(456));
    }

    public CompletableFuture<?> run2(GroupMessageEvent event) {
        final CompletableFuture<? extends Group> groupAsync = event.getGroupAsync();

        final CompletableFuture<ID> groupIdAsync = groupAsync.thenApply(Group::getId);
        final CompletableFuture<String> groupNameAsync = groupAsync.thenApply(Group::getName);


        // 返回 CompletableFuture 类型的结果时，则会在结果返回后非阻塞地挂起等待此Future结束，然后才会继续下一个监听函数
        return groupAsync.thenCompose(group -> group.sendAsync("Hello, simbot3"));

    }
}
