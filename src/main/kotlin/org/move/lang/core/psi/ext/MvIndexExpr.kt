package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvExpr
import org.move.lang.core.psi.MvIndexExpr

val MvIndexExpr.receiverExpr: MvExpr get() = exprList.first()

val MvIndexExpr.argExpr: MvExpr get() = exprList.drop(1).first()
