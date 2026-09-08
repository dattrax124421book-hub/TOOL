package com.example.data

import com.example.model.McpeOffsetInfo
import com.example.model.McpeVersionProfile
import com.example.model.ModuleCategory

object McpeDatabase {
    val profiles = listOf(
        McpeVersionProfile(
            version = "1.26.31.1",
            releaseDate = "2026",
            targetArch = "arm64-v8a",
            libraryName = "libminecraftpe.so",
            knownOffsets = listOf(
                McpeOffsetInfo(
                    id = "reach_126",
                    functionName = "Player::getReachDistance",
                    demangledSymbol = "_ZNK6Player16getReachDistanceEv",
                    offsetHex = "0x0381F4C0",
                    originalHex = "BD 04 00 12",
                    patchedHex = "00 00 20 52 C0 03 5F D6",
                    description = "Sets block breaking and entity interaction reach to custom extended distance (default 7.5m / 10m).",
                    category = ModuleCategory.COMBAT
                ),
                McpeOffsetInfo(
                    id = "fullbright_126",
                    functionName = "Brightness::getEffective",
                    demangledSymbol = "_ZNK10Brightness12getEffectiveEv",
                    offsetHex = "0x02B41AC4",
                    originalHex = "E0 03 00 32",
                    patchedHex = "00 00 80 52 C0 03 5F D6",
                    description = "Forces maximum gamma rendering across deep caves, nether, and underwater without torch light.",
                    category = ModuleCategory.RENDER
                ),
                McpeOffsetInfo(
                    id = "nofog_126",
                    functionName = "FogColorHandler::applyFog",
                    demangledSymbol = "_ZN15FogColorHandler10applyFogEv",
                    offsetHex = "0x01E49810",
                    originalHex = "FD 7B BE A9",
                    patchedHex = "1F 20 03 D5 1F 20 03 D5",
                    description = "NOPs distance & lava fog generation rendering crystal clear skies at infinite chunks.",
                    category = ModuleCategory.RENDER
                ),
                McpeOffsetInfo(
                    id = "antikb_126",
                    functionName = "Actor::applyImpulse",
                    demangledSymbol = "_ZN5Actor14applyImpulseEv",
                    offsetHex = "0x0349A100",
                    originalHex = "FD 7B BA A9",
                    patchedHex = "C0 03 5F D6",
                    description = "Instantly returns from knockback impulse calculation (0% horizontal & vertical velocity).",
                    category = ModuleCategory.COMBAT
                ),
                McpeOffsetInfo(
                    id = "fly_126",
                    functionName = "Actor::isInWater",
                    demangledSymbol = "_ZNK5Actor8isInWaterEv",
                    offsetHex = "0x0321A840",
                    originalHex = "E0 03 1F 2A",
                    patchedHex = "20 00 80 52 C0 03 5F D6",
                    description = "Triggers infinite 3D swimming/flying propulsion physics anywhere in the world.",
                    category = ModuleCategory.MOVEMENT
                ),
                McpeOffsetInfo(
                    id = "speed_126",
                    functionName = "Actor::setSpeed",
                    demangledSymbol = "_ZN5Actor8setSpeedEf",
                    offsetHex = "0x032900F0",
                    originalHex = "FD 7B BF A9",
                    patchedHex = "00 10 2E 1E C0 03 5F D6",
                    description = "Hooks movement velocity multiplier to maintain 2.5x to 5.0x sprint speed.",
                    category = ModuleCategory.MOVEMENT
                ),
                McpeOffsetInfo(
                    id = "killaura_126",
                    functionName = "GameMode::attack",
                    demangledSymbol = "_ZN8GameMode6attackER5Actor",
                    offsetHex = "0x0399CD80",
                    originalHex = "FD 7B 01 A9",
                    patchedHex = "E0 03 00 AA 1F 20 03 D5",
                    description = "Auto-targets and sends attack packets to closest valid hostiles within radius.",
                    category = ModuleCategory.COMBAT
                )
            )
        ),
        McpeVersionProfile(
            version = "1.25.10",
            releaseDate = "2025",
            targetArch = "arm64-v8a",
            libraryName = "libminecraftpe.so",
            knownOffsets = listOf(
                McpeOffsetInfo(
                    id = "reach_125",
                    functionName = "Player::getReachDistance",
                    demangledSymbol = "_ZNK6Player16getReachDistanceEv",
                    offsetHex = "0x03741080",
                    originalHex = "BC 04 00 12",
                    patchedHex = "00 00 20 52 C0 03 5F D6",
                    description = "Extended entity interaction range.",
                    category = ModuleCategory.COMBAT
                ),
                McpeOffsetInfo(
                    id = "fullbright_125",
                    functionName = "Brightness::getEffective",
                    demangledSymbol = "_ZNK10Brightness12getEffectiveEv",
                    offsetHex = "0x02A900A0",
                    originalHex = "E0 03 00 32",
                    patchedHex = "00 00 80 52 C0 03 5F D6",
                    description = "Maximum ambient luminance override.",
                    category = ModuleCategory.RENDER
                )
            )
        ),
        McpeVersionProfile(
            version = "1.21.50",
            releaseDate = "2024",
            targetArch = "arm64-v8a",
            libraryName = "libminecraftpe.so",
            knownOffsets = listOf(
                McpeOffsetInfo(
                    id = "reach_121",
                    functionName = "Player::getReachDistance",
                    demangledSymbol = "_ZNK6Player16getReachDistanceEv",
                    offsetHex = "0x0352A900",
                    originalHex = "B8 04 00 12",
                    patchedHex = "00 00 20 52 C0 03 5F D6",
                    description = "Extended entity interaction reach.",
                    category = ModuleCategory.COMBAT
                )
            )
        )
    )

    fun getProfile(version: String): McpeVersionProfile {
        return profiles.find { it.version == version } ?: profiles.first()
    }
}
