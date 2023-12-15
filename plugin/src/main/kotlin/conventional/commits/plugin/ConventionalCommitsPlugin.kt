package conventional.commits.plugin

import conventional.commits.tasks.CreateNewVersionTask
import conventional.commits.tasks.PrintNewVersionTask
import org.gradle.api.Plugin
import org.gradle.api.Project


class ConventionalCommitsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val config =
            project.extensions.create("conventionalCommitsConfig", ConventionalCommitConfigExtension::class.java)


        project.tasks.register("newVersion", CreateNewVersionTask::class.java, config)
        project.tasks.register("printNewVersion", PrintNewVersionTask::class.java, config)
    }
}
