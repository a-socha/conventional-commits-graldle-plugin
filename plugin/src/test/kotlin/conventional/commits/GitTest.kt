package conventional.commits

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class GitTest {
    @field:TempDir
    lateinit var projectDir: File

    private val git by lazy { Git(gitDirectory = projectDir) }

    private val expectedMessage = "build(orders): expected message"
    private val notImportantMessage = "build: not important message"


    @Test
    fun `should read all commits from repository from newest to oldest`() {
        // given
        val givenCommits = thereAreRepositoryWithFollowingCommits(
                givenCommit("build: init repo"),
                givenCommit("build(orders): order commit message"),
                givenCommit("refactor: refactor facade implementation"),
                givenCommit("test: additional test added"),
                givenCommit("feat: create endpoint"),
                givenCommit("feat!: added breaking feature"),
                givenCommit("feat(orders)!: added breaking change (to order scope)"),
                givenCommit("feat: some other message"),
                givenCommit("perf: improve performance"),
                givenCommit("docs: docs added"),
                givenCommit("ci: improve continous integration"),
                givenCommit("style: change code style"),
                givenCommit("docs: add readme", """some body with additional description
                        |Reviewed-by: Reviewer details
                        |""".trimMargin()
                )
        )

        // when
        val commits = git.allCommits()

        // then
        assertThat(commits).usingRecursiveFieldByFieldElementComparatorIgnoringFields("hash")
                .containsExactlyElementsOf(givenCommits.reversed())
    }

    @Test
    fun `should read commits between version tags`() {
        // given
        gitInit()
        gitCommit("build: init repo")
        gitCommit("build(orders): order commit message")
        gitTag("v0.0.0")
        gitCommit("refactor: refactor facade implementation")
        gitTag("other-tag")
        gitCommit("test: additional test added")
        gitCommit("feat: create endpoint")
        gitCommit("feat!: added breaking feature")
        gitTag("v0.0.1")

        // when
        val commits = git.commitsBetween("v0.0.0", "v0.0.1")

        // then
        assertThat(commits).usingRecursiveFieldByFieldElementComparatorIgnoringFields("hash")
                .containsExactly(
                        expectedCommit("feat!: added breaking feature"),
                        expectedCommit("feat: create endpoint"),
                        expectedCommit("test: additional test added"),
                        expectedCommit("refactor: refactor facade implementation"),
                )
    }

    @Test
    fun `should read commits from last tag till the end`() {
        // given
        gitInit()
        gitCommit("build: init repo")
        gitCommit("build(orders): order commit message")
        gitTag("v0.0.0")
        gitCommit("refactor: refactor facade implementation")
        gitCommit("test: additional test added")
        gitCommit("feat: create endpoint")
        gitTag("other-tag")
        gitCommit("feat!: added breaking feature")

        // when
        val commits = git.commitsBetween("v0.0.0")

        // then
        assertThat(commits).usingRecursiveFieldByFieldElementComparatorIgnoringFields("hash")
                .containsExactly(
                        expectedCommit("feat!: added breaking feature"),
                        expectedCommit("feat: create endpoint"),
                        expectedCommit("test: additional test added"),
                        expectedCommit("refactor: refactor facade implementation"),
                )
    }

    @Test
    fun `should return latest version tag `() {
        // given
        gitInit()
        gitCommit(notImportantMessage)
        gitTag("v0.0.0")
        gitCommit(notImportantMessage)
        gitTag("other-tag")
        gitCommit(notImportantMessage)
        gitTag("v0.0.1")
        gitCommit(notImportantMessage)
        gitTag("v0.0.10")
        gitCommit(notImportantMessage)
        gitTag("v1.0.0")
        gitTag("v0.1.0")
        gitCommit(notImportantMessage)

        // when
        val latestVersionTag = git.getLatestVersionTag()

        // then
        assertThat(latestVersionTag).isEqualTo("v1.0.0")
    }

    @Test
    fun `should return null if there is no version tag`() {
        // given
        gitInit()
        gitCommit(notImportantMessage)
        gitCommit(notImportantMessage)
        gitTag("other-tag")
        gitCommit(notImportantMessage)

        // when
        val latestVersionTag = git.getLatestVersionTag()

        // then
        assertThat(latestVersionTag).isNull()
    }

    @Test
    fun `should tag particular commit`() {
        // given
        val zeroTag = "v0.0.0"
        gitInit()
        gitCommit(notImportantMessage)
        gitTag(zeroTag)
        gitCommit(expectedMessage)
        gitCommit(notImportantMessage)

        val commitToTag = git.allCommits().drop(1).first()

        // when
        val newTag = "v0.0.1"
        git.tagCommit(commitToTag, newTag)

        // then
        assertThat(git.getLatestVersionTag()).isEqualTo(newTag)
        assertThat(git.commitsBetween(zeroTag, newTag)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("hash")
                .containsExactly(expectedCommit(expectedMessage))
    }

    @Test
    fun `should tag latest commit`() {
        // given
        val zeroTag = "v0.0.0"
        gitInit()
        gitCommit(notImportantMessage)
        gitCommit(notImportantMessage)
        gitTag(zeroTag)
        gitCommit(expectedMessage)

        // when
        val newTag = "v0.0.1"
        git.tagLatestCommit(newTag)

        // then
        assertThat(git.getLatestVersionTag()).isEqualTo(newTag)
        assertThat(git.commitsBetween(zeroTag, newTag)).usingRecursiveFieldByFieldElementComparatorIgnoringFields("hash")
                .containsExactly(expectedCommit(expectedMessage))
    }

    private fun thereAreRepositoryWithFollowingCommits(vararg givenCommits: Pair<String, String?>): List<Commit> {
        gitInit()
        return givenCommits
                .onEach { (subject, body) -> gitCommit(subject, body) }
                .map { (subject, body) -> Commit("any", subject, body ?: "") }
    }

    private fun expectedCommit(summary: String, body: String = "") = Commit("some", summary, body)
    private fun givenCommit(summary: String, body: String? = null): Pair<String, String?> = Pair(summary, body)

    private fun gitCommit(
            subject: String,
            body: String? = null
    ) {
        val message = body?.let { "$subject\n\n$it" } ?: subject
        runBashCommand("git", "commit", "-m", message, "--allow-empty")
    }

    private fun gitTag(tagName: String) {
        runBashCommand("git", "tag", tagName)
    }

    private fun gitInit() {
        runBashCommand("git", "init")
    }

    private fun runBashCommand(vararg command: String) {
        val output = ProcessBuilder(command.toList())
                .directory(projectDir)
                .start().inputStream.reader().readLines()
        println(output.joinToString("\n"))
    }

}
