package org.move.lang.core.psi.ext

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.move.ide.MoveIcons
import org.move.lang.core.psi.MvAttr
import org.move.lang.core.psi.MvBlockExpr
import org.move.lang.core.psi.MvLemma
import org.move.lang.core.psi.MvModificationTracker
import org.move.lang.core.psi.MvReturnType
import org.move.lang.core.psi.impl.MvNameIdentifierOwnerImpl
import javax.swing.Icon

abstract class MvLemmaMixin(node: ASTNode): MvNameIdentifierOwnerImpl(node),
                                            MvLemma {
    override val modificationTracker = MvModificationTracker(this)

    override fun incModificationCount(element: PsiElement): Boolean {
        val shouldInc = codeBlock?.isAncestorOf(element) == true
        if (shouldInc) modificationTracker.incModificationCount()
        return shouldInc
    }

    override fun getIcon(flags: Int): Icon = MoveIcons.FUNCTION

    override val attrList: List<MvAttr> = emptyList()

    override val returnType: MvReturnType? get() = null

    override val codeBlock: MvBlockExpr?
        get() = findChildByClass(MvBlockExpr::class.java)
}
