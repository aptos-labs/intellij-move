package org.move.ide.inspections.compilerV2

import com.intellij.codeInspection.ProblemHighlightType.WEAK_WARNING
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.nextLeaf
import org.move.ide.inspections.DiagnosticFix
import org.move.lang.core.psi.MvAcquiresType

class MvUnusedAcquiresInspection: Move2OnlyInspectionBase<MvAcquiresType>(MvAcquiresType::class.java) {
    override val isSyntaxOnly: Boolean get() = true

    override fun visitTargetElement(
        element: MvAcquiresType,
        holder: ProblemsHolder,
        isOnTheFly: Boolean
    ) {
        holder.registerProblem(
            element,
            "Acquires declarations are no longer needed and should be removed",
            WEAK_WARNING,
            RemoveAcquiresTypeFix(element)
        )
    }

    class RemoveAcquiresTypeFix(acquiresType: MvAcquiresType):
        DiagnosticFix<MvAcquiresType>(acquiresType) {

        override fun getText(): String = "Remove acquires declaration"

        override fun invoke(project: Project, file: PsiFile, element: MvAcquiresType) {
            val nextWs = element.nextLeaf { it is PsiWhiteSpace }
            nextWs?.delete()
            element.delete()
        }
    }
}