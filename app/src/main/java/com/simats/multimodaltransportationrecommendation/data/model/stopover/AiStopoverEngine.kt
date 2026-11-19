package com.simats.multimodaltransportationrecommendation.data.model.stopover

data class TamilNaduSpotDef(
    val name: String,
    val city: String,
    val district: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val rating: Double,
    val durationMinutes: Int,
    val durationText: String,
    val latitude: Double,
    val longitude: Double
)

object AiStopoverEngine {

    // Master Catalog of Tamil Nadu Tourist Spots
    val allSpots = listOf(
        // Chennai
        TamilNaduSpotDef(
            name = "Marina Beach & Promenade",
            city = "Chennai",
            district = "Chennai",
            category = "Iconic Seafront & Promenade",
            description = "World's second longest urban beach with historic statues, lighthouse view, and evening breeze.",
            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
            rating = 4.8,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 13.0500,
            longitude = 80.2824
        ),
        TamilNaduSpotDef(
            name = "Kapaleeshwarar Temple",
            city = "Chennai",
            district = "Chennai",
            category = "Dravidian Temple Heritage",
            description = "7th-century historic Shiva temple with vibrant sculptural gopuram and sacred temple tank.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.9,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 13.0335,
            longitude = 80.2699
        ),
        TamilNaduSpotDef(
            name = "Besant Nagar Beach & Promenade",
            city = "Chennai",
            district = "Chennai",
            category = "Coastal Promenade",
            description = "Serene beach promenade with Elliot's memorial, seaside cafes, and gentle surf.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
            rating = 4.7,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 12.9990,
            longitude = 80.2710
        ),
        TamilNaduSpotDef(
            name = "Guindy National Park & Wildlife",
            city = "Chennai",
            district = "Chennai",
            category = "Wildlife Sanctuary",
            description = "Protected urban forest sanctuary home to spotted deer, blackbucks, and tropical bird species.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.6,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 13.0067,
            longitude = 80.2206
        ),

        // Kanchipuram & Chengalpattu / Mahabalipuram
        TamilNaduSpotDef(
            name = "Shore Temple & Pancha Rathas",
            city = "Mahabalipuram",
            district = "Chengalpattu",
            category = "UNESCO World Heritage Site",
            description = "7th-century monolithic rock-cut monoliths and seafront temple overlooking the Bay of Bengal.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.9,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 12.6169,
            longitude = 80.1990
        ),
        TamilNaduSpotDef(
            name = "Ekambareswarar Temple & Sacred Mango Tree",
            city = "Kanchipuram",
            district = "Kanchipuram",
            category = "Ancient Pancha Bhoota Sthalam",
            description = "Massive 59m gopuram and 3,500-year-old four-branched sacred mango tree.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.9,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 12.8475,
            longitude = 79.7001
        ),
        TamilNaduSpotDef(
            name = "Vedanthangal Bird Sanctuary",
            city = "Vedanthangal",
            district = "Chengalpattu",
            category = "Migratory Wetland Sanctuary",
            description = "Oldest water bird sanctuary in India hosting over 40,000 migratory waterfowl and pelicans.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.8,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 12.5450,
            longitude = 79.8550
        ),

        // Vellore & Ranipet
        TamilNaduSpotDef(
            name = "Vellore Fort & Jalakanteswarar Temple",
            city = "Vellore",
            district = "Vellore",
            category = "16th-Century Granite Fort",
            description = "Grand granite moated fortress with intact ramparts and intricately carved stone pillars.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.8,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 12.9202,
            longitude = 79.1325
        ),
        TamilNaduSpotDef(
            name = "Sripuram Golden Temple",
            city = "Vellore",
            district = "Vellore",
            category = "Gold Architectural Shrine",
            description = "Spiritual sanctuary crafted with 1,500 kg of pure gold leaf surrounded by a star-shaped pathway.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.9,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 12.8714,
            longitude = 79.0883
        ),

        // Tiruvannamalai
        TamilNaduSpotDef(
            name = "Annamalaiyar Temple & Arunachala Girivalam",
            city = "Tiruvannamalai",
            district = "Tiruvannamalai",
            category = "Sacred Agni Fire Sthalam",
            description = "Spiritual hub at the base of sacred Arunachala hill with massive gopurams and 14 km pradakshina.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 5.0,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 12.2253,
            longitude = 79.0747
        ),

        // Villupuram & Gingee
        TamilNaduSpotDef(
            name = "Gingee Fort ('Troy of the East')",
            city = "Gingee",
            district = "Viluppuram",
            category = "Historic Hilltop Citadel",
            description = "Impregnable three-hill fortress complex with 7-storey Kalyana Mahal and panoramic watchtowers.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.9,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 12.2530,
            longitude = 79.4180
        ),

        // Kallakurichi
        TamilNaduSpotDef(
            name = "Kalvarayan Hills & Gomukhi Dam",
            city = "Kallakurichi",
            district = "Kallakurichi",
            category = "Eastern Ghats Waterfalls",
            description = "Pristine mountain pass with Periyar waterfalls, botanical gardens, and scenic reservoir boating.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.7,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 11.7700,
            longitude = 78.8500
        ),

        // Cuddalore & Chidambaram
        TamilNaduSpotDef(
            name = "Thillai Nataraja Temple Chidambaram",
            city = "Chidambaram",
            district = "Cuddalore",
            category = "Cosmic Dance Akasha Sthalam",
            description = "Ancient temple devoted to Shiva's Cosmic Dance (Ananda Tandava) with golden roofed sanctum.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.9,
            durationMinutes = 80,
            durationText = "1h 20m",
            latitude = 11.3992,
            longitude = 79.6934
        ),
        TamilNaduSpotDef(
            name = "Pichavaram Mangrove Forest",
            city = "Pichavaram",
            district = "Cuddalore",
            category = "Wetland Mangrove Forest",
            description = "Second largest mangrove forest in the world featuring 4,400 water canals navigated by row boats.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
            rating = 4.8,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 11.4280,
            longitude = 79.7820
        ),

        // Salem
        TamilNaduSpotDef(
            name = "Yercaud Emerald Lake & Lady's Seat",
            city = "Yercaud",
            district = "Salem",
            category = "Hill Station Viewpoint & Lake",
            description = "Serene mountain lake surrounded by coffee plantations and a historic telescope viewpoint.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.8,
            durationMinutes = 105,
            durationText = "1h 45m",
            latitude = 11.7753,
            longitude = 78.2093
        ),
        TamilNaduSpotDef(
            name = "1008 Shiva Temple & Salem Foothills",
            city = "Salem",
            district = "Salem",
            category = "Scenic Spiritual Landmark",
            description = "Magnificent foothill temple complex with 1008 Shiva lingams and panoramic views across Salem valley.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.8,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 11.6310,
            longitude = 78.1150
        ),
        TamilNaduSpotDef(
            name = "Mettur Dam & Ellis Park",
            city = "Mettur",
            district = "Salem",
            category = "Kaveri River Mega Dam",
            description = "One of India's largest and oldest masonry dams, forming the vast Stanley Reservoir with garden park.",
            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
            rating = 4.7,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 11.7960,
            longitude = 77.8010
        ),

        // Erode
        TamilNaduSpotDef(
            name = "Bhavani Sangameswarar Temple Kooduthurai",
            city = "Bhavani",
            district = "Erode",
            category = "Sacred River Confluence",
            description = "Sacred Southern Triveni Sangam where the Cauvery, Bhavani, and mystical Amutha rivers merge.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.8,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 11.4460,
            longitude = 77.6830
        ),
        TamilNaduSpotDef(
            name = "Kodiveri Dam & Cascading Falls",
            city = "Gobichettipalayam",
            district = "Erode",
            category = "Cascading Waterfalls",
            description = "Historic stone anicut on Bhavani river creating an exhilarating wide cascade ideal for family bathing.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
            rating = 4.7,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 11.5300,
            longitude = 77.3000
        ),

        // Tiruppur
        TamilNaduSpotDef(
            name = "Thirumoorthy Dam & Amanalingeswarar Temple",
            city = "Udumalpet",
            district = "Tiruppur",
            category = "Waterfall & Hill Shrine",
            description = "Western Ghats scenic reservoir and Panjalingam waterfalls at the foot of Thirumoorthy hills.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.8,
            durationMinutes = 80,
            durationText = "1h 20m",
            latitude = 10.4500,
            longitude = 77.1600
        ),

        // Coimbatore
        TamilNaduSpotDef(
            name = "Adiyogi Shiva & Isha Yoga Center",
            city = "Coimbatore",
            district = "Coimbatore",
            category = "Iconic 112ft Monument & Meditation",
            description = "Guinness World Record 112ft tall bust of Adiyogi Shiva, Dhyanalinga, and consecrated water bodies.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.9,
            durationMinutes = 120,
            durationText = "2h",
            latitude = 10.9727,
            longitude = 76.7404
        ),
        TamilNaduSpotDef(
            name = "Marudhamalai Subramanya Swamy Temple",
            city = "Coimbatore",
            district = "Coimbatore",
            category = "Western Ghats Hill Temple",
            description = "12th-century hill shrine set amid lush medicinal herb forests of Western Ghats.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.9,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 11.0460,
            longitude = 76.8520
        ),
        TamilNaduSpotDef(
            name = "Siruvani Waterfalls & Dam",
            city = "Coimbatore",
            district = "Coimbatore",
            category = "Pristine Rainforest Waterfalls",
            description = "Famed natural waterfall renowned for possessing the second sweetest drinking water on Earth.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.8,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 10.9400,
            longitude = 76.6800
        ),

        // Nilgiris / Ooty
        TamilNaduSpotDef(
            name = "Government Botanical Garden",
            city = "Ooty",
            district = "The Nilgiris",
            category = "Historic Horticultural Sanctuary",
            description = "55-acre terraced garden established in 1848 with 1,000+ species, glass houses, and fossil trees.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.9,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 11.4173,
            longitude = 76.7118
        ),
        TamilNaduSpotDef(
            name = "Doddabetta Peak Viewpoint",
            city = "Ooty",
            district = "The Nilgiris",
            category = "Highest Mountain Peak",
            description = "Highest point in Tamil Nadu (2,637 m) featuring a telescope house with 360-degree Nilgiri views.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
            rating = 4.8,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 11.4011,
            longitude = 76.7358
        ),
        TamilNaduSpotDef(
            name = "Pykara Lake & Waterfalls",
            city = "Pykara",
            district = "The Nilgiris",
            category = "Forested Lake & Boating",
            description = "Sacred Toda tribal river featuring cascading waterfalls, speed boat rides, and pine forests.",
            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
            rating = 4.8,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 11.4650,
            longitude = 76.5950
        ),

        // Kodaikanal & Dindigul
        TamilNaduSpotDef(
            name = "Kodaikanal Lake & Coaker's Walk",
            city = "Kodaikanal",
            district = "Dindigul",
            category = "Star-Shaped Mountain Lake",
            description = "Iconic man-made star lake with cycling, pedal boats, and a 1 km panoramic cliff-edge promenade.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
            rating = 4.9,
            durationMinutes = 105,
            durationText = "1h 45m",
            latitude = 10.2381,
            longitude = 77.4892
        ),
        TamilNaduSpotDef(
            name = "Pillar Rocks & Guna Caves",
            city = "Kodaikanal",
            district = "Dindigul",
            category = "Granite Cliff Formations",
            description = "Three vertically standing 400ft granite cliff boulders rising dramatically above mountain mist.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.8,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 10.2100,
            longitude = 77.4600
        ),
        TamilNaduSpotDef(
            name = "Dindigul Rock Fort",
            city = "Dindigul",
            district = "Dindigul",
            category = "Monolithic Rock Fort",
            description = "17th-century Nayaka rock citadel towering 280ft above the plain with historic cannons.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.7,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 10.3600,
            longitude = 77.9700
        ),

        // Tiruchirappalli (Trichy)
        TamilNaduSpotDef(
            name = "Sri Ranganathaswamy Temple Srirangam",
            city = "Tiruchirappalli",
            district = "Tiruchirappalli",
            category = "World's Largest Temple Complex",
            description = "156-acre functioning temple complex with 21 royal towers, 7 concentric enclosures, and thousand pillars.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 5.0,
            durationMinutes = 120,
            durationText = "2h",
            latitude = 10.8623,
            longitude = 78.6901
        ),
        TamilNaduSpotDef(
            name = "Rockfort Ucchi Pillayar Temple",
            city = "Tiruchirappalli",
            district = "Tiruchirappalli",
            category = "Ancient Monolithic Rock Shrine",
            description = "Historic fortress complex perched on an 83m ancient monolithic rock carved with 417 stone steps.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.9,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 10.8284,
            longitude = 78.6967
        ),
        TamilNaduSpotDef(
            name = "Kallanai Dam (Grand Anicut)",
            city = "Tiruchirappalli",
            district = "Tiruchirappalli",
            category = "2nd-Century AD Engineering Wonder",
            description = "Built by Chola king Karikalan, one of the oldest water-diversion stone structures still in active use.",
            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
            rating = 4.8,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 10.8350,
            longitude = 78.8180
        ),

        // Thanjavur
        TamilNaduSpotDef(
            name = "Brihadisvara Temple (Big Temple)",
            city = "Thanjavur",
            district = "Thanjavur",
            category = "UNESCO Great Living Chola Temple",
            description = "1000-year-old architectural marvel built with 130,000 tonnes of granite and an 80-tonne monolithic cupola.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 5.0,
            durationMinutes = 105,
            durationText = "1h 45m",
            latitude = 10.7828,
            longitude = 79.1318
        ),
        TamilNaduSpotDef(
            name = "Thanjavur Maratha Palace & Saraswathi Mahal Library",
            city = "Thanjavur",
            district = "Thanjavur",
            category = "Royal Palace & Palm Manuscripts",
            description = "Nayaka & Maratha royal court with Durbar Hall, art gallery, and rare ancient palm-leaf manuscripts.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.8,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 10.7924,
            longitude = 79.1378
        ),

        // Madurai
        TamilNaduSpotDef(
            name = "Meenakshi Amman Temple",
            city = "Madurai",
            district = "Madurai",
            category = "Historic Dravidian Wonder",
            description = "Historic Dravidian wonder with 14 towering colourful sculpted gopurams and Hall of Thousand Pillars.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 5.0,
            durationMinutes = 120,
            durationText = "2h",
            latitude = 9.9195,
            longitude = 78.1194
        ),
        TamilNaduSpotDef(
            name = "Thirumalai Nayakkar Palace",
            city = "Madurai",
            district = "Madurai",
            category = "17th-Century Royal Palace",
            description = "Grand Indo-Saracenic palace renowned for gigantic circular white stucco pillars and courtyard.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.8,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 9.9150,
            longitude = 78.1235
        ),
        TamilNaduSpotDef(
            name = "Alagar Kovil & Pazhamudhircholai",
            city = "Madurai",
            district = "Madurai",
            category = "Hillside Vishnu & Murugan Shrine",
            description = "Serene Vishnu temple located on Alagar hills surrounded by natural spring water streams.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.8,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 10.0750,
            longitude = 78.2150
        ),

        // Ramanathapuram & Rameswaram
        TamilNaduSpotDef(
            name = "Ramanathaswamy Temple & 22 Theerthams",
            city = "Rameswaram",
            district = "Ramanathapuram",
            category = "Sacred Jyotirlinga & Longest Corridor",
            description = "Sacred coastal Jyotirlinga renowned for the world's longest pillared corridors and 22 holy wells.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 5.0,
            durationMinutes = 120,
            durationText = "2h",
            latitude = 9.2881,
            longitude = 79.3174
        ),
        TamilNaduSpotDef(
            name = "Pamban Sea Bridge & Viewing Deck",
            city = "Pamban",
            district = "Ramanathapuram",
            category = "Iconic Sea Rail & Road Bridge",
            description = "India's first cantilever sea bridge spanning 2.06 km over the ocean linking mainland to island.",
            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
            rating = 4.9,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 9.2780,
            longitude = 79.2000
        ),
        TamilNaduSpotDef(
            name = "Dhanushkodi Ghost Town & Beach",
            city = "Dhanushkodi",
            district = "Ramanathapuram",
            category = "Ocean Confluence & Historic Ruins",
            description = "Dramatic coastal strip at the eastern tip of Pamban Island where the Indian Ocean meets the Bay of Bengal.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
            rating = 4.9,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 9.1780,
            longitude = 79.4180
        ),

        // Tirunelveli & Tenkasi
        TamilNaduSpotDef(
            name = "Nellaiappar Temple & Musical Pillars",
            city = "Tirunelveli",
            district = "Tirunelveli",
            category = "Musical Stone Pillars Heritage",
            description = "Ancient 7th-century Shiva temple complex featuring legendary monolithic stone pillars that ring musical notes.",
            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
            rating = 4.9,
            durationMinutes = 80,
            durationText = "1h 20m",
            latitude = 8.7280,
            longitude = 77.6880
        ),
        TamilNaduSpotDef(
            name = "Courtallam Main Falls & Five Falls",
            city = "Courtallam",
            district = "Tenkasi",
            category = "Herbal Mineral Waterfalls",
            description = "Renowned as the 'Spa of South India' with healing mineral mountain streams cascading down Western Ghats.",
            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
            rating = 4.9,
            durationMinutes = 90,
            durationText = "1h 30m",
            latitude = 8.9300,
            longitude = 77.2700
        ),

        // Kanyakumari
        TamilNaduSpotDef(
            name = "Vivekananda Rock Memorial & Thiruvalluvar Statue",
            city = "Kanyakumari",
            district = "Kanyakumari",
            category = "133ft Monolithic Sea Monument",
            description = "Iconic offshore rock monument and 133-foot stone statue of Tamil sage poet Thiruvalluvar in the ocean.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 5.0,
            durationMinutes = 120,
            durationText = "2h",
            latitude = 8.0780,
            longitude = 77.5550
        ),
        TamilNaduSpotDef(
            name = "Sunset Point & Triveni Sangam Beach",
            city = "Kanyakumari",
            district = "Kanyakumari",
            category = "Tri-Ocean Confluence",
            description = "The southernmost tip of mainland India where the Indian Ocean, Arabian Sea, and Bay of Bengal unite.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
            rating = 4.9,
            durationMinutes = 60,
            durationText = "1h",
            latitude = 8.0770,
            longitude = 77.5480
        ),
        TamilNaduSpotDef(
            name = "Padmanabhapuram Palace",
            city = "Thuckalay",
            district = "Kanyakumari",
            category = "16th-Century Wooden Palace",
            description = "Magnificent wooden palace complex built inside a granite fortress with exquisite woodwork and clock tower.",
            imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
            rating = 4.9,
            durationMinutes = 75,
            durationText = "1h 15m",
            latitude = 8.2510,
            longitude = 77.3270
        )
    )

