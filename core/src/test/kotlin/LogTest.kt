import love.forte.simbot.LoggerFactory


val l = LoggerFactory.getLogger("love.forte.test")

fun main() {
    l.info("HI")
    l.error("no")
    l.debug("DEB")
    l.warn("W")
}