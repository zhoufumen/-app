package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY timestamp DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSavedLeads(): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeads(leads: List<LeadEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity)

    @Query("UPDATE leads SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Int, isSaved: Boolean)

    @Query("DELETE FROM leads WHERE isSaved = 0")
    suspend fun clearUnsavedLeads()

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteLeadById(id: Int)
}
