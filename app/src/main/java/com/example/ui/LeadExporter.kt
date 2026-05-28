package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.LeadEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

object LeadExporter {

    private fun escapeCsvField(field: String?): String {
        if (field == null) return ""
        val cleaned = field.replace("\"", "\"\"")
        return if (cleaned.contains(",") || cleaned.contains("\n") || cleaned.contains("\r") || cleaned.contains("\"")) {
            "\"$cleaned\""
        } else {
            cleaned
        }
    }

    fun getPlatformSearchUrl(lead: LeadEntity): String {
        val cleanName = lead.accountName.trim()
        val nameWithoutAt = cleanName.removePrefix("@")
        val encodedName = android.net.Uri.encode(nameWithoutAt)
        return when (lead.source.uppercase(Locale.getDefault())) {
            "X", "TWITTER" -> {
                // Use search query to ensure we find matching users/discussions instead of direct 404 error
                "https://x.com/search?q=$encodedName"
            }
            "YOUTUBE" -> {
                "https://www.youtube.com/results?search_query=${android.net.Uri.encode(cleanName)}"
            }
            "TIKTOK" -> {
                // Search for the user name on TikTok search results to avoid direct profile 404
                "https://www.tiktok.com/search?q=$encodedName"
            }
            else -> {
                "https://www.google.com/search?q=site:${lead.source.lowercase(Locale.getDefault())}.com+$encodedName"
            }
        }
    }

    fun exportLeadsToCsv(context: Context, leads: List<LeadEntity>, selectedLanguage: String) {
        if (leads.isEmpty()) {
            val emptyMsg = when (selectedLanguage) {
                "中文" -> "当前列表为空，无法进行导出"
                "English" -> "The current list is empty. Nothing to export."
                "日本語" -> "リストが空のためエクスポートできません"
                "Português" -> "A lista está vazia, impossível exportar."
                "العربية" -> "القائمة فارغة، لا يمكن التصدير."
                else -> "Nothing to export"
            }
            Toast.makeText(context, emptyMsg, Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Determine headers based on selected language
            val headers = when (selectedLanguage) {
                "中文" -> listOf("账号名称", "平台渠道", "平台账号ID", "买家邮箱", "联系电话", "目标国家", "设备关键词", "详细采购询盘需求", "精准主页与找人链接", "是否已收藏", "采集时间")
                "English" -> listOf("Account/Contact", "Platform Source", "Platform ID", "Buyer Email", "Contact Phone", "Target Country", "Equipment Keyword", "Inquiry/Buyer Needs", "Direct Profile/Search Link", "Saved Status", "Time Grabbed")
                "日本語" -> listOf("アカウント名", "プラットフォーム", "プラットフォームID", "バイヤーメール", "連絡先電話", "対象地域", "設備キーワード", "お問い合わせ内容", "ダイレクト連絡リンク", "保存ステータス", "取得日時")
                "Português" -> listOf("Nome da Conta", "Plataforma", "Plataforma ID", "E-mail do Comprador", "Telefone de Contato", "País de Destino", "Palavra-chave", "Inquérito/Demanda", "Link Direto de Contato", "Status de Salvo", "Data de Coleta")
                "العربية" -> listOf("اسم الحساب", "قناة المنصة", "معرف المنصة", "البريد الإلكتروني", "رقم الهاتف", "البلد المستهدف", "الكلمة المفتاحية للمعدات", "تفاصيل الاستفسار والطلب", "رابط التواصل المباشر", "حالة الحفظ", "تاريخ الالتقاط")
                else -> listOf("Account/Contact", "Platform Source", "Platform ID", "Buyer Email", "Contact Phone", "Target Country", "Equipment Keyword", "Inquiry/Buyer Needs", "Direct Profile/Search Link", "Saved Status", "Time Grabbed")
            }

            // Standard date formatter
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            // Prepare cache folder
            val exportsDir = File(context.cacheDir, "exports")
            if (!exportsDir.exists()) {
                exportsDir.mkdirs()
            }

            // Create CSV file name with timestamp
            val fileTimestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "B2B_Leads_Export_$fileTimestamp.csv"
            val file = File(exportsDir, filename)

            // Write with BOM (Byte Order Mark) for Excel compatibility
            FileOutputStream(file).use { fos ->
                // Write standard UTF-8 BOM
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
                
                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    // Write Header
                    writer.write(headers.joinToString(",") { escapeCsvField(it) } + "\n")

                    // Write each lead row
                    leads.forEach { lead ->
                        val formattedTime = sdf.format(Date(lead.timestamp))
                        val savedStatus = when (selectedLanguage) {
                            "中文" -> if (lead.isSaved) "已收藏" else "未收藏"
                            "English" -> if (lead.isSaved) "Saved" else "Unsaved"
                            "日本語" -> if (lead.isSaved) "お気に入り" else "未保存"
                            "Português" -> if (lead.isSaved) "Salvo" else "Não Salvo"
                            "العربية" -> if (lead.isSaved) "محفوظ" else "غير محفوظ"
                            else -> if (lead.isSaved) "Saved" else "Unsaved"
                        }

                        val row = listOf(
                            lead.accountName,
                            lead.source,
                            lead.platformId,
                            lead.email,
                            lead.phone,
                            lead.country,
                            lead.keyword,
                            lead.content,
                            getPlatformSearchUrl(lead),
                            savedStatus,
                            formattedTime
                        )
                        writer.write(row.joinToString(",") { escapeCsvField(it) } + "\n")
                    }
                    writer.flush()
                }
            }

            // Share file via FileProvider
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/comma-separated-values"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "B2B_Leads_Table_Export")
                putExtra(Intent.EXTRA_TEXT, when (selectedLanguage) {
                    "中文" -> "这是由B2B海外拓客引擎自动生成的精准客群表格。"
                    "English" -> "This is a detailed lead sheet generated by B2B Overseas Target Leads Engine."
                    "日本語" -> "これはB2B海外リード獲得エンジンが自動生成した顧客リストです。"
                    "Português" -> "Esta é a lista detalhada de leads gerada pelo B2B Overseas Leads Engine."
                    "العربية" -> "هذه قائمة عملاء مفصلة تم إنشاؤها وتصديرها بواسطة B2B Overseas Leads Engine."
                    else -> "B2B Oversea Leads Export"
                })
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserTitle = when (selectedLanguage) {
                "中文" -> "导出数据表格"
                "English" -> "Export Leads Data Table"
                "日本語" -> "リードデータテーブルのエクスポート"
                "Português" -> "Exportar Tabela de Dados de Leads"
                "العربية" -> "تصدير جدول بيانات العملاء"
                else -> "Export Table"
            }
            context.startActivity(Intent.createChooser(shareIntent, chooserTitle))

        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = when (selectedLanguage) {
                "中文" -> "表格导出失败: ${e.message}"
                "English" -> "Export failed: ${e.message}"
                "日本語" -> "エクスポートに失敗しました: ${e.message}"
                "Português" -> "Falha na exportação: ${e.message}"
                "العربية" -> "فشل التصدير: ${e.message}"
                else -> "Export failed"
            }
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }
}
