import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.artifacts.Configuration
plugins{
    id("net.neoforged.moddev") version("2.0.141")
}
val platform:String=name
val minecraft_version:String=rootProject.property("version_${platform}").toString()
val mod_id:String=rootProject.property("mod_id").toString()
val shade:Configuration=extra["shade"] as Configuration
val deps:Map<String,String> =extra["deps"] as Map<String, String>
repositories{
    maven("https://maven.neoforged.net/releases")
}
configure<NeoForgeExtension>{
    version=deps["platform"].toString()
    runs{
        create("client"){
            client()
            systemProperty("neoforge.enabledGameTestNamespaces",mod_id)
        }
        create("server"){
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces",mod_id)
        }
    }
    mods{
        create(mod_id){
            sourceSet(sourceSets.main.get())
        }
    }
}
configurations.named("additionalRuntimeClasspath"){
    extendsFrom(shade)
}