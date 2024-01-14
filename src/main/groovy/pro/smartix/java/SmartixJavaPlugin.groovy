package pro.smartix.java

import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar

import java.text.SimpleDateFormat

class SmartixJavaPlugin implements Plugin<Project> {

    public static final String SMARTIX_JAVA_CONFIG = 'smartixJavaConfig'

    @Override
    void apply(Project project) {
        configure(project, createExtension(project))
    }

    private static createExtension(Project project) {
        def smartixJavaExtension = project.extensions.create(SMARTIX_JAVA_CONFIG, SmartixJavaExtension)
        smartixJavaExtension.javaVersion.convention(17)
        smartixJavaExtension
    }

    private static void configure(Project project, SmartixJavaExtension smartixJavaExtension) {
        project.plugins.apply(JavaPlugin)
        project.plugins.apply(LombokPlugin)

        def javaExtension = project.extensions.getByType(JavaPluginExtension)
        javaExtension.targetCompatibility = smartixJavaExtension.javaVersion.get()
        javaExtension.sourceCompatibility = smartixJavaExtension.javaVersion.get()

        project.tasks.withType(JavaCompile).configureEach {
            options.encoding = 'UTF-8'
        }

        project.tasks.withType(Jar).configureEach {
            manifest {
                attributes (jarAttributes(project))
            }
        }
    }

    static Map<String, Object> jarAttributes(Project project) {
        [
                'Organization'          : 'MyOrg',
                'Artifact'              : 'Spring App',
                'Implementation-Version': project.rootProject.version,
                'Created-By'            : "${System.getProperty('java.version')} (${System.getProperty('java.vendor')})",
                'Built-With'            : "gradle-${project.getGradle().getGradleVersion()}, groovy-${GroovySystem.getVersion()}",
                'Build-Time'            : new SimpleDateFormat('yyyy-MM-dd HH').format(new Date())
        ]
    }
}