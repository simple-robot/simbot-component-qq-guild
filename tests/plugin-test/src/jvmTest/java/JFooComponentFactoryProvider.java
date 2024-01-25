import kotlin.sequences.Sequence;
import love.forte.simbot.component.ComponentFactory;
import love.forte.simbot.component.ComponentFactoryConfigurerProvider;
import love.forte.simbot.component.ComponentFactoryProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author ForteScarlet
 */
public class JFooComponentFactoryProvider implements ComponentFactoryProvider<JFooComponentConfiguration> {
    @Nullable
    @Override
    public Sequence<ComponentFactoryConfigurerProvider<JFooComponentConfiguration>> loadConfigurers() {
        return null;
    }

    @NotNull
    @Override
    public ComponentFactory<?, JFooComponentConfiguration> provide() {
        return JFooComponent.FACTORY;
    }
}
