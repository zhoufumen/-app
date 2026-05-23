package com.example.data

import kotlinx.coroutines.flow.Flow

class LeadRepository(private val leadDao: LeadDao) {
    val allLeads: Flow<List<LeadEntity>> = leadDao.getAllLeads()
    val savedLeads: Flow<List<LeadEntity>> = leadDao.getSavedLeads()

    suspend fun insertLeads(leads: List<LeadEntity>) {
        leadDao.insertLeads(leads)
    }

    suspend fun insertLead(lead: LeadEntity) {
        leadDao.insertLead(lead)
    }

    suspend fun updateSavedStatus(id: Int, isSaved: Boolean) {
        leadDao.updateSavedStatus(id, isSaved)
    }

    suspend fun clearUnsavedLeads() {
        leadDao.clearUnsavedLeads()
    }

    suspend fun deleteLeadById(id: Int) {
        leadDao.deleteLeadById(id)
    }
}
