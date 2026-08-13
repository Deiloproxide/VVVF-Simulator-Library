import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
buildscript{
    repositories{
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
    }
    dependencies{
        classpath("com.google.code.gson:gson:2.11.0")
    }
}
plugins{
    id("java-library")
    id("com.gradleup.shadow") version("9.4.1")
    id("maven-publish")
}
val core:String=name
val mod_id:String=property("mod_id").toString()
val core_java:String=property("core_java").toString()
val versions_file:File=file("versions.json")
val type:Type=object:TypeToken<Map<String,Map<String,Map<String,String>>>>(){}.type
val versions:Map<String,Map<String,Map<String,String>>> =
    Gson().fromJson(versions_file.readText(),type)
repositories{
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies{
    implementation("com.github.wendykierp:JTransforms:3.2"){
        isTransitive=false
    }
    implementation("org.visnow:JLargeArrays:1.7"){
        isTransitive=false
    }
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("org.yaml:snakeyaml:2.6")
}
java{
    withSourcesJar()
    withJavadocJar()
    toolchain.languageVersion=JavaLanguageVersion.of(core_java)
}
tasks.withType<Jar>().configureEach{
    archiveBaseName=mod_id
    archiveAppendix=core
}
tasks.named<Jar>("sourcesJar"){
    include("**/*.java")
}
tasks.named<ShadowJar>("shadowJar"){
    enabled=false
}
publishing{
    publications{
        create<MavenPublication>("maven"){
            artifact(tasks.named<Jar>("jar"))
            artifact(tasks.named<Jar>("sourcesJar"))
            artifact(tasks.named<Jar>("javadocJar"))
        }
    }
}
subprojects{
    apply(plugin="java-library")
    apply(plugin="com.gradleup.shadow")
    apply(plugin="maven-publish")
    val platform:String=name
    val minecraft_version:String=property("version_${platform}").toString()
    val mod_name:String=property("mod_name").toString()
    val mod_license:String=property("mod_license").toString()
    val mod_authors:String=property("mod_authors").toString()
    val mod_description:String=property("mod_description").toString()
    val deps:Map<String,String> =versions[platform]?.get(minecraft_version)
        ?:throw GradleException("No version for ${platform} ${minecraft_version}.")
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
        "mod_version" to version as String,
        "mod_authors" to mod_authors,
        "mod_description" to mod_description
    )
    extra["mod_id"]=mod_id
    extra["minecraft_version"]=minecraft_version
    extra["platform_version"]=deps["platform"]
    repositories{
        maven("https://maven.aliyun.com/repository/public")
        mavenCentral()
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
    java{
        withSourcesJar()
        withJavadocJar()
        toolchain.languageVersion=JavaLanguageVersion.of(deps["java"]!!)
    }
    tasks.named<ProcessResources>("processResources"){
        inputs.properties(replaced)
        filesMatching(meta_inf){
            expand(replaced)
        }
    }
    tasks.withType<Jar>().configureEach{
        archiveBaseName=mod_id
        archiveAppendix=platform
    }
    tasks.named<Jar>("jar"){
        enabled=false
    }
    tasks.named<Jar>("sourcesJar"){
        include("**/*.java")
    }
    tasks.named<ShadowJar>("shadowJar"){
        configurations=listOf(project.configurations.shadow.get())
        archiveClassifier=""
        minimize()
        relocate("org.jtransforms","vvvfsimulator.shadow.jtransforms")
        relocate("org.visnow.jlargearrays","vvvfsimulator.shadow.jlargearrays")
        relocate("org.apache.commons.math3","vvvfsimulator.shadow.math3")
        relocate("org.yaml.snakeyaml","vvvfsimulator.shadow.snakeyaml")
    }
    publishing{
        publications{
            create<MavenPublication>("mavenJava"){
                artifact(tasks.named<ShadowJar>("shadowJar"))
                artifact(tasks.named<Jar>("sourcesJar"))
                artifact(tasks.named<Jar>("javadocJar"))
            }
        }
    }
}