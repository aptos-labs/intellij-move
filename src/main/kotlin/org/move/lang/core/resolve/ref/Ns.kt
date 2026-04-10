package org.move.lang.core.resolve.ref

import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.MvFieldDecl
import org.move.lang.core.resolve.scopeEntry.ScopeEntry
import org.move.stdext.emptyEnumSet
import java.util.*

sealed class Visibility {
    data object Public: Visibility()
    data object Private: Visibility()
    sealed class Restricted: Visibility() {
        class Friend(): Restricted()
        class Package(): Restricted()
    }
}

enum class Ns {
    NAME,
    FUNCTION,
    TYPE,
    TUPLE_STRUCT,
    ENUM,
    ENUM_VARIANT,
    SCHEMA,
    MODULE;

    companion object {
        fun all(): NsSet = EnumSet.allOf(Ns::class.java)
    }
}
typealias NsSet = EnumSet<Ns>

fun nsset(): NsSet = emptyEnumSet()
fun nsset(ns1: Ns): NsSet = NsSet.of(ns1)
fun nsset(ns1: Ns, ns2: Ns): NsSet = NsSet.of(ns1, ns2)
fun nsset(ns1: Ns, ns2: Ns, ns3: Ns): NsSet = NsSet.of(ns1, ns2, ns3)
fun nsset(ns1: Ns, ns2: Ns, ns3: Ns, ns4: Ns): NsSet = NsSet.of(ns1, ns2, ns3, ns4)

fun NsSet.intersects(other: NsSet): Boolean = other.any { this.contains(it) }
fun NsSet.add(other: NsSet): NsSet {
    return EnumSet.copyOf(this.plus(other))
}

fun NsSet.sub(other: NsSet): NsSet {
    val res = this.minus(other)
    return if (res.isEmpty()) {
        NONE
    } else {
        NsSet.copyOf(res)
    }
}

val NONE = nsset()
val NAMES = nsset(Ns.NAME)
val ENUM_VARIANTS = nsset(Ns.ENUM_VARIANT)

val MODULES = nsset(Ns.MODULE)
val SCHEMAS = nsset(Ns.SCHEMA)
val TYPES = nsset(Ns.TYPE)
val ENUMS = nsset(Ns.ENUM)
val FUNCTIONS = nsset(Ns.FUNCTION)

val ENUMS_N_MODULES = nsset(Ns.ENUM, Ns.MODULE)

// variables | enum variants
val VALUE_NS = nsset(Ns.NAME, Ns.FUNCTION, Ns.ENUM_VARIANT)
val CONTAINER_TYPE_NS = nsset(Ns.TYPE, Ns.TUPLE_STRUCT, Ns.ENUM, Ns.ENUM_VARIANT)
val CALLABLE_NS = nsset(Ns.NAME, Ns.FUNCTION, Ns.TUPLE_STRUCT, Ns.ENUM_VARIANT)
// vector | resource types
val INDEXABLE_NS = nsset(Ns.TYPE, Ns.ENUM, Ns.TUPLE_STRUCT, Ns.NAME)

val ITEM_TYPE_NS = nsset(Ns.TYPE, Ns.TUPLE_STRUCT, Ns.ENUM)

val ALL_NS = Ns.all()
val IMPORTABLE_NS: NsSet = EnumSet.of(Ns.NAME, Ns.FUNCTION, Ns.TYPE, Ns.TUPLE_STRUCT, Ns.SCHEMA, Ns.ENUM)

val MvNamedElement.itemNs: Ns
    get() = when (this) {
        is MvModule -> Ns.MODULE
        is MvFunctionLike -> Ns.FUNCTION
        is MvTypeParameter -> Ns.TYPE
        is MvStruct -> if (this.tupleFields != null) Ns.TUPLE_STRUCT else Ns.TYPE
        is MvEnum -> Ns.ENUM
        is MvEnumVariant -> Ns.ENUM_VARIANT
        is MvSchema -> Ns.SCHEMA
        is MvPatBinding -> Ns.NAME
        is MvFieldDecl -> Ns.NAME
        is MvConst -> Ns.NAME
        is MvGlobalVariableStmt -> Ns.NAME
        is MvLabelDecl -> Ns.NAME
        else -> error("when should be exhaustive, $this is not covered")
    }

fun List<ScopeEntry>.filterByNs(ns: NsSet): List<ScopeEntry> {
    return this.filter {
        ns.contains(it.ns)
    }

}