package com.latihan.storyou.data.local.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.latihan.storyou.data.remote.models.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Singleton

private val Context.datastore by preferencesDataStore("auth")
@Singleton
class AuthPreferences @Inject constructor(
   @ApplicationContext private val context: Context
) {
   companion object {
      private val TOKEN_KEY = stringPreferencesKey("auth_token")
      private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
      private val LOGIN_RESPONSE_KEY = stringPreferencesKey("login_response")
   }

   suspend fun saveAuthToken(token: String?) {
      withContext(Dispatchers.IO) {
         Log.d("CheckToken", "Entered saveAuthToken with token: $token")
         context.datastore.edit { preferences ->
            Log.d("CheckToken", "Auth Preferences Save Token: $token")
            token?.let {
               Log.d("CheckToken", "Auth Preferences Save Token Store: $token")
               preferences[TOKEN_KEY] = token
               preferences[IS_LOGGED_IN] = true
            }
         }
      }
   }

   suspend fun clearSession() {
      withContext(Dispatchers.IO) {
         context.datastore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(IS_LOGGED_IN)
            preferences.remove(LOGIN_RESPONSE_KEY)
         }
      }
   }

   suspend fun saveLoginResponse(response: LoginResponse) {
      Log.d("CheckLoginResponse", "Auth Preference Check Response id top: ${response.loginResult?.userId}")
      withContext(Dispatchers.IO) {
         Log.d("CheckLoginResponse", "Auth Preference Check Response id io: ${response.loginResult?.userId}")
         context.datastore.edit { preferences ->
            Log.d("CheckLoginResponse", "Auth Preference Check Response id edit: ${response
               .loginResult?.userId}")
            preferences[LOGIN_RESPONSE_KEY] = Gson().toJson(response)
            Log.d("CheckLoginResponse", "Auth Preference Check Response id after edit: ${Gson()
               .toJson(response
               .loginResult?.userId)}")
         }
      }
   }

   val authToken: Flow<String?> = context.datastore.data
      .map { preferences ->
         preferences[TOKEN_KEY]
      }

   val isLoggedIn: Flow<Boolean> = context.datastore.data
      .map { preferences ->
         preferences[IS_LOGGED_IN] ?: false
      }

   val loginResponse: Flow<LoginResponse?> = context.datastore.data
      .map { preferences ->
         preferences[LOGIN_RESPONSE_KEY]?.let { json ->
            Gson().fromJson(json, LoginResponse::class.java)
         }
      }
}