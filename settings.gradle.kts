pluginManagement{
    repositories{
        maven("https://maven.aliyun.com/repository/public")
        gradlePluginPortal()
        maven("https://maven.minecraftforge.net")
        maven("https://maven.fabricmc.net")
        maven("https://maven.neoforged.net/releases")
    }
}
plugins{
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.10.0")
}
include("forge","fabric","neoforge")