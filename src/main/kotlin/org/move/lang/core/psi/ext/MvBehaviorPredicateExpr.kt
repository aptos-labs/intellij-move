package org.move.lang.core.psi.ext

import org.move.lang.core.psi.MvBehaviorPredicateExpr
import org.move.lang.core.psi.MvPath
import org.move.lang.core.psi.MvPathType

val MvBehaviorPredicateExpr.funPathType: MvPathType?
    get() = this.typeArgumentList
        ?.typeArgumentList
        ?.firstOrNull()
        ?.type as? MvPathType

val MvBehaviorPredicateExpr.funPath: MvPath?
    get() = this.funPathType?.path