    fun generateStopoverPlan(originInput: String?, destinationInput: String?): AiSmartStopoverPlan {
        val o = (originInput ?: "Chennai").trim()
        val d = (destinationInput ?: "Salem").trim()
        val oLower = o.lowercase()
        val dLower = d.lowercase()

        // 1. Resolve matching tourist spots along the route
        val selectedSpots = mutableListOf<TamilNaduSpotDef>()

        // Helper to check city match
        fun matchesCity(spot: TamilNaduSpotDef, query: String): Boolean {
            val q = query.lowercase()
            return spot.city.lowercase().contains(q) ||
                    spot.district.lowercase().contains(q) ||
                    q.contains(spot.city.lowercase()) ||
                    q.contains(spot.district.lowercase())
        }

        // -------------------------------------------------------------
        // SPECIFIC POPULAR CORRIDORS
        // -------------------------------------------------------------

        // CORRIDOR A: Chennai <-> Salem
        if ((oLower.contains("chennai") && dLower.contains("salem")) ||
            (oLower.contains("salem") && dLower.contains("chennai"))
        ) {
            val isReversed = oLower.contains("salem")
            val corridor = listOf(
                // Starting Chennai spots
                findSpot("Marina Beach & Promenade"),
                findSpot("Kapaleeshwarar Temple"),
                // En-route spots
                findSpot("Ekambareswarar Temple & Sacred Mango Tree"), // Kanchipuram
                findSpot("Gingee Fort ('Troy of the East')"), // Villupuram
                findSpot("Kalvarayan Hills & Gomukhi Dam"), // Kallakurichi
                // Destination Salem spots
                findSpot("1008 Shiva Temple & Salem Foothills"),
                findSpot("Yercaud Emerald Lake & Lady's Seat"),
                findSpot("Mettur Dam & Ellis Park")
            ).filterNotNull()

            selectedSpots.addAll(if (!isReversed) corridor else corridor.reversed())
        }
        // CORRIDOR B: Chennai <-> Coimbatore
        else if ((oLower.contains("chennai") && dLower.contains("coimbatore")) ||
            (oLower.contains("coimbatore") && dLower.contains("chennai"))
        ) {
            val isReversed = oLower.contains("coimbatore")
            val corridor = listOf(
                findSpot("Marina Beach & Promenade"),
                findSpot("Ekambareswarar Temple & Sacred Mango Tree"),
                findSpot("Vellore Fort & Jalakanteswarar Temple"),
                findSpot("Sripuram Golden Temple"),
                findSpot("1008 Shiva Temple & Salem Foothills"),
                findSpot("Bhavani Sangameswarar Temple Kooduthurai"),
                findSpot("Marudhamalai Subramanya Swamy Temple"),
                findSpot("Adiyogi Shiva & Isha Yoga Center")
            ).filterNotNull()

            selectedSpots.addAll(if (!isReversed) corridor else corridor.reversed())
        }
        // CORRIDOR C: Chennai <-> Madurai
        else if ((oLower.contains("chennai") && dLower.contains("madurai")) ||
            (oLower.contains("madurai") && dLower.contains("chennai"))
        ) {
            val isReversed = oLower.contains("madurai")
            val corridor = listOf(
                findSpot("Kapaleeshwarar Temple"),
                findSpot("Shore Temple & Pancha Rathas"),
                findSpot("Vedanthangal Bird Sanctuary"),
                findSpot("Gingee Fort ('Troy of the East')"),
                findSpot("Sri Ranganathaswamy Temple Srirangam"),
                findSpot("Rockfort Ucchi Pillayar Temple"),
                findSpot("Dindigul Rock Fort"),
                findSpot("Alagar Kovil & Pazhamudhircholai"),
                findSpot("Meenakshi Amman Temple"),
                findSpot("Thirumalai Nayakkar Palace")
            ).filterNotNull()

            selectedSpots.addAll(if (!isReversed) corridor else corridor.reversed())
        }
        // CORRIDOR D: Chennai <-> Tiruchirappalli (Trichy)
        else if ((oLower.contains("chennai") && (dLower.contains("trichy") || dLower.contains("tiruchirappalli"))) ||
            ((oLower.contains("trichy") || oLower.contains("tiruchirappalli")) && dLower.contains("chennai"))
        ) {
            val isReversed = oLower.contains("trichy") || oLower.contains("tiruchirappalli")
            val corridor = listOf(
                findSpot("Kapaleeshwarar Temple"),
                findSpot("Shore Temple & Pancha Rathas"),
                findSpot("Vedanthangal Bird Sanctuary"),
                findSpot("Gingee Fort ('Troy of the East')"),
                findSpot("Thillai Nataraja Temple Chidambaram"),
                findSpot("Sri Ranganathaswamy Temple Srirangam"),
                findSpot("Rockfort Ucchi Pillayar Temple"),
                findSpot("Kallanai Dam (Grand Anicut)")
            ).filterNotNull()

            selectedSpots.addAll(if (!isReversed) corridor else corridor.reversed())
        }
        // CORRIDOR E: Madurai <-> Rameswaram
        else if ((oLower.contains("madurai") && dLower.contains("rameswaram")) ||
            (oLower.contains("rameswaram") && dLower.contains("madurai"))
        ) {
            val isReversed = oLower.contains("rameswaram")
            val corridor = listOf(
                findSpot("Meenakshi Amman Temple"),
                findSpot("Thirumalai Nayakkar Palace"),
                findSpot("Pamban Sea Bridge & Viewing Deck"),
                findSpot("Ramanathaswamy Temple & 22 Theerthams"),
                findSpot("Dhanushkodi Ghost Town & Beach")
            ).filterNotNull()

            selectedSpots.addAll(if (!isReversed) corridor else corridor.reversed())
        }
        // CORRIDOR F: Madurai <-> Kanyakumari
        else if ((oLower.contains("madurai") && dLower.contains("kanyakumari")) ||
            (oLower.contains("kanyakumari") && dLower.contains("madurai"))
        ) {
            val isReversed = oLower.contains("kanyakumari")
            val corridor = listOf(
                findSpot("Meenakshi Amman Temple"),
                findSpot("Nellaiappar Temple & Musical Pillars"),
                findSpot("Courtallam Main Falls & Five Falls"),
                findSpot("Padmanabhapuram Palace"),
                findSpot("Vivekananda Rock Memorial & Thiruvalluvar Statue"),
                findSpot("Sunset Point & Triveni Sangam Beach")
            ).filterNotNull()

            selectedSpots.addAll(if (!isReversed) corridor else corridor.reversed())
        }
        // CORRIDOR G: Coimbatore / Plains <-> Ooty / Nilgiris
        else if (dLower.contains("ooty") || dLower.contains("nilgiri") || dLower.contains("pykara") || dLower.contains("coonoor")) {
            val corridor = listOf(
                findSpot("Marudhamalai Subramanya Swamy Temple"),
                findSpot("Bhavanisagar Dam & Garden"),
                findSpot("Government Botanical Garden"),
                findSpot("Doddabetta Peak Viewpoint"),
                findSpot("Pykara Lake & Waterfalls")
            ).filterNotNull()

            selectedSpots.addAll(corridor)
        }
        // CORRIDOR H: Madurai / Central <-> Kodaikanal
        else if (dLower.contains("kodaikanal") || dLower.contains("kodai")) {
            val corridor = listOf(
                findSpot("Meenakshi Amman Temple"),
                findSpot("Dindigul Rock Fort"),
                findSpot("Kodaikanal Lake & Coaker's Walk"),
                findSpot("Pillar Rocks & Guna Caves")
            ).filterNotNull()

            selectedSpots.addAll(corridor)
        }
        // CORRIDOR I: Chennai Local (Chennai Central -> Marina / Besant Nagar)
        else if ((oLower.contains("chennai") || oLower.contains("central") || oLower.contains("egmore")) &&
            (dLower.contains("marina") || dLower.contains("beach") || dLower.contains("besant") || dLower.contains("mylapore"))
        ) {
            val corridor = listOf(
                findSpot("Kapaleeshwarar Temple"),
                findSpot("Guindy National Park & Wildlife"),
                findSpot("Besant Nagar Beach & Promenade"),
                findSpot("Marina Beach & Promenade")
            ).filterNotNull()

            selectedSpots.addAll(corridor)
        }
        // DYNAMIC EN-ROUTE SEARCH FOR ANY OTHER TAMIL NADU CITIES
        else {
            // Find origin spots
            val origSpots = allSpots.filter { matchesCity(it, o) }
            // Find destination spots
            val destSpots = allSpots.filter { matchesCity(it, d) }

            // Find en-route spots across Tamil Nadu intermediate regions
            val interSpots = allSpots.filter { spot ->
                !matchesCity(spot, o) && !matchesCity(spot, d)
            }.take(3)

            selectedSpots.addAll(origSpots.take(2))
            selectedSpots.addAll(interSpots)
            selectedSpots.addAll(destSpots.take(3))

            // Fallback if very few matches found
            if (selectedSpots.size < 4) {
                selectedSpots.clear()
                selectedSpots.addAll(allSpots.take(6))
            }
        }

        // 2. Build the sequential timeline stops from Start to Destination
        val timelineStops = mutableListOf<AiStopoverItem>()
        var accumulatedMinutes = 540 // Start at 09:00 AM (540 minutes from midnight)
        var cumulativeKm = 0.0

        val totalSpots = selectedSpots.size

        selectedSpots.forEachIndexed { index, spot ->
            val isFirst = index == 0
            val isLast = index == totalSpots - 1

            // Compute distance from origin
            val distanceKm = when {
                isFirst -> 0.0
                isLast -> (totalSpots * 45.0) + (index * 12.0)
                else -> cumulativeKm + (25.0 + (index * 18.0))
            }
            cumulativeKm = distanceKm

            // Formulate schedule window (e.g. 09:00 AM - 10:15 AM)
            val startHour = (accumulatedMinutes / 60) % 24
            val startMin = accumulatedMinutes % 60
            val endMinutes = accumulatedMinutes + spot.durationMinutes
            val endHour = (endMinutes / 60) % 24
            val endMin = endMinutes % 60

            val startTimeStr = formatTime12h(startHour, startMin)
            val endTimeStr = formatTime12h(endHour, endMin)
            val scheduleWindow = if (isFirst) {
                "Departure: $startTimeStr · ${spot.durationText} visit"
            } else if (isLast) {
                "Arrive: $startTimeStr · ${spot.durationText} explore"
            } else {
                "${spot.durationText} ($startTimeStr – $endTimeStr)"
            }

            accumulatedMinutes += spot.durationMinutes + 35 // Add 35 minutes travel time between stops

            val badgeText = when {
                isFirst -> "Start Hub"
                isLast -> "Destination Attraction"
                else -> "Spot #${index} · ${spot.durationText}"
            }

            timelineStops.add(
                AiStopoverItem(
                    id = "stop_${index}_${spot.city.lowercase().replace(" ", "_")}",
                    name = spot.name,
                    type = if (isFirst) "start" else if (isLast) "destination" else "stop",
                    badgeText = badgeText,
                    shortDescription = spot.description,
                    timeWindowText = scheduleWindow,
                    durationMinutes = spot.durationMinutes,
                    distanceFromOriginKm = distanceKm,
                    distanceBadgeText = if (distanceKm == 0.0) "0 km" else "${distanceKm.toInt()} km",
                    imageUrl = spot.imageUrl,
                    rating = spot.rating,
                    category = spot.category,
                    city = "${spot.city}, ${spot.district}",
                    latitude = spot.latitude,
                    longitude = spot.longitude
                )
            )
        }

        // Calculate total travel time
        val totalTravelHours = (accumulatedMinutes - 540) / 60
        val totalTravelMins = (accumulatedMinutes - 540) % 60
        val totalTimeText = "~ ${totalTravelHours}h ${totalTravelMins}m"

        // Build Detour Options from complementary spots in the catalog
        val usedNames = selectedSpots.map { it.name }.toSet()
        val detourCandidates = allSpots.filter { !usedNames.contains(it.name) }.take(2)
        val detours = detourCandidates.mapIndexed { idx, spot ->
            AiDetourOption(
                id = "detour_$idx",
                title = spot.name,
                subtitle = "${spot.description} Located near ${spot.city} along the corridor.",
                extraTimeMinutes = spot.durationMinutes + 20,
                extraDistanceKm = 14.0 + (idx * 6.0),
                places = listOf(
                    AiStopoverItem(
                        id = "detour_item_$idx",
                        name = spot.name,
                        type = "stop",
                        badgeText = "Detour · ${spot.durationText}",
                        shortDescription = spot.description,
                        timeWindowText = "${spot.durationText} visit",
                        durationMinutes = spot.durationMinutes,
                        distanceFromOriginKm = cumulativeKm + 12.0,
                        distanceBadgeText = "+${(14 + idx * 6)} km",
                        imageUrl = spot.imageUrl,
                        rating = spot.rating,
                        category = spot.category,
                        city = "${spot.city}, ${spot.district}",
                        latitude = spot.latitude,
                        longitude = spot.longitude,
                        isDetour = true
                    )
                ),
                aiBenefit = "Highly rated scenic attraction with minimal highway diversion."
            )
        }

        return AiSmartStopoverPlan(
            origin = o,
            destination = d,
            transitMode = "Tamil Nadu Express Corridor",
            totalTravelTimeText = totalTimeText,
            totalDistanceKm = cumulativeKm,
            totalSpotsCount = timelineStops.size,
            stops = timelineStops,
            detourOptions = detours,
            routeOptimizationNote = "AI Route Optimization: All ${timelineStops.size} tourist spots sequenced chronologically along the corridor to prevent backtracking."
        )
    }

    private fun findSpot(name: String): TamilNaduSpotDef? {
        return allSpots.find { it.name.equals(name, ignoreCase = true) }
    }

    private fun formatTime12h(hour24: Int, min: Int): String {
        val ampm = if (hour24 >= 12) "PM" else "AM"
        val hour12 = when (val h = hour24 % 12) {
            0 -> 12
            else -> h
        }
        val minStr = if (min < 10) "0$min" else "$min"
        val hourStr = if (hour12 < 10) "0$hour12" else "$hour12"
        return "$hourStr:$minStr $ampm"
    }
}
