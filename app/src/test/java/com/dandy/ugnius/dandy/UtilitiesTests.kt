package com.dandy.ugnius.dandy

import com.dandy.ugnius.dandy.model.entities.Track
import org.junit.Test
import junit.framework.Assert

class UtilitiesTests {

    @Test
    fun equalityAndHashesTest() {

        val firstTrack = Track(
            emptyList<String>(),
            "Schoolboy Q & Tyler, The Creator",
            "3:54",
            true,
            "4QNpBfC0zvjKqPJcyqBy9W",
            "The Purge",
            "spotify:track:4QNpBfC0zvjKqPJcyqBy9W"
        )

        val secondTrack = Track(
            emptyList<String>(),
            "Schoolboy Q",
            "3:54",
            false,
            "4QNpBfC0zvjKqPJcyqBy9W",
            "Oxymoron",
            "spotify:track:4QNpBfC0zvjKqPJcyqBy9W"
        )


        val thirdTrack = Track(
            emptyList<String>(),
            "Schoolboy Q",
            "3:54",
            true,
            "4QNpBfC0zvjKqPJcyqBy9W",
            "Man of the year",
            "spotify:track:4QNpBfC0zvjKqPJcyqBy9W"
        )


        val fourthTrack = Track(
            emptyList<String>(),
            "Tyler, The Creator",
            "2:51",
            false,
            "13plQdOoWSSXPRUSZc5FuM",
            "Golden",
            "spotify:track:13plQdOoWSSXPRUSZc5FuM"
        )

        Assert.assertTrue(firstTrack == secondTrack && secondTrack == thirdTrack && firstTrack == thirdTrack)
        Assert.assertTrue(firstTrack.hashCode() == secondTrack.hashCode() && secondTrack.hashCode() == thirdTrack.hashCode() && firstTrack.hashCode() == thirdTrack.hashCode())
        Assert.assertFalse(firstTrack == fourthTrack)
        Assert.assertFalse(firstTrack.hashCode() == fourthTrack.hashCode())
    }
}
