package pro.smartix.spring

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class SmartixSpringBootDependencyManagementPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        if (project.plugins.withType(SmartixSpringBootPlugin) != null) {
            project.plugins.apply(DependencyManagementPlugin)
        }
    }
}
