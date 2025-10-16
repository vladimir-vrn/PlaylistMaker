package com.practicum.playlistmaker.sharing.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.EmailData
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator

class ExternalNavigatorImpl(
    private val context: Context
) : ExternalNavigator {

    override fun shareLink(shareAppLink: String) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            getStringResourceByName(shareAppLink)
        )

        context.startActivity(Intent.createChooser(intent, context.getString(R.string.intent_share_app_header)))
    }

    override fun openLink(link: String) {

        val intent = Intent(
            Intent.ACTION_VIEW,
            getStringResourceByName(link).toUri()
        )

        context.startActivity(intent)
    }

    override fun openEmail(emailData: EmailData) {

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = context.getString(R.string.mailto_intent_data).toUri()
        intent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(getStringResourceByName(emailData.address))
        )
        intent.putExtra(
            Intent.EXTRA_TEXT,
            getStringResourceByName(emailData.text)
        )
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            getStringResourceByName(emailData.subject)
        )

        context.startActivity(intent)
    }

    @SuppressLint("DiscouragedApi")
    private fun getStringResourceByName(resourceName: String): String {

        val resourceId = context.resources.getIdentifier(
            resourceName, "string", context.packageName
        )
        return context.resources.getString(resourceId)
    }
}