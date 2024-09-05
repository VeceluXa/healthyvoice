package com.danilovfa.core.library.exception

import com.danilovfa.core.library.text.Text

/** Types can implement this interface when they can be displayed to the user. */
internal interface Displayable {
    val text: Text?
    val description: Text?
}