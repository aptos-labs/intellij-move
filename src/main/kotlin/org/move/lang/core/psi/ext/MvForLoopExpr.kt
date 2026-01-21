package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvExpr
import org.move.lang.core.psi.MvForIterCondition
import org.move.lang.core.psi.MvSpecBlockExpr

val MvForIterCondition.expr: MvExpr? get() = exprList.firstOrNull()
val MvForIterCondition.specExpr: MvSpecBlockExpr? get() = exprList.drop(1).firstOrNull() as? MvSpecBlockExpr