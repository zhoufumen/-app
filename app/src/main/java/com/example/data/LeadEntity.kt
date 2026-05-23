package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accountName: String,
    val country: String,
    val source: String,             // X, YouTube, TikTok
    val content: String,            // Contact inquiry content
    val keyword: String,            // e.g. 挖掘机, 矿卡
    val timestamp: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false,
    val avatarUrl: String = "",
    val externalLink: String = "",
    val email: String = "",
    val phone: String = "",
    val platformId: String = ""
)
