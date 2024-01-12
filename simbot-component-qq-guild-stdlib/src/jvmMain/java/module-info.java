module simbot.component.qqguild.stdlib {
    requires kotlin.stdlib;
    requires transitive simbot.component.qqguild.api;
    // simbot
    requires transitive simbot.common.stageloop;
    requires transitive simbot.common.atomic;
    requires static simbot.common.annotations;
    // ktor
    requires io.ktor.client.content.negotiation;
    requires io.ktor.serialization.kotlinx.json;
    requires io.ktor.client.websockets;

    exports love.forte.simbot.qguild;
}
