package com.google.aiedge.examples.imageclassification.navigation

enum class Pages {
    BodyRegions, ScanType, Scan, Settings, Gallery, ImageInfoPage
}

class NavigationStack(
    private val defaultPage: Pages
) {

    private var currentPage = defaultPage
    private val stack: ArrayDeque<Pages> = ArrayDeque()

    fun push(value: Pages) {
        if (stack.isNotEmpty() && value == stack.last()) return
        stack.add(value)
        currentPage = value
    }

    fun pop() {
        if (stack.size > 1) {
            stack.removeLast()
            currentPage = stack.last()
            return
        }
        if (stack.size == 1) stack.removeLast()
        currentPage = defaultPage
    }

    fun isEmpty() = stack.isEmpty()

    fun getCurrentPage(): Pages { return currentPage }
}