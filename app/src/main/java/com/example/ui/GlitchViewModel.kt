package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FeatureModuleRepository
import com.example.data.McpeDatabase
import com.example.data.ProjectRepository
import com.example.engine.ApkPackager
import com.example.engine.Arm64Assembler
import com.example.engine.AsmInstruction
import com.example.engine.HexEditorEngine
import com.example.engine.HexRow
import com.example.engine.ManifestModifier
import com.example.engine.PythonRunner
import com.example.engine.SearchResult
import com.example.engine.SmaliInjector
import com.example.model.ApkProject
import com.example.model.FeatureModule
import com.example.model.HexPatch
import com.example.model.HookTemplate
import com.example.model.LogcatEntry
import com.example.model.McpeOffsetInfo
import com.example.model.ModMenuConfig
import com.example.model.PythonScript
import com.example.model.SmaliClassFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GlitchUiState(
    val currentTab: GlitchNavTab = GlitchNavTab.WORKSPACE,
    val projects: List<ApkProject> = ProjectRepository.initialProjects,
    val activeProject: ApkProject = ProjectRepository.initialProjects.first(),
    val selectedMcpeVersion: String = "1.26.31.1",
    val knownOffsets: List<McpeOffsetInfo> = emptyList(),

    // Hex Editor State
    val hexBaseOffset: Long = 0x0381F4C0L,
    val hexRows: List<HexRow> = emptyList(),
    val hexPatches: List<HexPatch> = emptyList(),
    val hexSearchQuery: String = "BD 04 00 12",
    val hexSearchResults: List<SearchResult> = emptyList(),
    val isDiffViewActive: Boolean = false,

    // Smali State
    val smaliFiles: List<SmaliClassFile> = SmaliInjector.sampleSmaliFiles,
    val activeSmaliFile: SmaliClassFile = SmaliInjector.sampleSmaliFiles.first(),
    val smaliEditorText: String = SmaliInjector.sampleSmaliFiles.first().content,
    val smaliSearchQuery: String = "MainActivity",
    val smaliSearchResults: List<Pair<SmaliClassFile, Int>> = emptyList(),
    val smaliErrors: List<String> = emptyList(),
    val showInjectionDialog: Boolean = false,

    // Manifest State
    val manifestContent: String = ManifestModifier.defaultManifestXml,
    val injectedPermissions: List<String> = listOf("INTERNET", "ACCESS_NETWORK_STATE"),

    // Native Hook Framework
    val hookTargetFunction: String = "Player::getReachDistance",
    val hookAddressHex: String = "0x0381F4C0",
    val hookCaveAddressHex: String = "0x0381F4E8",
    val generatedTrampoline: List<AsmInstruction> = emptyList(),
    val isLiveTestActive: Boolean = false,
    val liveHookInterceptionCount: Int = 142,

    // Mod Menu Builder
    val modMenuConfig: ModMenuConfig = ModMenuConfig(),
    val isOverlayMenuOpen: Boolean = true,
    val activeMenuTab: String = "COMBAT",
    val generatedInjectionCode: String = "",

    // Feature Modules
    val modules: List<FeatureModule> = FeatureModuleRepository.getDefaultModules(),

    // Python Scripting
    val scripts: List<PythonScript> = PythonRunner.builtInScripts,
    val activeScript: PythonScript = PythonRunner.builtInScripts.first(),
    val scriptCode: String = PythonRunner.builtInScripts.first().code,
    val consoleOutput: String = ">>> Glitch Engine Python 3.12 Console Ready.\n>>> Select a script and tap Run to automate MCPE patches.",

    // Packaging & Signing
    val v1Signer: Boolean = true,
    val v2Signer: Boolean = true,
    val v3Signer: Boolean = true,
    val clonePackage: Boolean = true,
    val signingStatus: String = "Ready to Rebuild & Sign",
    val signingLogs: String = "",
    val logcatEntries: List<LogcatEntry> = ProjectRepository.getSampleLogcat(),
    val logcatFilter: String = "ALL",

    // Banner message
    val userMessage: String? = "Glitch Engine Loaded: MCPE 1.26.31.1 (arm64-v8a) ready for modding."
)

class GlitchViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GlitchUiState())
    val uiState: StateFlow<GlitchUiState> = _uiState.asStateFlow()

    private val hexEngine = HexEditorEngine(0x0381F4C0L)

    init {
        loadVersionProfile("1.26.31.1")
        refreshHexRows(0x0381F4C0L)
        generateHookInstructions(0x0381F4C0L, 0x0381F4E8L, "Player::getReachDistance")
        generateMenuCode()
    }

    fun selectTab(tab: GlitchNavTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    fun showMessage(msg: String) {
        _uiState.update { it.copy(userMessage = msg) }
    }

    // --- WORKSPACE ACTIONS ---
    fun selectProject(project: ApkProject) {
        _uiState.update { it.copy(activeProject = project) }
        loadVersionProfile(project.mcpeVersion)
        showMessage("Switched to project: ${project.name}")
    }

    fun loadVersionProfile(version: String) {
        val profile = McpeDatabase.getProfile(version)
        _uiState.update {
            it.copy(
                selectedMcpeVersion = version,
                knownOffsets = profile.knownOffsets
            )
        }
    }

    fun createBackup() {
        _uiState.update { state ->
            val updated = state.activeProject.copy(backupCreated = true)
            state.copy(
                activeProject = updated,
                userMessage = "Auto-backup created for ${updated.name} (Original APK preserved)"
            )
        }
    }

    fun restoreBackup() {
        _uiState.update { state ->
            state.copy(
                userMessage = "Project restored to clean original state from backup."
            )
        }
    }

    // --- HEX EDITOR ACTIONS ---
    fun refreshHexRows(offset: Long) {
        val rows = hexEngine.getWindowRows(offset, 18)
        _uiState.update {
            it.copy(
                hexBaseOffset = offset,
                hexRows = rows,
                hexPatches = hexEngine.getDiffList()
            )
        }
    }

    fun jumpToOffset(offsetHex: String) {
        try {
            val clean = offsetHex.replace("0x", "").trim()
            val parsed = clean.toLong(16)
            refreshHexRows(parsed)
            showMessage("Jumped to offset 0x${clean.uppercase()}")
        } catch (e: Exception) {
            showMessage("Invalid hex offset format.")
        }
    }

    fun applyHexPatch(patch: HexPatch) {
        hexEngine.applyPatch(patch)
        refreshHexRows(_uiState.value.hexBaseOffset)
        showMessage("Applied patch at 0x${java.lang.Long.toHexString(patch.offset).uppercase()}: ${patch.description}")
    }

    fun undoHexPatch() {
        val undone = hexEngine.undo()
        if (undone != null) {
            refreshHexRows(_uiState.value.hexBaseOffset)
            showMessage("Undid patch at 0x${java.lang.Long.toHexString(undone.offset).uppercase()}")
        } else {
            showMessage("No patches to undo.")
        }
    }

    fun redoHexPatch() {
        val redone = hexEngine.redo()
        if (redone != null) {
            refreshHexRows(_uiState.value.hexBaseOffset)
            showMessage("Redid patch: ${redone.description}")
        } else {
            showMessage("No patches to redo.")
        }
    }

    fun searchHexPattern(pattern: String) {
        val results = hexEngine.searchPattern(pattern)
        _uiState.update {
            it.copy(
                hexSearchQuery = pattern,
                hexSearchResults = results
            )
        }
        showMessage("Found ${results.size} match(es) for '$pattern'")
    }

    fun toggleDiffView() {
        _uiState.update { it.copy(isDiffViewActive = !it.isDiffViewActive) }
    }

    // --- SMALI ACTIONS ---
    fun selectSmaliFile(file: SmaliClassFile) {
        _uiState.update {
            it.copy(
                activeSmaliFile = file,
                smaliEditorText = file.content,
                smaliErrors = emptyList()
            )
        }
    }

    fun updateSmaliText(newText: String) {
        _uiState.update { it.copy(smaliEditorText = newText) }
    }

    fun checkSmaliSyntax() {
        val errors = SmaliInjector.validateSyntax(_uiState.value.smaliEditorText)
        _uiState.update { it.copy(smaliErrors = errors) }
        if (errors.isEmpty()) {
            showMessage("Smali syntax check PASSED. 0 errors.")
        } else {
            showMessage("Syntax check failed with ${errors.size} error(s).")
        }
    }

    fun autoInjectModMenu() {
        val (injectedSmali, success) = SmaliInjector.injectModMenuHook(_uiState.value.smaliEditorText)
        if (success) {
            _uiState.update {
                it.copy(
                    smaliEditorText = injectedSmali,
                    userMessage = "Register-safe Mod Menu Hook injected into onCreate()!"
                )
            }
        } else {
            showMessage("Could not find suitable injection anchor in this smali file.")
        }
    }

    fun searchSmaliAcrossDex(query: String) {
        val results = SmaliInjector.searchAcrossDex(query, _uiState.value.smaliFiles)
        _uiState.update {
            it.copy(
                smaliSearchQuery = query,
                smaliSearchResults = results
            )
        }
    }

    // --- MANIFEST ACTIONS ---
    fun updateManifestText(newXml: String) {
        _uiState.update { it.copy(manifestContent = newXml) }
    }

    fun injectPermission(permission: String) {
        val (updatedXml, injected) = ManifestModifier.injectPermission(_uiState.value.manifestContent, permission)
        if (injected) {
            val list = _uiState.value.injectedPermissions.toMutableList()
            val shortName = permission.substringAfterLast(".")
            if (!list.contains(shortName)) list.add(shortName)
            _uiState.update {
                it.copy(
                    manifestContent = updatedXml,
                    injectedPermissions = list,
                    userMessage = "Injected permission: $permission"
                )
            }
        } else {
            showMessage("Permission $permission already present in manifest.")
        }
    }

    fun injectOverlayService() {
        val (updated, ok) = ManifestModifier.injectOverlayService(_uiState.value.manifestContent)
        if (ok) {
            _uiState.update {
                it.copy(
                    manifestContent = updated,
                    userMessage = "GlitchOverlayService injected into AndroidManifest.xml!"
                )
            }
        } else {
            showMessage("GlitchOverlayService already declared in manifest.")
        }
    }

    // --- HOOK ACTIONS ---
    fun generateHookInstructions(hookAddr: Long, caveAddr: Long, targetFunc: String) {
        val asm = Arm64Assembler.generateTrampoline(hookAddr, caveAddr, targetFunc)
        _uiState.update {
            it.copy(
                hookTargetFunction = targetFunc,
                hookAddressHex = String.format("0x%08X", hookAddr),
                hookCaveAddressHex = String.format("0x%08X", caveAddr),
                generatedTrampoline = asm
            )
        }
    }

    fun toggleLiveHookTest() {
        _uiState.update {
            val newState = !it.isLiveTestActive
            it.copy(
                isLiveTestActive = newState,
                userMessage = if (newState) "Live Frida-style hook attached to MCPE process (Test Mode Active)"
                else "Live test detached. Engine normal."
            )
        }
    }

    // --- MOD MENU BUILDER ACTIONS ---
    fun updateModMenuConfig(config: ModMenuConfig) {
        _uiState.update { it.copy(modMenuConfig = config) }
        generateMenuCode()
    }

    fun setOverlayMenuOpen(open: Boolean) {
        _uiState.update { it.copy(isOverlayMenuOpen = open) }
    }

    fun setActiveMenuTab(tab: String) {
        _uiState.update { it.copy(activeMenuTab = tab) }
    }

    fun updateButtonPosition(x: Float, y: Float) {
        _uiState.update {
            it.copy(
                modMenuConfig = it.modMenuConfig.copy(
                    buttonPositionX = x,
                    buttonPositionY = y
                )
            )
        }
    }

    private fun generateMenuCode() {
        val cfg = _uiState.value.modMenuConfig
        val code = """
// Auto-Generated Glitch Engine Overlay Service & Bridge
package com.glitch.engine;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

public class GlitchOverlayService extends Service {
    private WindowManager windowManager;
    private GlitchFloatingView floatingView;

    @Override
    public void onCreate() {
        super.onCreate();
        // Title: "${cfg.title}" | Accent: ${cfg.accentColor}
        // Draggable HUD coords=${cfg.hudCoords}, fps=${cfg.hudFps}
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingView = new GlitchFloatingView(this, "${cfg.title}", "${cfg.accentColor}");
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = ${(cfg.buttonPositionX).toInt()};
        params.y = ${(cfg.buttonPositionY).toInt()};
        windowManager.addView(floatingView, params);
    }
}
        """.trimIndent()
        _uiState.update { it.copy(generatedInjectionCode = code) }
    }

    // --- FEATURE MODULES ACTIONS ---
    fun toggleModule(moduleId: String) {
        val updated = _uiState.value.modules.map { mod ->
            if (mod.id == moduleId) {
                val newEnabled = !mod.isEnabled
                // If native offset exists, auto-sync with hex patcher
                if (mod.nativeOffset.isNotEmpty()) {
                    if (newEnabled) {
                        applyHexPatch(
                            HexPatch(
                                id = "mod_${mod.id}",
                                offset = java.lang.Long.decode(mod.nativeOffset),
                                originalBytes = "BD 04 00 12",
                                patchedBytes = "00 00 20 52 C0 03 5F D6",
                                description = "${mod.name} Module Override",
                                functionName = mod.name
                            )
                        )
                    }
                }
                mod.copy(isEnabled = newEnabled)
            } else mod
        }
        _uiState.update { it.copy(modules = updated) }
    }

    fun updateModuleSlider(moduleId: String, value: Float) {
        val updated = _uiState.value.modules.map { mod ->
            if (mod.id == moduleId) mod.copy(sliderValue = value) else mod
        }
        _uiState.update { it.copy(modules = updated) }
    }

    // --- PYTHON SCRIPTING ACTIONS ---
    fun selectScript(script: PythonScript) {
        _uiState.update {
            it.copy(
                activeScript = script,
                scriptCode = script.code
            )
        }
    }

    fun updateScriptCode(code: String) {
        _uiState.update { it.copy(scriptCode = code) }
    }

    fun runPythonScript() {
        val script = _uiState.value.activeScript.copy(code = _uiState.value.scriptCode)
        val result = PythonRunner.executeScript(script) { newPatch ->
            hexEngine.applyPatch(newPatch)
        }
        refreshHexRows(_uiState.value.hexBaseOffset)
        _uiState.update {
            it.copy(
                consoleOutput = result,
                userMessage = "Executed Python script: ${script.name}"
            )
        }
    }

    // --- PACKAGING & RE-SIGNING ---
    fun setV1Signer(enabled: Boolean) { _uiState.update { it.copy(v1Signer = enabled) } }
    fun setV2Signer(enabled: Boolean) { _uiState.update { it.copy(v2Signer = enabled) } }
    fun setV3Signer(enabled: Boolean) { _uiState.update { it.copy(v3Signer = enabled) } }
    fun setClonePackage(enabled: Boolean) { _uiState.update { it.copy(clonePackage = enabled) } }
    fun setLogcatFilter(filter: String) { _uiState.update { it.copy(logcatFilter = filter) } }

    fun buildAndSignApk() {
        val state = _uiState.value
        val res = ApkPackager.rebuildAndSign(
            apkName = state.activeProject.apkFileName,
            isCloned = state.clonePackage,
            v1Enabled = state.v1Signer,
            v2Enabled = state.v2Signer,
            v3Enabled = state.v3Signer,
            patchCount = state.hexPatches.size
        )
        _uiState.update {
            it.copy(
                signingStatus = "SUCCESS: ${res.outputApkName} is Signed & Ready",
                signingLogs = res.log,
                userMessage = "APK Rebuilt & Signed: ${res.outputApkName}"
            )
        }
    }
}
