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
            id           = "san_agustin_lubao",
            name         = "San Agustin Lubao Church",
            type         = ObjectType.PLACE,
            description  = "Founded in 1572 as one of the earliest Augustinian mission churches in Central Luzon. The first church structure was built near the riverbanks of Gato using wood, bamboo, and nipa materials. During the British occupation of Manila in 1762, theology students from Manila were transferred to the Lubao convent. The church later became an emergency station for revolutionary forces in 1898, a hospital during the American occupation in 1899, and suffered damage during Japanese attacks in 1942. It also served as an evacuation shelter after the 1991 Mt. Pinatubo eruption.",
            significance = "The church symbolizes Lubao’s importance as one of the earliest Christian and missionary centers in Pampanga. It played major religious, political, and historical roles throughout Spanish, American, Japanese, and modern Philippine history.",
            yearBuilt    = "1572",
            modelAsset   = "models/church.glb"
        ),
        HistoricalObject(
            id           = "arrastia_house",
            name         = "Arrastia House (Casa Lubao)",
            type         = ObjectType.PLACE,
            description  = "The Arrastia House is the grand ancestral home of one of Lubao's most influential Spanish-Filipino families. It served as a center of social gatherings and economic power during the early American colonial period, reflecting the wealth and status of the sugar and rice plantation elite.",
            significance = "The Arrastia House symbolizes the old landed gentry that shaped Lubao's history. It is historically notable for being the place where a young Diosdado Macapagal was recognized and financially supported for his education, helping him rise from poverty to become President of the Philippines.",
            yearBuilt    = "1920",
            modelAsset   = "models/arrastia.glb"
        ),
        HistoricalObject(
            id           = "lubao_municipality",
            name         = "Municipal Hall of Lubao",
            type         = ObjectType.PLACE,
            description  = "The Municipal Hall of Lubao is the center of local government where official administrative and public service operations are conducted. It represents the development of Lubao’s political system from its historical roots during the Spanish period into a modern organized municipality. The building has served as the main seat of governance where local leaders implement policies and community programs for the town.",
            significance = "The Municipal Hall symbolizes modern governance and civic leadership in Lubao. It is the place where decisions for public service, development projects, and municipal administration are made, reflecting the town’s progress and modernization.",
            yearBuilt    = "Modern development (estimated 20th century–present)",
            modelAsset   = "models/Municipality.glb"
        )
    )

    // ── Lubao Historical People ────────────────────────────────────────────────
    private val lubaopeople = listOf(
        HistoricalObject(
            id           = "jose_b_lingad",
            name         = "Jose B. Lingad",
            type         = ObjectType.PERSON,
            description  = "Jose B. Lingad was a Filipino politician and war veteran born in Lubao, Pampanga. He served as a representative of Pampanga and later became the Secretary of Labor. He was also known for his role in the Philippine resistance during World War II as part of the guerrilla movement against Japanese occupation.",
            significance = "He represents Lubao’s contribution to national leadership and wartime resistance. His legacy is honored in Pampanga through public institutions named after him, including hospitals and schools.",
            yearBuilt    = "1914 – 1980",
            modelAsset   = "models/JoseBLingad.glb"
        ),
        HistoricalObject(
            id           = "diosdado_p_macapagal",
            name         = "Diosdado P. Macapagal",
            type         = ObjectType.PERSON,
            description  = "Born in Lubao, Pampanga, Diosdado P. Macapagal rose from poverty and became the 9th President of the Philippines. He served as congressman, vice president, and president from 1961 to 1965. He promoted land reform, anti-corruption programs, and changed the celebration of Philippine Independence Day from July 4 to June 12.",
            significance = "Macapagal became one of Lubao’s most recognized national figures and contributed greatly to Philippine politics, economic reform, and national identity.",
            yearBuilt    = "1910–1997",
            modelAsset   = "models/Diosdado(Male).glb"
        ),
        HistoricalObject(
            id           = "gloria_macapagal_arroyo",
            name         = "Gloria Macapagal-Arroyo",
            type         = ObjectType.PERSON,
            description  = "Gloria Macapagal-Arroyo, daughter of Diosdado Macapagal, became the 14th President of the Philippines. She served as senator, vice president, president, and representative of Pampanga’s 2nd district. Her administration supported infrastructure, education, healthcare, and heritage conservation projects in Lubao.",
            significance = "Gloria Macapagal-Arroyo strengthened Lubao’s connection to national leadership and contributed to the municipality’s modernization and development projects.",
            yearBuilt    = "1947–Present",
            modelAsset   = "models/Diosdado(female).glb"
        )
    )

    // ── Lubao Quiz ─────────────────────────────────────────────────────────────
    private val lubaoQuiz = listOf(
        QuizQuestion(
            id           = "q1",
            question     = "What year was the San Agustin Church in Lubao founded?",
            choices      = listOf("1521", "1572", "1660", "1898"),
            correctIndex = 1,
            explanation  = "The San Agustin Church of Lubao was founded in 1572 and became one of the earliest Augustinian mission churches in Central Luzon."
        ),
        QuizQuestion(
            id           = "q2",
            question     = "What natural disaster caused Lubao churches to become evacuation shelters in 1991?",
            choices      = listOf("Earthquake", "Flood", "Eruption of Mt. Pinatubo", "Typhoon"),
            correctIndex = 2,
            explanation  = "After the eruption of Mt. Pinatubo in 1991, many residents evacuated to churches and schools in Lubao for safety."
        ),
        QuizQuestion(
            id           = "q3",
            question     = "What is the main function of the Municipal Hall of Lubao?",
            choices      = listOf(
                "Religious worship center",
                "Center of local government and public service",
                "Ancient trading port",
                "Military fortress"
            ),
            correctIndex = 1,
            explanation  = "The Municipal Hall is the center of governance where local government services and administration are managed."
        ),
        QuizQuestion(
            id           = "q4",
            question     = "What best describes the early settlement of Lubao before Spanish colonization?",
            choices      = listOf("Mountain fortress settlement", "River-based agricultural and trading community", "Industrial city center", "Foreign capital city"),
            correctIndex = 1,
            explanation  = "Early Lubao was a river-based community where Kapampangans lived, farmed, and engaged in local trade."
        ),
        QuizQuestion(
            id           = "q5",
            question     = "Who was a Lubao-born public servant known for his role in government and World War II resistance?",
            choices      = listOf("Diosdado Macapagal", "Manuel Roxas", "Jose B. Lingad", "Sergio Osmeña"),
            correctIndex = 2,
            explanation  = "Jose B. Lingad was a war veteran and public official from Lubao who served in government and participated in resistance efforts during World War II."
        ),
        QuizQuestion(
            id           = "q6",
            question     = "Which Philippine president from Lubao changed Independence Day to June 12?",
            choices      = listOf("Ferdinand Marcos", "Diosdado Macapagal", "Ramon Magsaysay", "Manuel Quezon"),
            correctIndex = 1,
            explanation  = "President Diosdado Macapagal officially moved the celebration of Philippine Independence Day from July 4 to June 12."
        ),
        QuizQuestion(
            id           = "q7",
            question     = "Who became the 14th President of the Philippines and was born in Lubao?",
            choices      = listOf(
                "Corazon Aquino",
                "Gloria Macapagal-Arroyo",
                "Miriam Defensor Santiago",
                "Sara Duterte"
            ),
            correctIndex = 1,
            explanation  = "Gloria Macapagal-Arroyo, daughter of Diosdado Macapagal, became the 14th President of the Philippines."
        ),
        QuizQuestion(
            id           = "q8",
            question     = "What position did Diosdado Macapagal hold before becoming President?",
            choices      = listOf(
                "Senator",
                "Chief Justice",
                "Vice President",
                "Governor"
            ),
            correctIndex = 2,
            explanation  = "Before serving as President from 1961 to 1965, Diosdado Macapagal first served as Vice President of the Philippines."
        )
    )

    private val lubaoOnlineQuiz = listOf(
        QuizQuestion(
            id           = "online_q1",
            question     = "Which event in 1991 turned churches in Lubao into evacuation areas?",
            choices      = listOf("Strong earthquake", "Volcanic eruption of Mt. Pinatubo", "Major flood", "Typhoon disaster"),
            correctIndex = 1,
            explanation  = "The eruption of Mt. Pinatubo in 1991 forced many residents to seek shelter in churches and schools around Lubao."
        ),
        QuizQuestion(
            id           = "online_q2",
            question     = "How was Lubao commonly described before Spanish colonization?",
            choices      = listOf("Industrialized settlement", "Foreign trading capital", "Agricultural and river-based community", "Mountain defense village"),
            correctIndex = 2,
            explanation  = "Before Spanish colonization, Lubao was known as a river-based agricultural community where locals farmed and traded."
        ),
        QuizQuestion(
            id           = "online_q3",
            question     = "In what year was the San Agustin Church in Lubao established?",
            choices      = listOf("1898", "1572", "1660", "1521"),
            correctIndex = 1,
            explanation  = "The San Agustin Church in Lubao was established in 1572 and is considered one of the oldest Augustinian churches in Central Luzon."
        ),
        QuizQuestion(
            id           = "online_q4",
            question     = "What is the primary role of the Lubao Municipal Hall?",
            choices      = listOf("Military headquarters", "Public market center", "Seat of local government and services", "Religious gathering place"),
            correctIndex = 2,
            explanation  = "The Municipal Hall serves as the center for government operations and public services in Lubao."
        ),
        QuizQuestion(
            id           = "online_q5",
            question     = "Which Lubao-born figure became known for public service and resistance during World War II?",
            choices      = listOf("Jose B. Lingad", "Manuel Roxas", "Sergio Osmeña", "Diosdado Macapagal"),
            correctIndex = 0,
            explanation  = "Jose B. Lingad was a public servant and war veteran from Lubao who participated in resistance activities during World War II."
        ),
        QuizQuestion(
            id           = "online_q6",
            question     = "Which president from Lubao officially changed Philippine Independence Day to June 12?",
            choices      = listOf("Ramon Magsaysay", "Ferdinand Marcos", "Diosdado Macapagal", "Manuel Quezon"),
            correctIndex = 2,
            explanation  = "President Diosdado Macapagal moved the celebration of Independence Day from July 4 to June 12."
        ),
        QuizQuestion(
            id           = "online_q7",
            question     = "Who was the 14th President of the Philippines and a native of Lubao?",
            choices      = listOf("Sara Duterte", "Miriam Defensor Santiago", "Gloria Macapagal-Arroyo", "Corazon Aquino"),
            correctIndex = 2,
            explanation  = "Gloria Macapagal-Arroyo, daughter of Diosdado Macapagal, became the country's 14th President."
        ),
        QuizQuestion(
            id           = "online_q8",
            question     = "What government position did Diosdado Macapagal hold before becoming President?",
            choices      = listOf("Governor", "Vice President", "Chief Justice", "Senator"),
            correctIndex = 1,
            explanation  = "Before becoming President, Diosdado Macapagal first served as the Vice President of the Philippines."
        ),
    )

    // ── Municipality Catalog ───────────────────────────────────────────────────
    // Guagua content slots are intentionally empty for now.
    // Add future Guagua AR places, historical people, and quizzes here without changing screens.
    private val guaguaPlaces = emptyList<HistoricalObject>()
    private val guaguaPeople = emptyList<HistoricalObject>()
    private val guaguaQuiz = emptyList<QuizQuestion>()
    private val guaguaOnlineQuiz = emptyList<QuizQuestion>()

    val municipalities = listOf(
        Municipality(
            id               = "lubao",
            name             = "Lubao",
            province         = "Pampanga",
            description      = "Lubao is a first-class municipality in Pampanga known for its rich history, heritage churches, ancient trading settlements, and famous delicacy called Tibuk-tibuk. It is considered one of the earliest and most historic settlements in Central Luzon.",
            imageRes         = "lubao_hero",
            isUnlocked       = true,
            historicalPlaces = lubaoPlaces,
            historicalPeople = lubaopeople,
            quiz             = lubaoQuiz,
            onlineQuiz       = lubaoOnlineQuiz
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
            historicalPlaces = guaguaPlaces,
            historicalPeople = guaguaPeople,
            quiz        = guaguaQuiz,
            onlineQuiz  = guaguaOnlineQuiz
        )
    )

    fun getMunicipality(id: String): Municipality? = municipalities.find { it.id == id }

    fun getMunicipalities(ids: Set<String>): List<Municipality> =
        municipalities.filter { it.id in ids }
}
