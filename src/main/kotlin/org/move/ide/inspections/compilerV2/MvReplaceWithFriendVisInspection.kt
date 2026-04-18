package org.move.ide.inspections.compilerV2

import com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import org.move.ide.inspections.DiagnosticFix
import org.move.lang.MvElementTypes.L_PAREN
import org.move.lang.MvElementTypes.PUBLIC
import org.move.lang.MvElementTypes.R_PAREN
import org.move.lang.core.psi.MvFunction
import org.move.lang.core.psi.MvVisibilityModifier
import org.move.lang.core.psi.ext.getChild
import org.move.lang.core.psi.ext.isPublicFriend

class MvReplaceWithFriendVisInspection:
    Move2OnlyInspectionBase<MvFunction>(MvFunction::class.java) {

    override fun visitTargetElement(element: MvFunction, holder: ProblemsHolder, isOnTheFly: Boolean) {
        val visModifier = element.visibilityModifier ?: return
        if (!visModifier.isPublicFriend) return
        holder.registerProblem(
            visModifier,
            "`public(friend)` can be replaced with `friend`",
            WEAK_WARNING,
            ReplaceWithFriendFix(visModifier)
        )
    }

    class ReplaceWithFriendFix(visModifier: MvVisibilityModifier):
        DiagnosticFix<MvVisibilityModifier>(visModifier) {

        override fun getText(): String = "Replace `public(friend)` with `friend`"

        override fun invoke(project: Project, file: PsiFile, element: MvVisibilityModifier) {
            element.getChild(PUBLIC)?.delete()
            element.getChild(L_PAREN)?.delete()
            element.getChild(R_PAREN)?.delete()
        }
    }
}