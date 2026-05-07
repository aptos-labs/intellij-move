package org.move.lang.core.psi

import org.move.lang.MvElementTypes
import org.move.lang.core.psi.ext.hasChild

val MvConditionPredicateStmt.kind: SpecPredicateKind
    get() {
        return when {
            this.hasChild(MvElementTypes.ASSUME) -> SpecPredicateKind.ASSUME
            this.hasChild(MvElementTypes.ASSERT) -> SpecPredicateKind.ASSERT
            this.hasChild(MvElementTypes.REQUIRES) -> SpecPredicateKind.REQUIRES
            this.hasChild(MvElementTypes.ENSURES) -> SpecPredicateKind.ENSURES
            this.hasChild(MvElementTypes.DECREASES) -> SpecPredicateKind.DECREASES
            this.hasChild(MvElementTypes.MODIFIES) -> SpecPredicateKind.MODIFIES
            else -> error("unreachable")
        }
    }

enum class SpecPredicateKind {
    ASSERT,
    ASSUME,
    REQUIRES,
    ENSURES,
    DECREASES,
    MODIFIES;
}
