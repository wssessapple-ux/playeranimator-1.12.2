plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("com.gtnewhorizons.retrofuturagradle") version "1.4.9"
}

val mod_version: String by project
val maven_group: String by project
val mod_id: String by project
val mod_name: String by project
val archives_base_name: String by project
val mod_authors: String by project

val use_mixins: String by project
val use_coremod: String by project
val include_mod: String by project
val coremod_plugin_class_name: String by project

version = mod_version

base {
    archivesName.set(archives_base_name)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

minecraft {
    mcVersion.set("1.12.2")

    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("39")

    username.set("Developer")

    val args = mutableListOf("-ea:${group}")
    if (use_coremod.toBoolean()) {
        args += "-Dfml.coreMods.load=$coremod_plugin_class_name"
    }
    if (use_mixins.toBoolean()) {
        args += "-Dmixin.hotSwap=true"
        args += "-Dmixin.checks.interfaces=true"
        args += "-Dmixin.debug.export=true"
    }
    extraRunJvmArguments.addAll(args)

    useDependencyAccessTransformers.set(true)

    injectedTags.put("VERSION", project.version)
    injectedTags.put("MOD_ID", mod_id)
    injectedTags.put("MOD_NAME", mod_name)
}

tasks.injectTags.configure {
    outputClassName.set("dev.kosmx.playerAnim.Tags")
}

repositories {
    maven {
        name = "CleanroomMC Maven"
        url = uri("https://maven.cleanroommc.com")
    }
    maven {
        name = "SpongePowered Maven"
        url = uri("https://repo.spongepowered.org/maven")
    }
    mavenLocal()
}

dependencies {
    // Minecraft 1.12.2 ships gson 2.8.0 at runtime: compile against the same version
    compileOnly("com.google.code.gson:gson:2.8.0")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("org.jetbrains:annotations:26.0.2")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.code.gson:gson:2.8.0")
    testImplementation("com.google.code.findbugs:jsr305:3.0.2")
    testImplementation("org.jetbrains:annotations:26.0.2")

    if (use_mixins.toBoolean()) {
        val mixin = modUtils.enableMixins("zone.rong:mixinbooter:10.7", "mixins.${mod_id}.refmap.json") as String
        api(mixin) {
            isTransitive = true
        }
        annotationProcessor("org.ow2.asm:asm-debug-all:5.2")
        annotationProcessor("com.google.guava:guava:24.1.1-jre")
        annotationProcessor("com.google.code.gson:gson:2.8.6")
        annotationProcessor(mixin) {
            isTransitive = false
        }
    }
}

tasks.withType<Jar> {
    manifest {
        val attributeMap = mutableMapOf<String, String>()
        if (use_coremod.toBoolean()) {
            attributeMap["FMLCorePlugin"] = coremod_plugin_class_name
            if (include_mod.toBoolean()) {
                attributeMap["FMLCorePluginContainsFMLMod"] = true.toString()
            }
        }
        if (use_mixins.toBoolean()) {
            attributeMap["MixinConfigs"] = "mixins.${mod_id}.json"
        }
        attributeMap["Implementation-Title"] = mod_name
        attributeMap["Implementation-Version"] = mod_version
        attributeMap["Implementation-Vendor"] = mod_authors
        attributeMap["Specification-Title"] = mod_id
        attributeMap["Specification-Version"] = mod_version
        attributeMap["Specification-Vendor"] = mod_authors
        attributes(attributeMap)
    }
}

tasks.withType<ProcessResources> {
    inputs.property("version", mod_version)
    inputs.property("mcversion", minecraft.mcVersion.get())

    filesMatching(arrayListOf("mcmod.info")) {
        expand(
            "version" to mod_version,
            "mcversion" to minecraft.mcVersion.get()
        )
    }
}
