package pro.smartix.spring


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.springframework.boot.gradle.tasks.bundling.BootJar
import pro.smartix.java.SmartixJavaPlugin

class SmartixSpringBootPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(SpringBootPlugin)

        project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar) {
            enabled = false
        }

        project.tasks.named(SpringBootPlugin.BOOT_JAR_TASK_NAME, BootJar) {
            manifest {
                attributes(SmartixJavaPlugin.jarAttributes(project))
            }
        }
    }
}
