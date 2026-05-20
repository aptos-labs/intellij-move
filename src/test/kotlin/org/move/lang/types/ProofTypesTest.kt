package org.move.lang.types

import org.move.utils.tests.types.TypificationTestCase

class ProofTypesTest: TypificationTestCase() {
    fun `test type of forall apply lemma quant`() = testExpr("""
        module 0x1::main {
            fun main() {}
            spec lemma add_mono(_a: u64) {}
            spec main {} proof {
                forall a: u64 apply add_mono(a);
                                           //^ num
            }
        }
    """)

    fun `test forall expr parameter type`() = testExpr("""
        module 0x1::main {
            fun main() {}
            spec main {
                forall a: u64: a == 1;
                             //^ num
            }
        }
    """)

    fun `test lemma variable type`() = testExpr("""
        module 0x1::main {
            spec module {
                lemma add_zero_right(x: u64) {
                    ensures x + 0 == x;
                          //^ num
                }
            }
        }
    """)

    fun `test split stmt`() = testExpr("""
        module 0x1::main {
            spec module {
                lemma add_zero_right(x: u64) {
                    split 1 + 1;
                        //^ num
                }
            }
        }
    """)
}