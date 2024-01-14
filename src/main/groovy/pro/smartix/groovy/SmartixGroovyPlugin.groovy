package pro.smartix.groovy


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.tasks.compile.GroovyCompile

class SmartixGroovyPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        configure(project)
    }

    private static void configure(Project project) {
        project.plugins.apply(GroovyPlugin)

        project.tasks.withType(GroovyCompile).configureEach {
            options.encoding = 'UTF-8'
        }
    }
}