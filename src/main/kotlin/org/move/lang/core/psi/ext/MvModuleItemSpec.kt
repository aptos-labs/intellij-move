package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvBlockExpr
import org.move.lang.core.psi.MvItemSpec
import org.move.lang.core.psi.MvModuleItemSpec
import org.move.lang.core.psi.MvSpecInlineFunction
import org.move.lang.core.psi.MvSpecInlineFunctionStmt
import kotlin.collections.orEmpty

val MvModuleItemSpec.codeBlock: MvBlockExpr? get() = this.childOfType()

fun MvModuleItemSpec.specInlineFunctions(): List<MvSpecInlineFunction> {
    return this.codeBlock?.stmtList
        ?.filterIsInstance<MvSpecInlineFunctionStmt>()
        ?.map { it.specInlineFunction }
        .orEmpty()
}
