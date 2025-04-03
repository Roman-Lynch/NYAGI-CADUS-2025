package com.google.aiedge.examples.imageclassification.language

import android.content.Context

class BodyPartsPageText {
    companion object {
        // Define a function to create the TextField for different languages
        fun createTextFieldForLanguage(context: Context): Map<Language, TextField> {
            val languages = Language.values()
            val textFields = mutableMapOf<Language, TextField>()

            // Create a TextField for each supported language using context for resource access
            for (language in languages) {
                textFields[language] = TextField(context, language)
            }
            return textFields
        }

        // Example of how you can get a TextField for a specific language:
        fun getLocalizedTextField(context: Context, language: Language): TextField {
            val textFields = createTextFieldForLanguage(context)
            return textFields[language] ?: error("Language not found")
        }

        fun getBodyPartSelectorText(context: Context, language: Language): String {
            val textField = getLocalizedTextField(context, language)
            return textField.get("body_part_selector")
        }

        fun getBodyPartText(context: Context, language: Language, textID: String): String {
            val textField = getLocalizedTextField(context, language)
            return textField.get(textID)
        }

    }
}