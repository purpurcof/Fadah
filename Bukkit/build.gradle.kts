import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import info.preva1l.trashcan.description.paper.Permission
import info.preva1l.trashcan.description.paper.PluginLoadOrder
import info.preva1l.trashcan.description.paper.dependency
import info.preva1l.trashcan.paper
import info.preva1l.trashcan.setRemapped
import info.preva1l.trashcan.trashcan
import info.preva1l.trashcan.description.paper.PaperDependencyDefinition.RelativeLoadOrder as RLO

trashcan {
    paper = true
}

repositories {
    maven(url = "https://repo.auxilor.io/repository/maven-public/")
    maven(url = "https://oss.sonatype.org/content/groups/public/")
    maven(url = "https://jitpack.io")
    maven(url = "https://repo.codemc.io/repository/maven-snapshots/")
    maven(url = "https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven(url = "https://mvn-repo.arim.space/lesser-gpl3/")
    maven(url = "https://repo.rosewooddev.io/repository/public/")
    maven(url = "https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    implementation(project(":API"))
    paper("1.19.3-R0.1-SNAPSHOT")

    trashcan()

    library("com.zaxxer:HikariCP:6.3.0")
    library("org.xerial:sqlite-jdbc:3.49.1.0")
    library("com.mysql:mysql-connector-j:9.3.0")
    library("org.mariadb.jdbc:mariadb-java-client:3.5.3")
    library("org.mongodb:mongodb-driver-sync:5.5.0")

    library("net.wesjd:anvilgui:1.10.5-SNAPSHOT") { setRemapped(true) }
    library("net.kyori:adventure-text-serializer-gson:4.21.0")
    library("org.mozilla:rhino:1.8.0")
    library("com.influxdb:influxdb-client-java:7.2.0")

    dependency("me.clip:placeholderapi:2.11.6", "PlaceholderAPI") { load = RLO.AFTER ; required = false }
    dependency("net.luckperms:api:5.4", "LuckPerms") { load = RLO.AFTER ; required = false }
    dependency("com.gmail.nossr50.mcMMO:mcMMO:2.2.004", "mcMMO") { required = false }

    // Currency
    dependency("com.github.MilkBowl:VaultAPI:1.7", "Vault") { load = RLO.AFTER ; required = false }
    dependency("com.github.Emibergo02:RedisEconomy:4.3.9", "RedisEconomy") { load = RLO.AFTER ; required = false }
    dependency(files("../libs/CoinsEngine-2.3.5.jar"), "CoinsEngine") { load = RLO.AFTER ; required = false }
    dependency("org.black_ixx:playerpoints:3.2.0", "PlayerPoints") { load = RLO.AFTER ; required = false }

    // Eco Items
    compileOnly("com.willfp:libreforge:4.58.1") { isTransitive = false }
    compileOnly("com.willfp:eco:6.56.0") { isTransitive = false }
    dependency("com.willfp:EcoItems:5.43.1", "EcoItems") { required = false }

    // Migrators
    dependency("com.github.Maxlego08:zAuctionHouseV3-API:3.2.1.9", "zAuctionHouseV3") { required = false }
    dependency(files("../libs/AuctionHouse-1.20.4-3.7.1.jar"), "AuctionHouse") { required = false }
    compileOnly(files("../libs/AkarianAuctionHouse-1.3.1-b6.jar"))
}

tasks.withType<ShadowJar> {
    relocate("com.github.puregero.multilib", "info.preva1l.fadah.libs.multilib")
    relocate("info.preva1l.hooker", "info.preva1l.fadah.hooks.lib")
    relocate("info.preva1l.trashcan", "info.preva1l.fadah.trashcan")
}

paper {
    description = "Fadah (Finally a Decent Auction House) is the fast, modern and advanced auction house plugin that you have been looking for!"
    website = "https://docs.preva1l.info/fadah/"
    author = "Preva1l"
    main = rootProject.group.toString() + ".Fadah"
    loader = "info.preva1l.fadah.trashcan.extension.libloader.BaseLibraryLoader"
    foliaSupported = true
    apiVersion = "1.19"

    load = PluginLoadOrder.POSTWORLD

    permissions {
        register("fadah.max-listings.<amount>") {
            description = "The amount of items a player can list at any one time."
        }

        register("fadah.listing-tax.<amount>") {
            description = "The percentage a player will get taxed when creating a listing."
        }

        register("fadah.advert-price.<amount>") {
            description = "The cost of a listing advertisement."
            default = Permission.Default.TRUE
        }

        listOf(
            "fadah.use",
            "fadah.collection-box",
            "fadah.expired-items",
            "fadah.help",
            "fadah.profile",
            "fadah.active-listings",
            "fadah.watch",
            "fadah.search",
            "fadah.view",
        ).forEach {
            register(it) {
                default = Permission.Default.TRUE
            }
        }

        register("fadah.manage.*") {
            children = listOf(
                "fadah.manage.profile",
                "fadah.manage.active-listings",
                "fadah.manage.expired-items",
                "fadah.manage.collection-box"
            )
        }
    }
}