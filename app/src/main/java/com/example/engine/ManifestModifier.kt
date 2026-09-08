package com.example.engine

object ManifestModifier {

    val defaultManifestXml = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.mojang.minecraftpe"
    android:versionCode="952631001"
    android:versionName="1.26.31.1">

    <uses-feature android:glEsVersion="0x00030000" android:required="true" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />

    <application
        android:name="com.mojang.minecraftpe.MCPEApplication"
        android:allowBackup="false"
        android:hasCode="true"
        android:icon="@mipmap/icon"
        android:label="Minecraft"
        android:extractNativeLibs="true">

        <activity
            android:name="com.mojang.minecraftpe.MainActivity"
            android:configChanges="orientation|keyboardHidden|screenSize"
            android:exported="true"
            android:screenOrientation="sensorLandscape"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>
</manifest>
    """.trimIndent()

    fun injectPermission(manifest: String, permissionName: String): Pair<String, Boolean> {
        if (manifest.contains(permissionName)) {
            return Pair(manifest, false) // already present
        }

        val tag = "    <uses-permission android:name=\"$permissionName\" />\n"
        val appTagIndex = manifest.indexOf("<application")
        if (appTagIndex == -1) return Pair(manifest, false)

        val updated = manifest.substring(0, appTagIndex) + tag + manifest.substring(appTagIndex)
        return Pair(updated, true)
    }

    fun injectOverlayService(manifest: String): Pair<String, Boolean> {
        if (manifest.contains("GlitchOverlayService")) {
            return Pair(manifest, false)
        }

        val serviceXml = """
        <!-- [GLITCH ENGINE INJECTED MOD MENU OVERLAY SERVICE] -->
        <service
            android:name="com.glitch.engine.GlitchOverlayService"
            android:enabled="true"
            android:exported="false"
            android:foregroundServiceType="specialUse" />

    </application>
        """.trimIndent()

        val updated = manifest.replace("</application>", serviceXml)
        return Pair(updated, true)
    }
}
