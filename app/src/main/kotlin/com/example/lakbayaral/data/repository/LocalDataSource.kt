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
    // Guagua curated AR places, historical people, and quiz content.
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

    private val bacolorPlaces = listOf(
        HistoricalObject(
            id           = "san_guillermo_church",
            name         = "San Guillermo Parish Church",
            type         = ObjectType.PLACE,
            description  = "San Guillermo Parish Church is Bacolor's best-known heritage landmark. The first church was built by Augustinian friars in 1576 on land associated with Don Guillermo Manabat. After earthquakes and later rebuilding, the church became famous worldwide when lahar from Mt. Pinatubo buried much of the structure in the 1990s. Its upper windows were adapted as entrances, while the altar and religious furnishings were preserved by the community.",
            significance = "The half-buried church stands as Bacolor's strongest symbol of faith, memory, and resilience after the Pinatubo lahar tragedy. It also preserves the town's Spanish colonial religious heritage and remains an active place of worship.",
            yearBuilt    = "1576",
            modelAsset   = "models/church.glb"
        ),
        HistoricalObject(
            id           = "simon_de_anda_monument",
            name         = "Simon de Anda Monument",
            type         = ObjectType.PLACE,
            description  = "The Simon de Anda Monument stands inside the Bacolor Municipal Hall premises. It honors Governor-General Simon de Anda y Salazar, who retreated to Bacolor after the British captured Manila and made the town the seat of the Spanish colonial government from October 6, 1762 to May 30, 1764.",
            significance = "The monument commemorates the period when Bacolor functioned as the capital of the Spanish government in the Philippines. It reminds visitors that the town was once central to national colonial history, not only provincial history.",
            yearBuilt    = "2012",
            modelAsset   = "models/Municipality.glb"
        ),
        HistoricalObject(
            id           = "our_lady_lourdes_shrine",
            name         = "Our Lady of Lourdes Shrine",
            type         = ObjectType.PLACE,
            description  = "The Our Lady of Lourdes Shrine in Cabetican is an archdiocesan shrine and pilgrimage site in Bacolor. Devotees from Cabetican and other parts of Pampanga gather here, especially during first Saturdays and local religious celebrations.",
            significance = "The shrine reflects Bacolor's strong Marian devotion and the town's living religious traditions. Like San Guillermo Church, it is tied to community faith before and after the lahar years.",
            yearBuilt    = "20th century",
            modelAsset   = "models/church.glb"
        ),
    )
    private val bacolorPeople = listOf(
        HistoricalObject(
            id           = "juan_crisostomo_soto",
            name         = "Juan Crisostomo Soto",
            type         = ObjectType.PERSON,
            description  = "Juan Crisostomo Caballa Soto, also known as Crissot, was a Bacolor-born poet, dramatist, journalist, and newspaperman. His literary work helped shape Kapampangan theater and public writing during a period of cultural and political change.",
            significance = "Soto is remembered as the Father of Pampango Literature. His name lives on through 'crissotan,' a Kapampangan poetic debate form inspired by his legacy.",
            yearBuilt    = "1867-1918"
        ),
        HistoricalObject(
            id           = "felix_galura",
            name         = "Felix Napao Galura",
            type         = ObjectType.PERSON,
            description  = "Felix Napao Galura was a major Bacolor writer, translator, editor, grammarian, religious author, revolutionary, soldier, and local political leader. Among his achievements was translating Rizal's Noli Me Tangere into Kapampangan.",
            significance = "Galura represents Bacolor's reputation as a center of Kapampangan letters and civic leadership. His career joined literature, language, religion, revolution, and public service.",
            yearBuilt    = "1866-1919"
        ),
        HistoricalObject(
            id           = "pablo_angeles_david",
            name         = "Pablo Angeles David",
            type         = ObjectType.PERSON,
            description  = "Pablo Angeles David was a Filipino magistrate and statesman from Bacolor. His public career included service as a judge, member of the Philippine House of Representatives, Governor of Pampanga, and Senator of the Philippines.",
            significance = "David shows Bacolor's contribution to provincial and national governance. His monument in Cabambangan honors a career that connected law, public administration, and legislative service.",
            yearBuilt    = "1889-1965"
        ),
    )
    private val bacolorQuiz = listOf(
        QuizQuestion(
            id           = "bacolor_q1",
            question     = "Which Bacolor landmark is known as the half-buried church?",
            choices      = listOf("Betis Church", "San Guillermo Parish Church", "Lopez Mansion", "San Agustin Church"),
            correctIndex = 1,
            explanation  = "San Guillermo Parish Church became famous because lahar from Mt. Pinatubo buried much of the church, yet it remained a place of worship."
        ),
        QuizQuestion(
            id           = "bacolor_q2",
            question     = "What disaster caused major lahar damage in Bacolor during the 1990s?",
            choices      = listOf("Mt. Pinatubo eruption", "Taal Volcano eruption", "Bohol earthquake", "Typhoon Yolanda"),
            correctIndex = 0,
            explanation  = "The eruption of Mt. Pinatubo led to destructive lahar flows that buried many areas of Bacolor."
        ),
        QuizQuestion(
            id           = "bacolor_q3",
            question     = "Bacolor is remembered as the first capital of which province?",
            choices      = listOf("Bulacan", "Tarlac", "Pampanga", "Bataan"),
            correctIndex = 2,
            explanation  = "Bacolor served as the first capital of Pampanga for more than two centuries before the capital moved to San Fernando."
        ),
        QuizQuestion(
            id           = "bacolor_q4",
            question     = "Who made Bacolor the seat of the Spanish government after the British captured Manila?",
            choices      = listOf("Miguel Lopez de Legazpi", "Simon de Anda y Salazar", "William Howard Taft", "Jose Rizal"),
            correctIndex = 1,
            explanation  = "Governor-General Simon de Anda y Salazar retreated to Bacolor and used it as the seat of the Spanish government."
        ),
        QuizQuestion(
            id           = "bacolor_q5",
            question     = "Juan Crisostomo Soto is best remembered by what literary title?",
            choices      = listOf("Father of Pampango Literature", "Prince of Tagalog Poets", "Father of the Katipunan", "Brains of the Revolution"),
            correctIndex = 0,
            explanation  = "Juan Crisostomo Soto, also known as Crissot, is remembered as the Father of Pampango Literature."
        ),
        QuizQuestion(
            id           = "bacolor_q6",
            question     = "Which Bacolor figure translated Rizal's Noli Me Tangere into Kapampangan?",
            choices      = listOf("Pablo Angeles David", "Felix Napao Galura", "Simon de Anda", "Don Guillermo Manabat"),
            correctIndex = 1,
            explanation  = "Felix Napao Galura was a writer and translator credited with translating Noli Me Tangere into Kapampangan."
        ),
        QuizQuestion(
            id           = "bacolor_q7",
            question     = "Where is the Our Lady of Lourdes Shrine located in Bacolor?",
            choices      = listOf("Cabetican", "Betis", "San Nicolas", "Sta. Lucia"),
            correctIndex = 0,
            explanation  = "The Our Lady of Lourdes Shrine is located in Cabetican, Bacolor, and serves as a pilgrimage site."
        ),
        QuizQuestion(
            id           = "bacolor_q8",
            question     = "What was built in 1757 on the site where Bacolor Elementary School is now located?",
            choices      = listOf("Casa Real", "San Fernando Cathedral", "Betis Church", "Lopez Mansion"),
            correctIndex = 0,
            explanation  = "The Casa Real, the provincial government building, was constructed in 1757 in Bacolor."
        ),
    )

    private val bacolorOnlineQuiz = listOf(
        QuizQuestion(
            id           = "bacolor_online_q1",
            question     = "What does Bacolor's Villa de Bacolor seal commemorate?",
            choices      = listOf("Its role as Spanish government capital from 1762 to 1764", "Its founding as a modern city", "Its victory in a sports league", "Its transfer to Tarlac"),
            correctIndex = 0,
            explanation  = "The official seal recalls Bacolor's period as the seat of the Spanish colonial government in the Philippines."
        ),
        QuizQuestion(
            id           = "bacolor_online_q2",
            question     = "When did Bacolor become Villa de Bacolor by royal decree?",
            choices      = listOf("November 9, 1765", "June 12, 1898", "February 13, 1901", "July 4, 1946"),
            correctIndex = 0,
            explanation  = "Bacolor became Villa de Bacolor through a decree of the King of Spain on November 9, 1765."
        ),
        QuizQuestion(
            id           = "bacolor_online_q3",
            question     = "How many of Bacolor's 21 barangays were buried by lahar?",
            choices      = listOf("5", "10", "18", "21"),
            correctIndex = 2,
            explanation  = "Bacolor's local history records that 18 of its 21 barangays were buried during the lahar years."
        ),
        QuizQuestion(
            id           = "bacolor_online_q4",
            question     = "Which monument was inaugurated for Bacolor's 250th anniversary as seat of Spanish government?",
            choices      = listOf("Simon de Anda Monument", "Rizal Monument", "Aurelio Tolentino Monument", "Macapagal Monument"),
            correctIndex = 0,
            explanation  = "The Simon de Anda Monument was inaugurated in 2012 to commemorate Bacolor's historical role."
        ),
        QuizQuestion(
            id           = "bacolor_online_q5",
            question     = "Which Bacolor festival remembers people walking through lahar after the Pinatubo disaster?",
            choices      = listOf("Makatapak Festival", "Sinukwan Festival", "Giant Lantern Festival", "Kuraldal Festival"),
            correctIndex = 0,
            explanation  = "Makatapak means barefooted and commemorates Bacolor's difficult walk through the lahar years."
        ),
        QuizQuestion(
            id           = "bacolor_online_q6",
            question     = "Pablo Angeles David served Pampanga and the Philippines in which fields?",
            choices      = listOf("Law and government", "Medicine and aviation", "Painting and sculpture", "Sports and entertainment"),
            correctIndex = 0,
            explanation  = "Pablo Angeles David served as judge, representative, governor, and senator."
        ),
        QuizQuestion(
            id           = "bacolor_online_q7",
            question     = "What profession best describes Juan Crisostomo Soto?",
            choices      = listOf("Poet, dramatist, and journalist", "Naval commander", "Volcanologist", "Architect only"),
            correctIndex = 0,
            explanation  = "Soto was a poet, dramatist, journalist, and newspaperman from Bacolor."
        ),
        QuizQuestion(
            id           = "bacolor_online_q8",
            question     = "What colonial-era building in Bacolor was constructed in 1757?",
            choices      = listOf("Casa Real", "Malacanang Palace", "Fort Santiago", "Barasoain Church"),
            correctIndex = 0,
            explanation  = "Casa Real was constructed in Bacolor in 1757 as the provincial building."
        ),
    )

    private fun starterPlaces(
        idPrefix: String,
        placeName: String,
        description: String,
        significance: String,
        yearBuilt: String,
        modelAsset: String = ""
    ): List<HistoricalObject> = listOf(
        HistoricalObject(
            id           = "${idPrefix}_place",
            name         = placeName,
            type         = ObjectType.PLACE,
            description  = description,
            significance = significance,
            yearBuilt    = yearBuilt,
            modelAsset   = modelAsset
        )
    )

    private fun starterPeople(
        idPrefix: String,
        personName: String,
        description: String,
        significance: String,
        yearBuilt: String
    ): List<HistoricalObject> = listOf(
        HistoricalObject(
            id           = "${idPrefix}_person",
            name         = personName,
            type         = ObjectType.PERSON,
            description  = description,
            significance = significance,
            yearBuilt    = yearBuilt
        )
    )

    private fun place(
        id: String,
        name: String,
        description: String,
        significance: String,
        yearBuilt: String,
        modelAsset: String = ""
    ): HistoricalObject = HistoricalObject(
        id           = id,
        name         = name,
        type         = ObjectType.PLACE,
        description  = description,
        significance = significance,
        yearBuilt    = yearBuilt,
        modelAsset   = modelAsset
    )

    private fun person(
        id: String,
        name: String,
        description: String,
        significance: String,
        yearBuilt: String
    ): HistoricalObject = HistoricalObject(
        id           = id,
        name         = name,
        type         = ObjectType.PERSON,
        description  = description,
        significance = significance,
        yearBuilt    = yearBuilt
    )

    private fun quizChoices(correct: String, correctIndex: Int, vararg distractors: String): List<String> {
        val choices = distractors
            .filter { it != correct }
            .distinct()
            .take(3)
            .toMutableList()
        choices.add(correctIndex.coerceIn(0, choices.size), correct)
        return choices
    }

    private data class QuizFact(
        val question: String,
        val choices: List<String>,
        val correctIndex: Int,
        val explanation: String
    )

    private data class MunicipalityContent(
        val places: List<HistoricalObject>,
        val people: List<HistoricalObject>,
        val facts: List<QuizFact>
    )

    private fun completedQuiz(
        idPrefix: String,
        municipalityName: String,
        places: List<HistoricalObject>,
        people: List<HistoricalObject>,
        facts: List<QuizFact>
    ): List<QuizQuestion> {
        val factOne = facts[0]
        val factTwo = facts[1]

        return listOf(
            QuizQuestion(
                id           = "${idPrefix}_q1",
                question     = "Which place is highlighted in the $municipalityName AR places archive?",
                choices      = quizChoices(places[0].name, 1, places[1].name, places[2].name, "Betis Church", "San Guillermo Parish Church", "San Agustin Lubao Church"),
                correctIndex = 1,
                explanation  = "${places[0].name} is one of the featured historical places for $municipalityName."
            ),
            QuizQuestion(
                id           = "${idPrefix}_q2",
                question     = "Which other historical place is included for $municipalityName?",
                choices      = quizChoices(places[1].name, 2, places[0].name, places[2].name, "Lopez Mansion", "Pio Chapel", "Basa Air Base"),
                correctIndex = 2,
                explanation  = "${places[1].name} is included in the $municipalityName places archive."
            ),
            QuizQuestion(
                id           = "${idPrefix}_q3",
                question     = "Who is featured in the $municipalityName people archive?",
                choices      = quizChoices(people[0].name, 2, people[1].name, people[2].name, "Jose B. Lingad", "Aurelio Tolentino", "Diosdado P. Macapagal"),
                correctIndex = 2,
                explanation  = "${people[0].name} is one of the people connected with $municipalityName history."
            ),
            QuizQuestion(
                id           = "${idPrefix}_q4",
                question     = "Which person or group is also connected with $municipalityName history?",
                choices      = quizChoices(people[1].name, 0, people[0].name, people[2].name, "Felix Napao Galura", "Luis Taruc", "Jose Abad Santos"),
                correctIndex = 0,
                explanation  = "${people[1].name} is included in the $municipalityName historical people archive."
            ),
            QuizQuestion(
                id           = "${idPrefix}_q5",
                question     = "In which province is $municipalityName located?",
                choices      = listOf("Pampanga", "Bulacan", "Tarlac", "Bataan"),
                correctIndex = 0,
                explanation  = "$municipalityName is part of Pampanga."
            ),
            QuizQuestion(
                id           = "${idPrefix}_q6",
                question     = factOne.question,
                choices      = factOne.choices,
                correctIndex = factOne.correctIndex,
                explanation  = factOne.explanation
            ),
            QuizQuestion(
                id           = "${idPrefix}_q7",
                question     = factTwo.question,
                choices      = factTwo.choices,
                correctIndex = factTwo.correctIndex,
                explanation  = factTwo.explanation
            ),
            QuizQuestion(
                id           = "${idPrefix}_q8",
                question     = "Which place completes the $municipalityName AR places set?",
                choices      = quizChoices(places[2].name, 3, places[0].name, places[1].name, "Holy Rosary Parish Church", "Pampanga Provincial Capitol", "Candaba Swamp"),
                correctIndex = 3,
                explanation  = "${places[2].name} completes the three highlighted places for $municipalityName."
            )
        )
    }

    private fun completedOnlineQuiz(
        idPrefix: String,
        municipalityName: String,
        places: List<HistoricalObject>,
        people: List<HistoricalObject>,
        facts: List<QuizFact>
    ): List<QuizQuestion> {
        val factOne = facts[0]
        val factTwo = facts[1]

        return listOf(
            QuizQuestion(
                id           = "${idPrefix}_online_q1",
                question     = "What is the first featured historical place for $municipalityName?",
                choices      = quizChoices(places[0].name, 0, places[1].name, places[2].name, "Lopez Mansion", "San Fernando Train Station", "Betis Church"),
                correctIndex = 0,
                explanation  = "${places[0].name} anchors the historical places archive for $municipalityName."
            ),
            QuizQuestion(
                id           = "${idPrefix}_online_q2",
                question     = "Which place appears with ${places[0].name} in the $municipalityName archive?",
                choices      = quizChoices(places[1].name, 1, places[0].name, places[2].name, "San Agustin Lubao Church", "Basa Air Base", "Pio Chapel"),
                correctIndex = 1,
                explanation  = "${places[1].name} is another featured place for $municipalityName."
            ),
            QuizQuestion(
                id           = "${idPrefix}_online_q3",
                question     = "Which historical person or group is connected with $municipalityName?",
                choices      = quizChoices(people[2].name, 3, people[0].name, people[1].name, "Macario Arnedo", "Pedro Abad Santos", "Rufino J. Cardinal Santos"),
                correctIndex = 3,
                explanation  = "${people[2].name} is one of the featured people entries for $municipalityName."
            ),
            QuizQuestion(
                id           = "${idPrefix}_online_q4",
                question     = "Which person is part of the $municipalityName historical people archive?",
                choices      = quizChoices(people[0].name, 2, people[1].name, people[2].name, "Juan Crisostomo Soto", "Aurelio Tolentino", "Luis Taruc"),
                correctIndex = 2,
                explanation  = "${people[0].name} is included in the people archive for $municipalityName."
            ),
            QuizQuestion(
                id           = "${idPrefix}_online_q5",
                question     = factTwo.question,
                choices      = factTwo.choices,
                correctIndex = factTwo.correctIndex,
                explanation  = factTwo.explanation
            ),
            QuizQuestion(
                id           = "${idPrefix}_online_q6",
                question     = "Which province should an online player associate with $municipalityName?",
                choices      = listOf("Bulacan", "Tarlac", "Pampanga", "Bataan"),
                correctIndex = 2,
                explanation  = "$municipalityName belongs to Pampanga."
            ),
            QuizQuestion(
                id           = "${idPrefix}_online_q7",
                question     = "Which pairing belongs to $municipalityName?",
                choices      = listOf(
                    "${places[0].name} and ${people[0].name}",
                    "Betis Church and Aurelio Tolentino",
                    "San Guillermo Parish Church and Juan Crisostomo Soto",
                    "San Agustin Lubao Church and Diosdado P. Macapagal"
                ),
                correctIndex = 0,
                explanation  = "${places[0].name} and ${people[0].name} are both part of the $municipalityName module."
            ),
            QuizQuestion(
                id           = "${idPrefix}_online_q8",
                question     = factOne.question,
                choices      = factOne.choices,
                correctIndex = factOne.correctIndex,
                explanation  = factOne.explanation
            )
        )
    }

    private val completedMunicipalityContent = mapOf(
        "apalit" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "apalit_church",
                    name         = "Saint Peter the Apostle Parish Church",
                    description  = "Saint Peter the Apostle Parish Church, also called Apalit Church, is a Neo-Renaissance church tied to Apalit's devotion to Apung Iru. The parish dates to the early Spanish mission period, while the present church was completed in the late 19th century.",
                    significance = "The church anchors Apalit's Catholic heritage, old river culture, and the Libad fluvial procession honoring Saint Peter the Apostle.",
                    yearBuilt    = "1883",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "apalit_apung_iru_shrine",
                    name         = "Old Shrine of Apung Iru",
                    description  = "The old shrine area in Capalangan keeps the memory of Apung Iru and the riverside devotional traditions of Apalit. It is closely associated with the annual movement of the image during the town fiesta.",
                    significance = "The shrine connects local faith, family memory, and the Pampanga River route that shaped Apalit's public celebrations.",
                    yearBuilt    = "19th century"
                ),
                place(
                    id           = "apalit_sulipan_heritage",
                    name         = "Sulipan River Heritage Area",
                    description  = "Sulipan was a prominent riverside community in Apalit where elite Kapampangan families, river trade, and grand ancestral houses once gathered along the Rio Grande de Pampanga.",
                    significance = "The area recalls Apalit's role in river commerce, old Kapampangan social life, and the heritage of the Arnedo, Buencamino, and Gonzalez families.",
                    yearBuilt    = "Spanish to American period"
                )
            ),
            people = listOf(
                person(
                    id           = "apalit_macario_arnedo",
                    name         = "Macario Arnedo",
                    description  = "Macario Arnedo belonged to one of Apalit's prominent families and served as governor of Pampanga during the early American period.",
                    significance = "His public service reflects Apalit's influence in provincial politics and the old Sulipan social world.",
                    yearBuilt    = "1868-1941"
                ),
                person(
                    id           = "apalit_bienvenido_gonzalez",
                    name         = "Bienvenido M. Gonzalez",
                    description  = "Bienvenido M. Gonzalez was a scientist, educator, and the sixth president of the University of the Philippines, honored by a historical marker in Apalit.",
                    significance = "He represents Apalit's contribution to Philippine science and higher education.",
                    yearBuilt    = "1893-1953"
                ),
                person(
                    id           = "apalit_joaquin_gonzalez",
                    name         = "Joaquin Gonzalez",
                    description  = "Joaquin Gonzalez was a physician and scientist who represented Pampanga in the Malolos Congress.",
                    significance = "His career links Apalit to the intellectual and political work of the First Philippine Republic.",
                    yearBuilt    = "1853-1900"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What annual Apalit tradition honors Apung Iru on the Pampanga River?",
                    choices      = listOf("Makatapak Festival", "Giant Lantern Festival", "Libad fluvial procession", "Kuraldal Festival"),
                    correctIndex = 2,
                    explanation  = "The Libad is Apalit's fluvial procession honoring Apung Iru, Saint Peter the Apostle."
                ),
                QuizFact(
                    question     = "What earlier Apalit church structure was damaged before the present church was completed?",
                    choices      = listOf("A brick church damaged by the 1863 earthquake", "A fortress destroyed by pirates", "A railway station burned in 1945", "A stone bridge washed away in 1991"),
                    correctIndex = 0,
                    explanation  = "Historical accounts note that an earlier brick church was damaged by the 1863 earthquake before the present church was completed."
                )
            )
        ),
        "arayat" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "arayat_mount_arayat",
                    name         = "Mount Arayat",
                    description  = "Mount Arayat rises above the Central Luzon plain and is one of Pampanga's most recognizable natural landmarks. Its slopes are tied to agriculture, settlement memory, and stories of Maria Sinukuan.",
                    significance = "The mountain gives Arayat a powerful place in Kapampangan geography, folklore, and environmental heritage.",
                    yearBuilt    = "Natural landmark"
                ),
                place(
                    id           = "arayat_santa_catalina_church",
                    name         = "Santa Catalina de Alexandria Parish Church",
                    description  = "Santa Catalina de Alexandria Parish Church is Arayat's historic parish church. The Augustinian mission in Arayat began in 1590 and the church remains a major poblacion landmark.",
                    significance = "The church preserves Arayat's Spanish mission history and its devotion to Saint Catherine of Alexandria.",
                    yearBuilt    = "1590",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "arayat_alejandrino_monument",
                    name         = "Jose Alejandrino Monument",
                    description  = "The Jose Alejandrino Monument stands at Arayat Municipal Plaza and honors the revolutionary general, engineer, cabinet official, and senator connected with the town.",
                    significance = "The monument makes Arayat's revolutionary memory visible in the civic center of the municipality.",
                    yearBuilt    = "2025",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "arayat_jose_alejandrino",
                    name         = "Jose Alejandrino",
                    description  = "Jose Alejandrino was a Philippine Revolutionary Army general, engineer, minister of agriculture and industry, and senator with family roots in Arayat.",
                    significance = "His career connects Arayat to the Revolution, the Philippine-American War, and the early national government.",
                    yearBuilt    = "1870-1951"
                ),
                person(
                    id           = "arayat_juan_macapagal",
                    name         = "Don Juan Macapagal",
                    description  = "Don Juan Macapagal was a 17th-century Datu of Arayat and a descendant of Lakandula who became Maestre de Campo General of native forces in Arayat, Candaba, and Apalit.",
                    significance = "His story reflects the survival of local elite lineages and Arayat's role in early colonial Pampanga.",
                    yearBuilt    = "17th century"
                ),
                person(
                    id           = "arayat_fr_juan_de_valderrama",
                    name         = "Fr. Juan de Valderrama",
                    description  = "Fr. Juan de Valderrama is remembered in local church history for helping establish the first Augustinian mission in Arayat in 1590.",
                    significance = "His mission work marks the start of Arayat's formal Spanish-era parish history.",
                    yearBuilt    = "1590"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What natural landmark dominates Arayat?",
                    choices      = listOf("Candaba Swamp", "Mount Arayat", "Bataan Death March Marker", "Pio Chapel"),
                    correctIndex = 1,
                    explanation  = "Mount Arayat is the landmark most closely associated with the town."
                ),
                QuizFact(
                    question     = "Which revolutionary general is honored by a monument in Arayat Municipal Plaza?",
                    choices      = listOf("Jose Alejandrino", "Antonio Luna", "Gregorio del Pilar", "Emilio Jacinto"),
                    correctIndex = 0,
                    explanation  = "Jose Alejandrino is honored at Arayat Municipal Plaza."
                )
            )
        ),
        "candaba" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "candaba_swamp",
                    name         = "Candaba Swamp",
                    description  = "Candaba Swamp is a broad wetland that absorbs floodwater from surrounding river systems. In dry seasons, parts of the swamp become productive farmland, while migration seasons make it an important ecological site.",
                    significance = "The swamp explains Candaba's farming, fishing, flood-control, and bird-watching identity.",
                    yearBuilt    = "Wetland"
                ),
                place(
                    id           = "candaba_san_andres_church",
                    name         = "San Andres Apostol Parish Church",
                    description  = "San Andres Apostol Parish Church is Candaba's historic parish church, with the parish listed among Pampanga's early Augustinian foundations.",
                    significance = "The church preserves Candaba's Spanish-era religious life and its central poblacion identity.",
                    yearBuilt    = "1575",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "candaba_bahay_pare_marker",
                    name         = "Bahay Pare Macapagal Marker",
                    description  = "Bahay Pare Elementary School is one of the Pampanga sites where Diosdado P. Macapagal was honored with a historical marker as the poor boy from Lubao.",
                    significance = "The marker connects Candaba students to Pampanga's presidential history and the province-wide memory of Macapagal.",
                    yearBuilt    = "2001",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "candaba_anastacio_gallardo",
                    name         = "Anastacio L. Gallardo",
                    description  = "Anastacio L. Gallardo was a Candaba mayor associated with the Anti-Huk Mayor's League during the 1960s.",
                    significance = "His story connects Candaba to postwar Central Luzon agrarian unrest and local government history.",
                    yearBuilt    = "1960s"
                ),
                person(
                    id           = "candaba_juan_macapagal",
                    name         = "Don Juan Macapagal",
                    description  = "Don Juan Macapagal held authority over native forces linked with Arayat, Candaba, and Apalit during the 17th century.",
                    significance = "His title shows Candaba's place in the political geography of early colonial Pampanga.",
                    yearBuilt    = "17th century"
                ),
                person(
                    id           = "candaba_diosdado_macapagal",
                    name         = "Diosdado P. Macapagal",
                    description  = "Diosdado P. Macapagal was honored at Bahay Pare Elementary School in Candaba as part of Pampanga's wider remembrance of the ninth Philippine president.",
                    significance = "His marker gives Candaba a link to provincial education, public memory, and presidential history.",
                    yearBuilt    = "1910-1997"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What natural area is Candaba especially known for?",
                    choices      = listOf("Clark Freeport", "Basa Air Base", "Betis Church", "Candaba Swamp"),
                    correctIndex = 3,
                    explanation  = "Candaba is especially known for Candaba Swamp, its wetlands, farms, and migratory birds."
                ),
                QuizFact(
                    question     = "Which apostle is honored by Candaba's historic parish church?",
                    choices      = listOf("Saint Peter", "Saint Andrew", "Saint Thomas", "Saint James"),
                    correctIndex = 1,
                    explanation  = "San Andres Apostol Parish Church is dedicated to Saint Andrew the Apostle."
                )
            )
        ),
        "floridablanca" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "floridablanca_basa_air_base",
                    name         = "Basa Air Base",
                    description  = "Basa Air Base, formerly Floridablanca Airfield, was built before World War II and became a major Philippine Air Force installation in Floridablanca.",
                    significance = "The base links Floridablanca to World War II, Philippine aviation, and national defense history.",
                    yearBuilt    = "1941",
                    modelAsset   = "models/Municipality.glb"
                ),
                place(
                    id           = "floridablanca_st_joseph_church",
                    name         = "Saint Joseph Parish Church",
                    description  = "Saint Joseph Parish Church stands in the poblacion and is noted locally for combining classic church massing with pseudo-gothic details.",
                    significance = "The church represents Floridablanca's parish identity and the town center's old civic-religious landscape.",
                    yearBuilt    = "1867",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "floridablanca_bahay_kastila",
                    name         = "Bahay Kastila",
                    description  = "Bahay Kastila in Barangay Paguiruan is listed by the municipality as a Spanish-time historical place once owned by Don Alfredo.",
                    significance = "The house preserves Floridablanca's memory of Spanish-period domestic architecture and local elite settlement.",
                    yearBuilt    = "Spanish period"
                )
            ),
            people = listOf(
                person(
                    id           = "floridablanca_cesar_basa",
                    name         = "Cesar T. Basa",
                    description  = "Cesar T. Basa was a pioneering Filipino fighter pilot whose name is carried by Basa Air Base.",
                    significance = "His memory gives Floridablanca a direct connection to Philippine military aviation sacrifice.",
                    yearBuilt    = "1915-1941"
                ),
                person(
                    id           = "floridablanca_don_alfredo",
                    name         = "Don Alfredo",
                    description  = "Don Alfredo is remembered in local heritage listings as the Spanish-period owner associated with Bahay Kastila in Paguiruan.",
                    significance = "His name ties the house to Floridablanca's older landed and architectural history.",
                    yearBuilt    = "Spanish period"
                ),
                person(
                    id           = "floridablanca_wartime_defenders",
                    name         = "Floridablanca Wartime Defenders",
                    description  = "Local military units, Philippine Commonwealth forces, and later Philippine Air Force personnel shaped Floridablanca's wartime and postwar history.",
                    significance = "Their story connects the town to the defense and recovery of Central Luzon before and after World War II.",
                    yearBuilt    = "1940s"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Which military installation is located in Floridablanca?",
                    choices      = listOf("Basa Air Base", "San Fernando Train Station", "Kamikaze East Airfield", "Fort Santiago"),
                    correctIndex = 0,
                    explanation  = "Basa Air Base is located in Floridablanca, Pampanga."
                ),
                QuizFact(
                    question     = "Which Floridablanca historical place is listed as built during the Spanish period in Barangay Paguiruan?",
                    choices      = listOf("Bahay Kastila", "Pamintuan Mansion", "Casa Real", "Henson-Hizon House"),
                    correctIndex = 0,
                    explanation  = "Bahay Kastila in Barangay Paguiruan is listed by the municipality as a Spanish-period historical place."
                )
            )
        ),
        "mabalacat" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "mabalacat_kamikaze_airfield",
                    name         = "Kamikaze East Airfield",
                    description  = "The Mabalacat East Airfield area is remembered for its connection to the first organized Japanese kamikaze units during World War II.",
                    significance = "The site connects Mabalacat to difficult wartime memory and the military history of Clark and Central Luzon.",
                    yearBuilt    = "1944",
                    modelAsset   = "models/Municipality.glb"
                ),
                place(
                    id           = "mabalacat_clark_field",
                    name         = "Clark Field and Fort Stotsenburg",
                    description  = "Clark Field and Fort Stotsenburg began as an American military camp and airfield on former local lands and became one of the most important military sites in the Philippines.",
                    significance = "The site explains Mabalacat's military, aviation, and urban growth story.",
                    yearBuilt    = "1903",
                    modelAsset   = "models/Municipality.glb"
                ),
                place(
                    id           = "mabalacat_artillery_memorial",
                    name         = "Artillery Memorial at Dau",
                    description  = "The Artillery Memorial recalls the Philippine Army Artillery Training Center, formerly Camp Dau and later Camp del Pilar.",
                    significance = "It preserves the memory of military training and defense institutions in Mabalacat.",
                    yearBuilt    = "1996",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "mabalacat_caragan",
                    name         = "Caragan",
                    description  = "Caragan is remembered in local tradition as an early Aeta chieftain connected with the beginnings of Mabalacat.",
                    significance = "Caragan represents the city's indigenous roots and the stories behind its old settlement identity.",
                    yearBuilt    = "Early settlement era"
                ),
                person(
                    id           = "mabalacat_laureana_tolentino",
                    name         = "Laureana Tolentino",
                    description  = "Laureana Tolentino is remembered in local accounts as an early woman leader and cabeza de barangay connected with Mabalacat's founding story.",
                    significance = "Her story highlights women's authority in local governance memory.",
                    yearBuilt    = "18th century"
                ),
                person(
                    id           = "mabalacat_andres_de_san_fulgencio",
                    name         = "Fr. Andres de San Fulgencio",
                    description  = "Fr. Andres de San Fulgencio was one of the Recollect missionaries associated with Mabalacat's mission history in the early 1700s.",
                    significance = "His work helped define Mabalacat's Christian mission and town formation.",
                    yearBuilt    = "1712"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Mabalacat's name is linked to what tree?",
                    choices      = listOf("Narra tree", "Santol tree", "Balacat tree", "Mango tree"),
                    correctIndex = 2,
                    explanation  = "Mabalacat's name is commonly connected to the balacat tree once abundant in the area."
                ),
                QuizFact(
                    question     = "Which former military site is strongly tied to Mabalacat and Clark history?",
                    choices      = listOf("Fort Stotsenburg", "Fort Santiago", "Casa Real", "Malolos Arsenal"),
                    correctIndex = 0,
                    explanation  = "Fort Stotsenburg and Clark Field shaped Mabalacat's military and aviation history."
                )
            )
        ),
        "macabebe" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "macabebe_san_nicolas_church",
                    name         = "San Nicolas de Tolentino Parish Church",
                    description  = "San Nicolas de Tolentino Parish Church is Macabebe's old parish church, founded under the patronage of Saint Nicholas of Tolentino in the Spanish colonial period.",
                    significance = "The church reflects Macabebe's long-standing Catholic community and the wealth of its riverine heritage.",
                    yearBuilt    = "1575",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "macabebe_tarik_monument",
                    name         = "Tarik Sulayman Monument",
                    description  = "The Tarik Sulayman Monument near the municipal building honors the young leader of Macabebe associated with resistance at the Battle of Bangkusay in 1571.",
                    significance = "The monument makes one of Luzon's earliest anti-colonial resistance stories visible in Macabebe's civic center.",
                    yearBuilt    = "2016",
                    modelAsset   = "models/Municipality.glb"
                ),
                place(
                    id           = "macabebe_rio_grande",
                    name         = "Rio Grande de Pampanga Riverfront",
                    description  = "Macabebe's riverfront landscape along the Rio Grande de Pampanga shaped the town's name, livelihood, military mobility, and contact with Manila Bay.",
                    significance = "The riverfront explains why Macabebe became an old and strategically important Pampanga town.",
                    yearBuilt    = "River settlement"
                )
            ),
            people = listOf(
                person(
                    id           = "macabebe_tarik_sulayman",
                    name         = "Tarik Sulayman",
                    description  = "Tarik Sulayman, also remembered as Bambalito in local history, is associated with Macabebe resistance during the Battle of Bangkusay in 1571.",
                    significance = "His story places Macabebe in the earliest recorded resistance to Spanish conquest in Luzon.",
                    yearBuilt    = "1571"
                ),
                person(
                    id           = "macabebe_felipe_sonsong",
                    name         = "Felipe Sonsong",
                    description  = "Felipe Sonsong was a Macabebe-born figure linked to the Maniago revolt and later exiled to the Marianas.",
                    significance = "His life reflects Macabebe's place in 17th-century resistance, punishment, and colonial movement.",
                    yearBuilt    = "1611-1686"
                ),
                person(
                    id           = "macabebe_vicente_manansala",
                    name         = "Vicente S. Manansala",
                    description  = "Vicente S. Manansala was a Macabebe-born painter and muralist who became a National Artist of the Philippines.",
                    significance = "He represents Macabebe's contribution to modern Philippine art and Kapampangan cultural achievement.",
                    yearBuilt    = "1910-1981"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Which scouts were associated with Macabebe in the American period?",
                    choices      = listOf("Ilocos Scouts", "Macabebe Scouts", "Visayan Scouts", "Bataan Scouts"),
                    correctIndex = 1,
                    explanation  = "The Macabebe Scouts became associated with the town during the American period."
                ),
                QuizFact(
                    question     = "Which 1571 battle is linked with Tarik Sulayman?",
                    choices      = listOf("Battle of Bangkusay", "Battle of Tirad Pass", "Battle of Zapote Bridge", "Battle of Imus"),
                    correctIndex = 0,
                    explanation  = "Tarik Sulayman is associated with the Battle of Bangkusay in 1571."
                )
            )
        ),
        "magalang" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "magalang_san_bartolome_church",
                    name         = "San Bartolome Parish Church",
                    description  = "San Bartolome Parish Church is Magalang's historic church. The town became a separate parish from Arayat in 1605, and the church is closely tied to later relocations and rebuilding.",
                    significance = "The church preserves Magalang's religious and settlement history and is recognized as an important cultural heritage structure.",
                    yearBuilt    = "1605",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "magalang_lacson_house",
                    name         = "Lacson Ancestral House",
                    description  = "The Lacson Ancestral House is a 1923 heritage house behind San Bartolome Church, known for Kapampangan craftsmanship and its wartime use as a Japanese garrison.",
                    significance = "The house shows Magalang's domestic architecture, elite family history, and World War II memory.",
                    yearBuilt    = "1923"
                ),
                place(
                    id           = "magalang_eac_museum",
                    name         = "E. Aguilar Cruz Museum",
                    description  = "The E. Aguilar Cruz Museum at Abe's Farm preserves materials connected with Emilio Aguilar Cruz, a Magalang-linked writer, artist, diplomat, and public figure.",
                    significance = "The museum keeps Magalang's literary, artistic, and family heritage accessible to visitors.",
                    yearBuilt    = "20th century",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "magalang_gonzalo_de_salazar",
                    name         = "Fr. Gonzalo de Salazar",
                    description  = "Fr. Gonzalo de Salazar, OSA, was appointed as Magalang's first pastor when the parish separated from Arayat.",
                    significance = "His appointment marks an early milestone in Magalang's separate parish and town identity.",
                    yearBuilt    = "1605"
                ),
                person(
                    id           = "magalang_emilio_aguilar_cruz",
                    name         = "Emilio Aguilar Cruz",
                    description  = "Emilio Aguilar Cruz was a writer, editor, statesman, diplomat, and artist whose museum marker is associated with Magalang.",
                    significance = "His work connects Magalang to national arts, letters, and diplomacy.",
                    yearBuilt    = "1915-1991"
                ),
                person(
                    id           = "magalang_daniel_lacson",
                    name         = "Don Daniel O. Lacson",
                    description  = "Don Daniel O. Lacson and Lucia David built the Lacson Ancestral House in 1923 near the parish church.",
                    significance = "Their house preserves the craftsmanship and family memory of Magalang's old poblacion.",
                    yearBuilt    = "1923"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What heritage status was San Bartolome Parish Church given by the National Museum in 2015?",
                    choices      = listOf("National Park", "World Heritage Site", "Provincial Capitol", "Important Cultural Property"),
                    correctIndex = 3,
                    explanation  = "San Bartolome Parish Church was recognized as an Important Cultural Property."
                ),
                QuizFact(
                    question     = "What heritage house in Magalang was built in 1923 behind San Bartolome Church?",
                    choices      = listOf("Lacson Ancestral House", "Pamintuan Mansion", "Arrastia House", "Hizon-Singian House"),
                    correctIndex = 0,
                    explanation  = "The Lacson Ancestral House was built in 1923 behind San Bartolome Church."
                )
            )
        ),
        "masantol" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "masantol_river_wetlands",
                    name         = "Masantol River Wetlands",
                    description  = "Masantol's river and wetland landscape sits close to the Pampanga River delta and Manila Bay. Its communities have long depended on fishing, farming, and waterways.",
                    significance = "The wetlands explain Masantol's livelihood, settlement pattern, and identity as a southern Pampanga river town.",
                    yearBuilt    = "River delta"
                ),
                place(
                    id           = "masantol_san_miguel_church",
                    name         = "San Miguel Arcangel Parish Church",
                    description  = "San Miguel Arcangel Parish Church is Masantol's main parish church. The parish was erected after local residents sought separation from Macabebe's parish.",
                    significance = "The church represents Masantol's formation as San Miguel de Masantol and its independent religious identity.",
                    yearBuilt    = "1894",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "masantol_1878_monument",
                    name         = "1878 Masantol Monument",
                    description  = "The 1878 Masantol Monument at the civic center recalls the town's inauguration as San Miguel de Masantol after separation from Macabebe.",
                    significance = "The monument keeps the memory of Masantol's founding campaign visible for local residents.",
                    yearBuilt    = "1878",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "masantol_founding_patriarchs",
                    name         = "Masantol Founding Patriarchs",
                    description  = "Manuel Fajardo, Gregorio Bautista, and Juan Lacap were among the town leaders who moved for Masantol's separation from Macabebe in the 1870s.",
                    significance = "Their petition helped create San Miguel de Masantol as a separate Spanish pueblo.",
                    yearBuilt    = "1877-1878"
                ),
                person(
                    id           = "masantol_domingo_moriones",
                    name         = "Domingo Moriones y Murillo",
                    description  = "Governor-General Domingo Moriones y Murillo approved the creation of San Miguel de Masantol after the local separation petition.",
                    significance = "His approval made Masantol's new municipal status official in the Spanish colonial period.",
                    yearBuilt    = "1878"
                ),
                person(
                    id           = "masantol_cesar_guerrero",
                    name         = "Bishop Cesar Ma. Guerrero",
                    description  = "Bishop Cesar Ma. Guerrero founded the Pampanga crusade of Virgen de los Remedios, whose provincial visits began at San Miguel Arcangel Parish in Masantol.",
                    significance = "His work connects Masantol to one of Pampanga's major postwar devotional movements.",
                    yearBuilt    = "1952"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Masantol was historically separated from which neighboring town?",
                    choices      = listOf("Macabebe", "Mabalacat", "Porac", "Arayat"),
                    correctIndex = 0,
                    explanation  = "Masantol began as part of Macabebe before becoming a separate town."
                ),
                QuizFact(
                    question     = "What was Masantol's founding Spanish-period name?",
                    choices      = listOf("San Miguel de Masantol", "Villa de Bacolor", "Culiat", "Nuevo Mexico"),
                    correctIndex = 0,
                    explanation  = "The town was inaugurated as San Miguel de Masantol in 1878."
                )
            )
        ),
        "mexico" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "mexico_santa_monica_church",
                    name         = "Santa Monica Parish Church and Belfry",
                    description  = "Santa Monica Parish Church and its belfry recall Mexico's Spanish-era parish life and early role as a religious and trading center.",
                    significance = "The church and belfry connect Mexico to Pampanga's earliest colonial towns and old river commerce.",
                    yearBuilt    = "1581",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "mexico_gomez_legacy_park",
                    name         = "Gomez Legacy Park",
                    description  = "Gomez Legacy Park in Barangay Anao honors Remedios Gomez-Paraiso, also known as Kumander Liwayway, with a monument and historical marker.",
                    significance = "The park preserves Mexico's World War II resistance memory and the legacy of a woman guerrilla commander.",
                    yearBuilt    = "2024",
                    modelAsset   = "models/Municipality.glb"
                ),
                place(
                    id           = "mexico_old_poblacion",
                    name         = "Mexico Old Poblacion",
                    description  = "Mexico's old poblacion grew from an early river trading center and became one of the important colonial communities of Pampanga.",
                    significance = "The old town center recalls Mexico's role in trade, parish life, and provincial administration.",
                    yearBuilt    = "1581"
                )
            ),
            people = listOf(
                person(
                    id           = "mexico_francisco_maniago",
                    name         = "Francisco Maniago",
                    description  = "Francisco Maniago was a native leader from Mexico who led the Pampanga Revolt against forced labor and rice tribute in 1660.",
                    significance = "His leadership made Mexico central to one of Pampanga's most important anti-colonial uprisings.",
                    yearBuilt    = "1660"
                ),
                person(
                    id           = "mexico_remedios_gomez_paraiso",
                    name         = "Remedios Gomez-Paraiso",
                    description  = "Remedios Gomez-Paraiso, known as Kumander Liwayway, was born in Mexico and became a Hukbalahap guerrilla commander during World War II.",
                    significance = "Her life connects Mexico to anti-Japanese resistance and the history of women in wartime leadership.",
                    yearBuilt    = "1918-2014"
                ),
                person(
                    id           = "mexico_basilio_gomez",
                    name         = "Basilio Gomez",
                    description  = "Basilio Gomez, father of Remedios Gomez-Paraiso, was a Mexico local official who organized resistance and was executed during the Japanese occupation.",
                    significance = "His death helped push Remedios Gomez-Paraiso into the guerrilla movement and shaped Mexico's wartime memory.",
                    yearBuilt    = "World War II"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Which native leader from Mexico led the Pampanga Revolt of 1660?",
                    choices      = listOf("Jose Abad Santos", "Macario Arnedo", "Francisco Maniago", "Luis Taruc"),
                    correctIndex = 2,
                    explanation  = "Francisco Maniago led the Pampanga Revolt in 1660."
                ),
                QuizFact(
                    question     = "What was Remedios Gomez-Paraiso's wartime name?",
                    choices      = listOf("Kumander Liwayway", "Lakambini", "Henerala Agueda", "Tandang Sora"),
                    correctIndex = 0,
                    explanation  = "Remedios Gomez-Paraiso became known as Kumander Liwayway during World War II."
                )
            )
        ),
        "minalin" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "minalin_santa_monica_church",
                    name         = "Santa Monica Parish Church",
                    description  = "Santa Monica Parish Church is Minalin's Baroque parish church in San Nicolas. It is known for its retablo-like facade, preserved capillas posas, and National Cultural Treasure status.",
                    significance = "The church is one of Pampanga's major colonial religious treasures and one of Minalin's strongest heritage symbols.",
                    yearBuilt    = "1614",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "minalin_mural",
                    name         = "Minalin Mural",
                    description  = "The Minalin Mural inside the church-convent complex depicts an early riverine landscape with people, plants, and local symbols associated with the town's early world.",
                    significance = "The mural is a rare visual memory of early Kapampangan settlement and pre-colonial influences in church art.",
                    yearBuilt    = "1619"
                ),
                place(
                    id           = "minalin_sunset_park",
                    name         = "Minalin Sunset Park",
                    description  = "Minalin Sunset Park is a riverfront esplanade beside the tail dike, showing the town's continuing relationship with waterways and flood protection.",
                    significance = "The park connects present-day Minalin to the same river landscape that shaped its settlement legend and livelihood.",
                    yearBuilt    = "2018",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "minalin_fernando_malang_balagtas",
                    name         = "Fernando Malang Balagtas",
                    description  = "Fernando Malang Balagtas, also remembered as Pansomun, appears in local historical tradition as a descendant of Prince Balagtas connected with old Tabungao, Minalin.",
                    significance = "His will and lineage traditions link Minalin to pre-colonial Kapampangan nobility and early written records.",
                    yearBuilt    = "16th century"
                ),
                person(
                    id           = "minalin_fr_miguel_de_saldana",
                    name         = "Fr. Miguel de Saldana",
                    description  = "Fr. Miguel de Saldana was assigned as Minalin's own parish priest soon after the parish began as a visita of Macabebe.",
                    significance = "His assignment marks an early step in Minalin's independent parish identity.",
                    yearBuilt    = "1618"
                ),
                person(
                    id           = "minalin_capitan_diego_tolentino",
                    name         = "Capitan Diego Tolentino",
                    description  = "Capitan Diego Tolentino is associated with the written form of the town name Minalin after the legend of the moved lumber and church.",
                    significance = "His name helps explain how local oral tradition became part of Minalin's civic identity.",
                    yearBuilt    = "Spanish period"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What national heritage status was given to Santa Monica Parish Church in Minalin?",
                    choices      = listOf("National Cultural Treasure", "National Park", "UNESCO World Heritage Site", "Provincial Capitol"),
                    correctIndex = 0,
                    explanation  = "Santa Monica Parish Church was declared a National Cultural Treasure in 2011."
                ),
                QuizFact(
                    question     = "What Minalin celebration features men dressing as beauty queens on New Year's Day?",
                    choices      = listOf("Aguman Sanduk", "Makatapak Festival", "Sabuaga Festival", "Libad"),
                    correctIndex = 0,
                    explanation  = "Aguman Sanduk is Minalin's New Year's celebration known for festive cross-dressed pageantry."
                )
            )
        ),
        "porac" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "porac_pio_chapel",
                    name         = "Pio Chapel",
                    description  = "Pio Chapel, formally San Vicente Ferrer Chapel, is a circular chapel in Barangay Pio built in 1861 as part of the old Hacienda Gil.",
                    significance = "Its circular plan and hacienda history make it one of Porac's most distinctive built-heritage sites.",
                    yearBuilt    = "1861",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "porac_hacienda_gil",
                    name         = "Hacienda Gil Ancestral House",
                    description  = "The Hacienda Gil house stands near Pio Chapel and recalls the Spanish-period hacienda landscape established by the Gil family in Porac.",
                    significance = "Together with the chapel, it preserves Porac's hacienda memory, family history, and old rural architecture.",
                    yearBuilt    = "19th century"
                ),
                place(
                    id           = "porac_santa_catalina_church",
                    name         = "Santa Catalina de Alejandria Parish Church",
                    description  = "Porac's Santa Catalina de Alejandria Parish Church is a 19th-century Baroque parish church tied to Porac's old poblacion and Augustinian history.",
                    significance = "The church is the largest of Porac's extant Spanish-era religious structures and a key town landmark.",
                    yearBuilt    = "1872",
                    modelAsset   = "models/church.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "porac_felino_gil",
                    name         = "Don Felino Gil",
                    description  = "Don Felino Gil was a Spanish military figure and hacienda owner associated with Pio Chapel and early technical education in Pampanga.",
                    significance = "His legacy connects Porac's built heritage with the founding story of one of Asia's oldest trade schools.",
                    yearBuilt    = "1804-1883"
                ),
                person(
                    id           = "porac_paciano_dizon_santos",
                    name         = "Paciano Dizon y Santos",
                    description  = "Paciano Dizon y Santos was honored by a historical marker in Porac and recognized as the first Filipino deputy insular auditor.",
                    significance = "His career connects Porac to early Filipino service in the American-period bureaucracy.",
                    yearBuilt    = "American period"
                ),
                person(
                    id           = "porac_mateo_de_mendoza",
                    name         = "Fray Mateo de Mendoza",
                    description  = "Fray Mateo de Mendoza is connected with Porac's early Augustinian records as the town gained its first minister in the late 16th century.",
                    significance = "His appointment marks Porac's formal entry into Spanish-era parish administration.",
                    yearBuilt    = "1594"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What makes Pio Chapel in Porac unusual?",
                    choices      = listOf("It is a railway station", "It is a rare circular chapel", "It is a wetland reserve", "It is a volcano crater"),
                    correctIndex = 1,
                    explanation  = "Pio Chapel is known for its rare circular chapel form."
                ),
                QuizFact(
                    question     = "Which family is closely associated with Pio Chapel and Hacienda Gil?",
                    choices      = listOf("Gil family", "Pamintuan family", "Arnedo family", "Soto family"),
                    correctIndex = 0,
                    explanation  = "Pio Chapel was built as part of Hacienda Gil and is closely associated with Don Felino Gil."
                )
            )
        ),
        "san_fernando" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "san_fernando_train_station",
                    name         = "San Fernando Train Station",
                    description  = "San Fernando Train Station is a heritage railway site associated with Spanish and American-period travel and wartime history in Pampanga.",
                    significance = "The station connects San Fernando to transport history, World War II memory, and its role as a provincial center.",
                    yearBuilt    = "1892",
                    modelAsset   = "models/Municipality.glb"
                ),
                place(
                    id           = "san_fernando_cathedral",
                    name         = "Metropolitan Cathedral of San Fernando",
                    description  = "The Metropolitan Cathedral of San Fernando began as the parish church of the provincial capital town and later became the cathedral of the Archdiocese of San Fernando.",
                    significance = "The cathedral anchors Pampanga's ecclesiastical life and the civic center of the capital city.",
                    yearBuilt    = "1755",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "san_fernando_henson_hizon_house",
                    name         = "Henson-Hizon House",
                    description  = "The Henson-Hizon House on V. Tiomico Street is an NHCP-declared heritage house within San Fernando's heritage district.",
                    significance = "It preserves the city's ancestral-house architecture and the memory of prominent Fernandino families.",
                    yearBuilt    = "19th century"
                )
            ),
            people = listOf(
                person(
                    id           = "san_fernando_jose_abad_santos",
                    name         = "Jose Abad Santos",
                    description  = "Jose Abad Santos was a Filipino jurist and statesman from San Fernando who served as Chief Justice and was executed during World War II.",
                    significance = "He is remembered nationally for integrity, service, and sacrifice during the Japanese occupation.",
                    yearBuilt    = "1886-1942"
                ),
                person(
                    id           = "san_fernando_pedro_abad_santos",
                    name         = "Pedro Abad Santos",
                    description  = "Pedro Abad Santos was a San Fernando-born lawyer, labor organizer, and founder of the Socialist Party of the Philippines.",
                    significance = "His activism made San Fernando central to labor, peasant, and socialist movements in Central Luzon.",
                    yearBuilt    = "1876-1945"
                ),
                person(
                    id           = "san_fernando_vivencio_cuyugan",
                    name         = "Vivencio B. Cuyugan Sr.",
                    description  = "Vivencio B. Cuyugan Sr. was a San Fernando mayor remembered as the first socialist mayor of the Philippines and one of the founders of Hukbalahap.",
                    significance = "His life links San Fernando to prewar socialist politics and wartime resistance organizing.",
                    yearBuilt    = "1895-1971"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Which heritage site links San Fernando to railway and wartime history?",
                    choices      = listOf("Pio Chapel", "Candaba Swamp", "Basa Air Base", "San Fernando Train Station"),
                    correctIndex = 3,
                    explanation  = "San Fernando Train Station is one of the city's key transport and wartime heritage sites."
                ),
                QuizFact(
                    question     = "What public role did Jose Abad Santos hold before his wartime martyrdom?",
                    choices      = listOf("Chief Justice", "Governor-General", "Air Force commander", "Parish priest"),
                    correctIndex = 0,
                    explanation  = "Jose Abad Santos served as Chief Justice of the Philippines."
                )
            )
        ),
        "san_luis" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "san_luis_gonzaga_church",
                    name         = "San Luis Gonzaga Parish Church",
                    description  = "San Luis Gonzaga Parish Church is a historic church in Santa Cruz Poblacion dedicated to Saint Aloysius Gonzaga. The parish traces its roots to the 18th century.",
                    significance = "The church is a central symbol of San Luis faith, town identity, and Spanish-era heritage.",
                    yearBuilt    = "1734",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "san_luis_taruc_shrine",
                    name         = "Luis Taruc Freedom Park and Shrine",
                    description  = "The San Luis Freedom Park is also known as the Luis Taruc Shrine, with the Luis Taruc Museum, his remains, and an NHCP historical marker.",
                    significance = "The shrine makes San Luis a public memory site for agrarian history, Hukbalahap resistance, and Taruc's life.",
                    yearBuilt    = "2018",
                    modelAsset   = "models/Municipality.glb"
                ),
                place(
                    id           = "san_luis_santa_monica",
                    name         = "Santa Monica Birthplace Landscape",
                    description  = "Santa Monica is the San Luis barrio where Luis Taruc was born, rooted in the farming communities that shaped his politics and leadership.",
                    significance = "The barrio landscape links San Luis to the experiences of peasants and workers in Central Luzon.",
                    yearBuilt    = "1913"
                )
            ),
            people = listOf(
                person(
                    id           = "san_luis_luis_taruc",
                    name         = "Luis Taruc",
                    description  = "Luis Taruc was born in Santa Monica, San Luis, and became a peasant leader and commander of the Hukbalahap during World War II.",
                    significance = "His life connects San Luis to agrarian movements, anti-Japanese resistance, and postwar Central Luzon history.",
                    yearBuilt    = "1913-2005"
                ),
                person(
                    id           = "san_luis_ambrosio_de_san_agustin",
                    name         = "Fr. Ambrosio de San Agustin",
                    description  = "Fr. Ambrosio de San Agustin is remembered as an early priest of San Luis Gonzaga Parish in the 18th century.",
                    significance = "His ministry marks San Luis' early parish formation and the town's Spanish-era religious record.",
                    yearBuilt    = "1744"
                ),
                person(
                    id           = "san_luis_farmer_workers",
                    name         = "San Luis Farmer-Workers",
                    description  = "San Luis' farming communities shaped the social world from which Luis Taruc and other agrarian organizers emerged.",
                    significance = "Their experience ties the town to Central Luzon's struggle for farm labor rights and land reform.",
                    yearBuilt    = "20th century"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Who was the Hukbalahap leader born in San Luis?",
                    choices      = listOf("Luis Taruc", "Jose Abad Santos", "Macario Arnedo", "Cesar T. Basa"),
                    correctIndex = 0,
                    explanation  = "Luis Taruc was born in San Luis and became a leader of the Hukbalahap."
                ),
                QuizFact(
                    question     = "What San Luis site is also known as the Luis Taruc Shrine?",
                    choices      = listOf("San Luis Freedom Park", "Pio Chapel", "Sulipan River Heritage Area", "Lacson House"),
                    correctIndex = 0,
                    explanation  = "The San Luis Freedom Park became known as the Luis Taruc Shrine after his remains and marker were placed there."
                )
            )
        ),
        "san_simon" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "san_simon_parish_church",
                    name         = "San Simon Parish Church",
                    description  = "San Simon Parish Church, formally Nuestra Senora del Pilar Parish Church, represents the town's Catholic center and its development from Barrio del Pilar into an independent pueblo.",
                    significance = "The church stands for San Simon's community life and its emergence as a separate town in the late Spanish period.",
                    yearBuilt    = "1870",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "san_simon_del_pilar_site",
                    name         = "Barrio del Pilar Founding Area",
                    description  = "Barrio del Pilar was the farming settlement from which San Simon developed before it was renamed in honor of Simon de Anda y Salazar.",
                    significance = "The area preserves the story of the town's earlier name and community roots.",
                    yearBuilt    = "1766"
                ),
                place(
                    id           = "san_simon_old_convento",
                    name         = "Old San Simon Convento Site",
                    description  = "San Simon's old convento was built in the late 19th century and later destroyed during the Philippine-American War period.",
                    significance = "The convento site recalls the town's fragile Spanish-era built heritage and wartime losses.",
                    yearBuilt    = "1889",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "san_simon_mariano_del_pilar",
                    name         = "Don Mariano del Pilar de los Reyes",
                    description  = "Don Mariano del Pilar de los Reyes is remembered as the founder of Barrio del Pilar, the farming village that became San Simon.",
                    significance = "His role marks the beginning of San Simon's separate local identity.",
                    yearBuilt    = "1766"
                ),
                person(
                    id           = "san_simon_simon_de_anda",
                    name         = "Simon de Anda y Salazar",
                    description  = "Simon de Anda y Salazar was the Spanish governor-general whose name became attached to San Simon after the town's earlier Del Pilar identity.",
                    significance = "His reputation among Kapampangans after the British occupation helped shape the town's final name.",
                    yearBuilt    = "18th century"
                ),
                person(
                    id           = "san_simon_benito_ubierna",
                    name         = "Fr. Benito Ubierna",
                    description  = "Fr. Benito Ubierna built San Simon's stone church in the 19th century.",
                    significance = "His work gave San Simon a more permanent parish structure before the upheavals of the revolutionary era.",
                    yearBuilt    = "1870"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Who approved the official creation of San Simon in local historical tradition?",
                    choices      = listOf("William Howard Taft", "Jose Rizal", "Simon de Anda y Salazar", "Miguel Lopez de Legazpi"),
                    correctIndex = 2,
                    explanation  = "San Simon's name and official identity are connected with Governor-General Simon de Anda y Salazar."
                ),
                QuizFact(
                    question     = "What was San Simon formerly known as?",
                    choices      = listOf("Del Pilar", "Culiat", "Bacolod", "San Miguel de Masantol"),
                    correctIndex = 0,
                    explanation  = "San Simon was formerly known as Del Pilar or Barrio del Pilar."
                )
            )
        ),
        "santa_ana" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "santa_ana_parish_church",
                    name         = "Santa Ana Parish Church",
                    description  = "Santa Ana Parish Church is a historic church in San Joaquin, Santa Ana. It was founded as an Augustinian visita and the present stone-and-brick church was completed in the 1850s.",
                    significance = "The church preserves Santa Ana's devotion to Saint Anne and its long religious history.",
                    yearBuilt    = "1857",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "santa_ana_old_convent",
                    name         = "Old Santa Ana Convent",
                    description  = "The old Santa Ana convent beside the church served changing roles across time, including religious use, wartime memory, and early educational activity.",
                    significance = "The convent connects Santa Ana's parish life, World War II memory, and local education history.",
                    yearBuilt    = "1866"
                ),
                place(
                    id           = "santa_ana_holy_cross_academy",
                    name         = "Holy Cross Academy First Campus",
                    description  = "Holy Cross Academy began in 1945 using the old convent space after liberation.",
                    significance = "The site recalls postwar recovery and Catholic education in Santa Ana.",
                    yearBuilt    = "1945",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "santa_ana_paciano_aniceto",
                    name         = "Paciano B. Aniceto",
                    description  = "Paciano B. Aniceto is a Santa Ana-born Catholic prelate who became Archbishop of San Fernando.",
                    significance = "He represents Santa Ana's contribution to Pampanga's religious leadership.",
                    yearBuilt    = "Born 1937"
                ),
                person(
                    id           = "santa_ana_lorenzo_guerra",
                    name         = "Fr. Lorenzo Guerra",
                    description  = "Fr. Lorenzo Guerra is remembered as an early parish priest after Santa Ana became an independent parish.",
                    significance = "His ministry marks Santa Ana's development from visita to parish.",
                    yearBuilt    = "1760"
                ),
                person(
                    id           = "santa_ana_venancio_samson",
                    name         = "Fr. Venancio Samson",
                    description  = "Fr. Venancio Samson, a Santa Ana native, translated major Catholic texts into Kapampangan during the era of vernacular liturgy.",
                    significance = "His work shows Santa Ana's role in Kapampangan religious language and worship.",
                    yearBuilt    = "20th century"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Which saint is Santa Ana Parish Church dedicated to?",
                    choices      = listOf("Saint Peter", "Saint Anne", "Saint Bartholomew", "Saint Aloysius Gonzaga"),
                    correctIndex = 1,
                    explanation  = "Santa Ana Parish Church is dedicated to Saint Anne."
                ),
                QuizFact(
                    question     = "Santa Ana began as a visita of which nearby Pampanga town?",
                    choices      = listOf("Arayat", "Lubao", "Macabebe", "Porac"),
                    correctIndex = 0,
                    explanation  = "Santa Ana was founded as a visita of Arayat in the early Spanish mission period."
                )
            )
        ),
        "santa_rita" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "santa_rita_de_cascia_church",
                    name         = "Santa Rita de Cascia Parish Church",
                    description  = "Santa Rita de Cascia Parish Church is the town's heritage church, with the present church built in the 19th century after the parish developed its own identity.",
                    significance = "The church anchors Santa Rita's faith traditions and the old poblacion landscape.",
                    yearBuilt    = "1839",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "santa_rita_gasac",
                    name         = "Gasac Settlement Site",
                    description  = "Gasac, now Barangay San Isidro, is remembered as the place where Santa Rita began as a settlement in 1697.",
                    significance = "The site explains the town's early community roots before its parish and municipal development.",
                    yearBuilt    = "1697"
                ),
                place(
                    id           = "santa_rita_megadike",
                    name         = "Santa Rita Megadike",
                    description  = "The Santa Rita Megadike was built after Mt. Pinatubo lahar buried portions of San Juan, San Isidro, and San Jose in 1991.",
                    significance = "It represents the town's survival, flood protection, and post-Pinatubo resilience.",
                    yearBuilt    = "1991",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "santa_rita_francisco_rayo",
                    name         = "Fr. Francisco Rayo",
                    description  = "Fr. Francisco Rayo is remembered in Santa Rita history for leading the building of the present parish church.",
                    significance = "His work helped shape the church that remains the town's central heritage landmark.",
                    yearBuilt    = "1839"
                ),
                person(
                    id           = "santa_rita_basilio_ocampo",
                    name         = "Don Basilio Ocampo",
                    description  = "Don Basilio Ocampo helped defend Santa Rita's autonomy when annexation to Bacolor was attempted during the American period.",
                    significance = "His intervention helped preserve Santa Rita's separate municipal identity.",
                    yearBuilt    = "1904-1907"
                ),
                person(
                    id           = "santa_rita_magno_gosioco",
                    name         = "Don Magno Gosioco",
                    description  = "Don Magno Gosioco joined Don Basilio Ocampo in supporting Santa Rita's fight to retain autonomy.",
                    significance = "His work is remembered in the town's early 20th-century struggle for self-government.",
                    yearBuilt    = "1904-1907"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What delicacy is Santa Rita famous for in Pampanga?",
                    choices      = listOf("Tibuk-tibuk", "Pan de San Nicolas", "Pastillas", "Turrones de casoy"),
                    correctIndex = 3,
                    explanation  = "Santa Rita is famous for turrones de casoy, a cashew candy delicacy."
                ),
                QuizFact(
                    question     = "Where did Santa Rita begin as a settlement in 1697?",
                    choices      = listOf("Gasac", "Culiat", "Sulipan", "Bahay Pare"),
                    correctIndex = 0,
                    explanation  = "Santa Rita began at a settlement called Gasac, now Barangay San Isidro."
                )
            )
        ),
        "santo_tomas" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "santo_tomas_apostol_parish",
                    name         = "Santo Tomas Apostol Parish",
                    description  = "Santo Tomas Apostol Parish is the Catholic center of the town and is linked to the community's Spanish-period foundation and patronal devotion to Saint Thomas the Apostle.",
                    significance = "The parish reflects Santo Tomas' religious roots, while the town's pottery and craft traditions show its working heritage.",
                    yearBuilt    = "1767",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "santo_tomas_pottery_workshops",
                    name         = "Santo Tomas Pottery Workshops",
                    description  = "Pottery workshops in Santo Tomas preserve the town's clay-working tradition through jars, planters, and other ceramic products.",
                    significance = "The workshops represent a living craft heritage strongly associated with the municipality.",
                    yearBuilt    = "Traditional craft"
                ),
                place(
                    id           = "santo_tomas_casket_workshops",
                    name         = "Santo Tomas Casket Workshops",
                    description  = "Family-run casket workshops form one of Santo Tomas' most recognized local industries, alongside ceramics, carpentry, farming, and fishing.",
                    significance = "The industry explains the town's modern craft identity and its nickname as a casket-making center.",
                    yearBuilt    = "20th century"
                )
            ),
            people = listOf(
                person(
                    id           = "santo_tomas_alfredo_santos",
                    name         = "Alfredo C. Santos",
                    description  = "Alfredo C. Santos was a Santo Tomas-born pharmacist and chemist recognized as a National Scientist of the Philippines.",
                    significance = "His scientific work connects Santo Tomas to national contributions in chemistry, pharmacy, and medicinal plant research.",
                    yearBuilt    = "1900-1978"
                ),
                person(
                    id           = "santo_tomas_potters",
                    name         = "Santo Tomas Potters",
                    description  = "Generations of Santo Tomas potters have kept the town's ceramic craft alive through household workshops and local production.",
                    significance = "Their craft work is a living heritage that supports the town's identity beyond formal monuments.",
                    yearBuilt    = "Traditional craft"
                ),
                person(
                    id           = "santo_tomas_casket_makers",
                    name         = "Santo Tomas Casket Makers",
                    description  = "Santo Tomas casket makers and carpenters built a family-based industry that became widely associated with the municipality.",
                    significance = "They represent the town's practical craftsmanship and its regional economic reputation.",
                    yearBuilt    = "20th century"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Which craft tradition is strongly associated with Santo Tomas?",
                    choices      = listOf("Shipbuilding", "Gold mining", "Pottery and ceramics", "Watchmaking"),
                    correctIndex = 2,
                    explanation  = "Santo Tomas is widely associated with pottery and ceramics, along with other craft industries."
                ),
                QuizFact(
                    question     = "What nickname is often connected with Santo Tomas because of one of its industries?",
                    choices      = listOf("Casket capital", "Shoe capital", "Lantern capital", "Sugar capital"),
                    correctIndex = 0,
                    explanation  = "Santo Tomas is known for its casket-making industry."
                )
            )
        ),
        "sasmuan" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "sasmuan_santa_lucia_church",
                    name         = "Santa Lucia Parish Church",
                    description  = "Santa Lucia Parish Church is Sasmuan's old church beside the Rio Grande de Pampanga. It is associated with the earliest Catholic foundations in the province.",
                    significance = "The church is Sasmuan's strongest built landmark and a witness to the town's Spanish-era history.",
                    yearBuilt    = "1590",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "sasmuan_bangkung_malapad",
                    name         = "Bangkung Malapad Critical Habitat",
                    description  = "Bangkung Malapad is a coastal wetland and mangrove habitat in Sasmuan, formed and reshaped by sediments, tides, and the Pampanga River-Manila Bay environment.",
                    significance = "It connects Sasmuan's fishing, coastal ecology, and post-Pinatubo landscape recovery.",
                    yearBuilt    = "Coastal wetland"
                ),
                place(
                    id           = "sasmuan_rio_grande",
                    name         = "Sasmuan Riverfront",
                    description  = "Sasmuan's riverfront along the Rio Grande de Pampanga helped make it a major early settlement and a coastal gateway toward Manila Bay.",
                    significance = "The riverfront explains the town's old trade, fishpond, and church-centered settlement pattern.",
                    yearBuilt    = "16th century"
                )
            ),
            people = listOf(
                person(
                    id           = "sasmuan_monico_mercado",
                    name         = "Monico R. Mercado",
                    description  = "Monico R. Mercado was a Sasmuan-born lawyer, legislator, and Kapampangan writer who represented Pampanga in the Philippine Assembly.",
                    significance = "His career connects Sasmuan to early representative government and Kapampangan literature.",
                    yearBuilt    = "1875-1952"
                ),
                person(
                    id           = "sasmuan_santiago_sibug",
                    name         = "Don Santiago Sibug",
                    description  = "Don Santiago Sibug is remembered locally as a writer in Spanish and Kapampangan who helped preserve religious and literary materials.",
                    significance = "His memory reflects Sasmuan's contribution to local letters and church culture.",
                    yearBuilt    = "Spanish to American period"
                ),
                person(
                    id           = "sasmuan_principalia",
                    name         = "Sasmuan Principalia",
                    description  = "Sasmuan's principalia helped support the early Santa Lucia Church and local governance during the Spanish colonial period.",
                    significance = "Their role shows how local leaders shaped the town's church, civic, and coastal settlement life.",
                    yearBuilt    = "Spanish period"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "Which Sasmuan church stands beside the Rio Grande de Pampanga?",
                    choices      = listOf("Santa Lucia Parish Church", "San Guillermo Parish Church", "Betis Church", "Pio Chapel"),
                    correctIndex = 0,
                    explanation  = "Santa Lucia Parish Church stands beside the Rio Grande de Pampanga in Sasmuan."
                ),
                QuizFact(
                    question     = "Which Sasmuan-born figure served in the Philippine Assembly and wrote in Kapampangan?",
                    choices      = listOf("Monico R. Mercado", "Jose B. Lingad", "Cesar T. Basa", "Francisco Maniago"),
                    correctIndex = 0,
                    explanation  = "Monico R. Mercado represented Pampanga and is also remembered as a Kapampangan writer."
                )
            )
        ),
        "angeles_city" to MunicipalityContent(
            places = listOf(
                place(
                    id           = "angeles_holy_rosary_church",
                    name         = "Holy Rosary Parish Church",
                    description  = "Holy Rosary Parish Church, also called Pisamban Maragul, stands in old Angeles and was built through the labor and devotion of local families from 1877 to 1896.",
                    significance = "The church is one of Angeles City's strongest heritage symbols and a central landmark of the former town of Culiat.",
                    yearBuilt    = "1877-1896",
                    modelAsset   = "models/church.glb"
                ),
                place(
                    id           = "angeles_pamintuan_mansion",
                    name         = "Pamintuan Mansion",
                    description  = "Pamintuan Mansion became a headquarters of Emilio Aguinaldo during the Filipino-American War and is now preserved as a museum of Philippine social history.",
                    significance = "The mansion links Angeles City to the First Philippine Republic and elite Kapampangan domestic architecture.",
                    yearBuilt    = "1880s"
                ),
                place(
                    id           = "angeles_museo_ning_angeles",
                    name         = "Museo ning Angeles",
                    description  = "Museo ning Angeles occupies the 1922 former municipio in the Santo Rosario historic district across from Holy Rosary Parish Church.",
                    significance = "The museum preserves Angeles City's civic heritage, town hall memory, and local cultural collections.",
                    yearBuilt    = "1922",
                    modelAsset   = "models/Municipality.glb"
                )
            ),
            people = listOf(
                person(
                    id           = "angeles_angel_pantaleon_de_miranda",
                    name         = "Angel Pantaleon de Miranda",
                    description  = "Angel Pantaleon de Miranda is credited in local history as a founder of Culiat, the settlement that later became Angeles City.",
                    significance = "His name and founding story connect Angeles City to its early settlement roots before urbanization.",
                    yearBuilt    = "1796"
                ),
                person(
                    id           = "angeles_rosalia_de_jesus",
                    name         = "Rosalia de Jesus",
                    description  = "Rosalia de Jesus, wife of Angel Pantaleon de Miranda, is remembered as a co-founder of Culiat.",
                    significance = "Her story keeps women's roles in Angeles City's founding memory visible.",
                    yearBuilt    = "1796"
                ),
                person(
                    id           = "angeles_renato_tayag",
                    name         = "Renato D. Tayag",
                    description  = "Renato D. Tayag was an author, lawyer, soldier, and sportsman associated with Angeles City historical markers.",
                    significance = "His life connects Angeles to wartime service, civic writing, and Kapampangan intellectual history.",
                    yearBuilt    = "1913-1985"
                )
            ),
            facts = listOf(
                QuizFact(
                    question     = "What was Angeles City formerly called?",
                    choices      = listOf("Culiat", "Baculud", "Novo Mexico", "San Miguel de Masantol"),
                    correctIndex = 0,
                    explanation  = "Angeles City developed from the old settlement known as Culiat."
                ),
                QuizFact(
                    question     = "Which Angeles City mansion became connected with Emilio Aguinaldo's revolutionary government?",
                    choices      = listOf("Pamintuan Mansion", "Lopez Mansion", "Arrastia House", "Lacson Ancestral House"),
                    correctIndex = 0,
                    explanation  = "Pamintuan Mansion served as a headquarters of Emilio Aguinaldo during the Filipino-American War period."
                )
            )
        )
    )

    private fun starterQuiz(
        idPrefix: String,
        municipalityName: String,
        placeName: String,
        personName: String,
        factQuestion: String,
        factChoices: List<String>,
        factCorrectIndex: Int,
        factExplanation: String
    ): List<QuizQuestion> = listOf(
        QuizQuestion(
            id           = "${idPrefix}_q1",
            question     = "Which place is highlighted in the $municipalityName AR places archive?",
            choices      = quizChoices(placeName, 1, "San Guillermo Parish Church", "Betis Church", "San Agustin Lubao Church", "Pio Chapel"),
            correctIndex = 1,
            explanation  = "$placeName is the highlighted heritage place for $municipalityName."
        ),
        QuizQuestion(
            id           = "${idPrefix}_q2",
            question     = "Who is featured in the $municipalityName people archive?",
            choices      = quizChoices(personName, 2, "Diosdado P. Macapagal", "Juan Crisostomo Soto", "Jose B. Lingad", "Aurelio Tolentino"),
            correctIndex = 2,
            explanation  = "$personName is included in the $municipalityName historical people archive."
        ),
        QuizQuestion(
            id           = "${idPrefix}_q3",
            question     = "In which province is $municipalityName located?",
            choices      = listOf("Pampanga", "Bulacan", "Tarlac", "Bataan"),
            correctIndex = 0,
            explanation  = "$municipalityName is part of Pampanga."
        ),
        QuizQuestion(
            id           = "${idPrefix}_q4",
            question     = factQuestion,
            choices      = factChoices,
            correctIndex = factCorrectIndex,
            explanation  = factExplanation
        )
    )

    private fun starterOnlineQuiz(
        idPrefix: String,
        municipalityName: String,
        placeName: String,
        personName: String,
        factQuestion: String,
        factChoices: List<String>,
        factCorrectIndex: Int,
        factExplanation: String
    ): List<QuizQuestion> = listOf(
        QuizQuestion(
            id           = "${idPrefix}_online_q1",
            question     = "What is the featured historical place for $municipalityName?",
            choices      = quizChoices(placeName, 0, "Lopez Mansion", "Basa Air Base", "Candaba Swamp", "Holy Rosary Parish Church"),
            correctIndex = 0,
            explanation  = "$placeName anchors this starter module for $municipalityName."
        ),
        QuizQuestion(
            id           = "${idPrefix}_online_q2",
            question     = "Which historical person or group is connected with $municipalityName?",
            choices      = quizChoices(personName, 3, "Felix Napao Galura", "Luis Taruc", "Jose Abad Santos", "Macario Arnedo"),
            correctIndex = 3,
            explanation  = "$personName is the featured people entry for $municipalityName."
        ),
        QuizQuestion(
            id           = "${idPrefix}_online_q3",
            question     = "Which province should an online player associate with $municipalityName?",
            choices      = listOf("Bulacan", "Tarlac", "Pampanga", "Bataan"),
            correctIndex = 2,
            explanation  = "$municipalityName belongs to Pampanga."
        ),
        QuizQuestion(
            id           = "${idPrefix}_online_q4",
            question     = factQuestion,
            choices      = factChoices,
            correctIndex = factCorrectIndex,
            explanation  = factExplanation
        )
    )

    private fun starterMunicipality(
        id: String,
        name: String,
        description: String,
        placeName: String,
        placeDescription: String,
        placeSignificance: String,
        placeYearBuilt: String,
        personName: String,
        personDescription: String,
        personSignificance: String,
        personYearBuilt: String,
        factQuestion: String,
        factChoices: List<String>,
        factCorrectIndex: Int,
        factExplanation: String,
        placeModelAsset: String = ""
    ): Municipality {
        val completedContent = completedMunicipalityContent[id]
        val places = completedContent?.places ?: starterPlaces(
            idPrefix     = id,
            placeName    = placeName,
            description  = placeDescription,
            significance = placeSignificance,
            yearBuilt    = placeYearBuilt,
            modelAsset   = placeModelAsset
        )
        val people = completedContent?.people ?: starterPeople(
            idPrefix     = id,
            personName   = personName,
            description  = personDescription,
            significance = personSignificance,
            yearBuilt    = personYearBuilt
        )

        return Municipality(
            id               = id,
            name             = name,
            province         = "Pampanga",
            description      = description,
            imageRes         = "${id}_hero",
            isUnlocked       = false,
            historicalPlaces = places,
            historicalPeople = people,
            quiz             = if (completedContent != null) {
                completedQuiz(
                    idPrefix          = id,
                    municipalityName  = name,
                    places            = places,
                    people            = people,
                    facts             = completedContent.facts
                )
            } else {
                starterQuiz(
                    idPrefix          = id,
                    municipalityName  = name,
                    placeName         = placeName,
                    personName        = personName,
                    factQuestion      = factQuestion,
                    factChoices       = factChoices,
                    factCorrectIndex  = factCorrectIndex,
                    factExplanation   = factExplanation
                )
            },
            onlineQuiz       = if (completedContent != null) {
                completedOnlineQuiz(
                    idPrefix          = id,
                    municipalityName  = name,
                    places            = places,
                    people            = people,
                    facts             = completedContent.facts
                )
            } else {
                starterOnlineQuiz(
                    idPrefix          = id,
                    municipalityName  = name,
                    placeName         = placeName,
                    personName        = personName,
                    factQuestion      = factQuestion,
                    factChoices       = factChoices,
                    factCorrectIndex  = factCorrectIndex,
                    factExplanation   = factExplanation
                )
            }
        )
    }

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
            id               = "bacolor",
            name             = "Bacolor",
            province         = "Pampanga",
            description      = "Bacolor, the old capital of Pampanga, was buried under volcanic lahar after " +
                    "the 1991 Mt. Pinatubo eruption. Its half-buried church is a haunting reminder " +
                    "of nature's power and human resilience.",
            imageRes         = "bacolor_hero",
            isUnlocked       = false,
            historicalPlaces = bacolorPlaces,
            historicalPeople = bacolorPeople,
            quiz             = bacolorQuiz,
            onlineQuiz       = bacolorOnlineQuiz
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
        ),
        starterMunicipality(
            id                 = "apalit",
            name               = "Apalit",
            description        = "Apalit is a Pampanga river town known for Saint Peter the Apostle Parish Church, the Apung Iru fluvial procession, and old Sulipan heritage linked to prominent Kapampangan families.",
            placeName          = "Saint Peter the Apostle Parish Church",
            placeDescription   = "Saint Peter the Apostle Parish Church, also called Apalit Church, is a Neo-Renaissance parish church tied to Apalit's devotion to Apung Iru. The parish separated from Calumpit in 1597, and the present church was completed in 1883.",
            placeSignificance  = "The church anchors Apalit's Catholic heritage, old river culture, and the Libad fluvial procession honoring Saint Peter the Apostle.",
            placeYearBuilt     = "1883",
            personName         = "Macario Arnedo",
            personDescription  = "Macario Arnedo was a member of the prominent Arnedo family of Apalit and served as Pampanga governor during the early American period.",
            personSignificance = "His public service reflects Apalit's influence in provincial politics and the old Sulipan social world.",
            personYearBuilt    = "1868-1941",
            factQuestion       = "What annual Apalit tradition honors Apung Iru on the Pampanga River?",
            factChoices        = listOf("Makatapak Festival", "Giant Lantern Festival", "Libad fluvial procession", "Kuraldal Festival"),
            factCorrectIndex   = 2,
            factExplanation    = "The Libad is Apalit's fluvial procession honoring Apung Iru, Saint Peter the Apostle.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "arayat",
            name               = "Arayat",
            description        = "Arayat is a historic Pampanga town watched over by Mount Arayat, a natural landmark tied to Kapampangan folklore, agriculture, and regional identity.",
            placeName          = "Mount Arayat",
            placeDescription   = "Mount Arayat rises above the town as one of Pampanga's most recognizable natural landmarks. It is connected with local stories of Maria Sinukuan and has long shaped the landscape and identity of Arayat.",
            placeSignificance  = "The mountain gives Arayat a powerful place in Kapampangan geography, folklore, and environmental heritage.",
            placeYearBuilt     = "Natural landmark",
            personName         = "Jose Alejandrino",
            personDescription  = "Jose Alejandrino was a revolutionary general, engineer, and senator remembered in Arayat through family roots and local commemoration.",
            personSignificance = "His career connects Arayat memory with the Philippine Revolution, the Philippine-American War, and early national government.",
            personYearBuilt    = "1870-1951",
            factQuestion       = "What natural landmark dominates Arayat?",
            factChoices        = listOf("Candaba Swamp", "Mount Arayat", "Bataan Death March Marker", "Pio Chapel"),
            factCorrectIndex   = 1,
            factExplanation    = "Mount Arayat is the landmark most closely associated with the town."
        ),
        starterMunicipality(
            id                 = "candaba",
            name               = "Candaba",
            description        = "Candaba is known for its vast wetlands, fertile farms, bird migration areas, and old parish heritage along the Pampanga River system.",
            placeName          = "Candaba Swamp",
            placeDescription   = "Candaba Swamp is a broad wetland that absorbs floodwater from surrounding river systems. In dry seasons, parts of the swamp become productive farmland, while bird migration makes it an important ecological site.",
            placeSignificance  = "The swamp explains Candaba's farming, fishing, flood-control, and bird-watching identity.",
            placeYearBuilt     = "Wetland",
            personName         = "Anastacio L. Gallardo",
            personDescription  = "Anastacio L. Gallardo was a Candaba mayor associated with the Anti-Huk Mayor's League during the 1960s.",
            personSignificance = "His story connects Candaba to postwar Central Luzon agrarian unrest and local government history.",
            personYearBuilt    = "1960s",
            factQuestion       = "What natural area is Candaba especially known for?",
            factChoices        = listOf("Clark Freeport", "Basa Air Base", "Betis Church", "Candaba Swamp"),
            factCorrectIndex   = 3,
            factExplanation    = "Candaba is especially known for Candaba Swamp, its wetlands, farms, and migratory birds."
        ),
        starterMunicipality(
            id                 = "floridablanca",
            name               = "Floridablanca",
            description        = "Floridablanca is a western Pampanga municipality known for rural communities, military aviation history, and proximity to mountain and Pinatubo landscapes.",
            placeName          = "Basa Air Base",
            placeDescription   = "Basa Air Base, formerly Floridablanca Airfield, was built before World War II and later became a major Philippine Air Force installation in Floridablanca.",
            placeSignificance  = "The base links Floridablanca to World War II, Philippine aviation, and national defense history.",
            placeYearBuilt     = "1941",
            personName         = "Cesar T. Basa",
            personDescription  = "Cesar T. Basa was a pioneering Filipino fighter pilot for whom Basa Air Base was named.",
            personSignificance = "His memory gives the Floridablanca air base a direct connection to Philippine military aviation sacrifice.",
            personYearBuilt    = "1915-1941",
            factQuestion       = "Which military installation is located in Floridablanca?",
            factChoices        = listOf("Basa Air Base", "San Fernando Train Station", "Kamikaze East Airfield", "Fort Santiago"),
            factCorrectIndex   = 0,
            factExplanation    = "Basa Air Base is located in Floridablanca, Pampanga.",
            placeModelAsset    = "models/Municipality.glb"
        ),
        starterMunicipality(
            id                 = "mabalacat",
            name               = "Mabalacat",
            description        = "Mabalacat is a Pampanga component city in the Metro Clark area, known for Clark-related growth, Aeta roots, and World War II airfield history.",
            placeName          = "Kamikaze East Airfield",
            placeDescription   = "The Mabalacat East Airfield area is remembered for its connection to the first organized Japanese kamikaze units during World War II.",
            placeSignificance  = "The site connects Mabalacat to difficult wartime memory and the military history of Clark and Central Luzon.",
            placeYearBuilt     = "1944",
            personName         = "Caragan",
            personDescription  = "Caragan is remembered in local tradition as an early Aeta chieftain connected with the beginnings of Mabalacat.",
            personSignificance = "Caragan represents the city's indigenous roots and the stories behind its old settlement identity.",
            personYearBuilt    = "Early settlement era",
            factQuestion       = "Mabalacat's name is linked to what tree?",
            factChoices        = listOf("Narra tree", "Santol tree", "Balacat tree", "Mango tree"),
            factCorrectIndex   = 2,
            factExplanation    = "Mabalacat's name is commonly connected to the balacat tree once abundant in the area.",
            placeModelAsset    = "models/Municipality.glb"
        ),
        starterMunicipality(
            id                 = "macabebe",
            name               = "Macabebe",
            description        = "Macabebe is a southern Pampanga town with deep river history, Spanish colonial church heritage, and a complicated role in Philippine revolutionary and American-period history.",
            placeName          = "San Nicolas de Tolentino Parish Church",
            placeDescription   = "San Nicolas de Tolentino Parish Church is Macabebe's old parish church, founded under the patronage of Saint Nicholas of Tolentino in the Spanish colonial period.",
            placeSignificance  = "The church reflects Macabebe's long-standing Catholic community and the wealth of its riverine heritage.",
            placeYearBuilt     = "1575",
            personName         = "Tarik Sulayman",
            personDescription  = "Tarik Sulayman, also remembered as Bambalito in local history, is associated with Macabebe resistance during the Battle of Bangkusay in 1571.",
            personSignificance = "His story places Macabebe in the earliest recorded resistance to Spanish conquest in Luzon.",
            personYearBuilt    = "1571",
            factQuestion       = "Which scouts were associated with Macabebe in the American period?",
            factChoices        = listOf("Ilocos Scouts", "Macabebe Scouts", "Visayan Scouts", "Bataan Scouts"),
            factCorrectIndex   = 1,
            factExplanation    = "The Macabebe Scouts became associated with the town during the American period.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "magalang",
            name               = "Magalang",
            description        = "Magalang is a Pampanga town near Mount Arayat known for San Bartolome Parish Church, agricultural education, and Spanish-era settlement history.",
            placeName          = "San Bartolome Parish Church",
            placeDescription   = "San Bartolome Parish Church is Magalang's historic church. The town became a separate parish from Arayat in 1605, and the church is closely tied to later relocations and rebuilding.",
            placeSignificance  = "The church preserves Magalang's religious and settlement history and is recognized as an important cultural heritage structure.",
            placeYearBuilt     = "1605",
            personName         = "Fr. Gonzalo de Salazar",
            personDescription  = "Fr. Gonzalo de Salazar, OSA, was appointed as Magalang's first pastor when the parish separated from Arayat.",
            personSignificance = "His appointment marks an early milestone in Magalang's separate parish and town identity.",
            personYearBuilt    = "1605",
            factQuestion       = "What heritage status was San Bartolome Parish Church given by the National Museum in 2015?",
            factChoices        = listOf("National Park", "World Heritage Site", "Provincial Capitol", "Important Cultural Property"),
            factCorrectIndex   = 3,
            factExplanation    = "San Bartolome Parish Church was recognized as an Important Cultural Property.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "masantol",
            name               = "Masantol",
            description        = "Masantol is Pampanga's southernmost municipality, shaped by waterways, fishponds, farming communities, and its historic separation from Macabebe.",
            placeName          = "Masantol River Wetlands",
            placeDescription   = "Masantol's river and wetland landscape sits close to the Pampanga River delta and Manila Bay. Its communities have long depended on fishing, farming, and waterways.",
            placeSignificance  = "The wetlands explain Masantol's livelihood, settlement pattern, and identity as a southern Pampanga river town.",
            placeYearBuilt     = "River delta",
            personName         = "Masantol Founding Patriarchs",
            personDescription  = "Manuel Fajardo, Gregorio Bautista, and Juan Lacap were among the town leaders who moved for Masantol's separation from Macabebe in the 1870s.",
            personSignificance = "Their petition helped create San Miguel de Masantol as a separate Spanish pueblo.",
            personYearBuilt    = "1877-1878",
            factQuestion       = "Masantol was historically separated from which neighboring town?",
            factChoices        = listOf("Macabebe", "Mabalacat", "Porac", "Arayat"),
            factCorrectIndex   = 0,
            factExplanation    = "Masantol began as part of Macabebe before becoming a separate town."
        ),
        starterMunicipality(
            id                 = "mexico",
            name               = "Mexico",
            description        = "Mexico is a historic Pampanga town that began as an early river trading center and was once an important colonial capital area before Bacolor rose in political importance.",
            placeName          = "Santa Monica Parish Church and Belfry",
            placeDescription   = "Santa Monica Parish Church and its belfry recall Mexico's Spanish-era parish life and early role as a religious and trading center.",
            placeSignificance  = "The church and belfry connect Mexico to Pampanga's earliest colonial towns and old river commerce.",
            placeYearBuilt     = "1665",
            personName         = "Francisco Maniago",
            personDescription  = "Francisco Maniago was a native leader from Mexico who led the Pampanga Revolt against forced labor and rice tribute in 1660.",
            personSignificance = "His leadership made Mexico central to one of Pampanga's most important anti-colonial uprisings.",
            personYearBuilt    = "1660",
            factQuestion       = "Which native leader from Mexico led the Pampanga Revolt of 1660?",
            factChoices        = listOf("Jose Abad Santos", "Macario Arnedo", "Francisco Maniago", "Luis Taruc"),
            factCorrectIndex   = 2,
            factExplanation    = "Francisco Maniago led the Pampanga Revolt in 1660.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "minalin",
            name               = "Minalin",
            description        = "Minalin is a Pampanga municipality known for Santa Monica Parish Church, National Cultural Treasure heritage, riverine history, and the Aguman Sanduk New Year's tradition.",
            placeName          = "Santa Monica Parish Church",
            placeDescription   = "Santa Monica Parish Church is Minalin's Baroque parish church in San Nicolas, known for its retablo-like facade and preserved capillas posas.",
            placeSignificance  = "The church is one of Pampanga's major colonial religious treasures and one of Minalin's strongest heritage symbols.",
            placeYearBuilt     = "1614",
            personName         = "Fernando Malang Balagtas",
            personDescription  = "Fernando Malang Balagtas, also remembered as Pansomun, appears in local historical tradition as a descendant of Prince Balagtas connected with old Tabungao, Minalin.",
            personSignificance = "His will and lineage traditions link Minalin to pre-colonial Kapampangan nobility and early written records.",
            personYearBuilt    = "16th century",
            factQuestion       = "What national heritage status was given to Santa Monica Parish Church in Minalin?",
            factChoices        = listOf("National Cultural Treasure", "National Park", "UNESCO World Heritage Site", "Provincial Capitol"),
            factCorrectIndex   = 0,
            factExplanation    = "Santa Monica Parish Church was declared a National Cultural Treasure in 2011.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "porac",
            name               = "Porac",
            description        = "Porac is a western Pampanga municipality known for upland landscapes, Aeta communities, Pinatubo memory, and Spanish-era chapels.",
            placeName          = "Pio Chapel",
            placeDescription   = "Pio Chapel, formally San Vicente Ferrer Chapel, is a circular chapel in Barangay Pio built in 1861 as part of the old Hacienda Gil.",
            placeSignificance  = "Its circular plan and hacienda history make it one of Porac's most distinctive built-heritage sites.",
            placeYearBuilt     = "1861",
            personName         = "Don Felino Gil",
            personDescription  = "Don Felino Gil was a Spanish military figure and hacienda owner associated with Pio Chapel and early technical education in Pampanga.",
            personSignificance = "His legacy connects Porac's built heritage with the founding story of one of Asia's oldest trade schools.",
            personYearBuilt    = "1804-1883",
            factQuestion       = "What makes Pio Chapel in Porac unusual?",
            factChoices        = listOf("It is a railway station", "It is a rare circular chapel", "It is a wetland reserve", "It is a volcano crater"),
            factCorrectIndex   = 1,
            factExplanation    = "Pio Chapel is known for its rare circular chapel form.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "san_fernando",
            name               = "San Fernando",
            description        = "San Fernando is the capital city of Pampanga, known for railway history, heritage structures, lantern-making, and major figures in Philippine public life.",
            placeName          = "San Fernando Train Station",
            placeDescription   = "San Fernando Train Station is a heritage railway site associated with Spanish and American-period travel and wartime history in Pampanga.",
            placeSignificance  = "The station connects San Fernando to transport history, World War II memory, and its role as a provincial center.",
            placeYearBuilt     = "1892",
            personName         = "Jose Abad Santos",
            personDescription  = "Jose Abad Santos was a Filipino jurist and statesman from San Fernando who served as Chief Justice and was executed during World War II.",
            personSignificance = "He is remembered nationally for integrity, service, and sacrifice during the Japanese occupation.",
            personYearBuilt    = "1886-1942",
            factQuestion       = "Which heritage site links San Fernando to railway and wartime history?",
            factChoices        = listOf("Pio Chapel", "Candaba Swamp", "Basa Air Base", "San Fernando Train Station"),
            factCorrectIndex   = 3,
            factExplanation    = "San Fernando Train Station is one of the city's key transport and wartime heritage sites.",
            placeModelAsset    = "models/Municipality.glb"
        ),
        starterMunicipality(
            id                 = "san_luis",
            name               = "San Luis",
            description        = "San Luis is a Pampanga municipality known for San Luis Gonzaga Parish Church, farming communities, and the birthplace of peasant leader Luis Taruc.",
            placeName          = "San Luis Gonzaga Parish Church",
            placeDescription   = "San Luis Gonzaga Parish Church is a historic church in Sta. Cruz Poblacion dedicated to Saint Aloysius Gonzaga. The parish traces its roots to the 18th century.",
            placeSignificance  = "The church is a central symbol of San Luis faith, town identity, and Spanish-era heritage.",
            placeYearBuilt     = "1734",
            personName         = "Luis Taruc",
            personDescription  = "Luis Taruc was born in Santa Monica, San Luis, and became a peasant leader and commander of the Hukbalahap during World War II.",
            personSignificance = "His life connects San Luis to agrarian movements, anti-Japanese resistance, and postwar Central Luzon history.",
            personYearBuilt    = "1913-2005",
            factQuestion       = "Who was the Hukbalahap leader born in San Luis?",
            factChoices        = listOf("Luis Taruc", "Jose Abad Santos", "Macario Arnedo", "Cesar T. Basa"),
            factCorrectIndex   = 0,
            factExplanation    = "Luis Taruc was born in San Luis and became a leader of the Hukbalahap.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "san_simon",
            name               = "San Simon",
            description        = "San Simon is a Pampanga municipality formed from farming communities between San Luis and Apalit, with a town identity linked to Simon de Anda y Salazar.",
            placeName          = "San Simon Parish Church",
            placeDescription   = "San Simon Parish Church represents the town's Catholic center and its development from Barrio del Pilar into an independent pueblo.",
            placeSignificance  = "The church stands for San Simon's community life and its emergence as a separate town in the late Spanish period.",
            placeYearBuilt     = "1771",
            personName         = "Don Mariano del Pilar de los Reyes",
            personDescription  = "Don Mariano del Pilar de los Reyes is remembered as the founder of Barrio del Pilar, the farming village that became San Simon.",
            personSignificance = "His role marks the beginning of San Simon's separate local identity.",
            personYearBuilt    = "1770",
            factQuestion       = "Who approved the official creation of San Simon in 1771?",
            factChoices        = listOf("William Howard Taft", "Jose Rizal", "Simon de Anda y Salazar", "Miguel Lopez de Legazpi"),
            factCorrectIndex   = 2,
            factExplanation    = "Governor-General Simon de Anda y Salazar approved San Simon's creation.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "santa_ana",
            name               = "Santa Ana",
            description        = "Santa Ana is a Pampanga town known for its Saint Anne parish heritage, agricultural roots, and religious traditions.",
            placeName          = "Santa Ana Parish Church",
            placeDescription   = "Santa Ana Parish Church is a historic church in San Joaquin, Santa Ana. It was founded as an Augustinian visita and the present stone-and-brick church was completed in the 1850s.",
            placeSignificance  = "The church preserves Santa Ana's devotion to Saint Anne and its long religious history.",
            placeYearBuilt     = "1857",
            personName         = "Paciano B. Aniceto",
            personDescription  = "Paciano B. Aniceto is a Santa Ana-born Catholic prelate who became Archbishop of San Fernando.",
            personSignificance = "He represents Santa Ana's contribution to Pampanga's religious leadership.",
            personYearBuilt    = "Born 1937",
            factQuestion       = "Which saint is Santa Ana Parish Church dedicated to?",
            factChoices        = listOf("Saint Peter", "Saint Anne", "Saint Bartholomew", "Saint Aloysius Gonzaga"),
            factCorrectIndex   = 1,
            factExplanation    = "Santa Ana Parish Church is dedicated to Saint Anne.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "santa_rita",
            name               = "Santa Rita",
            description        = "Santa Rita is a central Pampanga town known for Santa Rita de Cascia Parish Church, agricultural life, and the turrones de casoy delicacy.",
            placeName          = "Santa Rita de Cascia Parish Church",
            placeDescription   = "Santa Rita de Cascia Parish Church is the town's heritage church, with the present church built in the 19th century after the parish developed its own identity.",
            placeSignificance  = "The church anchors Santa Rita's faith traditions and the old poblacion landscape.",
            placeYearBuilt     = "1839",
            personName         = "Fr. Francisco Rayo",
            personDescription  = "Fr. Francisco Rayo is remembered in Santa Rita history for leading the building of the present parish church.",
            personSignificance = "His work helped shape the church that remains the town's central heritage landmark.",
            personYearBuilt    = "1839",
            factQuestion       = "What delicacy is Santa Rita famous for in Pampanga?",
            factChoices        = listOf("Tibuk-tibuk", "Pan de San Nicolas", "Pastillas", "Turrones de casoy"),
            factCorrectIndex   = 3,
            factExplanation    = "Santa Rita is famous for turrones de casoy, a cashew candy delicacy.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "santo_tomas",
            name               = "Santo Tomas",
            description        = "Santo Tomas is a small Pampanga municipality known for pottery, casket-making, carpentry, farming, fishing, and Saint Thomas the Apostle parish traditions.",
            placeName          = "Santo Tomas Apostol Parish",
            placeDescription   = "Santo Tomas Apostol Parish is the Catholic center of the town and is linked to the community's Spanish-period foundation and patronal devotion to Saint Thomas the Apostle.",
            placeSignificance  = "The parish reflects Santo Tomas' religious roots, while the town's pottery and craft traditions show its working heritage.",
            placeYearBuilt     = "1767",
            personName         = "Alfredo C. Santos",
            personDescription  = "Alfredo C. Santos was a Santo Tomas-born pharmacist and chemist recognized as a National Scientist of the Philippines.",
            personSignificance = "His scientific work connects Santo Tomas to national contributions in chemistry, pharmacy, and medicinal plant research.",
            personYearBuilt    = "1900-1978",
            factQuestion       = "Which craft tradition is strongly associated with Santo Tomas?",
            factChoices        = listOf("Shipbuilding", "Gold mining", "Pottery and ceramics", "Watchmaking"),
            factCorrectIndex   = 2,
            factExplanation    = "Santo Tomas is widely associated with pottery and ceramics, along with other craft industries.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "sasmuan",
            name               = "Sasmuan",
            description        = "Sasmuan is a coastal Pampanga municipality known for Santa Lucia Parish Church, riverfront history, fishponds, and the Bangkung Malapad coastal wetland.",
            placeName          = "Santa Lucia Parish Church",
            placeDescription   = "Santa Lucia Parish Church is Sasmuan's old church beside the Rio Grande de Pampanga and one of the early Catholic landmarks of the province.",
            placeSignificance  = "The church is Sasmuan's strongest built landmark and a witness to the town's Spanish-era history.",
            placeYearBuilt     = "1590",
            personName         = "Monico R. Mercado",
            personDescription  = "Monico R. Mercado was a Sasmuan-born lawyer, legislator, and Kapampangan writer who represented Pampanga in the Philippine Assembly.",
            personSignificance = "His career connects Sasmuan to early representative government and Kapampangan literature.",
            personYearBuilt    = "1875-1952",
            factQuestion       = "Which Sasmuan church stands beside the Rio Grande de Pampanga?",
            factChoices        = listOf("Santa Lucia Parish Church", "San Guillermo Parish Church", "Betis Church", "Pio Chapel"),
            factCorrectIndex   = 0,
            factExplanation    = "Santa Lucia Parish Church stands beside the Rio Grande de Pampanga in Sasmuan.",
            placeModelAsset    = "models/church.glb"
        ),
        starterMunicipality(
            id                 = "angeles_city",
            name               = "Angeles City",
            description        = "Angeles City is a major urban center in Pampanga known for Holy Rosary Parish Church, old Culiat heritage, Clark history, and a strong arts and food culture.",
            placeName          = "Holy Rosary Parish Church",
            placeDescription   = "Holy Rosary Parish Church, also called Pisamban Maragul, stands in old Angeles and was built through the labor and devotion of local families from 1877 to 1896.",
            placeSignificance  = "The church is one of Angeles City's strongest heritage symbols and a central landmark of the former town of Culiat.",
            placeYearBuilt     = "1877-1896",
            personName         = "Angel Pantaleon de Miranda",
            personDescription  = "Angel Pantaleon de Miranda is credited in local history as a founder of Culiat, the settlement that later became Angeles City.",
            personSignificance = "His name and founding story connect Angeles City to its early settlement roots before urbanization.",
            personYearBuilt    = "1796",
            factQuestion       = "What was Angeles City formerly called?",
            factChoices        = listOf("Culiat", "Baculud", "Novo Mexico", "San Miguel de Masantol"),
            factCorrectIndex   = 0,
            factExplanation    = "Angeles City developed from the old settlement known as Culiat.",
            placeModelAsset    = "models/church.glb"
        )
    )

    fun getMunicipality(id: String): Municipality? = municipalities.find { it.id == id }

    fun getMunicipalities(ids: Set<String>): List<Municipality> =
        municipalities.filter { it.id in ids }
}
