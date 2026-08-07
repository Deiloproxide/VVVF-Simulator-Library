import net.minecraftforge.gradle.common.util.MinecraftExtension
plugins{
    id("net.minecraftforge.gradle") version("6.0.54")
}
val mod_id:String=rootProject.property("mod_id").toString()
val minecraft_version:String=extra["minecraft_version"] as String
val platform:String=extra["platform"] as String
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
dependencies{
    minecraft("net.minecraftforge:forge:${minecraft_version}-${platform}")
}