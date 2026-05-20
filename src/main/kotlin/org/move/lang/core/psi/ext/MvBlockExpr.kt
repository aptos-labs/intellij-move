package org.move.lang.core.psi.ext

import org.move.lang.core.psi.*

val MvBlockExpr.stmtListNoTailExpr: List<MvStmt>
    get() {
        val lastExprStmt = this.stmtList.lastOrNull() as? MvExprStmt
        if (lastExprStmt != null && lastExprStmt.semicolon == null) {
            return this.stmtList.dropLast(1)
        }
        return this.stmtList
    }

val MvBlockExpr.tailExpr: MvExpr?
    get() {
        val trailingExprStmt = this.stmtList.lastOrNull() as? MvExprStmt ?: return null
        // to be tail expr, it must be ExprStmt without trailing ';'
        if (trailingExprStmt.semicolon != null) {
            return null
        }
        return trailingExprStmt.expr
    }

fun MvBlockExpr.schemaFields(): List<MvSchemaFieldStmt> = childrenOfType()
fun MvBlockExpr.globalVariables(): List<MvGlobalVariableStmt> = childrenOfType()

fun MvBlockExpr.specInlineFunctions(): List<MvSpecInlineFunction> {
    return this.childrenOfType<MvSpecInlineFunctionStmt>()
        .map { it.specInlineFunction }
}

fun MvBlockExpr.lemmas(): List<MvLemma> = childrenOfType()

//val MvCodeBlock.rightBrace: PsiElement? get() = this.findLastChildByType(R_BRACE)

//val MvCodeBlock.letStmts: List<MvLetStmt> get() = stmtList.filterIsInstance<MvLetStmt>()

//abstract class MvCodeBlockMixin(node: ASTNode) : MvElementImpl(node), MvCodeBlock {
//
////    override val useStmts: List<MvUseStmt> get() = useStmtList
//}
