package org.move.ide.inspections.compilerV2

import org.intellij.lang.annotations.Language
import org.move.utils.tests.MoveV2
import org.move.utils.tests.annotation.InspectionTestBase

@MoveV2
class MvUnusedAcquiresInspectionTest:
    InspectionTestBase(MvUnusedAcquiresInspection::class) {

    fun `test unused acquires on regular fun`() = doFixTest(
        """
        module 0x1::m {
            struct S has key { val: u8 }
            fun main() <weak_warning descr="Acquires declarations are no longer needed and should be removed">/*caret*/acquires S</weak_warning> {
                move_from<S>(@0x1);
            }
        }
        """, """
        module 0x1::m {
            struct S has key { val: u8 }
            fun main() {
                move_from<S>(@0x1);
            }
        }
        """
    )

    fun `test unused acquires on inline fun`() = doFixTest(
        """
        module 0x1::m {
            struct S has key { val: u8 }
            inline fun main() <weak_warning descr="Acquires declarations are no longer needed and should be removed">/*caret*/acquires S</weak_warning> {
                move_from<S>(@0x1);
            }
        }
        """, """
        module 0x1::m {
            struct S has key { val: u8 }
            inline fun main() {
                move_from<S>(@0x1);
            }
        }
        """
    )

    fun `test no diagnostic when no acquires`() = doTest(
        """
        module 0x1::m {
            struct S has key { val: u8 }
            fun main() {
                move_from<S>(@0x1);
            }
        }
        """
    )

    private fun doTest(@Language("Move") text: String) =
        checkByText(text, checkWarn = false, checkWeakWarn = true)

    private fun doFixTest(
        @Language("Move") before: String,
        @Language("Move") after: String,
    ) =
        checkFixByText("Remove acquires declaration", before, after,
                       checkWarn = false, checkWeakWarn = true)
}
