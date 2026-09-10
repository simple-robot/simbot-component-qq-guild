package test

import io.ktor.http.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.api.menu.GetCustomMenuApi
import love.forte.simbot.qguild.api.menu.ModifyCustomMenuApi
import love.forte.simbot.qguild.api.panel.*
import love.forte.simbot.qguild.model.menu.CustomMenu
import love.forte.simbot.qguild.model.panel.CommandPanel
import love.forte.simbot.qguild.model.panel.CommandPanelScopeValues
import love.forte.simbot.qguild.model.panel.CommandPanelTargetTypeValues
import love.forte.simbot.qguild.model.panel.CommandPanelTargetUpdate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MenuPanelApiTests {
    @Test
    fun getCustomMenuApiUsesMenuResource() {
        val api = GetCustomMenuApi.create()

        assertEquals(HttpMethod.Get, api.method)
        assertEquals("/v2/menu", api.url.encodedPath)
        assertNull(api.body)
    }

    @Test
    fun modifyCustomMenuApiWrapsMenuBodyWithoutLocalValidation() {
        val api = ModifyCustomMenuApi.create {
            item {
                name = "unsupported-but-forwarded"
                type = "unknown"
                link = "http://not-validated-locally.example"
                subMenuItem {
                    name = "nested"
                    type = CustomMenu.SubItem.TYPE_LINK
                    link = "https://example.com"
                }
            }
        }

        val tree = QQGuild.DefaultJson.encodeToString(api.body).let(QQGuild.DefaultJson::parseToJsonElement).jsonObject
        val item = tree.getValue("menu").jsonObject
            .getValue("items").jsonArray.single().jsonObject

        assertEquals(HttpMethod.Put, api.method)
        assertEquals("/v2/menu", api.url.encodedPath)
        assertEquals(JsonPrimitive("unknown"), item["type"])
        assertEquals(JsonPrimitive("http://not-validated-locally.example"), item["link"])
        assertEquals(JsonPrimitive("nested"), item.getValue("sub_menu_items").jsonArray.single().jsonObject["name"])
    }

    @Test
    fun commandPanelApisUseDocumentedResourcesAndBodies() {
        val list = GetCommandPanelListApi.create(
            scope = CommandPanelScopeValues.C2C,
            cursor = "cursor-value",
            limit = 51,
        )
        val create = CreateCommandPanelApi.create {
            scope = CommandPanelScopeValues.C2C
            targetType = CommandPanelTargetTypeValues.SPECIFIC
            addUserOpenid("user-openid")
            panel {
                remark = "panel remark"
                item {
                    name = "/help"
                    desc = "show help"
                    type = CommandPanel.Item.TYPE_COMMAND
                    onlyAdmin = false
                }
            }
        }
        val detail = GetCommandPanelApi.create("panel-id")
        val modify = ModifyCommandPanelApi.create("panel-id") {
            item {
                name = "resource"
                type = CommandPanel.Item.TYPE_LINK
                link = "https://example.com"
            }
        }
        val delete = DeleteCommandPanelApi.create("panel-id")
        val target = ModifyCommandPanelTargetApi.create("panel-id") {
            op = CommandPanelTargetUpdate.OP_DEL
            clearGroupOpenids()
        }

        assertEquals(HttpMethod.Get, list.method)
        assertEquals("/v2/panels", list.url.encodedPath)
        assertEquals(CommandPanelScopeValues.C2C, list.url.parameters["scope"])
        assertEquals("cursor-value", list.url.parameters["cursor"])
        assertEquals("51", list.url.parameters["limit"])
        assertNull(list.body)

        assertEquals(HttpMethod.Post, create.method)
        assertEquals("/v2/panels", create.url.encodedPath)
        val createTree =
            QQGuild.DefaultJson.encodeToString(create.body).let(QQGuild.DefaultJson::parseToJsonElement).jsonObject
        assertEquals(JsonPrimitive(CommandPanelScopeValues.C2C), createTree["scope"])
        assertEquals(JsonPrimitive(CommandPanelTargetTypeValues.SPECIFIC), createTree["target_type"])
        assertEquals(JsonPrimitive("user-openid"), createTree.getValue("user_openids").jsonArray.single())
        assertEquals(
            JsonPrimitive("/help"), createTree.getValue("panel").jsonObject
                .getValue("items").jsonArray.single().jsonObject["name"]
        )

        assertEquals(HttpMethod.Get, detail.method)
        assertEquals("/v2/panels/panel-id", detail.url.encodedPath)
        assertNull(detail.body)

        assertEquals(HttpMethod.Put, modify.method)
        assertEquals("/v2/panels/panel-id", modify.url.encodedPath)
        val modifyTree =
            QQGuild.DefaultJson.encodeToString(modify.body).let(QQGuild.DefaultJson::parseToJsonElement).jsonObject
        assertEquals(
            JsonPrimitive("resource"), modifyTree.getValue("panel").jsonObject
                .getValue("items").jsonArray.single().jsonObject["name"]
        )

        assertEquals(HttpMethod.Delete, delete.method)
        assertEquals("/v2/panels/panel-id", delete.url.encodedPath)
        assertNull(delete.body)

        assertEquals(HttpMethod.Put, target.method)
        assertEquals("/v2/panels/panel-id/target", target.url.encodedPath)
        val targetTree =
            QQGuild.DefaultJson.encodeToString(target.body).let(QQGuild.DefaultJson::parseToJsonElement).jsonObject
        assertEquals(JsonPrimitive(CommandPanelTargetUpdate.OP_DEL), targetTree["op"])
        assertNull(targetTree["group_openids"])
    }
}
