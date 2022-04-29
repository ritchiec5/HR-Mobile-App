package edu.singaporetech.hrapp.attendance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Class that handles saving and retrieving user preferences, utilizing Preferences DataStore. This
 * class may be utilized in either the ViewModel or an Activity, depending on what preferences are
 * being saved.
 */
class UserPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private val showChecked = booleanPreferencesKey("showChecked")

    val preferenceFlow: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[showChecked] ?: false
        }

    /**
     * Function to show updated attendance
     * @param showCompleted
     */
    suspend fun updateShowCompleted(showCompleted: Boolean) {
        dataStore.edit { preferences ->
            preferences[showChecked] = showCompleted
        }
    }
}