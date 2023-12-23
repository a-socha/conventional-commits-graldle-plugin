package conventional.commits

import java.io.File

private val hookTemplate: String = """
    #!/usr/bin/env bash
    
    commit_message=${'$'}(cat "${'$'}1")
    
    if [[ "${'$'}commit_message" =~ ^{conventional_commits_regex}${'$'} ]]; then
       exit 0
    fi
    
    echo '{error_message}'
    exit 1
    
""".trimIndent()

class GitHookScript(
    config: ConventionalCommitConfig,
    private val errorMessage: String
) {
    private val regexpProvider = ConventionalCommitRegexpProvider(config)

    val hook: String
        get() = hookTemplate
            .replace("{conventional_commits_regex}", regexpProvider.commitPatternWithoutGroups.pattern())
            .replace("{error_message}", errorMessage)

}
