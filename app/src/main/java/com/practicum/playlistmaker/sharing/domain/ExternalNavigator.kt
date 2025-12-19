package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.common.domain.EmailData

interface ExternalNavigator {

    fun shareLink(shareAppLink: String)
    fun openLink(link: String)
    fun openEmail(emailData: EmailData)
}