package pro.smartix.spring

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class SmartixSpringBootDependencyManagementPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(SmartixSpringBootPlugin)) {
            throw new GradleException("Не найден spring boot плагин")
        }
        project.plugins.apply(DependencyManagementPlugin)
    }
}
