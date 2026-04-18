package org.move.ide.inspections.compilerV2

import org.intellij.lang.annotations.Language
import org.move.utils.tests.MoveV2
import org.move.utils.tests.annotation.InspectionTestBase

@MoveV2
class MvReplaceWithFriendVisInspectionTest:
    InspectionTestBase(MvReplaceWithFriendVisInspection::class) {

    fun `test public friend triggers with fix`() = doFixTest(
        """
        module 0x1::m {
            <weak_warning descr="`public(friend)` can be replaced with `friend`">/*caret*/public(friend)</weak_warning> fun f() {
                let x = 1;
                x + 2;
            }
        }
        """, """
        module 0x1::m {
            friend fun f() {
                let x = 1;
                x + 2;
            }
        }
        """
    )

    fun `test other visibilities do not trigger`() = doTest(
        """
        module 0x1::m {
            friend fun f_friend() { 1; }
            public fun f_public() { 1; }
            package fun f_package() { 1; }
            public(package) fun f_public_package() { 1; }
            fun f_private() { 1; }
        }
        """
    )

    private fun doTest(@Language("Move") text: String) =
        checkByText(text, checkWarn = false, checkWeakWarn = true)

    private fun doFixTest(
        @Language("Move") before: String,
        @Language("Move") after: String,
    ) =
        checkFixByText("Replace `public(friend)` with `friend`", before, after,
                       checkWarn = false, checkWeakWarn = true)
}