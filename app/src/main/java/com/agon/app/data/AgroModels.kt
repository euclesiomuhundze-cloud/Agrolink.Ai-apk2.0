package com.agon.app.data

import androidx.annotation.DrawableRes

enum class Severity { BAIXA, MEDIA, ALTA, SAUDAVEL }

enum class WeatherCondition { SOL, NUBLADO, CHUVA, TEMPESTADE }

enum class PriceTrend { SUBIU, DESCEU, ESTAVEL }

data class CropDisease(
    val id: String,
    val name: String,
    val cropType: String,
    val severity: Severity,
    val symptoms: String,
    val treatment: String,
    val prevention: String,
)

data class SampleLeaf(
    val id: String,
    val label: String,
    @DrawableRes val imageRes: Int,
    val diseaseId: String?, // null = saudável
)

data class DiagnosisRecord(
    val id: String,
    val leaf: SampleLeaf,
    val disease: CropDisease,
    val timestamp: Long,
)

data class WeatherDay(
    val dayLabel: String,
    val dateLabel: String,
    val condition: WeatherCondition,
    val tempHigh: Int,
    val tempLow: Int,
    val rainChance: Int,
    val humidity: Int,
    val advice: String,
)

data class MarketPrice(
    val cropName: String,
    val category: String,
    val unit: String,
    val price: Int, // in local currency units (Kz)
    val trend: PriceTrend,
    val changePercent: Int,
)

data class FarmingTip(
    val id: String,
    val title: String,
    val category: String,
    val contentPt: String,
    val contentLocal: String,
    val localLanguageName: String,
)

data class ProduceListing(
    val id: String,
    val farmerName: String,
    val cropName: String,
    val quantity: String,
    val pricePerUnit: Int,
    val unit: String,
    val location: String,
    val postedAgo: String,
    val verified: Boolean,
)
