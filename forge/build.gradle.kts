import net.minecraftforge.gradle.common.util.MinecraftExtension
import net.minecraftforge.gradle.userdev.tasks.RenameJarInPlace
plugins{
    id("net.minecraftforge.gradle") version("6.0.54")
}
val mod_id:String=extra["mod_id"] as String
val minecraft_version:String=extra["minecraft_version"] as String
val platform_version:String=extra["platform_version"] as String
dependencies{
    minecraft("net.minecraftforge:forge:${minecraft_version}-${platform_version}")
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
afterEvaluate{
    tasks.named<RenameJarInPlace>("reobfJar"){
        enabled=false
    }
}