package org.move.lang.core.resolve.scopeEntry

import org.move.lang.core.psi.MvModule
import org.move.lang.core.psi.ext.allNonTestFunctions
import org.move.lang.core.psi.ext.lemmas
import org.move.lang.core.psi.ext.specInlineFunctions

val MvModule.itemEntries: List<ScopeEntry>
    get() {
        return getItemEntriesInner(this)
    }

fun getItemEntriesInner(owner: MvModule): List<ScopeEntry> {
    val entries =
        buildList(owner.children.size / 2) {
            // consts
            addAll(owner.constList.asEntries())

            // types
            addAll(owner.enumList.asEntries())
            addAll(owner.schemaList.asEntries())
            addAll(owner.structList.asEntries())

            // callables
            addAll(owner.allNonTestFunctions().asEntries())

            // spec callables
            addAll(owner.specFunctionList.asEntries())
            addAll(owner.moduleItemSpecList.flatMap { it.specInlineFunctions() }.asEntries())

            // lemmas (top-level `spec lemma`)
            addAll(owner.specLemmaList.map { it.lemma }.asEntries())
            // lemmas (inline, inside `spec module {}`)
            addAll(owner.moduleItemSpecList.flatMap { it.lemmas() }.asEntries())
        }
    return entries
}
