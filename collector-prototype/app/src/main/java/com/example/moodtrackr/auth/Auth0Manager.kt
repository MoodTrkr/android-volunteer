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
import com.auth0.android.management.ManagementException
import com.auth0.android.management.UsersAPIClient
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.request.DefaultClient
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.example.moodtrackr.R
import com.google.gson.Gson
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking

class Auth0Manager(context: Context) {
    private val context: Context = context //must be activity context (get using requireActivity())
    private val account: Auth0 = Auth0(
        context.resources.getString(R.string.com_auth0_clientId),
        context.resources.getString(R.string.com_auth0_domain),
    )
    private val apiClient = AuthenticationAPIClient(account)
    private val credentialsManager = CredentialsManager(apiClient, SharedPreferencesStorage(context))
    private lateinit var usersClient: UsersAPIClient

//    init {
//        account.networkingClient = DefaultClient(enableLogging = true)
//    }

    fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope(context.resources.getString(R.string.com_auth0_scope))
            .withAudience(context.resources.getString(R.string.com_auth0_audience))
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
                    credentialsManager.saveCredentials(credentials)
                    retrieveAccessToken()
                    getUserMetadata()
                }
            })
    }

    fun loginAsync(): Deferred<Credentials?> {
        val deferred = CompletableDeferred<Credentials?>()
        // Setup the WebAuthProvider, using the custom scheme and scope.
        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope(context.resources.getString(R.string.com_auth0_scope))
            .withAudience(context.resources.getString(R.string.com_auth0_audience))
            // Launch the authentication passing the callback where the results will be received
            .start(context, object: Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(exception: AuthenticationException) {
                    // Something went wrong!
                    deferred.cancel()
                }
                // Called when authentication completed successfully
                override fun onSuccess(credentials: Credentials) {
                    credentialsManager.saveCredentials(credentials)
                    retrieveAccessToken()
                    Log.e("DEBUG", "tempp")
                    usersClient = UsersAPIClient(account, credentials.accessToken)
                    runBlocking {
                        var userId = credentials.user.getId()
                        if (userId == null) userId = ""
                        getUserMetadataAsync(context, userId, usersClient)
                    }.invokeOnCompletion {
                        deferred.complete(credentials)
                    }
                }
            })
        return deferred
    }

    fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(context, object: Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    // The user has been logged out!
                    credentialsManager.clearCredentials()
                    SharedPreferencesStorage(context).store(context.resources.getString(R.string.login_status_identifier),
                        false)
                    SharedPreferencesStorage(context).store(context.resources.getString(R.string.setup_status_identifier),
                        false)
                    SharedPreferencesStorage(context).remove(context.resources.getString(R.string.auth0_user_metadata))
                    SharedPreferencesStorage(context).remove(context.resources.getString(R.string.token_refresh))
                    SharedPreferencesStorage(context).remove(context.resources.getString(R.string.token_expiry))
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

                apiClient.userInfo(accessToken!!)
                    .start(object: Callback<UserProfile, AuthenticationException> {
                        override fun onFailure(exception: AuthenticationException) {
                            // Something went wrong!
                        }
                        override fun onSuccess(profile: UserProfile) {
                            // We have the user's profile!
                            Log.e("DEBUG", "User Profile: $profile")
                        }
                    })
            }
        })
    }

    fun refreshCredentials() {
        refreshCredentials(context, apiClient, credentialsManager)
    }

    fun getCredentials(): String? {
        // With the access token, call `userInfo` and get the profile from Auth0.
        var accessToken: String? = null
        credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
            override fun onFailure(error: CredentialsManagerException) {
                // Something went wrong!
            }
            override fun onSuccess(result: Credentials) {
                // We have the user's credentials!
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
        return accessToken
    }
    fun retrieveAccessToken() {
        Companion.retrieveAccessToken(context, credentialsManager)
    }

    fun getUserMetadata() {
        val accessToken: String? = SharedPreferencesStorage(context)
            .retrieveString(context.resources.getString(R.string.token_identifier))
        val userId: String? = SharedPreferencesStorage(context)
            .retrieveString(context.resources.getString(R.string.token_user_id))
        if (accessToken != null && userId != null) {
            this.usersClient = UsersAPIClient(account, accessToken)
            getUserMetadata(context, userId, accessToken, usersClient)
        }
    }

    fun updateUserMetadata(metadata: Map<String, String>) {
        val accessToken: String? = SharedPreferencesStorage(context)
            .retrieveString(context.resources.getString(R.string.token_identifier))
        val userId: String? = SharedPreferencesStorage(context)
            .retrieveString(context.resources.getString(R.string.token_user_id))
        if (accessToken != null && userId != null) {
            this.usersClient = UsersAPIClient(account, accessToken)
            updateUserMetadata(context, userId, accessToken, usersClient, metadata)
        }
    }

    companion object {
        fun authSetup(context: Context): Triple<Auth0, AuthenticationAPIClient, CredentialsManager> {
            val account: Auth0 = Auth0(
                context.resources.getString(R.string.com_auth0_clientId),
                context.resources.getString(R.string.com_auth0_domain),
            )
//            account.networkingClient = DefaultClient(enableLogging = true)
            val apiClient = AuthenticationAPIClient(account);
            val credentialsManager = CredentialsManager(apiClient, SharedPreferencesStorage(context))
            return Triple(account, apiClient, credentialsManager)
        }

        fun refreshCredentials(context: Context) {
            val triple = authSetup(context)
            refreshCredentials(context,
                triple.second,
                triple.third
            )
        }

        private fun refreshCredentials(context: Context, apiClient: AuthenticationAPIClient,
                               credentialsManager: CredentialsManager) {
            val refreshToken = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_refresh))
            Log.e("MDTKR_AUTH", "refreshToken_Refresh_Credentials: $refreshToken")
            if (refreshToken != null) {
                val account = Auth0(
                    context.resources.getString(R.string.com_auth0_clientId),
                    context.resources.getString(R.string.com_auth0_domain)
                )
                val client = AuthenticationAPIClient(account)
                client.renewAuth(refreshToken)
                    .start(object: Callback<Credentials, AuthenticationException> {
                        override fun onFailure(exception: AuthenticationException) {
                            // Error
                            Log.e("MDTKR_AUTH", "${exception.message}: ${exception.getCode()}\n ${exception.getDescription()}")
                        }

                        override fun onSuccess(credentials: Credentials) {
                            // Use the credentials
                            Log.e("MDTKR_AUTH","LOGGED IN")
                            credentialsManager.saveCredentials(credentials)
                            refreshAccessToken(context, credentialsManager)
                        }
                    })
            }
        }

        fun retrieveAccessToken(context: Context, credentialsManager: CredentialsManager) {
            // With the access token, call `userInfo` and get the profile from Auth0.
            runBlocking {
                credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
                    override fun onFailure(exception: CredentialsManagerException) {
                        // Something went wrong!
                        Log.e("DEBUG", "Failed to refresh credentials: $exception")
                    }
                    override fun onSuccess(result: Credentials) {
                        // We have the user's credentials!
                        Log.e("MDTKR_AUTH", "refresh Token: ${result.refreshToken}")
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_identifier),
                            result.accessToken)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_expiry),
                            result.expiresAt.time)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_refresh),
                            result.refreshToken)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_user_id),
                            result.user.getId())
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.login_status_identifier),
                            true)
                    }
                })
            }
        }

        fun refreshAccessToken(context: Context, credentialsManager: CredentialsManager) {
            // With the access token, call `userInfo` and get the profile from Auth0.
            runBlocking {
                credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
                    override fun onFailure(exception: CredentialsManagerException) {
                        // Something went wrong!
                        Log.e("DEBUG", "Failed to refresh credentials: $exception")
                    }
                    override fun onSuccess(result: Credentials) {
                        // We have the user's credentials!
                        Log.e("MDTKR_AUTH", "refresh Token: ${result.refreshToken}")
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_identifier),
                            result.accessToken)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_expiry),
                            result.expiresAt.time)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_user_id),
                            result.user.getId())
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.login_status_identifier),
                            true)
                    }
                })
            }
        }

        fun getUserMetadata(context: Context) {
            val accessToken: String? = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_identifier))
            val userId: String? = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_user_id))
            if (accessToken != null && userId != null) {
                val auth = authSetup(context)
                val usersClient = UsersAPIClient(auth.first, accessToken)
                getUserMetadata(context, userId, accessToken, usersClient)
            }
        }

        fun getUserMetadata(context: Context, userId: String, accessToken: String, usersClient: UsersAPIClient) {
            // Get the user ID and call the full getUser Management API endpoint, to retrieve the full profile information
            // Create the user API client using the account details and the access token from Credentials
            usersClient
                .getProfile(userId)
                .start(object: Callback<UserProfile, ManagementException> {
                    override fun onFailure(exception: ManagementException) {
                        // Something went wrong!
                        Log.e("DEBUG", "$exception")
                    }
                    override fun onSuccess(profile: UserProfile) {
                        val profileMetadata = profile.getUserMetadata()
                        Log.e("DEBUG", "User Metadata: $profileMetadata")
                        Log.e("DEBUG", "User Metadata Size: ${profileMetadata.size}")
                        val gson = Gson().toJson(profileMetadata)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.auth0_user_metadata),
                            gson)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.setup_status_identifier),
                            profileMetadata.size>1)
                    }
                })
        }

        fun getUserMetadataAsync(context: Context, userId: String, usersClient: UsersAPIClient): Deferred<Map<String, Any>?> {
            var deferredUser = CompletableDeferred<Map<String, Any>?>()
            usersClient
                .getProfile(userId)
                .start(object: Callback<UserProfile, ManagementException> {
                    override fun onFailure(exception: ManagementException) {
                        // Something went wrong!
                        Log.e("DEBUG", "$exception")
                        deferredUser.cancel()
                    }
                    override fun onSuccess(profile: UserProfile) {
                        val profileMetadata = profile.getUserMetadata()
                        Log.e("DEBUG", "User Metadata: $profileMetadata")
                        Log.e("DEBUG", "User Metadata Size: ${profileMetadata.size}")
                        val gson = Gson().toJson(profileMetadata)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.auth0_user_metadata),
                            gson)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.setup_status_identifier),
                            profileMetadata.size>1)
                        deferredUser.complete(profileMetadata)
                    }
                })
            return deferredUser
        }

        fun updateUserMetadata(context: Context, metadata: Map<String, String>) {
            val accessToken: String? = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_identifier))
            val userId: String? = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_user_id))
            if (accessToken != null && userId != null) {
                val auth = authSetup(context)
                val usersClient = UsersAPIClient(auth.first, accessToken)
                updateUserMetadata(context, userId, accessToken, usersClient, metadata)
            }
        }

        fun updateUserMetadataAsync(context: Context, metadata: Map<String, String>): CompletableDeferred<Boolean> {
            val deferredNever = CompletableDeferred<Boolean>()
            val accessToken: String? = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_identifier))
            val userId: String? = SharedPreferencesStorage(context)
                .retrieveString(context.resources.getString(R.string.token_user_id))
            if (accessToken != null && userId != null) {
                val auth = authSetup(context)
                val usersClient = UsersAPIClient(auth.first, accessToken)
                return updateUserMetadataAsync(context, userId, accessToken, usersClient, metadata)
            }
            deferredNever.complete(false)
            return deferredNever
        }

        fun updateUserMetadataAsync(context: Context, userId: String, accessToken: String, usersClient: UsersAPIClient, metadata: Map<String, String>): CompletableDeferred<Boolean> {
            var userMetadataDeferred = CompletableDeferred<Boolean>()
            // Call updateMetadata with the id of the user to update, and the map of data
            usersClient.updateMetadata(userId, metadata)
                .start(object: Callback<UserProfile, ManagementException> {
                    override fun onFailure(exception: ManagementException) {
                        // Something went wrong!
                        Log.e("DEBUG", "Failed to update metadata!")
                        Log.e("DEBUG", "$exception")
                    }

                    override fun onSuccess(profile: UserProfile) {
                        val profileMetadata = profile.getUserMetadata()
                        val gson = Gson().toJson(profileMetadata)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.auth0_user_metadata),
                            gson)
                        Log.e("DEBUG", "Updated metadata! $gson")
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.setup_status_identifier),
                            profileMetadata.size>1)
                        userMetadataDeferred.complete(profileMetadata.size>1)
                    }
                })
            return userMetadataDeferred
        }

        fun updateUserMetadata(context: Context, userId: String, accessToken: String, usersClient: UsersAPIClient, metadata: Map<String, String>) {
            // Call updateMetadata with the id of the user to update, and the map of data
            usersClient.updateMetadata(userId, metadata)
                .start(object: Callback<UserProfile, ManagementException> {
                    override fun onFailure(exception: ManagementException) {
                        // Something went wrong!
                        Log.e("DEBUG", "Failed to update metadata!")
                        Log.e("DEBUG", "$exception")
                    }

                    override fun onSuccess(profile: UserProfile) {
                        val profileMetadata = profile.getUserMetadata()
                        val gson = Gson().toJson(profileMetadata)
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.auth0_user_metadata),
                            gson)
                        Log.e("DEBUG", "Updated metadata! $gson")
                        SharedPreferencesStorage(context).store(context.resources.getString(R.string.setup_status_identifier),
                            profileMetadata.size>1)
                    }
                })
        }

        fun retrieveAccessTokenAsync(context: Context, credentialsManager: CredentialsManager): CompletableDeferred<String> {
            val deferred = CompletableDeferred<String>()
            credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
                override fun onFailure(exception: CredentialsManagerException) {
                    // Something went wrong!
                    Log.e("DEBUG", "Failed to refresh credentials: $exception")
                    deferred.cancel()
                }
                override fun onSuccess(result: Credentials) {
                    // We have the user's credentials!
                    SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_identifier),
                        result.accessToken)
                    SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_expiry),
                        result.expiresAt.time)
                    SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_refresh),
                        result.refreshToken)
                    SharedPreferencesStorage(context).store(context.resources.getString(R.string.token_user_id),
                        result.user.getId())
                    SharedPreferencesStorage(context).store(context.resources.getString(R.string.login_status_identifier),
                        true)
                    deferred.complete(result.accessToken)
                }
            })
            return deferred
        }
    }
}