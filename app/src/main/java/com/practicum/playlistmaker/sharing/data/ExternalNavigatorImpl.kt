package com.practicum.playlistmaker.sharing.data

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
        intent.putExtra(Intent.EXTRA_TEXT, getStringResourceByName(shareAppLink))

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
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getStringResourceByName(emailData.address)))
        intent.putExtra(Intent.EXTRA_TEXT, getStringResourceByName(emailData.text))
        intent.putExtra(Intent.EXTRA_SUBJECT, getStringResourceByName(emailData.subject))

        context.startActivity(intent)
    }

    private fun getStringResourceByName(name: String): String {
        val resId = stringRes[name]?.toInt() ?: 0
        return if (resId == 0) name else context.getString(resId)
    }

    companion object {
        private val stringRes = mapOf(
            "share_app" to R.string.share_app,
            "write_to_support" to R.string.write_to_support,
            "user_agreement" to R.string.user_agreement,
            "user_agreement_website" to R.string.user_agreement_website,
            "intent_share_app" to R.string.intent_share_app,
            "intent_share_app_header" to R.string.intent_share_app_header,
            "support_message" to R.string.support_message,
            "support_email" to R.string.support_email,
            "support_subject" to R.string.support_subject,
        )
    }
}