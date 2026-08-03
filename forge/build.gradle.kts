import net.minecraftforge.gradle.common.util.MinecraftExtension
plugins{
    id("net.minecraftforge.gradle") version("6.0.54")
}
val platform:String=name
val minecraft_version:String=rootProject.property("version_${platform}").toString()
val mod_id:String=rootProject.property("mod_id").toString()
val shade:Configuration=extra["shade"] as Configuration
val deps:Map<String,String> =extra["deps"] as Map<String,String>
repositories{
    maven("https://maven.minecraftforge.net")
}
configure<MinecraftExtension>{
    mappings("official",minecraft_version)
    runs{
        create("client"){
            property("forge.enabledGameTestNamespaces",mod_id)
        }
        create("server"){
            args("--nogui")
            property("forge.enabledGameTestNamespaces",mod_id)
        }
        configureEach{
            mods.create(mod_id){
                source(sourceSets.main.get())
            }
        }
    }
}
configurations.named("minecraftLibrary"){
    extendsFrom(shade)
}
dependencies{
    add("minecraft","net.minecraftforge:forge:${minecraft_version}-${deps["platform"]}")
}