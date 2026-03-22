plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    // Use the core plugin ID for v2.0.0
    id("org.jetbrains.intellij.platform") version "2.0.0"
}

group = "com.v0.plugin"
version = "1.0.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1.1")
        bundledPlugins("com.intellij.java")
        instrumentationTools()
    }
    
    implementation("org.jmdns:jmdns:3.5.9")
    implementation("org.java-websocket:Java-WebSocket:1.5.7")
    implementation("org.json:json:20240303")
}

intellijPlatform {
    pluginConfiguration {
        id = "com.v0.plugin"
        name = "Version0 Api"
        vendor { 
            name = "TigauTechnologies"
            email = "connect@tigau-enterprises.com"
            url = "https://tigau-enterprises.com"
        }
        description = "Version0 app-to-IDE Bridge"
    }
}
kotlin{
    jvmToolchain(17)
}