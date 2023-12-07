package conventional.commits

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class ConventionalCommitsPluginTest {
    @Test
    fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("conventional.commits")

        // Verify the result
        assertNotNull(project.tasks.findByName("newVersion"))
        assertNotNull(project.tasks.findByName("printNewVersion"))
    }
}
