package com.diskin.alon.movieguide.common.util.messaging

data class NewsUpdateConfigEvent(val enabled: Boolean,
                                 val vibrate: Boolean)