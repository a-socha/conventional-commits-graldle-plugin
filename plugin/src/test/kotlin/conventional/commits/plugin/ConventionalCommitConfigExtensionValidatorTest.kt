package conventional.commits.plugin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ConventionalCommitConfigExtensionValidatorTest {

    private val validator = ConventionalCommitConfigExtensionValidator()

    @ParameterizedTest
    @MethodSource("validationCases")
    fun `should validate config`(
        configExtension: ConventionalCommitConfigExtension, expectedError: ExtensionValidationError?
    ) {
        assertThat(validator.validateConventionalCommitConfig(configExtension)).isEqualTo(expectedError)
    }

    companion object {
        @JvmStatic
        fun validationCases() = Stream.of(
            arguments(ConventionalCommitConfigExtension(), null),
            arguments(
                ConventionalCommitConfigExtension().apply {
                    major = listOf("major", "ci")
                    minor = listOf("major")
                    patch = listOf("feat", "ci")
                    none = listOf("feat", "docs")
                },
                ExtensionValidationError("Following types occur more than one in configuration: major, ci, feat")
            ),
            arguments(
                ConventionalCommitConfigExtension().apply {
                    major = listOf("ci)")
                    minor = listOf("major@", "NOT CORRECT")
                    patch = listOf("test!", "CORRECT")
                    none = listOf("er1", "OkEy")
                },
                ExtensionValidationError("Following types contains other character than letters: ci), major@, NOT CORRECT, test!, er1")
            ),
        )
    }
}