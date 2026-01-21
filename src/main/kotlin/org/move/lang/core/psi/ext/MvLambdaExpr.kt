package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvExpr
import org.move.lang.core.psi.MvForIterCondition
import org.move.lang.core.psi.MvLambdaExpr
import org.move.lang.core.psi.MvLambdaParameter
import org.move.lang.core.psi.MvPatBinding
import org.move.lang.core.psi.MvSpecBlockExpr

val MvLambdaExpr.lambdaParameters: List<MvLambdaParameter> get() = this.lambdaParameterList.lambdaParameterList
val MvLambdaExpr.lambdaParametersAsBindings: List<MvPatBinding> get() = lambdaParameters.map { it.patBinding }

val MvLambdaExpr.expr: MvExpr? get() = exprList.firstOrNull()
val MvLambdaExpr.specExpr: MvSpecBlockExpr? get() = exprList.drop(1).firstOrNull() as? MvSpecBlockExpr