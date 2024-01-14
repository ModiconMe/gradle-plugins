package pro.smartix.test

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport

class SmartixBaseTestPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        baseTestConfig(project)
        jacocoConfig(project)
    }

    private static baseTestConfig(Project project) {
        project.tasks.withType(Test).configureEach {
            useJUnitPlatform()
            testLogging.showStackTraces = true
            testLogging.exceptionFormat = TestExceptionFormat.FULL
            testLogging.events "PASSED", "FAILED", "SKIPPED"
            systemProperties.put('file.encoding', 'UTF-8')
        }
    }

    private static jacocoConfig(Project project) {
        project.plugins.apply(JacocoPlugin)

        def jacocoTestReport = project.tasks.withType(JacocoReport)
        jacocoTestReport.configureEach {
            reports.xml.required = true
            executionData.from += project.file('build/jacoco/test.exec')
        }

        project.tasks.named('check', DefaultTask) {
            finalizedBy jacocoTestReport
        }
    }
}
