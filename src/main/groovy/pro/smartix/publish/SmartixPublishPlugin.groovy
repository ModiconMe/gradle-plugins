package pro.smartix.publish

import com.palantir.gradle.shadowjar.ShadowJarPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class SmartixPublishPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(MavenPublishPlugin)

        project.extensions.getByType(PublishingExtension).publications.create('maven', MavenPublication) {
            def hasSpringPlugin = project.plugins.hasPlugin(SpringBootPlugin)
            if (hasSpringPlugin) {
                artifacts += project.tasks.named(SpringBootPlugin.BOOT_JAR_TASK_NAME, Jar)
                artifactId = project.tasks.named(SpringBootPlugin.BOOT_JAR_TASK_NAME, Jar).get().archiveBaseName.get()
            } else {
                artifacts += project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar)
                artifactId = project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar).get().archiveBaseName.get()
            }
        }
    }
}
