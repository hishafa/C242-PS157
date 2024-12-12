package com.example.capstone

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import android.app.Activity

class BackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    init {
        // Set the drawable icon
        setImageResource(R.drawable.ic_arrow_back)
        // Add padding for click area
        setPadding(12, 12, 12, 12)
        // Set content description for accessibility
        contentDescription = "Back"

        // Set default click behavior to go back to the previous screen
        setOnClickListener {
            (context as? Activity)?.onBackPressed()
        }
    }
}

