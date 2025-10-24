package org.example.app

import org.junit.Assert.assertTrue
import org.junit.Test

// PUBLIC_INTERFACE
class SmokeJUnit4Test {
    /** Minimal JUnit4 smoke test to ensure test discovery finds at least one test. */
    @Test
    fun testAlwaysPasses() {
        assertTrue(true)
    }
}
