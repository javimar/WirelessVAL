package eu.javimar.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform