package com.baseer.baseer.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.baseer.baseer.presentation.utils.PhoneDialer

class PhoneDialerImpl(
    private val context: Context
) : PhoneDialer {

    override fun dial(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}