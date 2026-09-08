package com.example.data

import com.example.model.ApkProject
import com.example.model.LogcatEntry

object ProjectRepository {
    val initialProjects = listOf(
        ApkProject(
            id = "proj_mcpe_126",
            name = "Minecraft 1.26.31.1 [PVP Mod Project]",
            mcpeVersion = "1.26.31.1",
            apkFileName = "Minecraft-v1.26.31.1-arm64.apk",
            packageId = "com.mojang.minecraftpe",
            architecture = "arm64-v8a",
            soSizeMb = 612.4f,
            dexCount = 3,
            backupCreated = true,
            isSigned = true,
            injectedMenu = true,
            appliedPatchesCount = 4
        ),
        ApkProject(
            id = "proj_mcpe_clean",
            name = "Minecraft 1.26.31.1 [Clean Backup]",
            mcpeVersion = "1.26.31.1",
            apkFileName = "Minecraft-v1.26.31.1-Original.apk",
            packageId = "com.mojang.minecraftpe",
            architecture = "arm64-v8a",
            soSizeMb = 612.4f,
            dexCount = 3,
            backupCreated = true,
            isSigned = false,
            injectedMenu = false,
            appliedPatchesCount = 0
        ),
        ApkProject(
            id = "proj_mcpe_125",
            name = "Minecraft 1.25.10 [Render Mod]",
            mcpeVersion = "1.25.10",
            apkFileName = "Minecraft-v1.25.10-arm64.apk",
            packageId = "com.mojang.minecraftpe",
            architecture = "arm64-v8a",
            soSizeMb = 589.1f,
            dexCount = 3,
            backupCreated = true,
            isSigned = false,
            injectedMenu = false,
            appliedPatchesCount = 2
        )
    )

    fun getSampleLogcat(): List<LogcatEntry> = listOf(
        LogcatEntry("12:04:12.102", "I", "GlitchEngine", "Initializing Glitch Native Hook Engine v2.6..."),
        LogcatEntry("12:04:12.150", "I", "GlitchEngine", "Memory-mapped libminecraftpe.so (arm64-v8a) base at 0x0000007824000000"),
        LogcatEntry("12:04:12.180", "D", "GlitchEngine", "Applying inline trampoline at 0x0381F4C0 (Player::getReachDistance)"),
        LogcatEntry("12:04:12.210", "D", "GlitchEngine", "Hook status: SUCCESS. Branch offset: +0x00000028 -> cave allocated"),
        LogcatEntry("12:04:12.260", "I", "GlitchOverlay", "SYSTEM_ALERT_WINDOW active. Floating button initialized at (24, 160)"),
        LogcatEntry("12:04:12.300", "I", "GlitchConfig", "Loaded glitch_config.json: Reach=6.5m, Fullbright=true, AntiKB=true"),
        LogcatEntry("12:04:12.510", "D", "MCPE_Native", "libminecraftpe.so: AppPlatform::init() complete"),
        LogcatEntry("12:04:13.004", "I", "MCPE_Native", "Level::tick() world loaded. Entity list populated (34 actors)"),
        LogcatEntry("12:04:14.210", "D", "GlitchEngine", "Actor::applyImpulse hooked. Impulse vector intercepted -> zeroed"),
        LogcatEntry("12:04:15.890", "I", "GlitchEngine", "Game running at 60.0 FPS. Anti-cheat integrity checks silenced.")
    )
}
