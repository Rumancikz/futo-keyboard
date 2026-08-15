package org.futo.inputmethod.latin.uix.settings.pages

import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.futo.inputmethod.latin.R
import org.futo.inputmethod.latin.uix.ANIMATE_BUBBLE
import org.futo.inputmethod.latin.uix.AUDIO_FOCUS
import org.futo.inputmethod.latin.uix.CAN_EXPAND_SPACE
import org.futo.inputmethod.latin.uix.DISALLOW_SYMBOLS
import org.futo.inputmethod.latin.uix.ENABLE_SOUND
import org.futo.inputmethod.latin.uix.PREFER_BLUETOOTH
import org.futo.inputmethod.latin.uix.USE_CHEEP_VOICE_INPUT
import org.futo.inputmethod.latin.uix.USE_PERSONAL_DICT
import org.futo.inputmethod.latin.uix.USE_SYSTEM_VOICE_INPUT
import org.futo.inputmethod.latin.uix.USE_VAD_AUTOSTOP
import org.futo.inputmethod.latin.uix.VERBOSE_PROGRESS
import org.futo.inputmethod.latin.uix.VOICE_INPUT_IME_ID
import org.futo.inputmethod.latin.uix.settings.DropDownPickerSettingItem
import org.futo.inputmethod.latin.uix.settings.NavigationItemStyle
import org.futo.inputmethod.latin.uix.settings.SettingItem
import org.futo.inputmethod.latin.uix.settings.UserSettingsMenu
import org.futo.inputmethod.latin.uix.settings.useDataStore
import org.futo.inputmethod.latin.uix.settings.useDataStoreValue
import org.futo.inputmethod.latin.uix.settings.userSettingDecorationOnly
import org.futo.inputmethod.latin.uix.settings.userSettingNavigationItem
import org.futo.inputmethod.latin.uix.settings.userSettingToggleDataStore

private val visibilityCheckBuiltinVoiceInput = @Composable {
    useDataStoreValue(USE_CHEEP_VOICE_INPUT) == false && useDataStoreValue(USE_SYSTEM_VOICE_INPUT) == false
}

val VoiceInputMenu = UserSettingsMenu(
    title = R.string.voice_input_settings_title,
    navPath = "voiceInput", registerNavPath = true,
    settings = listOf(
        userSettingToggleDataStore(
            title = R.string.voice_input_settings_use_cheep_board,
            subtitle = R.string.voice_input_settings_use_cheep_board_subtitle,
            setting = USE_CHEEP_VOICE_INPUT
        ),

        userSettingDecorationOnly {
            VoiceInputTargetKeyboardPicker()
        }.copy(visibilityCheck = { useDataStoreValue(USE_CHEEP_VOICE_INPUT) }),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_disable_builtin_voice_input,
            subtitle = R.string.voice_input_settings_disable_builtin_voice_input_subtitle,
            setting = USE_SYSTEM_VOICE_INPUT
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        //if(!systemVoiceInput.value) {
        userSettingToggleDataStore(
            title = R.string.voice_input_settings_indication_sounds,
            subtitle = R.string.voice_input_settings_indication_sounds_subtitle,
            setting = ENABLE_SOUND
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        /*
        userSettingToggleDataStore(
            title = R.string.voice_input_settings_verbose_progress,
            subtitle = R.string.voice_input_settings_verbose_progress_subtitle,
            setting = VERBOSE_PROGRESS
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),
         */

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_use_personal_dict,
            subtitle = R.string.voice_input_settings_use_personal_dict_subtitle,
            setting = USE_PERSONAL_DICT
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_use_bluetooth_mic,
            subtitle = R.string.voice_input_settings_use_bluetooth_mic_subtitle,
            setting = PREFER_BLUETOOTH
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_audio_focus,
            subtitle = R.string.voice_input_settings_audio_focus_subtitle,
            setting = AUDIO_FOCUS
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_suppress_symbols,
            setting = DISALLOW_SYMBOLS
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_long_form,
            subtitle = R.string.voice_input_settings_long_form_subtitle,
            setting = CAN_EXPAND_SPACE
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_autostop_vad,
            subtitle = R.string.voice_input_settings_autostop_vad_subtitle,
            setting = USE_VAD_AUTOSTOP
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        userSettingToggleDataStore(
            title = R.string.voice_input_settings_animate_bubble,
            subtitle = R.string.voice_input_settings_animate_bubble_subtitle,
            setting = ANIMATE_BUBBLE
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),

        userSettingNavigationItem(
            title = R.string.voice_input_settings_change_models,
            subtitle = R.string.voice_input_settings_change_models_subtitle,
            style = NavigationItemStyle.Misc,
            navigateTo = "languages"
        ).copy(visibilityCheck = visibilityCheckBuiltinVoiceInput),
        //}
    )
)

@Composable
private fun VoiceInputTargetKeyboardPicker() {
    val context = LocalContext.current
    val inputMethodManager = context.getSystemService(InputMethodManager::class.java)
    val keyboards = remember {
        inputMethodManager.enabledInputMethodList
            .filter { it.packageName != context.packageName }
    }
    val (targetImeId, setTargetImeId) = useDataStore(VOICE_INPUT_IME_ID)

    if (keyboards.isEmpty()) {
        SettingItem(
            title = stringResource(R.string.voice_input_settings_target_keyboard),
            subtitle = stringResource(R.string.voice_input_settings_target_keyboard_none),
        ) {}
        return
    }

    val selection = if (keyboards.any { it.id == targetImeId }) {
        targetImeId
    } else {
        keyboards.first().id
    }
    DropDownPickerSettingItem(
        label = stringResource(R.string.voice_input_settings_target_keyboard),
        options = keyboards.map { it.id },
        selection = selection,
        onSet = { setTargetImeId(it) },
        getDisplayName = { id ->
            keyboards.firstOrNull { it.id == id }?.loadLabel(context.packageManager)?.toString() ?: id
        }
    )
}