package conventional.commits.plugin

import conventional.commits.ConventionalCommitConfig
import conventional.commits.VersionIncrement

open class ConventionalCommitConfigExtension {
    var major: List<String> = emptyList()
    var minor: List<String> = listOf("feat")
    var patch: List<String> = listOf("fix", "chore", "style", "refactor", "perf", "test", "ci", "build")
    var none: List<String> = listOf("docs")


    internal fun toConfig() = ConventionalCommitConfig(
        mapOf(
            VersionIncrement.MAJOR to major.toList(),
            VersionIncrement.MINOR to minor.toList(),
            VersionIncrement.PATCH to patch.toList(),
            VersionIncrement.NONE to none.toList()
        )
    )
}

class ConventionalCommitConfigExtensionValidator() {
    fun validateConventionalCommitConfig(config: ConventionalCommitConfigExtension): ExtensionValidationError? =
        sameTypeCannotOccurTwice(config) ?: typeContainCharactersOtherThanLetters(config)

    private fun sameTypeCannotOccurTwice(config: ConventionalCommitConfigExtension): ExtensionValidationError? =
        typesThatOccursMoreThanOne(config)
            .takeIfNotEmpty()
            ?.collectToMessageFormat()
            ?.let { "Following types occur more than one in configuration: $it" }
            ?.let { ExtensionValidationError(it) }

    private fun typesThatOccursMoreThanOne(config: ConventionalCommitConfigExtension) =
        allTypes(config)
            .groupBy { it }
            .mapValues { it.value.size }
            .filterValues { it > 1 }
            .keys

    private fun allTypes(config: ConventionalCommitConfigExtension): List<String> =
        (config.major + config.minor + config.patch + config.none)

    private fun typeContainCharactersOtherThanLetters(config: ConventionalCommitConfigExtension) =
        allTypes(config).filter { !it.containOnlyLetters() }
            .takeIfNotEmpty()
            ?.collectToMessageFormat()
            ?.let { "Following types contains other character than letters: $it" }
            ?.let { ExtensionValidationError(it) }

}

private fun String.containOnlyLetters(): Boolean = this.matches(Regex("[a-zA-Z]+"))
private fun <T> Collection<T>.takeIfNotEmpty(): List<T>? = takeIf { it.isNotEmpty() }?.toList()
private fun List<String>.collectToMessageFormat(): String = joinToString(", ")