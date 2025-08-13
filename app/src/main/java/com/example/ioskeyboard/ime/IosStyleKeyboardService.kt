package com.example.ioskeyboard.ime

import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.InputType
import android.view.View
import androidx.compose.ui.platform.ComposeView

class IosStyleKeyboardService : InputMethodService() {

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            setContent {
                KeyboardRoot(
                    onCommitText = { text -> currentInputConnection?.commitText(text, 1) },
                    onBackspace = {
                        val ic = currentInputConnection ?: return@KeyboardRoot
                        val selected = ic.getSelectedText(0)
                        if (selected?.isNotEmpty() == true) {
                            ic.commitText("", 1)
                        } else {
                            ic.deleteSurroundingText(1, 0)
                        }
                    },
                    onEnter = {
                        val info = currentInputEditorInfo
                        val actionId = info?.imeOptions?.and(android.view.inputmethod.EditorInfo.IME_MASK_ACTION)
                        val ic = currentInputConnection ?: return@KeyboardRoot
                        if (actionId != null && actionId != 0 && actionId != android.view.inputmethod.EditorInfo.IME_ACTION_NONE) {
                            ic.performEditorAction(actionId)
                        } else {
                            ic.commitText("\n", 1)
                        }
                    },
                    isPasswordField = {
                        val itype = currentInputEditorInfo?.inputType ?: 0
                        (itype and InputType.TYPE_TEXT_VARIATION_PASSWORD) == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                        (itype and InputType.TYPE_NUMBER_VARIATION_PASSWORD) == InputType.TYPE_NUMBER_VARIATION_PASSWORD ||
                        (Build.VERSION.SDK_INT >= 26 && (itype and InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD) == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD)
                    },
                    onSwitchToNextIme = {
                        // Globe: بدّل للكيبورد التالي
                        if (Build.VERSION.SDK_INT >= 28) {
                            switchToNextInputMethod(false)
                        } else {
                            // fallback: لا شيء
                        }
                    }
                )
            }
        }
    }
}
