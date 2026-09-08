package com.example.engine

data class AsmInstruction(
    val addressHex: String,
    val hexBytes: String,
    val mnemonic: String,
    val operands: String,
    val note: String = "",
    val address: Long = try { java.lang.Long.decode(addressHex) } catch (e: Exception) { 0L },
    val instruction: String = if (operands.isEmpty()) mnemonic else "$mnemonic $operands",
    val opcodeHex: String = hexBytes,
    val comment: String = note
)

object Arm64Assembler {
    const val NOP_HEX = "1F 20 03 D5"
    const val RET_HEX = "C0 03 5F D6"

    fun encodeNop(): String = NOP_HEX
    fun encodeRet(): String = RET_HEX

    fun encodeMovW0(value: Int): String {
        // MOV W0, #value
        val imm16 = value and 0xFFFF
        val hw = 0
        val inst = 0x52800000 or (hw shl 21) or (imm16 shl 5) or 0
        return formatHex32(inst)
    }

    fun encodeBranch(fromAddress: Long, toAddress: Long): String {
        val diff = (toAddress - fromAddress) / 4
        val imm26 = (diff and 0x03FFFFFF).toInt()
        val inst = 0x14000000 or imm26
        return formatHex32(inst)
    }

    fun encodeBranchLink(fromAddress: Long, toAddress: Long): String {
        val diff = (toAddress - fromAddress) / 4
        val imm26 = (diff and 0x03FFFFFF).toInt()
        val inst = 0x94000000.toInt() or imm26
        return formatHex32(inst)
    }

    private fun formatHex32(value: Int): String {
        val b0 = value and 0xFF
        val b1 = (value shr 8) and 0xFF
        val b2 = (value shr 16) and 0xFF
        val b3 = (value shr 24) and 0xFF
        return String.format("%02X %02X %02X %02X", b0, b1, b2, b3)
    }

    fun generateTrampoline(
        hookAddress: Long,
        caveAddress: Long,
        hookFunctionName: String
    ): List<AsmInstruction> {
        val branchToCave = encodeBranch(hookAddress, caveAddress)
        return listOf(
            AsmInstruction(
                addressHex = String.format("0x%08X", hookAddress),
                hexBytes = branchToCave,
                mnemonic = "B",
                operands = String.format("0x%08X", caveAddress),
                note = "Trampoline branch into code-cave"
            ),
            AsmInstruction(
                addressHex = String.format("0x%08X", hookAddress + 4),
                hexBytes = NOP_HEX,
                mnemonic = "NOP",
                operands = "",
                note = "Alignment padding"
            ),
            AsmInstruction(
                addressHex = String.format("0x%08X", caveAddress),
                hexBytes = "FD 7B BE A9",
                mnemonic = "STP",
                operands = "X29, X30, [SP, #-32]!",
                note = "Save frame pointer & link register"
            ),
            AsmInstruction(
                addressHex = String.format("0x%08X", caveAddress + 4),
                hexBytes = "FD 03 00 91",
                mnemonic = "MOV",
                operands = "X29, SP",
                note = "Setup frame pointer"
            ),
            AsmInstruction(
                addressHex = String.format("0x%08X", caveAddress + 8),
                hexBytes = "E0 03 1F AA",
                mnemonic = "MOV",
                operands = "X0, XZR",
                note = "Mod override logic ($hookFunctionName)"
            ),
            AsmInstruction(
                addressHex = String.format("0x%08X", caveAddress + 12),
                hexBytes = "FD 7B C2 A8",
                mnemonic = "LDP",
                operands = "X29, X30, [SP], #32",
                note = "Restore registers"
            ),
            AsmInstruction(
                addressHex = String.format("0x%08X", caveAddress + 16),
                hexBytes = encodeBranch(caveAddress + 16, hookAddress + 8),
                mnemonic = "B",
                operands = String.format("0x%08X", hookAddress + 8),
                note = "Return to engine pipeline"
            )
        )
    }

    fun parseHexBytes(hexStr: String): ByteArray {
        val clean = hexStr.replace(" ", "").replace("0x", "").uppercase()
        val result = ByteArray(clean.length / 2)
        for (i in 0 until clean.length step 2) {
            val byteStr = clean.substring(i, i + 2)
            result[i / 2] = byteStr.toInt(16).toByte()
        }
        return result
    }

    fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString(" ") { String.format("%02X", it) }
    }
}
