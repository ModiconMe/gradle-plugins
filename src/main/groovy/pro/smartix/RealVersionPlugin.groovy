package pro.smartix

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Property
import org.gradle.process.ExecOperations

import javax.inject.Inject
import java.util.function.Function

class RealVersionPlugin implements Plugin<Settings> {


    private final ExecOperations execOperations

    @Inject
    RealVersionPlugin(ExecOperations execOperations) {
        this.execOperations = execOperations
    }

    @Override
    void apply(Settings settings) {
        def extension = settings.extensions.create('versionExt', RealVersionExtension)
        extension.realVersion.convention((Function<String, String>)((String version) -> getRealVersion(version)))
    }

    private String getRealVersion(String curVersion) {
        def branchVersion = new ByteArrayOutputStream()

        execOperations.exec {
            it.commandLine 'git', 'rev-parse', '--abbrev-ref', 'HEAD'
            it.standardOutput = branchVersion
        }

        def isMaster = branchVersion.toString().startsWith('master')

        isMaster ? curVersion : curVersion + "-SNAPSHOT"
    }
}

interface RealVersionExtension {
    Property<Function<String, String>> getRealVersion()
}
