package pro.smartix.test


import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport
import pro.smartix.groovy.SmartixGroovyPlugin

class SmartixIntegrationTestPlugin implements Plugin<Project> {

    public static final String INTEGRATION_TEST_NAME = 'iTest'
    public static final String INTEGRATION_TEST_IMPLEMENTATION_DEPENDENCY = INTEGRATION_TEST_NAME + 'Implementation'
    public static final String INTEGRATION_TEST_COMPILE_ONLY_DEPENDENCY = INTEGRATION_TEST_NAME + 'CompileOnly'

    @Override
    void apply(Project project) {
        if (!project.plugins.hasPlugin(SmartixBaseTestPlugin)) {
            throw new GradleException("Не найден базовый плагин для тестов")
        }
        integrationTestConfig(project)
        jacocoConfig(project)
    }

    private static void jacocoConfig(Project project) {
        project.tasks.withType(JacocoReport).configureEach {
            executionData.from += project.file('build/jacoco/' + INTEGRATION_TEST_NAME + '.exec')
        }
    }

    private static void integrationTestConfig(Project project) {
        def javaPluginExtension = project.extensions.getByType(JavaPluginExtension)

        def iTestSourceSet = javaPluginExtension.sourceSets.register(INTEGRATION_TEST_NAME) {
            java {
                compileClasspath += javaPluginExtension.sourceSets.main.output
                runtimeClasspath += javaPluginExtension.sourceSets.main.output
            }
        }.get()

        project.configurations.named(INTEGRATION_TEST_IMPLEMENTATION_DEPENDENCY) {
            extendsFrom project.configurations.named(JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME).get()
        }

        project.configurations.named(INTEGRATION_TEST_COMPILE_ONLY_DEPENDENCY) {
            extendsFrom project.configurations.named(JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME).get()
        }

        def integrationTestTask = project.tasks.register(INTEGRATION_TEST_NAME, Test) {
            description = 'Runs the integration tests.'
            group = 'verification'

            testClassesDirs = iTestSourceSet.output.classesDirs
            classpath = iTestSourceSet.runtimeClasspath
            outputs.upToDateWhen { false }
        }

        project.tasks.named('check') {
            dependsOn integrationTestTask
        }
    }
}
