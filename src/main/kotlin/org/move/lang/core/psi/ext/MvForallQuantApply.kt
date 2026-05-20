package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvExpr
import org.move.lang.core.psi.MvForallQuantApply

val MvForallQuantApply.triggerExprs: List<MvExpr> get() = this.quantTriggerList?.exprList.orEmpty()