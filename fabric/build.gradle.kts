plugins{
    id("fabric-loom") version("1.10.5")
}
val platform:String=name
val minecraft_version:String=rootProject.property("version_${platform}").toString()
val mod_id:String=rootProject.property("mod_id").toString()
val deps:Map<String,String> =extra["deps"] as Map<String, String>
repositories{
    maven("https://maven.fabricmc.net")
}
loom{
    mods{
        create(mod_id){
            sourceSet(sourceSets.main.get())
        }
    }
}
dependencies{
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${deps["map"]}:v2")
    modImplementation("net.fabricmc:fabric-loader:${deps["platform"]}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${deps["api"]}")
}