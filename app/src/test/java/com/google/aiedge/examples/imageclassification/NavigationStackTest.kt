package com.google.aiedge.examples.imageclassification

import com.google.aiedge.examples.imageclassification.navigation.NavigationStack
import com.google.aiedge.examples.imageclassification.navigation.Pages
import org.junit.Test

class NavigationStackTest {

    @Test
    fun testNavigationStack() {

        val stack = NavigationStack(Pages.ScanType)

        assert(stack.getCurrentPage() == Pages.ScanType)

        stack.push(Pages.Settings)
        assert(stack.getCurrentPage() == Pages.Settings)

        stack.push(Pages.BodyRegions)
        assert(stack.getCurrentPage() == Pages.BodyRegions)

        stack.pop()
        assert(stack.getCurrentPage() == Pages.Settings)

        stack.push(Pages.Scan)
        assert(stack.getCurrentPage() == Pages.Scan)

        stack.pop()
        assert(stack.getCurrentPage() == Pages.Settings)

        stack.pop()
        assert(stack.getCurrentPage() == Pages.ScanType)

        assert(stack.isEmpty())
    }
}