# core


请先阅读 [**模块说明**](Module.md) .

## 事件支持

目前，组件支持的事件有：

| 事件                                 | 描述          |
|:-----------------------------------|:------------|
| `QGGuildEvent`                     | 频道服务器相关事件   |
| -> `QGGuildCreateEvent`            | 频道服务器进入     |
| -> `QGGuildUpdateEvent`            | 频道服务器信息更新   |
| -> `QGGuildDeleteEvent`            | 频道服务器离开     |
| `QGChannelEvent`                   | 子频道相关事件     |
| -> `QGChannelCreateEvent`          | 子频道新增       |
| -> `QGChannelUpdateEvent`          | 子频道信息变更     |
| -> `QGChannelDeleteEvent`          | 子频道删除       |
| -> `QGChannelCategoryCreateEvent`  | 子频道分类新增     |
| -> `QGChannelCategoryUpdateEvent`  | 子频道分类信息变更   |
| -> `QGChannelCategoryDeleteEvent`  | 子频道分类删除     |
| `QGMemberEvent`                    | 成员相关事件      |
| ->  `QGMemberAddEvent`             | 新增频道成员      |
| ->  `QGMemberUpdateEvent`          | 频道成员信息更新    |
| ->  `QGMemberRemoveEvent`          | 频道成员离开/移除   |


## 注意事项

### 内建缓存

### 权限不足

### 子频道分类

