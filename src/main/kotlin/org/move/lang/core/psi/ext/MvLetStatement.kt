package org.move.lang.core.psi.ext

import org.move.lang.MvElementTypes.POST
import org.move.lang.core.psi.MvLetStmt
import org.move.lang.core.psi.MvPostStmt
import org.move.lang.core.psi.ext.hasAncestor

val MvLetStmt.isPost: Boolean get() =
    this.hasChild(POST) || this.hasAncestor<MvPostStmt>()
