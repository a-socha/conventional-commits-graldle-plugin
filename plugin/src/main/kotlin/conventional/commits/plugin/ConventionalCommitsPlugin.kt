package conventional.commits.plugin

import conventional.commits.tasks.CreateNewVersionTask
import conventional.commits.tasks.PrintNewVersionTask
import org.gradle.api.Project
import org.gradle.api.Plugin

class ConventionalCommitsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register("newVersion", CreateNewVersionTask::class.java)
        project.tasks.register("printNewVersion", PrintNewVersionTask::class.java)
    }
}
