package pro.smartix.java

import org.gradle.api.provider.Property

interface SmartixJavaExtension {

    Property<Integer> getJavaVersion()

}