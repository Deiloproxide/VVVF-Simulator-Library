plugins{
    id("fabric-loom") version("1.10.5")
}
val mod_id:String=rootProject.property("mod_id").toString()
val minecraft_version:String=extra["minecraft_version"] as String
val platform:String=extra["platform"] as String
val map:String=extra["map"] as String
repositories{
    maven("https://maven.fabricmc.net")
}
loom{
    mods.create(mod_id){
        sourceSet(sourceSets.main.get())
    }
}
dependencies{
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${map}:v2")
    implementation("net.fabricmc:fabric-loader:${platform}")
}