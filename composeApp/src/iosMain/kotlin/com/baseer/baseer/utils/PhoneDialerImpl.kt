package com.baseer.baseer.utils

import com.baseer.baseer.presentation.utils.PhoneDialer
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class PhoneDialerImpl : PhoneDialer {

    override fun dial(phoneNumber: String) {
        val url = NSURL.URLWithString("tel:$phoneNumber") ?: return

        UIApplication.sharedApplication.openURL(
            url = url,
            options = emptyMap<Any?, Any>(),
            completionHandler = null
        )
    }
}