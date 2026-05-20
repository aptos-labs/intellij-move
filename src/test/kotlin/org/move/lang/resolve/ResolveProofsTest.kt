package org.move.lang.resolve

import org.move.utils.tests.resolve.ResolveTestCase

class ResolveProofsTest: ResolveTestCase() {
    fun `test proof has access to function type parameters`() = checkByCode("""
        module 0x1::main {
            fun main<Type>() {}
                   //X
            spec main {} proof {
                let a: Type = 1;
                     //^
            }
        }
    """)

    fun `test proof has access to function parameters`() = checkByCode("""
        module 0x1::main {
            fun main(acc: &signer) {}
                   //X
            spec main {} proof {
                acc;
                //^
            }
        }
    """)

    fun `test resolve lemma variable`() = checkByCode("""
        module 0x1::main {
            spec module {
                lemma add_zero_right(x: u64) {
                                   //X
                    ensures x + 0 == x;
                          //^
                }
            }
        }
    """)

    fun `test resolve lemma type param`() = checkByCode("""
        module 0x1::main {
            spec module {
                lemma add_zero_right<T>() {
                                   //X
                    let a: T = 1;
                         //^
                }
            }
        }
    """)

    fun `test apply lemma from proof`() = checkByCode("""
        module 0x1::main {
            spec module {
                lemma add_zero_right() {
                        //X
                }
            }
            fun main() {}
            spec main {} proof {
                apply add_zero_right();
                       //^
            }
        }
    """)

    fun `test apply top level lemma from proof`() = checkByCode("""
        module 0x1::main {
            spec lemma add_zero_right() {
                        //X
            }
            fun main() {}
            spec main {} proof {
                apply add_zero_right();
                       //^
            }
        }
    """)

    fun `test apply lemma from its own proof`() = checkByCode("""
        module 0x1::main {
            spec module {
                lemma add_zero_right() {
                        //X
                } proof {
                    apply add_zero_right();
                           //^
                }
            }
        }
    """)

    fun `test spec fun accessible from lemma`() = checkByCode("""
        module 0x1::main {
            spec fun sum(n: num): num {
                    //X
                n
            }
            spec module {
                lemma add_zero_right() {
                    ensures sum(n) == n;
                          //^
                }
            }
        }
    """)

    fun `test spec fun accessible from lemma proof`() = checkByCode("""
        module 0x1::main {
            spec fun sum(n: num): num {
                    //X
                n
            }
            spec module {
                lemma add_zero_right() {
                } proof {
                    ensures sum(n) == n;
                          //^
                }
            }
        }
    """)

    fun `test lemma accessible forall apply`() = checkByCode("""
        module 0x1::main {
            spec fun sum(n: num): num {
                n
            }
            fun main() {}
            spec main {} proof {
                forall x: num {sum(x)} apply add_zero_right();
                                                //^
            }
            spec module {
                lemma add_zero_right() {
                         //X
                } proof {
                }
            }
        }
    """)

    fun `test spec fun accessible forall triggers`() = checkByCode("""
        module 0x1::main {
            spec fun sum(n: num): num {
                    //X
                n
            }
            fun main() {}
            spec main {} proof {
                forall x: num {sum(x)} apply add_zero_right();
                              //^
            }
            spec module {
                lemma add_zero_right() {
                } proof {
                }
            }
        }
    """)

    fun `test proof resolve let variable`() = checkByCode("""
        module 0x1::main {
            fun main() {}
            spec main {} proof {
                let acc = 1;
                   //X
                acc;
               //^
            }
        }
    """)

    fun `test proof resolve let variable with post`() = checkByCode("""
        module 0x1::main {
            fun main() {}
            spec main {} proof {
                post let acc1 = 1;
                        //X
                post acc1;
                    //^
            }
        }
    """)

    fun `test proof resolve post has access to pre lets inline`() = checkByCode("""
        module 0x1::main {
            fun main() {}
            spec main {} proof {
                let acc1 = 1;
                   //X
                post acc1;
                    //^
            }
        }
    """)

    fun `test proof resolve post has access to pre lets block`() = checkByCode("""
        module 0x1::main {
            fun main() {}
            spec main {} proof {
                let acc1 = 1;
                   //X
                post { acc1; }
                      //^
            }
        }
    """)

    fun `test proof resolve let variable in post block`() = checkByCode("""
        module 0x1::main {
            fun main() {}
            spec main {} proof {
                post {
                    let acc = 1;
                        //X
                    acc;
                   //^
                }
            }
        }
    """)

    fun `test pre expr cant access post let`() = checkByCode("""
        module 0x1::main {
            fun main() {}
            spec main {} proof {
                post {
                    let acc = 1;
                }
                acc1;
              //^ unresolved
            }
        }
    """)

    fun `test resolve forall apply variables`() = checkByCode("""
        module 0x1::main {
            fun main() {}
            spec lemma add_mono(a: u64) {}
            spec main {} proof {
                forall a: u64
                     //X
                    apply add_mono(a);
                                 //^
            }
        }
    """)

    fun `test import lemma from another module`() = checkByCode("""
        module 0x42::LemmaProvider {
            spec lemma add_comm(a: u64, b: u64) {
                      //X
                ensures a + b == b + a;
            } proof {
                assume [trusted] true;
            }
        }
        module 0x42::LemmaConsumer {
            use 0x42::LemmaProvider;
            fun commutative_add(x: u64, y: u64): u64 { x + y }
            spec commutative_add {
                ensures result == y + x;
            } proof {
                apply LemmaProvider::add_comm(x, y);
                                      //^
            }
        }
    """)

    fun `test import lemma from another module fq path`() = checkByCode("""
        module 0x42::LemmaProvider {
            spec lemma add_comm(a: u64, b: u64) {
                      //X
                ensures a + b == b + a;
            } proof {
                assume [trusted] true;
            }
        }
        module 0x42::LemmaConsumer {
            fun commutative_add(x: u64, y: u64): u64 { x + y }
            spec commutative_add {
                ensures result == y + x;
            } proof {
                apply 0x42::LemmaProvider::add_comm(x, y);
                                              //^
            }
        }
    """)
}