package com.google.aiedge.examples.imageclassification

import com.google.aiedge.examples.imageclassification.navigation.NavigationStack
import com.google.aiedge.examples.imageclassification.view.Pages
import org.junit.Test

class NavigationStackTest {

    @Test
    fun testNavigationStack() {

        var counter = 0
        var lastPage: Pages? = null

        fun setPageOnChange(page: Pages) {
            lastPage = page
            counter++
        }

        val stack = NavigationStack(::setPageOnChange, Pages.ScanType)
        assert(counter == 0)
        assert(lastPage == null)

        stack.push(Pages.Settings)
        assert(counter == 1)
        assert(lastPage == Pages.Settings)

        stack.push(Pages.BodyRegions)
        assert(counter == 2)
        assert(lastPage == Pages.BodyRegions)

        stack.pop()
        assert(counter == 3)
        assert(lastPage == Pages.Settings)

        stack.push(Pages.Scan)
        assert(counter == 4)
        assert(lastPage == Pages.Scan)

        stack.pop()
        assert(counter == 5)
        assert(lastPage == Pages.Settings)

        stack.pop()
        assert(counter == 6)
        assert(lastPage == Pages.ScanType)

        assert(stack.isEmpty())
    }
}