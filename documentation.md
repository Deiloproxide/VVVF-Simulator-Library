<div align="center">

![icon](icon/icon.svg)
# Documentation
How to use VVVF Simulator Library in Minecraft mods.
</div>

## Overview
VVVF Simulator Library packages
[VVVF Simulator Core](https://github.com/Deiloproxide/VVVF-Simulator-Core)
as a Minecraft mod so Forge, Fabric, and NeoForge projects can share
the same VVVF calculation and audio-generation code at runtime.
- [For Users](#for-users)
- [For Developers](#for-developers)
## For Users
This library doesn't contain any function. It just provides a runtime
environment for other mods.
### Environment
- Client: necessary
- Server: invalid

| Loader   | Minecraft | Runtime Java |
|----------|-----------|--------------|
| Forge    | \>=1.20.1 | 17           |
| Fabric   | \>=1.20.1 | 17           |
| NeoForge | \>=1.21.1 | 21           |
### Installation
#### Prebuilt Artifact
You can get this mod from
[Modrinth](https://modrinth.com/mod/vvvf-simulator-library),
[CurseForge](https://www.curseforge.com/minecraft/mc-mods/vvvf-simulator-library) and
[GitHub Release](https://github.com/Deiloproxide/VVVF-Simulator-Library/releases).
#### Build From Source
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
The current build reads loader versions from `versions.json` and writes platform
artifacts to `[platform]/build/libs` directory.
## For Developers
### Dependencies
Add the platform jar as both a compile dependency and a runtime mod dependency.
The exact Maven coordinates depend on where you publish or consume the artifact.
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
|            | repository                     | dependency                                                          |
|------------|--------------------------------|---------------------------------------------------------------------|
| JitPack    | https://jitpack.io             | com.github.Deiloproxide.VVVF-Simulator-Library:[platform]:[version] |
| Modrinth   | https://api.modrinth.com/maven | maven.modrinth:vvvf-simulator-library:[platform]-[version]          |
| CurseMaven | https://cursemaven.com         | curse.maven:vvvf-simulator-library-1638825:[file_id]                |
### Loader Metadata
Declare a dependency on this library in your own mod metadata so users get a
clear error when the wrapper mod is missing.
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
### Usage
Import Core classes directly from the `loader` and `vvvfsimulator` packages.
```java
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import loader.LoadContext;
import loader.LoadException;
import vvvfsimulator.data.vvvf.Manager;
import vvvfsimulator.data.vvvf.Struct;
public class YamlLoader{
    public static void load(){
        Path yamlPath=Path.of("your_config.yaml");
        try(InputStream in=Files.newInputStream(yamlPath)){
            LoadContext context=Manager.load(yamlPath.toString(),in);
            if(context.exception!=LoadException.normal)
                throw new IllegalArgumentException(
                        "Failed to load YAML: "+context.exception+
                                " at "+context.row+":"+context.col);
        }
        Struct strategy=Manager.deepClone(Manager.current);
    }
}
```
For more detailed usage, see the
[core documentation](https://github.com/Deiloproxide/VVVF-Simulator-Core/blob/main/documentation.md).
### Notes
The distributed platform jars include:
- `com.github.wendykierp:JTransforms:3.2`
- `org.visnow:JLargeArrays:1.7`
- `org.apache.commons:commons-math3:3.6.1`
- `org.yaml:snakeyaml:2.6`

They are relocated under the library's shadow namespace. Do not
import relocated packages from your own mod; use the public Core API instead.