package com.danilovfa.common.uikit.composables.textfield

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.danilovfa.common.core.presentation.get
import com.danilovfa.common.uikit.composables.popup.MenuItemLoader
import com.danilovfa.common.uikit.composables.popup.MenuItems
import com.danilovfa.common.uikit.composables.popup.MenuItemsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    isDropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    dropdownItems: List<MenuItemsData>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: TextFieldColors = AppTextFieldDefaults.textFieldColors(),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    labelText: String = "",
    hintText: String = "",
) {
    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { onDropdownExpandedChange(it) },
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            colors = colors,
            keyboardOptions = keyboardOptions,
            labelText = labelText,
            hintText = hintText,
            showHint = false,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            containerColor = colors.focusedContainerColor,
            expanded = isDropdownExpanded,
            onDismissRequest = { onDropdownExpandedChange(false) }
        ) {
            if (isLoading) {
                MenuItemLoader()
            }

            dropdownItems.forEach { item ->
                MenuItems(
                    title = item.title.get(),
                    icon = item.icon,
                    tint = item.tint ?: colors.focusedTextColor,
                    onClick = item.onClick
                )
            }
        }
    }
}