package com.google.aiedge.examples.imageclassification.navigation

class NavigationStack<PageType: Enum<PageType>>(private val setPageOnChange: (PageType) -> Unit, private val defaultPage: PageType) {

    private val stack: ArrayDeque<PageType> = ArrayDeque()

    fun push(value: PageType) {

        if (!stack.isEmpty() && value == stack.last()) return

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