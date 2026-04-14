package org.move.lang.core.psi

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import org.move.lang.core.psi.ext.MvFunctionMixin

fun MvModule.builtinFunctions(): List<MvFunction> {
    return this.project.service<BuiltinsService>().builtinFunctions()
}

fun MvModule.builtinConsts(): List<MvConst> {
    return this.project.service<BuiltinsService>().builtinConsts()
}

fun MvModule.builtinSpecFunctions(): List<MvSpecFunction> {
    return this.project.service<BuiltinsService>().builtinSpecFunctions()
}

//class BuiltinSpecConsts(override val owner: MvSpecCodeBlock): PsiCachedValueProvider<List<MvConst>> {
//    override fun compute(): CachedValueProvider.Result<List<MvConst>> {
//        return owner.psiCacheResult(
//            owner.project.service<BuiltinsService>().builtinSpecConsts()
//        )
//    }
//}

//fun MvSpecCodeBlock.builtinSpecConsts(): List<MvConst> {
//    return this.project.service<BuiltinsService>().builtinSpecConsts()
//}


@Service(Service.Level.PROJECT)
class BuiltinsService(val project: Project) {

    fun builtinFunctions(): List<MvFunction> {
        val builtinFunctions = builtinsModule.functionList
//        val builtinFunctions = (this.builtinsModule.copy() as? MvModule)?.functionList.orEmpty()
        builtinFunctions.forEach { f ->
            (f as MvFunctionMixin).builtIn = true
        }
        return builtinFunctions
    }

    fun builtinConsts(): List<MvConst> {
        val builtinConsts = builtinsModule.constList
//        val builtinFunctions = (this.builtinsModule.copy() as? MvModule)?.functionList.orEmpty()
//        builtinFunctions.forEach { f ->
//            (f as MvFunctionMixin).builtIn = true
//        }
        return builtinConsts
    }

    fun builtinSpecFunctions(): List<MvSpecFunction> {
        return this.builtinsSpecModule.specFunctionList
//        return (this.builtinsSpecModule.copy() as? MvModule)?.specFunctionList.orEmpty()
    }

//    fun builtinSpecConsts(): List<MvConst> {
//        return builtinsSpecConstModule.constList
//    }

    private val builtinsModule: MvModule = project.psiFactory.module(
        """
            module 0x0::builtins {
                const __COMPILE_FOR_TESTING__: bool = false;

                const MAX_U8: u8 = 255;
                const MAX_U16: u16 = 65535;
                const MAX_U32: u32 = 4294967295;
                const MAX_U64: u64 = 18446744073709551615;
                const MAX_U128: u128 = 340282366920938463463374607431768211455;
                const MAX_U256: u256 = 115792089237316195423570985008687907853269984665640564039457584007913129639935;
        
                const MAX_I8: i8 = 127;
                const MAX_I16: i16 = 32767;
                const MAX_I32: i32 = 2147483647;
                const MAX_I64: i64 = 9223372036854775807;
                const MAX_I128: i128 = 170141183460469231731687303715884105727;
                const MAX_I256: i256 = 57896044618658097711785492504343953926634992332820282019728792003956564819967;
                const MIN_I8: i8 = -128;
                const MIN_I16: i16 = -32768;
                const MIN_I32: i32 = -2147483648;
                const MIN_I64: i64 = -9223372036854775808;
                const MIN_I128: i128 = -170141183460469231731687303715884105728;
                const MIN_I256: i256 = -57896044618658097711785492504343953926634992332820282019728792003956564819968;

                /// Removes `T` from address and returns it. 
                /// Aborts if address does not hold a `T`.
                native fun move_from<T: key>(addr: address): T acquires T;
                            
                /// Publishes `T` under `signer.address`. 
                /// Aborts if `signer.address` already holds a `T`.
                native fun move_to<T: key>(acc: &signer, res: T);
                                        
                native fun borrow_global<T: key>(addr: address): &T acquires T;           
                                         
                native fun borrow_global_mut<T: key>(addr: address): &mut T acquires T;
                
                /// Returns `true` if a `T` is stored under address
                native fun exists<T: key>(addr: address): bool;
                
                native fun freeze<S>(mut_ref: &mut S): &S;
            }            
        """.trimIndent()
    )

    private val builtinsSpecModule = project.psiFactory.module(
        """
            module 0x0::builtin_spec_functions {
                spec native fun max_u8(): num;
                spec native fun max_u64(): num;
                spec native fun max_u128(): num;
                spec native fun global<T: key>(addr: address): T;
                spec native fun old<T>(_: T): T;
                spec native fun update_field<S, F, V>(s: S, fname: F, val: V): S;
                spec native fun TRACE<T>(_: T): T;
                
                spec native fun concat<T>(v1: vector<T>, v2: vector<T>): vector<T>;
                spec native fun vec<T>(_: T): vector<T>;
                spec native fun len<T>(_: vector<T>): num;
                spec native fun contains<T>(v: vector<T>, e: T): bool;
                spec native fun index_of<T>(_: vector<T>, _: T): num;
                spec native fun range<T>(_: vector<T>): range;
                spec native fun update<T>(_: vector<T>, _: num, _: T): vector<T>;
                spec native fun in_range<T>(_: vector<T>, _: num): bool;
                spec native fun int2bv(_: num): bv;
                spec native fun bv2int(_: bv): num;
            }            
        """.trimIndent()
    )

//    private val builtinsSpecConstModule = project.psiFactory.module(
//        """
//            module 0x0::builtin_spec_functions {
//                const MAX_U8: u8 = 255;
//                const MAX_U16: u16 = 65535;
//                const MAX_U32: u32 = 4294967295;
//                const MAX_U64: u64 = 18446744073709551615;
//                const MAX_U128: u128 = 340282366920938463463374607431768211455;
//                const MAX_U256: u256 = 115792089237316195423570985008687907853269984665640564039457584007913129639935;
//            }
//        """.trimIndent()
//    )
}