package com.latihan.storyou.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class AuthPreferences @Inject constructor(
   @ApplicationContext private val context: Context
) {
   private val Context.datastore by preferencesDataStore("auth")

   companion object {
      private val TOKEN_KEY = stringPreferencesKey("auth_token")
      private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
   }

   suspend fun saveAuthToken(token: String?) {
      context.datastore.edit { preferences ->
         token?.let {
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGGED_IN] = true
         }
      }
   }

   suspend fun clearSession() {
      context.datastore.edit { preferences ->
         preferences.remove(TOKEN_KEY)
         preferences[IS_LOGGED_IN] = false
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
}