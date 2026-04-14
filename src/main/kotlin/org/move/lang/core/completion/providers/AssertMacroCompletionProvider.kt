package org.move.lang.core.completion.providers

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PlatformPatterns.psiElement
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import org.move.lang.MvElementTypes.COLON_COLON
import org.move.lang.core.MvPsiPattern
import org.move.lang.core.completion.MACRO_PRIORITY
import org.move.lang.core.psi.MvPath

object AssertMacroCompletionProvider: MvCompletionProvider() {
    override val elementPattern: ElementPattern<out PsiElement>
        get() = MvPsiPattern.pathExpr()
            .andNot(
                psiElement().afterLeaf(psiElement(COLON_COLON))
            )


    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val maybePath = parameters.position.parent
        val path = maybePath as? MvPath ?: maybePath.parent as MvPath

        if (parameters.position !== path.referenceNameElement) return

        result.addElement(assertElement("assert!", "(_: bool, err: u64)"))
        result.addElement(assertElement("assert_eq!", "(_: T, _: T, err: vector<u8>)"))
        result.addElement(assertElement("assert_ne!", "(_: T, _: T, err: vector<u8>)"))
    }

    fun assertElement(name: String, signature: String): LookupElement {
        val lookupElement = LookupElementBuilder
            .create(name)
            .withTailText(signature)
            .withTypeText("()")
            .withInsertHandler { ctx, _ ->
                val document = ctx.document
                document.insertString(ctx.selectionEndOffset, "()")
                EditorModificationUtil.moveCaretRelatively(ctx.editor, 1)
            }
        return PrioritizedLookupElement.withPriority(lookupElement, MACRO_PRIORITY)
    }
}
