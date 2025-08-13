package com.example.ioskeyboard.ime

// تعريف المفاتيح
sealed class Key {
    data class CharKey(val label: String, val value: String = label) : Key()
    object Shift : Key()
    object Backspace : Key()
    object Space : Key()
    object Enter : Key()
    object Switch123 : Key()
    object SwitchABC : Key()
    object Emoji : Key()
    object Globe : Key()
}

enum class ShiftState { Off, On, CapsLock }
enum class Lang { EN, AR }

data class KeyboardLayout(
    val rows: List<List<Key>>,
    val show123: Boolean,
    val rtl: Boolean = false
)

// EN QWERTY
fun letterLayoutEN(): KeyboardLayout {
    return KeyboardLayout(
        rows = listOf(
            "q w e r t y u i o p".split(" ").map { Key.CharKey(it) },
            "a s d f g h j k l".split(" ").map { Key.CharKey(it) },
            listOf(Key.Shift) +
                "z x c v b n m".split(" ").map { Key.CharKey(it) } +
                listOf(Key.Backspace),
            listOf(Key.Globe, Key.Emoji, Key.Switch123, Key.Space, Key.Enter)
        ),
        show123 = true,
        rtl = false
    )
}

// رموز EN
fun symbolLayoutEN(): KeyboardLayout {
    return KeyboardLayout(
        rows = listOf(
            "` 1 2 3 4 5 6 7 8 9 0".split(" ").map { Key.CharKey(it) },
            "- / : ; ( ) $ & @ \"".split(" ").map { Key.CharKey(it) },
            listOf(Key.SwitchABC) +
                " . , ? ! ' ".trim().split(" ").map { Key.CharKey(it) } +
                listOf(Key.Backspace),
            listOf(Key.Globe, Key.Emoji, Key.SwitchABC, Key.Space, Key.Enter)
        ),
        show123 = false,
        rtl = false
    )
}

// AR (تقريبًا أسلوب iOS)
fun letterLayoutAR(): KeyboardLayout {
    val row1 = "ض ص ث ق ف غ ع ه خ ح ج د".split(" ").map { Key.CharKey(it) }
    val row2 = "ش س ي ب ل ا ت ن م ك ط".split(" ").map { Key.CharKey(it) }
    val row3 = listOf(Key.Shift) +
        listOf("ئ","ء","ؤ","ر","لا","ى","ة","و","ز","ظ").map { Key.CharKey(it) } +
        listOf(Key.Backspace)
    val bottom = listOf(Key.Globe, Key.Emoji, Key.Switch123, Key.Space, Key.Enter)
    return KeyboardLayout(
        rows = listOf(row1, row2, row3, bottom),
        show123 = true,
        rtl = true
    )
}

// رموز AR (أقواس وعلامات عربية)
fun symbolLayoutAR(): KeyboardLayout {
    val row1 = "ٮ ١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ ٠".split(" ").map { Key.CharKey(it) }
    val row2 = "، ؛ : ؟ ( ) ٪ & @ \"".split(" ").map { Key.CharKey(it) }
    val row3 = listOf(Key.SwitchABC) +
        listOf(".","،",",","؟","!","'").map { Key.CharKey(it) } +
        listOf(Key.Backspace)
    val bottom = listOf(Key.Globe, Key.Emoji, Key.SwitchABC, Key.Space, Key.Enter)
    return KeyboardLayout(
        rows = listOf(row1, row2, row3, bottom),
        show123 = false,
        rtl = true
    )
}
