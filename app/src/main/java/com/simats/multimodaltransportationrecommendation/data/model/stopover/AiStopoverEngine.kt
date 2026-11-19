package com.simats.multimodaltransportationrecommendation.data.model.stopover

import kotlin.math.roundToInt

object AiStopoverEngine {

    fun generateStopoverPlan(originInput: String?, destinationInput: String?): AiSmartStopoverPlan {
        val o = (originInput ?: "Chennai").trim()
        val d = (destinationInput ?: "Marina Beach").trim()
        val oLower = o.lowercase()
        val dLower = d.lowercase()

        // 1. CHENNAI LOCAL (e.g., Chennai Central -> Marina Beach)
        if ((oLower.contains("chennai") || oLower.contains("central") || oLower.contains("egmore")) &&
            (dLower.contains("marina") || dLower.contains("beach") || dLower.contains("besant") || dLower.contains("mylapore"))
        ) {
            val stops = listOf(
                AiStopoverItem(
                    id = "stop_chn_start",
                    name = if (o.contains("Central", ignoreCase = true)) o else "Chennai Central",
                    type = "start",
                    badgeText = "Start",
                    shortDescription = "Departure: 09:00 AM",
                    timeWindowText = "09:00 AM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 0.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
                    rating = 4.8,
                    category = "Transit Terminal",
                    city = "Chennai"
                ),
                AiStopoverItem(
                    id = "stop_chn_1",
                    name = "Kapaleeswarar Temple",
                    type = "stop",
                    badgeText = "Stop 1 • 1h",
                    shortDescription = "Explore the historic temple",
                    timeWindowText = "1h (09:45 AM – 10:45 AM)",
                    durationMinutes = 60,
                    distanceFromOriginKm = 12.0,
                    distanceBadgeText = "12 km",
                    imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
                    rating = 4.9,
                    category = "Dravidian Heritage",
                    city = "Mylapore, Chennai"
                ),
                AiStopoverItem(
                    id = "stop_chn_2",
                    name = "Besant Nagar Beach",
                    type = "stop",
                    badgeText = "Stop 2 • 1h 30m",
                    shortDescription = "Relax by the beach",
                    timeWindowText = "1h 30m (11:15 AM – 12:45 PM)",
                    durationMinutes = 90,
                    distanceFromOriginKm = 20.0,
                    distanceBadgeText = "20 km",
                    imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
                    rating = 4.7,
                    category = "Coastal Promenade",
                    city = "Besant Nagar, Chennai"
                ),
                AiStopoverItem(
                    id = "stop_chn_dest",
                    name = if (d.isNotBlank()) d else "Marina Beach",
                    type = "destination",
                    badgeText = "Destination",
                    shortDescription = "Arrive: 01:00 PM",
                    timeWindowText = "01:00 PM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 24.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
                    rating = 4.8,
                    category = "Iconic Seafront",
                    city = "Chennai"
                )
            )

            val detours = listOf(
                AiDetourOption(
                    id = "detour_chn_1",
                    title = "Santhome Basilica & Lighthouse",
                    subtitle = "Historic colonial seafront neo-gothic cathedral & scenic panoramic elevator view.",
                    extraTimeMinutes = 40,
                    extraDistanceKm = 3.5,
                    places = listOf(
                        AiStopoverItem(
                            id = "detour_place_santhome",
                            name = "Santhome Cathedral Basilica",
                            type = "stop",
                            badgeText = "Detour • 40m",
                            shortDescription = "Apostle St. Thomas shrine & museum",
                            timeWindowText = "40m (01:15 PM – 01:55 PM)",
                            durationMinutes = 40,
                            distanceFromOriginKm = 14.5,
                            distanceBadgeText = "+3.5 km",
                            imageUrl = "https://images.unsplash.com/photo-1548625361-195fe61775e5?w=600&q=80",
                            rating = 4.7,
                            category = "Historical Monument",
                            city = "Chennai",
                            isDetour = true
                        )
                    ),
                    aiBenefit = "Directly on coastal corridor route with zero highway delay."
                ),
                AiDetourOption(
                    id = "detour_chn_2",
                    title = "Guindy National Park & Snake Park",
                    subtitle = "Protected urban deer park & rainforest sanctuary in the heart of Chennai.",
                    extraTimeMinutes = 55,
                    extraDistanceKm = 6.2,
                    places = listOf(
                        AiStopoverItem(
                            id = "detour_place_guindy",
                            name = "Guindy National Park",
                            type = "stop",
                            badgeText = "Detour • 55m",
                            shortDescription = "Green urban forest & wildlife safari",
                            timeWindowText = "55m (02:15 PM – 03:10 PM)",
                            durationMinutes = 55,
                            distanceFromOriginKm = 16.0,
                            distanceBadgeText = "+6.2 km",
                            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
                            rating = 4.6,
                            category = "Nature & Wildlife",
                            city = "Chennai",
                            isDetour = true
                        )
                    ),
                    aiBenefit = "Family & kids friendly eco-stopover with shady tree canopy."
                )
            )

            return AiSmartStopoverPlan(
                origin = o,
                destination = d,
                transitMode = "Bus & Metro",
                totalTravelTimeText = "~ 5h 30m",
                totalDistanceKm = 24.0,
                stops = stops,
                detourOptions = detours,
                insights = TravelInsightsData(
                    temperature = "30°C",
                    weatherCondition = "Sunny",
                    crowdLevel = "Moderate Crowd",
                    overallRating = "4.6 / 5",
                    bestTimeVisit = "Nov – Feb",
                    summaryText = "Comfortable sea breeze along coastal Marina corridor. Low humidity expected until afternoon.",
                    peakHours = "04:30 PM – 07:30 PM",
                    localAdvice = "Sun protection recommended at beaches. Evening street food at Marina is vibrant and safe."
                ),
                specialOffers = listOf(
                    SpecialOfferItem(
                        id = "offer_1",
                        code = "CHENNAIEXPLORE",
                        title = "Chennai Heritage Hop-on Pass",
                        discount = "25% OFF",
                        description = "Unlimited AC Hop-on Hop-off bus rides between Central, Mylapore, and Marina.",
                        validUntil = "Today only"
                    ),
                    SpecialOfferItem(
                        id = "offer_2",
                        code = "MYLAPOREEATS",
                        title = "Traditional South Indian Filter Coffee",
                        discount = "FREE Voucher",
                        description = "Complimentary filter coffee & ghee sweet at Rayar's Mess Mylapore.",
                        validUntil = "This weekend"
                    )
                ),
                routeOptimizationNote = "AI Route Optimization: Attractions sequenced to avoid GST Road morning congestion, saving 35 mins."
            )
        }

        // 2. CHENNAI <-> COIMBATORE CORRIDOR
        if ((oLower.contains("chennai") && dLower.contains("coimbatore")) ||
            (oLower.contains("coimbatore") && dLower.contains("chennai"))
        ) {
            val stops = listOf(
                AiStopoverItem(
                    id = "cbe_start",
                    name = o,
                    type = "start",
                    badgeText = "Start",
                    shortDescription = "Departure: 06:20 AM",
                    timeWindowText = "06:20 AM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 0.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
                    rating = 4.8,
                    category = "Transit Terminal",
                    city = o
                ),
                AiStopoverItem(
                    id = "cbe_stop1",
                    name = "Kanchipuram Silk Temples",
                    type = "stop",
                    badgeText = "Stop 1 • 1h 15m",
                    shortDescription = "Explore Ekambareswarar & Silk Looms",
                    timeWindowText = "1h 15m (07:45 AM – 09:00 AM)",
                    durationMinutes = 75,
                    distanceFromOriginKm = 72.0,
                    distanceBadgeText = "72 km",
                    imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
                    rating = 4.9,
                    category = "UNESCO Heritage & Handlooms",
                    city = "Kanchipuram"
                ),
                AiStopoverItem(
                    id = "cbe_stop2",
                    name = "Salem 1008 Shiva Temple & Mango Valley",
                    type = "stop",
                    badgeText = "Stop 2 • 1h",
                    shortDescription = "Scenic foothill shrine & Salem cuisine",
                    timeWindowText = "1h (12:00 PM – 01:00 PM)",
                    durationMinutes = 60,
                    distanceFromOriginKm = 340.0,
                    distanceBadgeText = "340 km",
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
                    rating = 4.8,
                    category = "Spiritual & Nature",
                    city = "Salem"
                ),
                AiStopoverItem(
                    id = "cbe_dest",
                    name = d,
                    type = "destination",
                    badgeText = "Destination",
                    shortDescription = "Arrive: 03:30 PM",
                    timeWindowText = "03:30 PM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 496.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
                    rating = 4.9,
                    category = "Smart City Destination",
                    city = d
                )
            )

            val detours = listOf(
                AiDetourOption(
                    id = "detour_yercaud",
                    title = "Yercaud Hill Station Foothill Loop",
                    subtitle = "Ascend the 20 hairpin curves for cool mountain views and Emerald Lake boating.",
                    extraTimeMinutes = 75,
                    extraDistanceKm = 32.0,
                    places = listOf(
                        AiStopoverItem(
                            id = "detour_place_yercaud",
                            name = "Yercaud Emerald Lake & Lady's Seat",
                            type = "stop",
                            badgeText = "Detour • 1h 15m",
                            shortDescription = "Serene mountain lake & valley viewpoint",
                            timeWindowText = "1h 15m (01:15 PM – 02:30 PM)",
                            durationMinutes = 75,
                            distanceFromOriginKm = 372.0,
                            distanceBadgeText = "+32 km",
                            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
                            rating = 4.8,
                            category = "Hill Station Retreat",
                            city = "Yercaud, Salem",
                            isDetour = true
                        )
                    ),
                    aiBenefit = "Escape the plains heat with pleasant 22°C mountain weather."
                ),
                AiDetourOption(
                    id = "detour_bhavani",
                    title = "Bhavani Sangameswarar Kooduthurai",
                    subtitle = "The holy confluence of Cauvery, Bhavani, and the mystical Amudha rivers.",
                    extraTimeMinutes = 40,
                    extraDistanceKm = 14.0,
                    places = listOf(
                        AiStopoverItem(
                            id = "detour_place_bhavani",
                            name = "Bhavani Kooduthurai Sangam",
                            type = "stop",
                            badgeText = "Detour • 40m",
                            shortDescription = "Sacred river confluence & stone ghats",
                            timeWindowText = "40m (02:45 PM – 03:25 PM)",
                            durationMinutes = 40,
                            distanceFromOriginKm = 398.0,
                            distanceBadgeText = "+14 km",
                            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
                            rating = 4.7,
                            category = "Riverside Heritage",
                            city = "Bhavani, Erode",
                            isDetour = true
                        )
                    ),
                    aiBenefit = "Directly along NH 544 highway with zero toll detour."
                )
            )

            return AiSmartStopoverPlan(
                origin = o,
                destination = d,
                transitMode = "Express Highway & Train",
                totalTravelTimeText = "~ 7h 45m",
                totalDistanceKm = 496.0,
                stops = stops,
                detourOptions = detours,
                insights = TravelInsightsData(
                    temperature = "28°C",
                    weatherCondition = "Partly Cloudy",
                    crowdLevel = "Low to Moderate",
                    overallRating = "4.8 / 5",
                    bestTimeVisit = "Oct – Mar",
                    summaryText = "Smooth 4-lane NH 544 express corridor with excellent highway amenities and toll plazas.",
                    peakHours = "08:30 AM – 10:00 AM (Chengalpattu bypass)",
                    localAdvice = "Famous Salem Thattu Vadai Set and Erode fresh ghee sweets available en-route."
                ),
                specialOffers = listOf(
                    SpecialOfferItem(
                        id = "offer_cbe_1",
                        code = "NH544EXPRESS",
                        title = "Highway Food Court & Fuel Rebate",
                        discount = "₹150 OFF",
                        description = "Flat ₹150 off at Saravana Bhavan & A2B highway express stopovers on orders above ₹500.",
                        validUntil = "Valid for 7 days"
                    ),
                    SpecialOfferItem(
                        id = "offer_cbe_2",
                        code = "ADIYOGIPASS",
                        title = "Isha Yoga & Adiyogi Priority Entry",
                        discount = "FREE Guide",
                        description = "Complimentary audio guide and parking pass at Adiyogi Shiva Statue.",
                        validUntil = "Valid all month"
                    )
                ),
                routeOptimizationNote = "AI Route Optimization: Timed stopovers align with off-peak highway hours, saving 50 mins."
            )
        }

        // 3. CHENNAI <-> MADURAI CORRIDOR
        if ((oLower.contains("chennai") && dLower.contains("madurai")) ||
            (oLower.contains("madurai") && dLower.contains("chennai"))
        ) {
            val stops = listOf(
                AiStopoverItem(
                    id = "mdu_start",
                    name = o,
                    type = "start",
                    badgeText = "Start",
                    shortDescription = "Departure: 06:00 AM",
                    timeWindowText = "06:00 AM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 0.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
                    rating = 4.8,
                    category = "Departure Hub",
                    city = o
                ),
                AiStopoverItem(
                    id = "mdu_stop1",
                    name = "Gingee Fort (Troy of the East)",
                    type = "stop",
                    badgeText = "Stop 1 • 1h",
                    shortDescription = "Explore the historic hill fortress",
                    timeWindowText = "1h (08:30 AM – 09:30 AM)",
                    durationMinutes = 60,
                    distanceFromOriginKm = 155.0,
                    distanceBadgeText = "155 km",
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
                    rating = 4.9,
                    category = "National Heritage Monument",
                    city = "Villupuram"
                ),
                AiStopoverItem(
                    id = "mdu_stop2",
                    name = "Srirangam Ranganathaswamy Temple",
                    type = "stop",
                    badgeText = "Stop 2 • 1h 30m",
                    shortDescription = "World's largest functioning temple complex",
                    timeWindowText = "1h 30m (11:45 AM – 01:15 PM)",
                    durationMinutes = 90,
                    distanceFromOriginKm = 330.0,
                    distanceBadgeText = "330 km",
                    imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
                    rating = 5.0,
                    category = "Dravidian Architectural Wonder",
                    city = "Tiruchirappalli (Trichy)"
                ),
                AiStopoverItem(
                    id = "mdu_dest",
                    name = d,
                    type = "destination",
                    badgeText = "Destination",
                    shortDescription = "Arrive: 03:15 PM",
                    timeWindowText = "03:15 PM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 460.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
                    rating = 4.9,
                    category = "Temple City Destination",
                    city = d
                )
            )

            val detours = listOf(
                AiDetourOption(
                    id = "detour_rockfort",
                    title = "Trichy Rockfort Ucchi Pillayar",
                    subtitle = "Climb the 417 stone steps carved into an 83-meter ancient monolithic rock.",
                    extraTimeMinutes = 50,
                    extraDistanceKm = 8.0,
                    places = listOf(
                        AiStopoverItem(
                            id = "detour_place_rockfort",
                            name = "Trichy Rockfort Temple",
                            type = "stop",
                            badgeText = "Detour • 50m",
                            shortDescription = "Monolithic cliff-top temple with panoramic view",
                            timeWindowText = "50m (01:25 PM – 02:15 PM)",
                            durationMinutes = 50,
                            distanceFromOriginKm = 338.0,
                            distanceBadgeText = "+8 km",
                            imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
                            rating = 4.8,
                            category = "Historical Rock Architecture",
                            city = "Trichy",
                            isDetour = true
                        )
                    ),
                    aiBenefit = "Breath-taking panoramic 360° views across the entire Cauvery basin."
                ),
                AiDetourOption(
                    id = "detour_dindigul",
                    title = "Dindigul Rock Fort & Thalappakatti Origin",
                    subtitle = "Historic Nayakar hill fort and authentic traditional Dindigul biryani tasting.",
                    extraTimeMinutes = 45,
                    extraDistanceKm = 10.0,
                    places = listOf(
                        AiStopoverItem(
                            id = "detour_place_dindigul",
                            name = "Dindigul Historic Fort",
                            type = "stop",
                            badgeText = "Detour • 45m",
                            shortDescription = "17th century hill citadel & culinary hub",
                            timeWindowText = "45m (02:30 PM – 03:15 PM)",
                            durationMinutes = 45,
                            distanceFromOriginKm = 425.0,
                            distanceBadgeText = "+10 km",
                            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
                            rating = 4.7,
                            category = "Fortress & Food Heritage",
                            city = "Dindigul",
                            isDetour = true
                        )
                    ),
                    aiBenefit = "Perfect lunch stopover point right at the foot of Kodai hills."
                )
            )

            return AiSmartStopoverPlan(
                origin = o,
                destination = d,
                transitMode = "NH 45 Express & Rail",
                totalTravelTimeText = "~ 7h 15m",
                totalDistanceKm = 460.0,
                stops = stops,
                detourOptions = detours,
                insights = TravelInsightsData(
                    temperature = "31°C",
                    weatherCondition = "Sunny & Warm",
                    crowdLevel = "Moderate Crowd",
                    overallRating = "4.9 / 5",
                    bestTimeVisit = "Oct – Mar",
                    summaryText = "NH 45 Grand Southern Trunk road is in prime condition. Optimal afternoon temple timing.",
                    peakHours = "12:00 PM – 01:30 PM (Trichy bypass)",
                    localAdvice = "Srirangam Temple closes sanctum between 1:00 PM - 3:30 PM. Arrive before 12:30 PM for darshan."
                ),
                specialOffers = listOf(
                    SpecialOfferItem(
                        id = "offer_mdu_1",
                        code = "MEENAKSHI20",
                        title = "Heritage Temple Special Darshan",
                        discount = "20% OFF",
                        description = "Special fast-track queue pass and certified temple guide assistance.",
                        validUntil = "Valid today"
                    ),
                    SpecialOfferItem(
                        id = "offer_mdu_2",
                        code = "JIGARTHANDA",
                        title = "Authentic Famous Jigarthanda",
                        discount = "Buy 1 Get 1 Free",
                        description = "Famous Madurai Royal Jigarthanda voucher at East Gate outlet.",
                        validUntil = "Valid for 3 days"
                    )
                ),
                routeOptimizationNote = "AI Route Optimization: Srirangam timed right before mid-day sanctum recess, saving 1 hour wait."
            )
        }

        // 4. HILL STATIONS (Ooty, Kodaikanal, Yercaud, Coonoor)
        if (dLower.contains("ooty") || dLower.contains("kodaikanal") || dLower.contains("yercaud") ||
            dLower.contains("coonoor") || dLower.contains("valparai")
        ) {
            val hillName = when {
                dLower.contains("ooty") -> "Ooty"
                dLower.contains("kodaikanal") -> "Kodaikanal"
                dLower.contains("yercaud") -> "Yercaud"
                dLower.contains("coonoor") -> "Coonoor"
                else -> "Hill Station"
            }

            val stops = listOf(
                AiStopoverItem(
                    id = "hill_start",
                    name = o,
                    type = "start",
                    badgeText = "Start",
                    shortDescription = "Departure: 07:00 AM",
                    timeWindowText = "07:00 AM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 0.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
                    rating = 4.8,
                    category = "Departure City",
                    city = o
                ),
                AiStopoverItem(
                    id = "hill_stop1",
                    name = "Foothill Mountain Gateway & Waterfall",
                    type = "stop",
                    badgeText = "Stop 1 • 1h",
                    shortDescription = "Scenic mountain stream & tea gardens",
                    timeWindowText = "1h (10:00 AM – 11:00 AM)",
                    durationMinutes = 60,
                    distanceFromOriginKm = 120.0,
                    distanceBadgeText = "120 km",
                    imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
                    rating = 4.9,
                    category = "Mountain Waterfall",
                    city = "$hillName Foothills"
                ),
                AiStopoverItem(
                    id = "hill_stop2",
                    name = "Heritage Tea Plantation & Spice Garden",
                    type = "stop",
                    badgeText = "Stop 2 • 1h 30m",
                    shortDescription = "Guided tea tasting & Eucalyptus factory",
                    timeWindowText = "1h 30m (12:30 PM – 02:00 PM)",
                    durationMinutes = 90,
                    distanceFromOriginKm = 165.0,
                    distanceBadgeText = "165 km",
                    imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
                    rating = 4.9,
                    category = "Agro-Tourism & Nature",
                    city = hillName
                ),
                AiStopoverItem(
                    id = "hill_dest",
                    name = d,
                    type = "destination",
                    badgeText = "Destination",
                    shortDescription = "Arrive: 03:00 PM",
                    timeWindowText = "03:00 PM",
                    durationMinutes = 0,
                    distanceFromOriginKm = 210.0,
                    distanceBadgeText = "0 km",
                    imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
                    rating = 4.9,
                    category = "Hill Station Capital",
                    city = d
                )
            )

            val detours = listOf(
                AiDetourOption(
                    id = "detour_pineneedle",
                    title = "Pine Forest & Valley Viewpoint",
                    subtitle = "Towering pine tree canopy and scenic mist-covered valley viewpoint.",
                    extraTimeMinutes = 45,
                    extraDistanceKm = 12.0,
                    places = listOf(
                        AiStopoverItem(
                            id = "detour_pine",
                            name = "Pine Forest Nature Trail",
                            type = "stop",
                            badgeText = "Detour • 45m",
                            shortDescription = "Serene pine woods with photography spots",
                            timeWindowText = "45m (02:15 PM – 03:00 PM)",
                            durationMinutes = 45,
                            distanceFromOriginKm = 190.0,
                            distanceBadgeText = "+12 km",
                            imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
                            rating = 4.8,
                            category = "Forest Trail",
                            city = hillName,
                            isDetour = true
                        )
                    ),
                    aiBenefit = "Highest rated sunset viewpoint with crisp cool pine air."
                )
            )

            return AiSmartStopoverPlan(
                origin = o,
                destination = d,
                transitMode = "Mountain Ghat Highway",
                totalTravelTimeText = "~ 6h 30m",
                totalDistanceKm = 210.0,
                stops = stops,
                detourOptions = detours,
                insights = TravelInsightsData(
                    temperature = "18°C",
                    weatherCondition = "Cool & Misting",
                    crowdLevel = "Moderate Crowd",
                    overallRating = "4.9 / 5",
                    bestTimeVisit = "Sep – May",
                    summaryText = "Chilly mountain weather with light fog in early mornings and late afternoons.",
                    peakHours = "01:00 PM – 04:00 PM (Ghat road hairpin bends)",
                    localAdvice = "Drive cautiously on hairpin bends. Warm jackets and woolens strongly recommended."
                ),
                specialOffers = listOf(
                    SpecialOfferItem(
                        id = "offer_hill_1",
                        code = "HILLRESCUE15",
                        title = "Tea Factory & Handmade Chocolate Tasting",
                        discount = "FREE Box",
                        description = "Free 100g assortment box of handcrafted Nilgiri fudge chocolates on tour visit.",
                        validUntil = "Valid this week"
                    )
                ),
                routeOptimizationNote = "AI Route Optimization: Ghat ascent scheduled before dense evening fog sets in, saving 40 mins."
            )
        }

        // 5. GENERAL TAMIL NADU SMART PLAN
        val stops = listOf(
            AiStopoverItem(
                id = "gen_start",
                name = o,
                type = "start",
                badgeText = "Start",
                shortDescription = "Departure: 08:30 AM",
                timeWindowText = "08:30 AM",
                durationMinutes = 0,
                distanceFromOriginKm = 0.0,
                distanceBadgeText = "0 km",
                imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&q=80",
                rating = 4.8,
                category = "Starting Point",
                city = o
            ),
            AiStopoverItem(
                id = "gen_stop1",
                name = "En-route Historic Landmark & Heritage Hub",
                type = "stop",
                badgeText = "Stop 1 • 1h",
                shortDescription = "Explore historic temple architecture & local art",
                timeWindowText = "1h (10:15 AM – 11:15 AM)",
                durationMinutes = 60,
                distanceFromOriginKm = 58.0,
                distanceBadgeText = "58 km",
                imageUrl = "https://images.unsplash.com/photo-1609137144813-7d9921338f24?w=600&q=80",
                rating = 4.8,
                category = "Tamil Nadu Heritage",
                city = "En-route Corridor"
            ),
            AiStopoverItem(
                id = "gen_stop2",
                name = "Highway Oasis & Traditional Delicacy Center",
                type = "stop",
                badgeText = "Stop 2 • 1h 15m",
                shortDescription = "Taste traditional delicacies & garden relaxation",
                timeWindowText = "1h 15m (01:00 PM – 02:15 PM)",
                durationMinutes = 75,
                distanceFromOriginKm = 145.0,
                distanceBadgeText = "145 km",
                imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80",
                rating = 4.7,
                category = "Highway Oasis",
                city = "En-route Transit Stop"
            ),
            AiStopoverItem(
                id = "gen_dest",
                name = d,
                type = "destination",
                badgeText = "Destination",
                shortDescription = "Arrive: 03:45 PM",
                timeWindowText = "03:45 PM",
                durationMinutes = 0,
                distanceFromOriginKm = 210.0,
                distanceBadgeText = "0 km",
                imageUrl = "https://images.unsplash.com/photo-1582510003544-4d00b7f74220?w=600&q=80",
                rating = 4.9,
                category = "Final Destination",
                city = d
            )
        )

        val detours = listOf(
            AiDetourOption(
                id = "detour_gen_1",
                title = "Scenic Lake & Sanctuary Detour",
                subtitle = "Take a short 25-minute diversion to enjoy peaceful water views and bird watching.",
                extraTimeMinutes = 45,
                extraDistanceKm = 11.5,
                places = listOf(
                    AiStopoverItem(
                        id = "detour_place_gen_1",
                        name = "Scenic Lakeside Promenade",
                        type = "stop",
                        badgeText = "Detour • 45m",
                        shortDescription = "Serene lakefront park & boating",
                        timeWindowText = "45m (02:30 PM – 03:15 PM)",
                        durationMinutes = 45,
                        distanceFromOriginKm = 170.0,
                        distanceBadgeText = "+11.5 km",
                        imageUrl = "https://images.unsplash.com/photo-1448375240586-882707db888b?w=600&q=80",
                        rating = 4.8,
                        category = "Scenic Nature",
                        city = "Tamil Nadu",
                        isDetour = true
                    )
                ),
                aiBenefit = "Low crowd density with picturesque photography viewpoints."
            )
        )

        return AiSmartStopoverPlan(
            origin = o,
            destination = d,
            transitMode = "Tamil Nadu Highway Corridor",
            totalTravelTimeText = "~ 6h 15m",
            totalDistanceKm = 210.0,
            stops = stops,
            detourOptions = detours,
            insights = TravelInsightsData(
                temperature = "29°C",
                weatherCondition = "Clear Sky",
                crowdLevel = "Moderate Crowd",
                overallRating = "4.7 / 5",
                bestTimeVisit = "Oct – Mar",
                summaryText = "Favorable weather throughout Tamil Nadu with pleasant travel conditions.",
                peakHours = "11:00 AM – 02:00 PM",
                localAdvice = "Comfortable walking shoes recommended for temple and garden stopovers."
            ),
            specialOffers = listOf(
                SpecialOfferItem(
                    id = "offer_gen_1",
                    code = "TNTOUR15",
                    title = "Tamil Nadu Tourism Special Pass",
                    discount = "15% OFF",
                    description = "Special discount on tourist attractions, museums, and partnered restaurants.",
                    validUntil = "Valid for 14 days"
                )
            ),
            routeOptimizationNote = "AI Route Optimization: Multi-modal connections synchronized to prevent traffic bottlenecks."
        )
    }
}
