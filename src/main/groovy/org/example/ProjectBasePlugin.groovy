package org.example

import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.springframework.boot.gradle.plugin.SpringBootPlugin

interface ProjectBaseExtension {
    Property<Integer> getVersion()

    Property<Boolean> getIntegrationTestReport()

    Property<Boolean> getSpring()
}

class Dependencies {
    static def JUNIT = 'org.junit.jupiter:junit-jupiter:5.8.2'
}

class ProjectBasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = configPluginExtension(project)

        project.plugins.apply(SpringBootPlugin)
        javaConfig(project, extension)
        jacocoConfig(project, extension)

        getRealVersion(project)
    }

    private static ProjectBaseExtension configPluginExtension(Project project) {
        def extension = project.extensions.create('javaSettings', ProjectBaseExtension)
        extension.version.convention(17)
        extension.integrationTestReport.convention(false)

        extension
    }

    private static javaConfig(Project project, ProjectBaseExtension extension) {
        project.plugins.apply(JavaPlugin)
        project.plugins.apply(LombokPlugin)

        def javaExtension = getJavaExtension(project)
        javaExtension.targetCompatibility = extension.version.get()
        javaExtension.sourceCompatibility = extension.version.get()

        testConfig(project, extension)

        project.afterEvaluate {
            def named = project.configurations.named('implementation')
            println named.get().dependencies
        }
    }

    private static JavaPluginExtension getJavaExtension(Project project) {
        project.extensions.getByType(JavaPluginExtension)
    }

    private static testConfig(Project project, ProjectBaseExtension extension) {
        project.tasks.withType(Test).configureEach {
            useJUnitPlatform()
            testLogging.showStackTraces = true
            testLogging.exceptionFormat = TestExceptionFormat.FULL
            testLogging.events "PASSED", "FAILED", "SKIPPED"
        }

        if (extension.integrationTestReport) {
            configIntegrationTests(project)
        }
    }

    private static configIntegrationTests(Project project) {
        def intTestSourceSetName = 'intTest'
        getJavaExtension(project).sourceSets.register(intTestSourceSetName) {
            java {
                compileClasspath += getJavaExtension(project).sourceSets.main.output
                runtimeClasspath += getJavaExtension(project).sourceSets.main.output
            }
        }

        project.configurations.register('iTestImplementation') {
            def configurations = project.configurations
            extendsFrom configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME).get(),
                    configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).get()
        }

        project.tasks.register(intTestSourceSetName, Test) {
            description = 'Runs the integration tests.'
            group = 'verification'

            testClassesDirs = getJavaExtension(project).sourceSets.named(intTestSourceSetName).get().output.classesDirs
            classpath = getJavaExtension(project).sourceSets.named(intTestSourceSetName).get().runtimeClasspath
            outputs.upToDateWhen { false }
        }
    }

    private static jacocoConfig(Project project, ProjectBaseExtension extension) {
        project.plugins.apply(JacocoPlugin)

        def jacocoTestReport = project.tasks.withType(JacocoReport)
        jacocoTestReport.configureEach {
            reports.xml.required = true

            executionData.from += project.file('build/jacoco/test.exec')
            if (extension.integrationTestReport.get()) {
                executionData.from += project.file('build/jacoco/intTest.exec')
            }
        }

        def check = project.tasks.named('check', DefaultTask)
        check.configure {
            finalizedBy jacocoTestReport
        }
    }

    private static void getRealVersion(Project project) {
        def branchVersion = new ByteArrayOutputStream()

        project.exec {
            commandLine 'git', 'rev-parse', '--abbrev-ref', 'HEAD'
            standardOutput = branchVersion
        }

        println branchVersion.toString()

//        def isMaster = branchVersion.toString().startsWith('master')

//        def getRealVersion(curVersion) {
//            return (isMaster ? curVersion : curVersion + "-SNAPSHOT")
//        }
//
//        ext {
//            getRealVersion = this.&getRealVersion
//        }
    }
}
