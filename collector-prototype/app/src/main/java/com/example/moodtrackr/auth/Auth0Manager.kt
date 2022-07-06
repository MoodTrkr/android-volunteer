package com.example.moodtrackr.auth

import android.content.Context
import android.util.Log
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.example.moodtrackr.R

class Auth0Manager(context: Context) {
    private val context: Context = context //must be activity context (get using requireActivity())
    private val account: Auth0 = Auth0(
        context.resources.getString(R.string.com_auth0_clientId),
        context.resources.getString(R.string.com_auth0_domain))
    private val apiClient = AuthenticationAPIClient(account)
    private val credentialsManager = CredentialsManager(apiClient, SharedPreferencesStorage(context))

    fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.

        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(context, object: Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(exception: AuthenticationException) {
                    // Something went wrong!
                }

                // Called when authentication completed successfully
                override fun onSuccess(credentials: Credentials) {
                    // Get the access token from the credentials object.
                    // This can be used to call APIs
                    val accessToken = credentials.accessToken
                    credentialsManager.saveCredentials(credentials)
                    Log.e("DEBUG", "access token: $accessToken")
                }
            })
    }

    fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(context, object: Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    // The user has been logged out!
                    credentialsManager.clearCredentials()
                }

                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                }
            })
    }

    fun showUserProfile() {
        // With the access token, call `userInfo` and get the profile from Auth0.
        var accessToken: String? = null
        credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
            override fun onFailure(error: CredentialsManagerException) {
                // Something went wrong!
            }
            override fun onSuccess(result: Credentials) {
                // We have the user's credentials!
                accessToken=result.accessToken
                Log.e("DEBUG", "access token: $accessToken")

                apiClient.userInfo(accessToken!!)
                    .start(object: Callback<UserProfile, AuthenticationException> {
                        override fun onFailure(exception: AuthenticationException) {
                            // Something went wrong!
                        }
                        override fun onSuccess(profile: UserProfile) {
                            // We have the user's profile!
                            Log.e("DEBUG", "User Profile: ${profile}")
                        }
                    })
            }
        })
    }

}