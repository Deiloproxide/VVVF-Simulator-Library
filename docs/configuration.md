<div align="center">

![icon](../icon/icon.svg)
# Configuration
How to config VVVF Simulator Library in your projects.
</div>

## Notes
VVVF Simulator Core was merged into VVVF Simulator Library since
version 1.0.6. Now that is archived. Use `VVVF-Simulator-Library:core`
instead of `VVVF-Simulator-Core`.
## Contents
- [For Core Users](#for-core-users)
- [For Core Developers](#for-core-developers)
- [For Mod Users](#for-mod-users)
- [For Mod Developers](#for-mod-developers)
## For Core Users
### Prebuilt Artifacts
You can get this library from
[VVVF Simulator Library Release](https://github.com/Deiloproxide/VVVF-Simulator-Library/releases).

For version<1.0.6, you can get it from
[VVVF Simulator Core Release](https://github.com/Deiloproxide/VVVF-Simulator-Core/releases).
### Build From Source
The repository uses Java 17 to run Gradle.
```bash
./gradlew :core:build --configure-on-demand
```
The build writes core artifacts to `/build/libs` directory.
### Publish To MavenLocal
When using a local checkout before publishing, run:
```bash
./gradlew :core:publishToMavenLocal --configure-on-demand
```
## For Core Developers
### Dependencies
#### Common Library
The core project depends on:
- `com.github.wendykierp:JTransforms:3.2`
- `org.visnow:JLargeArrays:1.7`
- `org.apache.commons:commons-math3:3.6.1`
- `org.yaml:snakeyaml:2.6`

You can find them on the MavenCentral.
#### Core Library
|                | Group Id                                       | Artifact Id         |
|----------------|------------------------------------------------|---------------------|
| Version<1.0.6  | com.github.deiloproxide                        | VVVF-Simulator-Core |
| Version>=1.0.6 | com.github.deiloproxide.VVVF-Simulator-Library | core                |

Maven configuration:
- pom
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
- dependency
```xml
<dependency>
    <groupId>[group_id]</groupId>
    <artifactId>[artifact_id]</artifactId>
    <version>[version]</version>
</dependency>
```
Gradle configuration:
- Groovy DSL
```groovy
repositories{
    maven{url="https://jitpack.io"}
}
dependencies{
    implementation("[group_id]:[artifact_id]:[version]")
}
```
- Kotlin DSL
```kotlin
repositories{
    maven(url=uri("https://jitpack.io"))
}
dependencies{
    implementation("[group_id]:[artifact_id]:[version]")
}
```
## For Mod Users
This library doesn't contain any function. It just provides a runtime
environment for other mods.
### Environment
- Client: optional
- Server: optional

| Loader   | Minecraft | Runtime Java |
|----------|-----------|--------------|
| Forge    | \>=1.20.1 | 17           |
| Fabric   | \>=1.20.1 | 17           |
| NeoForge | \>=1.21.1 | 21           |
### Prebuilt Artifacts
You can get this mod from
[Modrinth](https://modrinth.com/mod/vvvf-simulator-library),
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/vvvf-simulator-library) and
[GitHub Release](https://github.com/Deiloproxide/VVVF-Simulator-Library/releases).
### Build From Source
The repository uses Java 21 to run Gradle. Platform jars are compiled for the
Java version shown in the table.
- Build all platforms.
```bash
./gradlew build
```
- Build one platform.
```bash
./gradlew :[platform]:build --configure-on-demand
```
The build reads loader versions from `versions.json` and writes platform
artifacts to `[platform]/build/libs` directory.
## For Mod Developers
### Dependencies
|  Provider  | Repository                     | Dependency                                                          |
|------------|--------------------------------|---------------------------------------------------------------------|
| JitPack    | https://jitpack.io             | com.github.Deiloproxide.VVVF-Simulator-Library:[platform]:[version] |
| Modrinth   | https://api.modrinth.com/maven | maven.modrinth:vvvf-simulator-library:[platform]-[version]          |
| CurseMaven | https://cursemaven.com         | curse.maven:vvvf-simulator-library-1638825:[file_id]                |

Gradle configuration:
- Groovy DSL
```groovy
repositories{
    maven{url="[repository]"}
}
dependencies{
    implementation("[dependency]")
}
```
- Kotlin DSL
```kotlin
repositories{
    maven(url=uri("[repository]"))
}
dependencies{
    implementation("[dependency]")
}
```
### Loader Metadata
#### Forge
```toml
[[dependencies."[mod_id]"]]
modId="vvvf_simulator_lib"
mandatory=true
versionRange="[mod_version_range]"
ordering="NONE"
side="CLIENT"
```
#### Fabric
```json
{
  "depends":{
    "vvvf_simulator_lib":"[mod_version_range]"
  }
}
```
#### NeoForge
```toml
[[dependencies."[mod_id]"]]
modId="vvvf_simulator_lib"
type="required"
versionRange="[mod_version_range]"
ordering="NONE"
side="CLIENT"
```
### Notes
The distributed platform jars include and relocate these libraries:
- `com.github.wendykierp:JTransforms:3.2`
- `org.visnow:JLargeArrays:1.7`
- `org.apache.commons:commons-math3:3.6.1`
- `org.yaml:snakeyaml:2.6`

They are relocated under this mod's shadow namespace. Treat relocated packages as
internal implementation details; do not import them from another mod.