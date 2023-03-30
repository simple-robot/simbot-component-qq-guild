/*
 * Copyright (c) 2023. ForteScarlet.
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

package love.forte.simbot.component.qguild.internal

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

/**
 * Parse iso 8601 datetime string to [Timestamp]
 */
@ExperimentalSimbotApi
public fun String.toTimestamp(): Timestamp {
    return DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(this) { temporal ->
        when (temporal) {
            is OffsetDateTime -> Timestamp.bySecond(temporal.toEpochSecond(), temporal.toLocalTime().nano)
            is Instant -> Timestamp.byInstant(temporal)
            else -> {
                val instantSecs = temporal.getLong(ChronoField.INSTANT_SECONDS)
                val nanoOfSecond = temporal[ChronoField.NANO_OF_SECOND]

                Timestamp.bySecond(instantSecs, nanoOfSecond)
            }
        }
    }
}



