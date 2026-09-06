/*
 * Copyright (c) 2026. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.model.panel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.qguild.ApiModel
import love.forte.simbot.qguild.ApiModelConstructor
import love.forte.simbot.qguild.QQGuild
import kotlin.jvm.JvmStatic

/**
 * 在会话中展示指令或链接的指令面板配置。
 *
 * [官方文档](https://bot.q.qq.com/wiki/develop/api-v2/server-inter/menu-panel/)
 *
 * @property items 面板元素。
 * @property remark 开发者可见的备注。
 * @property version 面板配置版本。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanel @ApiModelConstructor internal constructor(
    public val items: List<Item> = emptyList(),
    public val remark: String? = null,
    public val version: Int? = null,
) {
    /**
     * 指令面板中的元素。
     *
     * @since 4.7.0
     */
    @ApiModel
    @Serializable
    public class Item @ApiModelConstructor internal constructor(
        /**
         * 元素名称。
         */
        public val name: String? = null,
        /**
         * 元素描述。
         */
        public val desc: String? = null,
        /**
         * 元素类型。
         */
        public val type: String? = null,
        /**
         * 是否仅管理员可操作。
         */
        @SerialName("only_admin")
        public val onlyAdmin: Boolean? = null,
        /**
         * 仅 [TYPE_LINK] 有效的跳转链接。
         */
        public val link: String? = null,
    ) {
        public companion object {
            /**
             * 指令元素类型。
             */
            public const val TYPE_COMMAND: String = "command"

            /**
             * 链接元素类型。
             */
            public const val TYPE_LINK: String = "link"
        }
    }

    public companion object {
        /**
         * 获取 [CommandPanelBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelBuilder = CommandPanelBuilder()

        /**
         * 将 JSON 字符串解析为 [CommandPanel]。
         */
        @JvmStatic
        public fun parse(jsonString: String): CommandPanel =
            QQGuild.DefaultJson.decodeFromString(serializer(), jsonString)
    }
}

/**
 * 指令面板记录。
 *
 * 列表查询不会返回 [userOpenids] 和 [groupOpenids]；详情查询才会在适用时返回它们。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelRecord @ApiModelConstructor internal constructor(
    /**
     * 面板 ID。
     */
    @SerialName("panel_id")
    public val panelId: String,
    /**
     * 面板生效场景。
     */
    public val scope: String,
    /**
     * 面板生效范围。
     */
    @SerialName("target_type")
    public val targetType: String,
    /**
     * 面板配置。
     */
    public val panel: CommandPanel,
    /**
     * 面板创建时间。
     */
    @SerialName("created_at")
    public val createdAt: String? = null,
    /**
     * 面板更新时间。
     */
    @SerialName("updated_at")
    public val updatedAt: String? = null,
    /**
     * 面板版本。
     */
    public val version: Int? = null,
    /**
     * 关联的 C2C 用户 OpenID 列表。
     */
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    /**
     * 关联的群 OpenID 列表。
     */
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
) {
    public companion object {
        /**
         * C2C 单聊场景。
         */
        public const val SCOPE_C2C: String = "c2c"

        /**
         * 群聊场景。
         */
        public const val SCOPE_GROUP: String = "group"

        /**
         * 文字子频道场景。
         */
        public const val SCOPE_CHANNEL: String = "channel"

        /**
         * 频道私信场景。
         */
        public const val SCOPE_DM: String = "dm"

        /**
         * 对指定场景下的所有目标生效。
         */
        public const val TARGET_TYPE_ALL: String = "all"

        /**
         * 仅对指定用户或群生效。
         */
        public const val TARGET_TYPE_SPECIFIC: String = "specific"
    }
}

/**
 * 指令面板分页查询结果。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelPage @ApiModelConstructor internal constructor(
    /** 本页记录。 */
    public val records: List<CommandPanelRecord> = emptyList(),
    /** 下一页游标。 */
    @SerialName("next_cursor")
    public val nextCursor: String = "",
    /** 是否已经到达最后一页。 */
    @SerialName("is_end")
    public val isEnd: Boolean = false,
)

/**
 * 创建指令面板的请求体。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelCreate @ApiModelConstructor internal constructor(
    /**
     * 面板生效场景。
     */
    public val scope: String? = null,
    /**
     * 面板生效范围。
     */
    @SerialName("target_type")
    public val targetType: String? = null,
    /**
     * C2C 场景中关联的用户 OpenID。
     */
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    /**
     * 群聊场景中关联的群 OpenID。
     */
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
    /**
     * 面板配置。
     */
    public val panel: CommandPanel? = null,
) {
    public companion object {
        /**
         * 获取 [CommandPanelCreateBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelCreateBuilder = CommandPanelCreateBuilder()
    }
}

/**
 * 修改指令面板关联对象的请求体。
 *
 * @property op 关联操作类型。
 * @property userOpenids C2C 场景中要操作的用户 OpenID。
 * @property groupOpenids 群聊场景中要操作的群 OpenID。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelTargetUpdate @ApiModelConstructor internal constructor(
    public val op: String? = null,
    @SerialName("user_openids")
    public val userOpenids: List<String>? = null,
    @SerialName("group_openids")
    public val groupOpenids: List<String>? = null,
) {
    public companion object {
        /**
         * 添加关联对象的操作类型。
         */
        public const val OP_ADD: String = "add"

        /**
         * 删除关联对象的操作类型。
         */
        public const val OP_DEL: String = "del"

        /**
         * 获取 [CommandPanelTargetUpdateBuilder]。
         */
        @JvmStatic
        public fun builder(): CommandPanelTargetUpdateBuilder = CommandPanelTargetUpdateBuilder()
    }
}

/**
 * 创建指令面板后的结果。
 *
 * @property panelId 新建面板 ID。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelCreated @ApiModelConstructor internal constructor(
    @SerialName("panel_id")
    public val panelId: String,
)

/**
 * 指令面板更新后的版本。
 *
 * @property version 更新后的面板版本。
 *
 * @since 4.7.0
 */
@ApiModel
@Serializable
public class CommandPanelUpdated @ApiModelConstructor internal constructor(
    public val version: Int = 0,
)
