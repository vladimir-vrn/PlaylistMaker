package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.common.domain.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(INTENT_SHARE_APP)
    }

    override fun openTerms() {
        externalNavigator.openLink(USER_AGREEMENT_WEBSITE)
    }

    override fun openSupport() {
        externalNavigator.openEmail(
            EmailData(
                SUPPORT_EMAIL,
                SUPPORT_MESSAGE,
                SUPPORT_SUBJECT,
            )
        )
    }

    companion object {
        private const val INTENT_SHARE_APP = "intent_share_app"
        private const val USER_AGREEMENT_WEBSITE = "user_agreement_website"
        private const val SUPPORT_EMAIL = "support_email"
        private const val SUPPORT_MESSAGE = "support_message"
        private const val SUPPORT_SUBJECT = "support_subject"
    }
}