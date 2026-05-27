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
    private val guaguaPlaces = listOf(
        HistoricalObject(
            id           = "betis_church",
            name         = "Betis Church",
            type         = ObjectType.PLACE,
            description  = "Founded in 1607 as an Augustinian mission church in Betis, Guagua, the Santiago Apostol Parish Church is a Baroque masterpiece famous for its intricately painted ceiling murals, which earned it the title 'Sistine Chapel of Pampanga.' The structure features a retablo-like altar, ornate wooden carvings, and centuries-old religious art. It was declared a National Cultural Treasure on November 5, 2001, cementing its status as one of the country’s most important heritage churches.",
            significance = "The church represents the highest expression of local ecclesiastical art and architecture in the region. Its ceiling frescoes and Baroque design showcase the fusion of Filipino artistry and Catholic devotion. As a National Cultural Treasure, Betis Church holds exceptional cultural, artistic, and historical value for the entire Philippines, making it a key landmark in Pampanga's heritage.",
            yearBuilt    = "1607",
            modelAsset   = "models/BetisChurch.glb"
        ),
        HistoricalObject(
            id           = "lopez_mansion",
            name         = "Lopez Mansion",
            type         = ObjectType.PLACE,
            description  = "Built in 1929 by sugar magnate Don Alejandro Lopez, the Lopez Mansion is the first all-concrete house in Pampanga. Designed in Greek revival style, it was hailed as 'The Pride of Guagua Pampanga' in 1935. After falling into disrepair, the mansion underwent a full restoration in 2016 and now serves as an elegant events venue, blending early 20th-century luxury with modern heritage conservation.",
            significance = "The mansion symbolizes the economic prosperity of Guagua during the American colonial period and marks a pioneering architectural achievement in the province. Its all-concrete construction represented a bold departure from traditional wood-and-stone bahay na bato. Restored to its former glory, the Lopez Mansion stands as a premier heritage landmark and a functional cultural space, bridging past and present.",
            yearBuilt    = "1929",
            modelAsset   = "models/LopezMansion.glb"
        ),
        HistoricalObject(
            id           = "immaculate_conception_parish_church",
            name         = "Immaculate Conception Parish Church",
            type         = ObjectType.PLACE,
            description  = "Founded in 1590 by Augustinian Friars, the Immaculate Conception Parish Church is a 17th-century Baroque church prominently located at Plaza Burgos in the heart of Guagua. Its stone façade, bell tower, and adjoining convent showcase classic Spanish colonial architecture. The church bears a historical marker from the National Historical Commission of the Philippines (NHCP), recognizing its enduring heritage and cultural significance.",
            significance = "As one of the earliest Augustinian mission churches in Pampanga, it served as the spiritual and communal center of Guagua for over four centuries. Its Baroque architecture and continued presence reflect the town's deep-rooted Catholic faith and resilience through different historical eras. The NHCP marker solidifies its place as a vital heritage landmark.",
            yearBuilt    = "1590",
            modelAsset   = "models/Immaculate.glb"
        ),
    )
    private val guaguaPeople = listOf(
        HistoricalObject(
            id           = "aurelio_tolentino",
            name         = "Aurelio Tolentino",
            type         = ObjectType.PERSON,
            description  = "Aurelio Tolentino, born on October 13, 1867, was an acclaimed playwright, poet, and revolutionary from Sto. Cristo, Guagua. An active Katipunan member, he was a close associate of Andres Bonifacio and a signatory of the Philippine Declaration of Independence. His most famous work, the allegorical play 'Kahapon, Ngayon, at Bukas,' bravely critiqued American colonization, leading to his arrest. He died in 1915 but his legacy continues through his powerful literary and revolutionary contributions.",
            significance = "He is a towering figure in Kapampangan and Philippine literature, a revolutionary who used his pen for the country's freedom. His works are studied for their historical and nationalistic importance. A prominent historical marker and monument dedicated to him stands in Guagua's Plaza Burgos (Manuel P. Santiago Park).",
            yearBuilt    = "1867-1915",
            modelAsset   = "models/Aurelio.glb"
        ),
        HistoricalObject(
            id           = "nicolasa_dayrit_panlilio",
            name         = "Nicolasa Dayrit-Panlilio",
            type         = ObjectType.PERSON,
            description  = "Nicolasa Dayrit-Panlilio, born on September 10, 1874, in San Fernando, Pampanga, was a heroic non-combatant during the Philippine-American War. A well-educated woman and an accomplished pianist, she is best known for her courageous act in Guagua in 1899. There, she intervened and helped pacify the furious General Antonio Luna during his heated confrontation with General Tomas Mascardo, preventing bloodshed among Filipino forces.",
            significance = "She symbolizes the quiet strength and critical role of women in Philippine history. Her diplomatic courage in the face of military conflict prevented a potentially disastrous division in the revolutionary army. She was reintroduced to a new generation in 2017 with the children's book 'Ing Bayaning Fernandino.'",
            yearBuilt    = "1874-1945",
            modelAsset   = "models/nicolasa.glb"
        ),
        HistoricalObject(
            id           = "dona_alejandro_lopez",
            name         = "Don Alejandro Lopez",
            type         = ObjectType.PERSON,
            description  = "Don Alejandro Lopez was a prominent sugar magnate born in Guagua on May 16, 1883. A successful businessman, he served as director and vice president of the Pampanga Sugar Mills Planters Association. His enduring legacy is the magnificent Lopez Mansion he built in 1929, the first all-concrete house in Pampanga, which was celebrated in 1935 as 'The Pride of Guagua Pampanga'.",
            significance = "He is a powerful symbol of the economic prosperity and modernizing spirit of Guagua during the early 20th century. The mansion he built is an architectural pioneer and a restored heritage icon, representing the town's ambition and cultural pride.",
            yearBuilt    = "1883-Unknown",
            modelAsset   = "models/alejandro.glb"
        ),
    )
    private val guaguaQuiz = listOf(
        QuizQuestion(
            id           = "guagua_q1",
            question     = "What title is Betis Church famously known by?",
            choices      = listOf("The Baroque Giant", "Sistine Chapel of Pampanga", "Mother of All Churches", "Chapel of the Plains"),
            correctIndex = 1,
            explanation  = "Betis Church is called the 'Sistine Chapel of Pampanga' because of its elaborate ceiling murals painted by local artists."
        ),
        QuizQuestion(
            id           = "guagua_q2",
            question     = "In what year was Betis Church declared a National Cultural Treasure?",
            choices      = listOf("1998", "2001", "2010", "1987"),
            correctIndex = 1,
            explanation  = "Santiago Apostol Parish Church, or Betis Church, was officially declared a National Cultural Treasure on November 5, 2001."
        ),
        QuizQuestion(
            id           = "guagua_q3",
            question     = "Who built the Lopez Mansion, the first all-concrete house in Pampanga?",
            choices      = listOf("Aurelio Tolentino", "Agapito Conchu", "Don Alejandro Lopez", "Jose B. Lingad"),
            correctIndex = 2,
            explanation  = "Don Alejandro Lopez, a sugar magnate born on May 16, 1883, built the mansion in 1929."
        ),
        QuizQuestion(
            id           = "guagua_q4",
            question     = "What makes the Lopez Mansion historically significant in Pampanga?",
            choices      = listOf("It was a Katipunan hideout", "It was the first all-concrete house", "It was a Spanish fort", "It was a former church"),
            correctIndex = 1,
            explanation  = "The Lopez Mansion, completed in 1929, is recognized as the first fully concrete residential structure in the province."
        ),
        QuizQuestion(
            id           = "guagua_q5",
            question     = "Which revolutionary playwright from Guagua wrote 'Kahapon, Ngayon, at Bukas'?",
            choices      = listOf("Jose Rizal", "Aurelio Tolentino", "Andres Bonifacio", "Emilio Jacinto"),
            correctIndex = 1,
            explanation  = "Aurelio Tolentino, a Katipunan member and signatory of the Declaration of Independence, wrote the allegorical play criticizing colonialism."
        ),
        QuizQuestion(
            id           = "guagua_q6",
            question     = "What important document did Aurelio Tolentino sign?",
            choices      = listOf("Treaty of Paris", "1899 Constitution", "Philippine Declaration of Independence", "Pact of Biak-na-Bato"),
            correctIndex = 2,
            explanation  = "Tolentino was a signatory of the Philippine Declaration of Independence proclaimed in Kawit, Cavite on June 12, 1898."
        ),
        QuizQuestion(
            id           = "guagua_q7",
            question     = "How did Nicolasa Dayrit-Panlilio make history during the Philippine-American War?",
            choices      = listOf("She led a battalion", "She pacified General Antonio Luna", "She translated the constitution", "She founded a hospital"),
            correctIndex = 1,
            explanation  = "She courageously intervened and helped calm General Antonio Luna during a confrontation in Guagua, preventing conflict among Filipino forces."
        ),
        QuizQuestion(
            id           = "guagua_q8",
            question     = "What year was the Immaculate Conception Parish Church in Guagua founded?",
            choices      = listOf("1572", "1607", "1590", "1929"),
            correctIndex = 2,
            explanation  = "The church was founded by Augustinian Friars in 1590, making it one of the oldest mission churches in Pampanga."
        )
    )

    private val guaguaOnlineQuiz = listOf(
        QuizQuestion(
            id           = "guagua_online_q1",
            question     = "Why is Betis Church referred to as the 'Sistine Chapel of Pampanga'?",
            choices      = listOf("Its golden altar", "Its painted ceiling frescoes", "Its large dome", "Its bell tower"),
            correctIndex = 1,
            explanation  = "The nickname comes from the richly painted ceiling murals that cover the entire interior, similar to the Sistine Chapel."
        ),
        QuizQuestion(
            id           = "guagua_online_q2",
            question     = "When was Betis Church given National Cultural Treasure status?",
            choices      = listOf("November 5, 2001", "June 12, 1998", "January 1, 2010", "December 25, 1987"),
            correctIndex = 0,
            explanation  = "Betis Church was officially declared a National Cultural Treasure by the National Museum of the Philippines on November 5, 2001."
        ),
        QuizQuestion(
            id           = "guagua_online_q3",
            question     = "What was the Lopez Mansion called in 1935?",
            choices      = listOf("The White House of Guagua", "The Pride of Guagua Pampanga", "The Grand Manor", "The Sugar Palace"),
            correctIndex = 1,
            explanation  = "Due to its modern all-concrete design and elegance, the mansion was hailed as 'The Pride of Guagua Pampanga' in 1935."
        ),
        QuizQuestion(
            id           = "guagua_online_q4",
            question     = "Don Alejandro Lopez was a prominent figure in which industry?",
            choices      = listOf("Textile", "Sugar", "Tobacco", "Shipbuilding"),
            correctIndex = 1,
            explanation  = "He was a sugar magnate and served as director and vice president of the Pampanga Sugar Mills Planters Association."
        ),
        QuizQuestion(
            id           = "guagua_online_q5",
            question     = "What was the title of Aurelio Tolentino's famous allegorical play?",
            choices      = listOf("Noli Me Tangere", "El Filibusterismo", "Kahapon, Ngayon, at Bukas", "Tanikalang Ginto"),
            correctIndex = 2,
            explanation  = "'Kahapon, Ngayon, at Bukas' (Yesterday, Today, and Tomorrow) was a bold allegory against American colonization that led to his arrest."
        ),
        QuizQuestion(
            id           = "guagua_online_q6",
            question     = "Which revolutionary leader was Aurelio Tolentino closely associated with?",
            choices      = listOf("Antonio Luna", "Emilio Aguinaldo", "Andres Bonifacio", "Gregorio del Pilar"),
            correctIndex = 2,
            explanation  = "Tolentino was an active member of the Katipunan and a close associate of Supremo Andres Bonifacio."
        ),
        QuizQuestion(
            id           = "guagua_online_q7",
            question     = "Nicolasa Dayrit-Panlilio is best remembered for preventing bloodshed involving which two generals?",
            choices      = listOf("Aguinaldo and Bonifacio", "Luna and Mascardo", "MacArthur and Quezon", "Rizal and del Pilar"),
            correctIndex = 1,
            explanation  = "She stepped in when General Antonio Luna was enraged at General Tomas Mascardo, calming the situation and averting an internal conflict."
        ),
        QuizQuestion(
            id           = "guagua_online_q8",
            question     = "What historical marker does the Immaculate Conception Parish Church hold?",
            choices      = listOf("UNESCO Heritage plaque", "NHCP historical marker", "National Museum marker", "Katipunan flag marker"),
            correctIndex = 1,
            explanation  = "The church bears a marker from the National Historical Commission of the Philippines, recognizing its centuries-old heritage."
        )
    )

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
            description = "Guagua is a first-class municipality in Pampanga known for its rich history, the National Cultural Treasure Betis Church (dubbed the \"Sistine Chapel of Pampanga\"), the revolutionary playwright Aurelio Tolentino, and well-preserved heritage landmarks such as the Lopez Mansion. It played a significant role in the Philippine Revolution and remains a vital cultural and historical center in Central Luzon.",
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
