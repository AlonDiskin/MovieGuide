package com.diskin.alon.movieguide.common.util.messaging

data class NewsNotificationConfigEvent(val enabled: Boolean,
                                       val vibrate: Boolean)