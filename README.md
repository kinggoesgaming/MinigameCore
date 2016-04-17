# MinigameCore [![Issues](https://img.shields.io/github/issues/MinigameCore/MinigameCore.svg?style=flat-square)](http://www.github.com/MinigameCore/MinigameCore/issues/) [![Gitter](https://img.shields.io/badge/chat-on_gitter-3F51B5.svg?style=flat-square)](https://gitter.im/MinigameCore/MinigameCore) [![JitPack](https://img.shields.io/badge/dependency-jitpack-4CAF50.svg?style=flat-square)](https://jitpack.io/#MinigameCore/MinigameCore) [![Travis](https://img.shields.io/travis/MinigameCore/MinigameCore.svg?style=flat-square)](https://travis-ci.org/MinigameCore/MinigameCore)

MinigameCore is a framework for Sponge that provides a core set of functions to make minigame development faster.

### Using MinigameCore

In order to use MinigameCore, you must add it as a Gradle dependency.

```gradle
repositories {
    maven {
        url "https://jitpack.io"
    }
}
dependencies {
    compile 'com.github.MinigameCore:MinigameCore:TARGET_VERSION'
}
```

Replace `TARGET_VERSION` with the intended version. The intended version must be a valid GitHub tag on the MinigameCore repository.

After adding MinigameCore as a Gradle dependency, you must add the shadow plugin to your project.

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version '1.2.3'
}

artifacts {
  archives shadowJar
}
```

Next, add MinigameCore to the Shadow JAR and relocate it.

```gradle
shadowJar {
    dependencies {
      include dependency('com.github.MinigameCore:MinigameCore')
    }
  
    relocate 'io.github.flibio.minigamecore', 'YOUR.PLUGIN.PACKAGE.minigamecore'
}
```

Replace `YOUR.PLUGIN.PACKAGE` with your plugin package.

Finally, refresh your Gradle dependencies. You can now develop an awesome minigame plugin using MinigameCore!
