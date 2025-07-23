import com.sparkedember.ankotocompose.convertAnkoToCompose

fun main() {
    val ankoCode = """
        coordinatorLayout {
            fitsSystemWindows = true

            appBarLayout {
                fitsSystemWindows = true

                collapsingToolbarLayout {
                    fitsSystemWindows = true
                    title = "My App Title"

                    toolbar {
                        // Toolbar properties
                    }.lparams(width = matchParent, height = dip(56))

                }.lparams(width = matchParent, height = matchParent)

            }.lparams(width = matchParent, height = dip(200))

            nestedScrollView {
                textView("Scrollable content...")
            }

            floatingActionButton {
                imageResource = android.R.drawable.ic_input_add
            }
        }
    """.trimIndent()

    val result = convertAnkoToCompose(ankoCode)
    println("Result:")
    println(result)
}