package com.example.lakbayaral.data.repository

import com.example.lakbayaral.data.models.*

/**
 * Local data source with curated historical content for Pampanga municipalities.
 * Extend this file to add more municipalities and their content.
 */
object LocalDataSource {

    // ── Lubao Historical Places ────────────────────────────────────────────────
    private val lubaoPlaces = listOf(
        HistoricalObject(
            id           = "san_pedro_lubao",
            name         = "St. Peter the Apostle Parish Church",
            type         = ObjectType.PLACE,
            description  = "One of the oldest churches in Pampanga, the St. Peter the Apostle Parish " +
                    "Church in Lubao dates back to the Spanish colonial period, originally built " +
                    "in 1572. The church is a fine example of Baroque architecture and has " +
                    "survived several earthquakes and the eruption of Mt. Pinatubo in 1991.",
            significance = "The church served as the center of spiritual and civic life for the " +
                    "people of Lubao for over 400 years. It witnessed key events in Philippine " +
                    "history including the resistance against colonial rule.",
            yearBuilt    = "1572",
            modelAsset   = "models/church.glb"
        ),
        HistoricalObject(
            id           = "lubao_bridge",
            name         = "Old Lubao Bridge",
            type         = ObjectType.PLACE,
            description  = "The Old Lubao Bridge spanning the Lubao River was a critical " +
                    "infrastructure during the American colonial period. It enabled commerce " +
                    "and communication between Lubao and surrounding municipalities.",
            significance = "The bridge symbolizes the modernization era of Lubao under American " +
                    "administration and remains a heritage structure recognized by the National " +
                    "Historical Commission of the Philippines.",
            yearBuilt    = "1910",
            modelAsset   = "models/bridge.glb"
        ),
        HistoricalObject(
            id           = "lubao_plaza",
            name         = "Lubao Town Plaza",
            type         = ObjectType.PLACE,
            description  = "The historic town plaza of Lubao has been the site of countless " +
                    "civic events, proclamations, and celebrations since the Spanish era. " +
                    "It features a monument to the town's founding and a traditional bandstand.",
            significance = "The plaza represents the democratic and civic heart of Lubao, " +
                    "where major historical announcements and community gatherings have taken " +
                    "place for centuries.",
            yearBuilt    = "c. 1600",
            modelAsset   = "models/monument.glb"
        )
    )

    // ── Lubao Historical People ────────────────────────────────────────────────
    private val lubaopeople = listOf(
        HistoricalObject(
            id           = "general_tinio",
            name         = "General Manuel Tinio",
            type         = ObjectType.PERSON,
            description  = "General Manuel Tinio was one of the youngest generals of the " +
                    "Philippine Revolution, born in Nueva Ecija but closely associated with " +
                    "Pampanga. He commanded forces against Spanish colonial rule and later " +
                    "against American occupation.",
            significance = "Tinio represents the fierce resistance of Central Luzon patriots " +
                    "against colonial oppression. His military campaigns in the region inspired " +
                    "future generations of Filipino nationalists.",
            yearBuilt    = "1876–1930",
            modelAsset   = "models/bust_general.glb"
        ),
        HistoricalObject(
            id           = "don_macabulos",
            name         = "Don Francisco Macabulos",
            type         = ObjectType.PERSON,
            description  = "Don Francisco Macabulos was a Kapampangan chieftain and revolutionary " +
                    "leader who led the uprising against Spanish authorities in Pampanga during " +
                    "the Philippine Revolution of 1896.",
            significance = "Macabulos was instrumental in organizing Kapampangan resistance and " +
                    "is considered one of the greatest heroes of Pampanga, representing the " +
                    "fighting spirit of the region.",
            yearBuilt    = "1861–1897",
            modelAsset   = "models/bust_chief.glb"
        )
    )

