package com.example.model

enum class ModuleCategory {
    COMBAT,
    MOVEMENT,
    RENDER,
    WORLD,
    MISC
}

data class McpeOffsetInfo(
    val id: String,
    val functionName: String,
    val demangledSymbol: String,
    val offsetHex: String,
    val originalHex: String,
    val patchedHex: String,
    val description: String,
    val category: ModuleCategory
)

data class McpeVersionProfile(
    val version: String,
    val releaseDate: String,
    val targetArch: String = "arm64-v8a",
    val libraryName: String = "libminecraftpe.so",
    val knownOffsets: List<McpeOffsetInfo>
)

data class ApkProject(
    val id: String,
    val name: String,
    val mcpeVersion: String,
    val apkFileName: String,
    val packageId: String = "com.mojang.minecraftpe",
    val architecture: String = "arm64-v8a",
    val soSizeMb: Float = 612.4f,
    val dexCount: Int = 3,
    val backupCreated: Boolean = true,
    val isSigned: Boolean = false,
    val injectedMenu: Boolean = false,
    val appliedPatchesCount: Int = 0
)

data class SmaliClassFile(
    val dexName: String,
    val className: String,
    val packagePath: String,
    val methodsCount: Int,
    val content: String,
    val isInjected: Boolean = false,
    val path: String = packagePath,
    val dexLocation: String = dexName
)

data class HexPatch(
    val id: String,
    val offset: Long,
    val originalBytes: String,
    val patchedBytes: String,
    val description: String,
    val functionName: String,
    val applied: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class HookTemplate(
    val id: String,
    val name: String,
    val targetFunction: String,
    val offsetHex: String,
    val trampolineSize: Int = 16,
    val hookType: String = "Inline Hook (Trampoline)",
    val asmSnippet: String,
    val active: Boolean = false
)

data class FeatureModule(
    val id: String,
    val name: String,
    val category: ModuleCategory,
    val description: String,
    val isEnabled: Boolean = false,
    val hasSlider: Boolean = false,
    val sliderValue: Float = 0f,
    val sliderMin: Float = 0f,
    val sliderMax: Float = 10f,
    val sliderUnit: String = "",
    val nativeOffset: String = ""
)

data class ModMenuConfig(
    val title: String = "GLITCH ENGINE v2.6",
    val accentColor: String = "#00F5FF",
    val glowEnabled: Boolean = true,
    val hudFps: Boolean = true,
    val hudCoords: Boolean = true,
    val hudSpeed: Boolean = false,
    val hudSpeedometer: Boolean = false,
    val buttonPositionX: Float = 24f,
    val buttonPositionY: Float = 160f,
    val transparency: Float = 0.88f
)

data class PythonScript(
    val id: String,
    val name: String,
    val description: String,
    val code: String,
    val isBuiltIn: Boolean = true
)

data class LogcatEntry(
    val timestamp: String,
    val level: String, // D, I, W, E
    val tag: String,
    val message: String
)
