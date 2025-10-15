package com.practicum.playlistmaker.sharing.domain

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink("intent_share_app")
    }

    override fun openTerms() {
        externalNavigator.openLink("user_agreement_website")
    }

    override fun openSupport() {
        externalNavigator.openEmail(
            EmailData(
                "support_email",
                "support_message",
                "support_subject",
            )
        )
    }
}