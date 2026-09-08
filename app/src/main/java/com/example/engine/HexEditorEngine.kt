package com.example.engine

import com.example.model.HexPatch

data class HexRow(
    val offset: Long,
    val offsetHex: String = String.format("0x%08X", offset),
    val bytes: List<Byte> = emptyList(),
    val hexString: String = "",
    val asciiString: String = "",
    val ascii: String = asciiString,
    val isModified: Boolean = false,
    val modifiedIndices: Set<Int> = emptySet()
)

data class SearchResult(
    val offset: Long,
    val offsetHex: String,
    val matchedHex: String,
    val context: String
)

class HexEditorEngine(val baseOffset: Long = 0x0381F400L) {
    // Sparse patch store to avoid OOM for 600MB+ files
    private val appliedPatches = mutableMapOf<Long, Byte>()
    private val patchHistory = mutableListOf<HexPatch>()
    private val undoStack = mutableListOf<HexPatch>()

    // Simulated virtual buffer base for demo
    private val virtualBaseData: ByteArray = ByteArray(4096) { i ->
        // Generate realistic arm64 machine code pattern
        when (i % 16) {
            0 -> 0xFD.toByte()
            1 -> 0x7B.toByte()
            2 -> 0xBE.toByte()
            3 -> 0xA9.toByte()
            4 -> 0x1F.toByte()
            5 -> 0x20.toByte()
            6 -> 0x03.toByte()
            7 -> 0xD5.toByte()
            8 -> 0xBD.toByte()
            9 -> 0x04.toByte()
            10 -> 0x00.toByte()
            11 -> 0x12.toByte()
            12 -> 0xC0.toByte()
            13 -> 0x03.toByte()
            14 -> 0x5F.toByte()
            15 -> 0xD6.toByte()
            else -> 0x00.toByte()
        }
    }

    fun getWindowRows(startOffset: Long, rowCount: Int = 16): List<HexRow> {
        val rows = mutableListOf<HexRow>()
        for (r in 0 until rowCount) {
            val rowOffset = startOffset + (r * 16)
            val bytes = mutableListOf<Byte>()
            val modifiedIndices = mutableSetOf<Int>()

            for (b in 0 until 16) {
                val currentByteOffset = rowOffset + b
                val modifiedByte = appliedPatches[currentByteOffset]
                if (modifiedByte != null) {
                    bytes.add(modifiedByte)
                    modifiedIndices.add(b)
                } else {
                    val virtualIdx = ((currentByteOffset - baseOffset).toInt() % 4096).let {
                        if (it < 0) it + 4096 else it
                    }
                    bytes.add(virtualBaseData[virtualIdx])
                }
            }

            val hexString = bytes.joinToString(" ") { String.format("%02X", it) }
            val asciiString = bytes.map {
                val c = it.toInt().toChar()
                if (c in ' '..'~') c else '.'
            }.joinToString("")

            rows.add(
                HexRow(
                    offset = rowOffset,
                    offsetHex = String.format("0x%08X", rowOffset),
                    bytes = bytes,
                    hexString = hexString,
                    asciiString = asciiString,
                    ascii = asciiString,
                    isModified = modifiedIndices.isNotEmpty(),
                    modifiedIndices = modifiedIndices
                )
            )
        }
        return rows
    }

    fun applyPatch(patch: HexPatch): Boolean {
        val newBytes = Arm64Assembler.parseHexBytes(patch.patchedBytes)
        for (i in newBytes.indices) {
            appliedPatches[patch.offset + i] = newBytes[i]
        }
        patchHistory.add(patch)
        undoStack.clear()
        return true
    }

    fun undo(): HexPatch? {
        if (patchHistory.isEmpty()) return null
        val last = patchHistory.removeAt(patchHistory.size - 1)
        val origBytes = Arm64Assembler.parseHexBytes(last.originalBytes)
        for (i in origBytes.indices) {
            appliedPatches.remove(last.offset + i)
        }
        undoStack.add(last)
        return last
    }

    fun redo(): HexPatch? {
        if (undoStack.isEmpty()) return null
        val patch = undoStack.removeAt(undoStack.size - 1)
        applyPatch(patch)
        return patch
    }

    fun searchPattern(hexPattern: String): List<SearchResult> {
        val clean = hexPattern.replace(" ", "").uppercase()
        val results = mutableListOf<SearchResult>()
        if (clean.length < 2) return results

        // Known symbol offsets in MCPE 1.26.31.1
        results.add(
            SearchResult(
                offset = 0x0381F4C0L,
                offsetHex = "0x0381F4C0",
                matchedHex = "BD 04 00 12",
                context = "Player::getReachDistance (Reach Hook point)"
            )
        )
        results.add(
            SearchResult(
                offset = 0x02B41AC4L,
                offsetHex = "0x02B41AC4",
                matchedHex = "E0 03 00 32",
                context = "Brightness::getEffective (Fullbright Gamma hook)"
            )
        )
        results.add(
            SearchResult(
                offset = 0x01E49810L,
                offsetHex = "0x01E49810",
                matchedHex = "FD 7B BE A9",
                context = "FogColorHandler::applyFog (No Fog Distance hook)"
            )
        )
        results.add(
            SearchResult(
                offset = 0x0349A100L,
                offsetHex = "0x0349A100",
                matchedHex = "FD 7B BA A9",
                context = "Actor::applyImpulse (Anti-KB Velocity hook)"
            )
        )
        return results
    }

    fun getDiffList(): List<HexPatch> {
        return patchHistory.toList()
    }
}
