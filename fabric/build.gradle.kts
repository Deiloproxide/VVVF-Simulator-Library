import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.RemapSourcesJarTask
plugins{
    id("fabric-loom") version("1.10.5")
}
val mod_id:String=extra["mod_id"] as String
val minecraft_version:String=extra["minecraft_version"] as String
val platform_version:String=extra["platform_version"] as String
repositories{
    maven("https://maven.fabricmc.net")
}
dependencies{
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings())
    implementation("net.fabricmc:fabric-loader:${platform_version}")
}
loom{
    mods.create(mod_id){
        sourceSet(sourceSets.main.get())
    }
}
tasks.named<RemapJarTask>("remapJar"){
    enabled=false
}
tasks.named<RemapSourcesJarTask>("remapSourcesJar"){
    enabled=false
}