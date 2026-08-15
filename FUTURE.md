# FUTURE

Ideas and observations collected while wiring the microphone key to the cheep-board STT keyboard (see `CheepBoardAction` in `java/src/org/futo/inputmethod/latin/uix/actions/VoiceInputAction.kt`). Deliberately NOT changed here — candidates for follow-up work.
- **`tools/make-keyboard-text-py/src/generate.py` needed explicit `encoding="utf-8"`** to run on Windows (default cp1252 crashes on the non-ASCII locale JSONs; exit 1 in `:updateLocales`). Fixed locally — if this ever gets rebased onto upstream, keep the fix (or push it upstream). Also, the task calls bare `python` on Windows; if that resolves to Python 2 the script's f-strings will also fail — `py -3` or a Python 3 install fixes that.

## Voice input / action system

- **Generalize the cheep routing.** `CHEEP_BOARD_IME_ID` is hard-coded in `VoiceInputAction.kt`. A setting that lets the user pick *any* enabled IME as the voice-routing target (reusing `InputMethodManager.getEnabledInputMethodList()`) would replace the hard-coded id and the `USE_CHEEP_VOICE_INPUT` boolean. The language switcher (`Subtypes.kt` `LanguageSwitcherDialog`) already has the IME list UI to borrow from.
- **Dead legacy voice-key code.** `SettingsValues.mShowsVoiceInputKey` is hard-coded `true` (`settings/SettingsValues.java:169`) and never read; `needsToShowVoiceInputKey()` migrates `PREF_VOICE_INPUT_KEY` but its result is unused; `MainKeyboardView.updateShortcutKey()` is a no-op marked `// TODO: Remove` (`keyboard/MainKeyboardView.java:774-776`); obsolete prefs `PREF_VOICE_MODE_OBSOLETE` / `PREF_VOICE_INPUT_KEY` (`settings/Settings.java:56-58`).
- **Action key-code block has finite capacity.** `CODE_ACTION_0..CODE_ACTION_MAX` provides 100 slots (`common/src/org/futo/inputmethod/latin/common/Constants.java:260-261`); 24 actions are defined today. New actions are appended at the end of `AllActionsMap` (indices must stay stable for saved user key bindings) — beyond 100 actions the range needs extending.

## IME switching

- **`RichInputMethodManager` uses deprecated API.** `switchToTargetIME()` calls `InputMethodManager.setInputMethodAndSubtype(...)` (`RichInputMethodManager.java:328`), which is deprecated in favor of `InputMethodService.switchInputMethod(String)`. The "shortcut IME" feature also has unresolved TODOs (icon, multi-shortcut selection, subtype picking).
- **Unguarded `switchInputMethod` in the language switcher.** `UixManager.showLanguageSwitcher` (`uix/UixManager.kt:~942`) calls `latinIME.switchInputMethod(it.id)` without try/catch — if the IME is uninstalled in the meantime this throws `IllegalArgumentException` and crashes the IME window.
- **Flavor-specific IME ids.** `applicationId` suffixes (`.unstable` default build, `.playstore`) make FUTO's own IME id differ per flavor. External keyboards that want to route back to FUTO should match on the service suffix `/org.futo.inputmethod.latin.LatinIME` rather than the full id (cheep-board does this).

## Settings UX

- **Home screen subtitle is stale w.r.t. cheep routing.** `settings/pages/Home.kt:93` only reflects `USE_SYSTEM_VOICE_INPUT`, so the "Built-in voice input is disabled!" notice does not show even though the mic key is routed to cheep-board by default (`USE_CHEEP_VOICE_INPUT` defaults to `true`).
