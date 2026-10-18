package com.example.lakbayaral

// ─────────────────────────────────────────────────────────────────────────────
// DEVELOPER / TEST ACCOUNT CONFIGURATION
//
// This is deliberately an email allow-list only.  A developer unlock is evaluated
// *after* Firebase Authentication succeeds, using FirebaseAuth.currentUser.email.
// Do not put an account password or a Firebase UID in the app.
// ─────────────────────────────────────────────────────────────────────────────
object DevAccount {
    const val EMAIL = "jmxbng@gmail.com"

    fun isDeveloperEmail(email: String?): Boolean =
        email?.trim()?.equals(EMAIL, ignoreCase = true) == true
}
