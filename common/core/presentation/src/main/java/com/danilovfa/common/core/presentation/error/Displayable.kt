package com.danilovfa.core.presentation.error

import com.danilovfa.common.core.presentation.Text

/** Types can implement this interface when they can be displayed to the user. */
internal interface Displayable {
    val text: Text?
    val description: Text?
}