package org.example

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class SettingsBasePlugin implements Plugin<Settings> {

    @Override
    void apply(Settings settings) {
        def repos = [
                settings.pluginManagement.repositories.mavenCentral(),
                settings.pluginManagement.repositories.gradlePluginPortal(),
                settings.pluginManagement.repositories.mavenLocal()
        ]

        settings.dependencyResolutionManagement.repositories.addAll(repos)
    }
}
