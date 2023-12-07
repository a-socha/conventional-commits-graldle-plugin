package conventional.commits

data class SemVer(
        val major: Int,
        val minor: Int,
        val patch: Int
) {
    fun toTag() = "v$major.$minor.$patch"
    fun increment(increment: VersionIncrement): SemVer = when (increment) {
        VersionIncrement.MAJOR -> copy(major = major + 1)
        VersionIncrement.MINOR -> copy(minor = minor + 1)
        VersionIncrement.PATCH -> copy(patch = patch + 1)
        VersionIncrement.NONE -> copy()
    }


    companion object {
        val ZERO: SemVer = SemVer(0, 0, 0)

        fun from(tag: String): SemVer = tag
                .removePrefix("v")
                .split(".")
                .map { it.toInt() }
                .let { SemVer(it[0], it[1], it[2]) }
    }
}

enum class VersionIncrement {
    MAJOR,
    MINOR,
    PATCH,
    NONE,
}