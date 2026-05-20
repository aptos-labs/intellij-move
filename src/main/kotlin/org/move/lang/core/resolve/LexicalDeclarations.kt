package org.move.lang.core.resolve

import com.intellij.psi.util.CachedValueProvider
import com.intellij.util.containers.addIfNotNull
import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.*
import org.move.lang.core.resolve.ref.Ns
import org.move.lang.core.resolve.ref.NsSet
import org.move.lang.core.resolve.scopeEntry.*
import org.move.stdext.chain
import org.move.utils.PsiCachedValueProvider
import org.move.utils.getResults
import org.move.utils.psiCacheResult

fun getEntriesInScope(scope: MvElement, cameFrom: MvElement, ns: NsSet): List<ScopeEntry> {
    return when (scope) {
        is MvBlockExpr, is MvMatchArm -> getEntriesInBlocks(scope, cameFrom, ns)
        else -> {
            getEntriesInResolveScopes(scope)
        }
    }
}

private fun getEntriesInBlocks(scope: MvElement, cameFrom: MvElement, ns: NsSet): List<ScopeEntry> {
    return buildList {
        if (Ns.NAME in ns) {
            val msl = scope.isMsl()
            when (scope) {
                is MvBlockExpr if msl -> {
                    val (letBindings, visited) = getVisibleLetPatBindingsWithShadowing(scope, cameFrom, true)
                    addAll(letBindings)

                    val specBlockEntries = SpecCodeBlockNonBindings(scope).getResults()
                    addAll(
                        specBlockEntries.filter { it.name !in visited }
                    )
                }
                is MvBlockExpr -> {
                    val (letBindings, _) = getVisibleLetPatBindingsWithShadowing(scope, cameFrom, false)
                    addAll(letBindings)
                }

                is MvMatchArm -> {
                    if (cameFrom !is MvPat) {
                        // coming from rhs, use pat bindings from lhs
                        addAll(scope.pat.bindings.asEntries())
                    }
                }
            }
        }

        if (scope is MvItemsOwner) {
            addAll(scope.useSpeckEntries)
        }
    }
}

class SpecCodeBlockNonBindings(override val owner: MvBlockExpr): PsiCachedValueProvider<List<ScopeEntry>> {
    override fun compute(): CachedValueProvider.Result<List<ScopeEntry>> {
        val entries = buildList {
//            addAll(owner.builtinSpecConsts().asEntries())
            addAll(owner.globalVariables().asEntries())
            addAll(owner.specInlineFunctions().asReversed().asEntries())
        }
        return owner.psiCacheResult(entries)
    }
}

class EntriesInResolveScopes(override val owner: MvElement): PsiCachedValueProvider<List<ScopeEntry>> {
    override fun compute(): CachedValueProvider.Result<List<ScopeEntry>> {
        val entries = buildList(10) {
            if (owner is MvGenericDeclaration) {
                addAll(owner.typeParameters.asEntries())
            }
            when (owner) {
                is MvModule -> {
                    addAll(owner.itemEntries)
                    addAll(owner.enumVariants().asEntries())
                    addAll(owner.builtinFunctions().asEntries())
                    addAll(owner.builtinConsts().asEntries())
                    addAll(owner.builtinSpecFunctions().asEntries())
                }
                is MvScript -> {
                    addAll(owner.constList.asEntries())
                }
                is MvFunctionLike -> {
                    addAll(owner.parametersAsBindings.asEntries())
                }
                is MvLambdaExpr -> {
                    addAll(owner.lambdaParametersAsBindings.asEntries())
                }
                is MvItemSpec -> {
                    val refItem = owner.item
                    when (refItem) {
                        is MvFunction -> {
                            addAll(refItem.typeParameters.asEntries())

                            addAll(refItem.parametersAsBindings.asEntries())
                            addAll(refItem.specFunctionResultParameters.map { it.patBinding }.asEntries())
                        }
                        is MvStruct -> {
                            addAll(refItem.namedFields.asEntries())
                        }
                    }
                }

                is MvSchema -> {
                    addAll(owner.fieldsAsBindings.asEntries())
                }

                is MvModuleSpecBlock -> {
                    val specFuns = owner.specFunctionList
                    addAll(specFuns.asEntries())

                    val specInlineFuns = owner.moduleItemSpecList.flatMap { it.specInlineFunctions() }
                    addAll(specInlineFuns.asEntries())

                    addAll(owner.schemaList.asEntries())
                }

                is MvApplyLemmaStmt -> {
                    addAll(owner.forallQuantApply?.bindings.orEmpty().asEntries())
                }

                is MvQuantBindingsOwner -> {
                    addAll(owner.bindings.asEntries())
                }

                is MvForExpr -> {
                    val iterBinding = owner.forIterCondition?.patBinding
                    if (iterBinding != null) {
                        addIfNotNull(iterBinding.asEntry())
                    }
                }

                is MvApplySchemaStmt -> {
                    val toPatterns = owner.applyTo?.functionPatternList.orEmpty()
                    val patternTypeParams =
                        toPatterns.flatMap { it.typeParameterList?.typeParameterList.orEmpty() }
                    addAll(patternTypeParams.asEntries())
                }
            }

            if (owner is MvItemsOwner) {
                addAll(owner.useSpeckEntries)
            }
        }
        return owner.psiCacheResult(entries)
    }
}

private fun getEntriesInResolveScopes(scope: MvElement): List<ScopeEntry> {
    return EntriesInResolveScopes(scope).getResults()
}

private fun getVisibleLetPatBindingsWithShadowing(
    scope: MvElement,
    stmtOrTailExpr: MvElement,
    msl: Boolean
): Pair<List<ScopeEntry>, MutableSet<String>> {
    val visibleLetStmts = when (scope) {
        is MvBlockExpr if msl -> {
            val currentLetStmt = stmtOrTailExpr as? MvLetStmt
            val allLetStmts = BlockExprLetStmts(scope).getResults()
            when {
                currentLetStmt != null -> {
                    // if post = true, then both pre and post are accessible, else only pre
                    val letStmts = if (currentLetStmt.isPost) {
                        allLetStmts
                    } else {
                        allLetStmts.filter { !it.first.isPost }
                    }
                    letStmts
                        // drops all let-statements after the current position
                        .filter { it.first.strictlyBefore(stmtOrTailExpr) }
                }
                else -> allLetStmts
            }
        }
        is MvBlockExpr -> {
            BlockExprLetStmts(scope).getResults()
                // drops all let-statements after the current position
                .filter { it.first.strictlyBefore(stmtOrTailExpr) }
        }
        else -> error("unreachable")
    }
    // shadowing support (look at latest first)
    val bindingEntries = visibleLetStmts.asReversed().flatMap { it.second }

    val visited = mutableSetOf<String>()
    val bindings = buildList {
        for (binding in bindingEntries) {
            val name = binding.name
            if (name in visited) continue
            visited += name
            add(binding)
        }
    }
    return bindings to visited
}

class BlockExprLetStmts(override val owner: MvBlockExpr):
    PsiCachedValueProvider<List<Pair<MvLetStmt, List<ScopeEntry>>>> {
    override fun compute(): CachedValueProvider.Result<List<Pair<MvLetStmt, List<ScopeEntry>>>> {
        val letStmts = owner.stmtList.filterIsInstance<MvLetStmt>()
            .chain(owner.stmtList.filterIsInstance<MvPostStmt>().mapNotNull { it.stmt as? MvLetStmt })
            .map {
                val bindings = it.pat?.bindings.orEmpty().asEntries()
                it to bindings
            }
        return owner.psiCacheResult(letStmts.toList())
    }
}
