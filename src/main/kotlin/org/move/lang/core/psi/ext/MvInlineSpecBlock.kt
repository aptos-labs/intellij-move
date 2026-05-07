package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvBlockExpr
import org.move.lang.core.psi.MvInlineSpecBlock

val MvInlineSpecBlock.bodyExpr: MvBlockExpr? get() = this.childOfType()