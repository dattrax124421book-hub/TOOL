package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.ApkPackager
import com.example.engine.Arm64Assembler
import com.example.engine.HexEditorEngine
import com.example.engine.ManifestModifier
import com.example.engine.PythonRunner
import com.example.engine.SmaliInjector
import com.example.model.HexPatch
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Glitch Engine", appName)
    }

    @Test
    fun `test hex editor engine patch and undo`() {
        val engine = HexEditorEngine(0x0381F4C0L)
        val patch = HexPatch(
            id = "test_reach",
            offset = 0x0381F4C0L,
            originalBytes = "BD 04 00 12",
            patchedBytes = "00 00 20 52",
            description = "Test Patch",
            functionName = "Player::getReachDistance"
        )
        engine.applyPatch(patch)
        assertEquals(1, engine.getDiffList().size)

        val undone = engine.undo()
        assertNotNull(undone)
        assertEquals(0, engine.getDiffList().size)

        val redone = engine.redo()
        assertNotNull(redone)
        assertEquals(1, engine.getDiffList().size)
    }

    @Test
    fun `test arm64 assembler trampoline calculation`() {
        val instructions = Arm64Assembler.generateTrampoline(
            hookAddress = 0x0381F4C0L,
            caveAddress = 0x0381F4E8L,
            hookFunctionName = "Player::getReachDistance"
        )
        assertTrue(instructions.isNotEmpty())
        assertEquals(7, instructions.size)
        assertTrue(instructions.first().instruction.startsWith("B 0x0381F4E8"))
    }

    @Test
    fun `test smali injector registers bump`() {
        val originalSmali = """
.class public Lcom/mojang/minecraftpe/MainActivity;
.super Landroid/app/Activity;

.method protected onCreate(Landroid/os/Bundle;)V
    .registers 3
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V
    return-void
.end method
        """.trimIndent()

        val (injected, success) = SmaliInjector.injectModMenuHook(originalSmali)
        assertTrue(success)
        assertTrue(injected.contains(".registers 4"))
        assertTrue(injected.contains("Lcom/glitch/engine/GlitchModService;->initMenu"))
    }

    @Test
    fun `test manifest modifier permission injection`() {
        val manifest = "<manifest><application></application></manifest>"
        val (updated, ok) = ManifestModifier.injectPermission(manifest, "android.permission.SYSTEM_ALERT_WINDOW")
        assertTrue(ok)
        assertTrue(updated.contains("android.permission.SYSTEM_ALERT_WINDOW"))
    }

    @Test
    fun `test apk packager signature and clone package`() {
        val result = ApkPackager.rebuildAndSign(
            apkName = "mcpe.apk",
            isCloned = true,
            v1Enabled = true,
            v2Enabled = true,
            v3Enabled = true,
            patchCount = 3
        )
        assertTrue(result.success)
        assertTrue(result.log.contains("com.mojang.minecraftpe.glitch"))
        assertEquals(3, result.signaturesApplied.size)
    }

    @Test
    fun `test python runner automated patch`() {
        var applied = false
        val script = PythonRunner.builtInScripts.first()
        val log = PythonRunner.executeScript(script) {
            applied = true
        }
        assertTrue(applied)
        assertTrue(log.contains("SUCCESS"))
    }
}