    // ── Lubao Quiz ─────────────────────────────────────────────────────────────
    private val lubaoQuiz = listOf(
        QuizQuestion(
            id           = "q1",
            question     = "In what year was the St. Peter the Apostle Parish Church in Lubao originally established?",
            choices      = listOf("1521", "1572", "1635", "1700"),
            correctIndex = 1,
            explanation  = "The church was originally built in 1572, making it one of the oldest in Pampanga."
        ),
        QuizQuestion(
            id           = "q2",
            question     = "What major natural disaster affected the St. Peter the Apostle Parish Church in 1991?",
            choices      = listOf("Earthquake", "Typhoon Yolanda", "Eruption of Mt. Pinatubo", "Flood"),
            correctIndex = 2,
            explanation  = "The 1991 eruption of Mt. Pinatubo deposited heavy lahar around the church area."
        ),
        QuizQuestion(
            id           = "q3",
            question     = "What is the primary significance of the Old Lubao Bridge?",
            choices      = listOf(
                "It was built during the Spanish era",
                "It enabled commerce between Lubao and other municipalities",
                "It was the site of a famous battle",
                "It was built by Filipino laborers only"
            ),
            correctIndex = 1,
            explanation  = "The bridge enabled commerce and communication during the American colonial period."
        ),
        QuizQuestion(
            id           = "q4",
            question     = "General Manuel Tinio was known for being one of the youngest generals during:",
            choices      = listOf("World War II", "The Philippine Revolution", "Korean War", "Spanish-American War"),
            correctIndex = 1,
            explanation  = "Tinio commanded Philippine revolutionary forces against Spanish colonial rule."
        ),
        QuizQuestion(
            id           = "q5",
            question     = "Don Francisco Macabulos led an uprising against which colonial power?",
            choices      = listOf("American", "Spanish", "British", "Dutch"),
            correctIndex = 1,
            explanation  = "Macabulos led Kapampangan resistance against Spanish authorities in 1896."
        ),
        QuizQuestion(
            id           = "q6",
            question     = "What architectural style is the St. Peter the Apostle Parish Church?",
            choices      = listOf("Gothic", "Baroque", "Renaissance", "Romanesque"),
            correctIndex = 1,
            explanation  = "The church is a fine example of Baroque architecture from the Spanish colonial era."
        ),
        QuizQuestion(
            id           = "q7",
            question     = "Which government body recognizes the Old Lubao Bridge as a heritage structure?",
            choices      = listOf(
                "Department of Tourism",
                "National Historical Commission of the Philippines",
                "UNESCO",
                "National Museum"
            ),
            correctIndex = 1,
            explanation  = "The National Historical Commission of the Philippines (NHCP) officially recognizes it."
        ),
        QuizQuestion(
            id           = "q8",
            question     = "What does the Lubao Town Plaza primarily represent?",
            choices      = listOf(
                "Military power",
                "Spanish dominance",
                "Civic and democratic life",
                "Economic prosperity"
            ),
            correctIndex = 2,
            explanation  = "The plaza represents the civic heart of Lubao, hosting community gatherings for centuries."
        )
    )

    // ── Municipality Catalog ───────────────────────────────────────────────────
    val municipalities = listOf(
        Municipality(
            id               = "lubao",
            name             = "Lubao",
            province         = "Pampanga",
            description      = "Lubao is a first-class municipality in Pampanga known for its rich " +
                    "history, heritage churches, and its famous traditional delicacy, the Tibuk-tibuk. " +
                    "It was one of the earliest settlements in Central Luzon during the Spanish era.",
            imageRes         = "lubao_hero",
            isUnlocked       = true,
            historicalPlaces = lubaoPlaces,
            historicalPeople = lubaopeople,
            quiz             = lubaoQuiz
        ),
        Municipality(
            id          = "bacolor",
            name        = "Bacolor",
            province    = "Pampanga",
            description = "Bacolor, the old capital of Pampanga, was buried under volcanic lahar after " +
                    "the 1991 Mt. Pinatubo eruption. Its half-buried church is a haunting reminder " +
                    "of nature's power and human resilience.",
            imageRes    = "bacolor_hero",
            isUnlocked  = false,
            quiz        = emptyList()
        ),
        Municipality(
            id          = "guagua",
            name        = "Guagua",
            province    = "Pampanga",
            description = "Guagua is a historic municipality famous for its role in the Philippine " +
                    "Revolution and its vibrant cultural traditions including Lenten rites.",
            imageRes    = "guagua_hero",
            isUnlocked  = false,
            quiz        = emptyList()
        )
    )

    fun getMunicipality(id: String): Municipality? = municipalities.find { it.id == id }
}
