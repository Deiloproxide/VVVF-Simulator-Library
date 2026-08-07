import net.neoforged.moddevgradle.dsl.NeoForgeExtension
plugins{
    id("net.neoforged.moddev") version("2.0.141")
}
val mod_id:String=rootProject.property("mod_id").toString()
val minecraft_version:String=extra["minecraft_version"] as String
val platform:String=extra["platform"] as String
configure<NeoForgeExtension>{
    version=platform
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
    mods.create(mod_id){
        sourceSet(sourceSets.main.get())
    }
}