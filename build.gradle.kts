import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
buildscript{
    repositories{
        mavenCentral()
    }
    dependencies{
        classpath("com.google.code.gson:gson:2.11.0")
    }
}
plugins{
    id("java-library")
    id("com.gradleup.shadow") version("8.3.6")
    id("maven-publish")
}
val versions_file:File=file("versions.json")
val type:Type=object:TypeToken<Map<String,Map<String,Map<String,String>>>>(){}.type
val versions:Map<String,Map<String,Map<String,String>>> =
    Gson().fromJson(versions_file.readText(),type)
subprojects{
    apply(plugin="java-library")
    apply(plugin="com.gradleup.shadow")
    apply(plugin="maven-publish")
    val platform:String=name
    val minecraft_version:String=rootProject.property("version_${platform}").toString()
    val mod_id:String=rootProject.property("mod_id").toString()
    val mod_name:String=rootProject.property("mod_name").toString()
    val mod_license:String=rootProject.property("mod_license").toString()
    val mod_version:String=rootProject.property("mod_version").toString()
    val mod_group_id:String=rootProject.property("mod_group_id").toString()
    val mod_authors:String=rootProject.property("mod_authors").toString()
    val mod_description:String=rootProject.property("mod_description").toString()
    val shade:Configuration=configurations.create("shade")
    val deps:Map<String,String> =versions[platform]?.get(minecraft_version)
        ?:throw GradleException("No version data for platform ${platform}, Minecraft ${minecraft_version}.")
    val meta_inf:List<String> =listOf(
        "META-INF/mods.toml",
        "fabric.mod.json",
        "META-INF/neoforge.mods.toml"
    )
    val replaced:Map<String,String> =mapOf(
        "java" to deps["java"]!!,
        "minecraft_version" to minecraft_version,
        "minecraft_range" to deps["minecraft_range"]!!,
        "platform" to platform,
        "platform_range" to deps["platform_range"]!!,
        "loader_range" to deps["loader_range"]!!,
        "map" to deps["map"]!!,
        "api" to deps["api"]!!,
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_license" to mod_license,
        "mod_version" to mod_version,
        "mod_authors" to mod_authors,
        "mod_description" to mod_description
    )
    extra["platform"]=platform
    extra["minecraft_version"]=minecraft_version
    extra["shade"]=shade
    extra["deps"]=deps
    version=mod_version
    group=mod_group_id
    base{
        archivesName.set(mod_id)
    }
    java{
        withSourcesJar()
        withJavadocJar()
    }
    tasks.withType<JavaCompile>().configureEach{
        options.release=deps["java"]!!.toInt()
    }
    repositories{
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
        maven("https://jitpack.io")
    }
    sourceSets{
        main{
            java{
                srcDirs("../src/main/java","../src/${platform}/java")
            }
            resources{
                srcDirs("../src/main/resources")
            }
        }
    }
    configurations.named("implementation") {
        extendsFrom(shade)
    }
    dependencies{
        add(shade.name,"com.github.Deiloproxide:VVVF-Simulator-Core:${mod_version}"){
            isTransitive=false
        }
        add(shade.name,"com.github.wendykierp:JTransforms:3.2"){
            isTransitive=false
        }
        add(shade.name,"org.visnow:JLargeArrays:1.7"){
            isTransitive=false
        }
        add(shade.name,"org.apache.commons:commons-math3:3.6.1")
        add(shade.name,"org.yaml:snakeyaml:2.6")
    }
    tasks.named<ShadowJar>("shadowJar"){
        configurations=listOf(shade)
        archiveFileName.set("${mod_id}-${platform}-${mod_version}.jar")
        relocate("org.jtransforms","${mod_group_id}.shadow.org.jtransforms")
        relocate("org.visnow.jlargearrays","${mod_group_id}.shadow.org.visnow.jlargearrays")
        relocate("org.apache.commons.math3","${mod_group_id}.shadow.org.apache.commons.math3")
        relocate("org.yaml.snakeyaml","${mod_group_id}.shadow.org.yaml.snakeyaml")
    }
    tasks.named("assemble"){
        dependsOn("shadowJar")
    }
    tasks.named("build"){
        dependsOn("shadowJar")
    }
    tasks.named<ProcessResources>("processResources"){
        inputs.properties(replaced)
        filesMatching(meta_inf){
            expand(replaced)
        }
    }
    publishing{
        publications{
            create<MavenPublication>("mavenJava"){
                from(components["java"])
            }
        }
    }
}