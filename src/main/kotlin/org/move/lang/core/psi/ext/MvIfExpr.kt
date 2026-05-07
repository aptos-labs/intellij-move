package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvElseBlock
import org.move.lang.core.psi.MvExpr

//val MvIfExpr.tailExpr: MvExpr?
//    get() {
//        val codeBlock = this.codeBlock
//        if (codeBlock != null) return codeBlock.returningExpr
//        return this.inlineBlock?.expr
//    }

val MvElseBlock.tailExpr: MvExpr?
    get() {
        return this.bodyExpr?.tailExpr
//        val elseCodeBlock = this.expr
//        if (elseCodeBlock != null) return elseCodeBlock.expr
//        return this.inlineBlock?.expr
    }

//val MvIfExpr.elseTailExpr: MvExpr?
//    get() {
//        val elseBlock = this.elseBlock ?: return null
//        val elseCodeBlock = elseBlock.codeBlock
//        if (elseCodeBlock != null) return elseCodeBlock.returningExpr
//        return elseBlock.inlineBlock?.expr
//    }
