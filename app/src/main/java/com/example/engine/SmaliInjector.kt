package com.example.engine

import com.example.model.SmaliClassFile

object SmaliInjector {

    val sampleSmaliFiles = listOf(
        SmaliClassFile(
            dexName = "classes.dex",
            className = "MainActivity",
            packagePath = "com/mojang/minecraftpe/MainActivity.smali",
            methodsCount = 18,
            content = """
.class public Lcom/mojang/minecraftpe/MainActivity;
.super Landroid/app/NativeActivity;
.source "MainActivity.java"

# static fields
.field private static final TAG:Ljava/lang/String; = "MCPE_MainActivity"

# direct methods
.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Landroid/app/NativeActivity;-><init>()V
    return-void
.end method

.method protected onCreate(Landroid/os/Bundle;)V
    .registers 3
    .param p1, "savedInstanceState"

    invoke-super {p0, p1}, Landroid/app/NativeActivity;->onCreate(Landroid/os/Bundle;)V

    # Native engine loading
    const-string v0, "minecraftpe"
    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    return-void
.end method

.method protected onResume()V
    .registers 1
    invoke-super {p0}, Landroid/app/NativeActivity;->onResume()V
    return-void
.end method
            """.trimIndent(),
            isInjected = false
        ),
        SmaliClassFile(
            dexName = "classes2.dex",
            className = "MCPEApplication",
            packagePath = "com/mojang/minecraftpe/MCPEApplication.smali",
            methodsCount = 8,
            content = """
.class public Lcom/mojang/minecraftpe/MCPEApplication;
.super Landroid/app/Application;
.source "MCPEApplication.java"

.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Landroid/app/Application;-><init>()V
    return-void
.end method

.method public onCreate()V
    .registers 2
    invoke-super {p0}, Landroid/app/Application;->onCreate()V
    return-void
.end method
            """.trimIndent(),
            isInjected = false
        ),
        SmaliClassFile(
            dexName = "classes3.dex",
            className = "GlitchModService",
            packagePath = "com/glitch/engine/GlitchModService.smali",
            methodsCount = 6,
            content = """
.class public Lcom/glitch/engine/GlitchModService;
.super Landroid/app/Service;
.source "GlitchModService.smali"

# static fields
.field private static isLoaded:Z = 0x0

.method public static initMenu(Landroid/app/Activity;)V
    .registers 3
    .param p0, "activity"

    sget-boolean v0, Lcom/glitch/engine/GlitchModService;->isLoaded:Z
    if-nez v0, :cond_skip

    const/4 v0, 0x1
    sput-boolean v0, Lcom/glitch/engine/GlitchModService;->isLoaded:Z

    new-instance v1, Landroid/content/Intent;
    const-class v2, Lcom/glitch/engine/GlitchOverlayService;
    invoke-direct {v1, p0, v2}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V
    invoke-virtual {p0, v1}, Landroid/app/Activity;->startService(Landroid/content/Intent;)Landroid/content/ComponentName;

    :cond_skip
    return-void
.end method
            """.trimIndent(),
            isInjected = true
        )
    )

    fun injectModMenuHook(sourceSmali: String): Pair<String, Boolean> {
        val onCreateIdx = sourceSmali.indexOf(".method protected onCreate(Landroid/os/Bundle;)V")
        if (onCreateIdx == -1) return Pair(sourceSmali, false)

        val returnIdx = sourceSmali.indexOf("return-void", onCreateIdx)
        if (returnIdx == -1) return Pair(sourceSmali, false)

        // Register-safe bump: if .registers 3, bump to .registers 4 to prevent register clobbering
        val updatedSmali = sourceSmali.replaceFirst(
            ".registers 3",
            ".registers 4"
        ).replace(
            "return-void",
            """
    # [GLITCH ENGINE INJECTION START - Register-Safe Menu Loader]
    invoke-static {p0}, Lcom/glitch/engine/GlitchModService;->initMenu(Landroid/app/Activity;)V
    # [GLITCH ENGINE INJECTION END]

    return-void
            """.trimIndent()
        )

        return Pair(updatedSmali, true)
    }

    fun validateSyntax(code: String): List<String> {
        val errors = mutableListOf<String>()
        val lines = code.lines()
        var methodOpen = false
        var openLine = 0

        for ((idx, line) in lines.withIndex()) {
            val trimmed = line.trim()
            if (trimmed.startsWith(".method")) {
                if (methodOpen) {
                    errors.add("Line ${idx + 1}: Nested .method directive without closing previous method at line $openLine")
                }
                methodOpen = true
                openLine = idx + 1
            } else if (trimmed == ".end method") {
                if (!methodOpen) {
                    errors.add("Line ${idx + 1}: .end method without corresponding .method")
                }
                methodOpen = false
            }
        }

        if (methodOpen) {
            errors.add("Unclosed .method starting at line $openLine")
        }
        return errors
    }

    fun searchAcrossDex(query: String, files: List<SmaliClassFile>): List<Pair<SmaliClassFile, Int>> {
        val results = mutableListOf<Pair<SmaliClassFile, Int>>()
        for (file in files) {
            val count = file.content.split(query, ignoreCase = true).size - 1
            if (count > 0) {
                results.add(Pair(file, count))
            }
        }
        return results
    }
}
