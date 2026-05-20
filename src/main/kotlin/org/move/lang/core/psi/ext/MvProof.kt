package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvBlockExpr
import org.move.lang.core.psi.MvProof

val MvProof.codeBlock: MvBlockExpr? get() = this.childOfType()