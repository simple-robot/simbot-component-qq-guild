package love.forte.simbot.tencentguild

/**
 * 标记一个类型为作为QQ频道API请求响应体模型的类型。
 *
 * 这些类型对于使用者来讲应当仅作为API响应的**反序列化**结果使用，
 * 并尽量避免直接序列化它们。
 *
 * _此注解目前的作用仅用于源码标记_
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class ApiModel


