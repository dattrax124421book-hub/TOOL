package com.example.data

import com.example.model.FeatureModule
import com.example.model.ModuleCategory

object FeatureModuleRepository {
    fun getDefaultModules(): List<FeatureModule> = listOf(
        FeatureModule(
            id = "killaura",
            name = "KillAura",
            category = ModuleCategory.COMBAT,
            description = "Auto-attacks hostile entities and rivals within sphere radius.",
            isEnabled = false,
            hasSlider = true,
            sliderValue = 4.5f,
            sliderMin = 3.0f,
            sliderMax = 8.0f,
            sliderUnit = "m",
            nativeOffset = "0x0399CD80"
        ),
        FeatureModule(
            id = "reach",
            name = "Extended Reach",
            category = ModuleCategory.COMBAT,
            description = "Increases block interaction and hit distance without packet desync.",
            isEnabled = true,
            hasSlider = true,
            sliderValue = 6.5f,
            sliderMin = 3.0f,
            sliderMax = 12.0f,
            sliderUnit = "m",
            nativeOffset = "0x0381F4C0"
        ),
        FeatureModule(
            id = "antikb",
            name = "Velocity (Anti-KB)",
            category = ModuleCategory.COMBAT,
            description = "Cancels knockback impulse vectors received from swords, bows, and explosions.",
            isEnabled = true,
            hasSlider = true,
            sliderValue = 0f,
            sliderMin = 0f,
            sliderMax = 100f,
            sliderUnit = "% KB",
            nativeOffset = "0x0349A100"
        ),
        FeatureModule(
            id = "fly",
            name = "Air Jump / Fly",
            category = ModuleCategory.MOVEMENT,
            description = "Enables full 3D flight mechanics by overriding engine ground checks.",
            isEnabled = false,
            hasSlider = true,
            sliderValue = 2.0f,
            sliderMin = 1.0f,
            sliderMax = 5.0f,
            sliderUnit = "x speed",
            nativeOffset = "0x0321A840"
        ),
        FeatureModule(
            id = "speed",
            name = "Speed (BunnyHop)",
            category = ModuleCategory.MOVEMENT,
            description = "Accelerates ground friction and sprint velocity multiplier.",
            isEnabled = false,
            hasSlider = true,
            sliderValue = 2.5f,
            sliderMin = 1.0f,
            sliderMax = 6.0f,
            sliderUnit = "x sprint",
            nativeOffset = "0x032900F0"
        ),
        FeatureModule(
            id = "fullbright",
            name = "Fullbright (Max Gamma)",
            category = ModuleCategory.RENDER,
            description = "Overrides ambient lighting shaders to render 1000.0f daylight in all dimensions.",
            isEnabled = true,
            hasSlider = false,
            nativeOffset = "0x02B41AC4"
        ),
        FeatureModule(
            id = "nofog",
            name = "No Fog (Clear Sky)",
            category = ModuleCategory.RENDER,
            description = "Removes volumetric density fog, lava blindness, and distance haze.",
            isEnabled = true,
            hasSlider = false,
            nativeOffset = "0x01E49810"
        ),
        FeatureModule(
            id = "esp",
            name = "ESP Boxes & Tracers",
            category = ModuleCategory.RENDER,
            description = "Draws 3D bounding boxes and line tracers through walls around entities.",
            isEnabled = false,
            hasSlider = false
        ),
        FeatureModule(
            id = "xray",
            name = "X-Ray Ores",
            category = ModuleCategory.RENDER,
            description = "Filters terrain chunk rendering to only show Diamond, Netherite, and Ancient Debris.",
            isEnabled = false,
            hasSlider = true,
            sliderValue = 30f,
            sliderMin = 10f,
            sliderMax = 90f,
            sliderUnit = "% opacity"
        ),
        FeatureModule(
            id = "freecam",
            name = "Freecam & TP",
            category = ModuleCategory.WORLD,
            description = "Detaches camera for noclip scouting with one-tap player teleportation.",
            isEnabled = false,
            hasSlider = false
        )
    )
}
