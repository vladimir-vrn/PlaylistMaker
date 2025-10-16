package com.practicum.playlistmaker.sharing.domain

interface ExternalNavigator {

    fun shareLink(shareAppLink: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}