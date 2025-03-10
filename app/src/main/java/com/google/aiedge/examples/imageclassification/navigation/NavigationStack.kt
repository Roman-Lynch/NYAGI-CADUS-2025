package com.google.aiedge.examples.imageclassification.navigation

class NavigationStack<PageType: Enum<PageType>>(val setPageOnChange: (PageType) -> Unit, val defaultPage: PageType) {

    private val stack: ArrayDeque<PageType> = ArrayDeque()

    fun push(value: PageType) {
        stack.add(value)
        setPageOnChange(value)
    }

    fun pop() {
        if (stack.size > 1) {
            stack.removeLast()
            setPageOnChange(stack.last())
            return
        }
        if (stack.size == 1) stack.removeLast()
        setPageOnChange(defaultPage)
    }

    fun isEmpty() = stack.isEmpty()
}