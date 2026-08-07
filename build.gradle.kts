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
    val build_version:String=(rootProject.findProperty("version") as? String)?:mod_version
    val build_group_id:String=(rootProject.findProperty("group") as? String)?:mod_group_id
    val deps:Map<String,String> =versions[platform]?.get(minecraft_version)
        ?:throw GradleException("No version for ${platform} ${minecraft_version}.")
    val sources:Configuration=configurations.create("source"){
        isCanBeConsumed=false
        isCanBeResolved=true
    }
    val docs:Configuration=configurations.create("docs"){
        isCanBeConsumed=false
        isCanBeResolved=true
    }
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
        "mod_id" to mod_id,
        "mod_name" to mod_name,
        "mod_license" to mod_license,
        "mod_version" to mod_version,
        "mod_authors" to mod_authors,
        "mod_description" to mod_description
    )
    extra["minecraft_version"]=minecraft_version
    extra["platform"]=deps["platform"]
    extra["map"]=deps["map"]
    version=build_version
    group=build_group_id
    base{
        archivesName.set(mod_id)
    }
    java{
        withSourcesJar()
        withJavadocJar()
        toolchain.languageVersion=JavaLanguageVersion.of(deps["java"]!!)
    }
    tasks.withType<JavaCompile>().configureEach{
        options.release=deps["java"]!!.toInt()
    }
    repositories{
        mavenLocal()
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
    dependencies{
        shadow("com.github.wendykierp:JTransforms:3.2"){
            isTransitive=false
        }
        shadow("org.visnow:JLargeArrays:1.7"){
            isTransitive=false
        }
        shadow("org.apache.commons:commons-math3:3.6.1")
        shadow("org.yaml:snakeyaml:2.6")
        shadow("com.github.Deiloproxide:VVVF-Simulator-Core:${mod_version}")
        sources("com.github.Deiloproxide:VVVF-Simulator-Core:${mod_version}:sources")
        docs("com.github.Deiloproxide:VVVF-Simulator-Core:${mod_version}:javadoc")
    }
    tasks.named<ShadowJar>("shadowJar"){
        configurations=listOf(project.configurations.shadow.get())
        archiveBaseName=mod_id
        archiveAppendix=platform
        archiveVersion=mod_version
        archiveClassifier=""
        relocate("org.jtransforms","${mod_group_id}.shadow.jtransforms")
        relocate("org.visnow.jlargearrays","${mod_group_id}.shadow.jlargearrays")
        relocate("org.apache.commons.math3","${mod_group_id}.shadow.math3")
        relocate("org.yaml.snakeyaml","${mod_group_id}.shadow.snakeyaml")
    }
    tasks.register<Jar>("shadowSourceJar"){
        archiveBaseName=mod_id
        archiveAppendix=platform
        archiveVersion=mod_version
        archiveClassifier="sources"
        from(sourceSets.main.get().allSource)
        from({
            sources.resolvedConfiguration.resolvedArtifacts
                .filter{it.moduleVersion.id.name=="VVVF-Simulator-Core" && it.classifier=="sources"}
                .map{zipTree(it.file).matching{include("**/*.java")}}
        })
    }
    tasks.register<Jar>("shadowJavadocJar"){
        archiveBaseName=mod_id
        archiveAppendix=platform
        archiveVersion=mod_version
        archiveClassifier="javadoc"
        dependsOn(tasks.javadoc)
        from({
            docs.resolvedConfiguration.resolvedArtifacts
                .filter{it.moduleVersion.id.name=="VVVF-Simulator-Core" && it.classifier=="javadoc"}
                .map{zipTree(it.file)}
        })
    }
    tasks.named("assemble"){
        dependsOn("shadowJar")
    }
    tasks.named("build"){
        dependsOn("shadowJar","shadowSourceJar","shadowJavadocJar")
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
                artifact(tasks.named("shadowJar"))
                artifact(tasks.named("shadowSourceJar"))
                artifact(tasks.named("shadowJavadocJar"))
            }
        }
    }
}