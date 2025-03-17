# internal ed25519 module

一个内部使用的用于提供对 ed25519 签名的支持的模块。

JVM 中优先尝试通过 `Security.getProvider("BC")` 检测是否存在 bouncycastle，如果存在则使用它的 `Ed25519` 类库。
