package org.move.lang.core.psi.ext

import com.intellij.psi.PsiElement
import org.move.lang.MvElementTypes.IDENTIFIER
import org.move.lang.core.psi.MvAssertMacroExpr

val MvAssertMacroExpr.identifier: PsiElement get() = this.findFirstChildByType(IDENTIFIER)!!

enum class AssertKind {
    PLAIN,
    EQ,
    NOT_EQ,
}

val MvAssertMacroExpr.assertKind: AssertKind
    get() =
        when (this.identifier.text) {
            "assert", "debug_assert" -> AssertKind.PLAIN
            "assert_eq", "debug_assert_eq" -> AssertKind.EQ
            "assert_ne", "debug_assert_ne" -> AssertKind.NOT_EQ
            else -> error("exhaustive")
        }