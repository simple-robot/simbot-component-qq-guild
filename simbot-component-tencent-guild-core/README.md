# core


请先阅读 [**模块说明**](Module.md) .

## 事件支持

目前，组件支持的事件有：

  - `QGGuildEvent` -> 频道服务器相关事件
    - `QGGuildCreateEvent` -> 频道服务器 - 进入
    - `QGGuildUpdateEvent` -> 频道服务器 - 更新
    - `QGGuildDeleteEvent` -> 频道服务器 - 离开
- `QGChannelEvent` -> 子频道相关事件
    - `QGChannelCreateEvent` -> 子频道 - 新增
    - `QGChannelUpdateEvent` -> 子频道 - 信息变更
    - `QGChannelDeleteEvent` -> 子频道 - 删除
    - `QGChannelCategoryCreateEvent` -> 子频道分类 - 新增
    - `QGChannelCategoryUpdateEvent` -> 子频道分类 - 信息变更
    - `QGChannelCategoryDeleteEvent` -> 子频道分类 - 删除


## 注意事项

### 权限不足

### 子频道分类

