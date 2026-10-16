package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ActivityLogEntity
import com.example.data.local.entity.DictionaryTermEntity
import com.example.data.local.entity.JournalEntryEntity
import com.example.data.local.entity.PortfolioProjectEntity
import com.example.data.local.entity.QuestEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.data.model.ActivityLog
import com.example.data.model.DictionaryTerm
import com.example.data.model.JournalEntry
import com.example.data.model.PortfolioProject
import com.example.data.model.Quest
import com.example.data.model.UserProfile
import com.example.data.util.OperationType
import com.example.data.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class NetQuestRepository(
    private val db: FirebaseFirestore? = null,
    private val context: Context? = null
) {

    constructor(context: Context) : this(
        db = try {
            FirebaseFirestore.getInstance(
                context.applicationContext.getString(R.string.firestore_database_id)
            )
        } catch (_: Exception) {
            null
        },
        context = context.applicationContext
    )

    constructor(db: FirebaseFirestore) : this(db = db, context = null)

    private val localDb: AppDatabase? = context?.let { AppDatabase.getDatabase(it) }
    private val auth = Firebase.auth

    fun requireUserId(): String {
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) return currentUid
        if (localDb != null) return "local_offline_engineer"
        throw IllegalStateException("User must be signed in to perform this operation")
    }

    fun currentUserIdOrNull(): String? = auth.currentUser?.uid ?: if (localDb != null) "local_offline_engineer" else null

    // ==========================================
    // CAPPED PROGRESSIVE XP STREAK MULTIPLIER
    // Days 1 to 20: +0.05x per day (M = 1.0 + 0.05 * d)
    // Day 21+: +0.01x per day, capped at 2.5x max
    // ==========================================
    fun calculateStreakMultiplier(days: Long): Float {
        if (days <= 0L) return 1.0f
        return if (days <= 20L) {
            1.0f + (days * 0.05f)
        } else {
            minOf(2.5f, 2.0f + ((days - 20L) * 0.01f))
        }
    }

    // ==========================================
    // USER PROFILE & GAMIFICATION
    // ==========================================

    fun observeUserProfile(): Flow<UserProfile?> = flow {
        if (localDb != null) {
            val uid = requireUserId()
            emitAll(
                localDb.userProfileDao().getUserProfile(uid).map { entity ->
                    if (entity == null) {
                        getOrCreateUserProfile("engineer@networkteach.local", "Senior IT Engineer")
                    } else {
                        entity.toModel()
                    }
                }
            )
        } else if (db != null) {
            val uid = requireUserId()
            val path = "users/$uid"
            emitAll(
                db.collection("users").document(uid).snapshots()
                    .map { snapshot ->
                        if (snapshot.exists()) {
                            snapshot.toObject(UserProfile::class.java)
                        } else {
                            null
                        }
                    }
                    .catch { error ->
                        if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                        throw error
                    }
            )
        } else {
            emit(UserProfile(userId = "local_offline_engineer"))
        }
    }

    suspend fun getOrCreateUserProfile(email: String, displayName: String): UserProfile {
        val uid = requireUserId()

        if (localDb != null) {
            val existing = localDb.userProfileDao().getProfileSync(uid)
            if (existing != null) {
                val profile = existing.toModel()
                checkAndUpdateStreak(profile)
                return profile
            }

            val initialProfile = UserProfile(
                userId = uid,
                displayName = displayName.ifBlank { "Senior IT Engineer" },
                email = email,
                xp = 150L,
                level = 1L,
                streakCount = 1L,
                lastActiveDate = todayString(),
                badges = listOf("rookie_badge"),
                pinHash = "",
                failedPinAttempts = 0L,
                lockoutUntil = 0L
            )
            localDb.userProfileDao().insertOrUpdate(UserProfileEntity.fromModel(initialProfile))
            seedInitialLocalData(uid)
            return initialProfile
        }

        if (db != null) {
            val docRef = db.collection("users").document(uid)
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                val profile = snapshot.toObject(UserProfile::class.java) ?: UserProfile(userId = uid)
                checkAndUpdateStreak(profile)
                return profile
            }

            val initialProfile = UserProfile(
                userId = uid,
                displayName = displayName.ifBlank { "IT Engineer" },
                email = email,
                xp = 100L,
                level = 1L,
                streakCount = 1L,
                lastActiveDate = todayString(),
                badges = listOf("rookie_badge"),
                pinHash = "",
                failedPinAttempts = 0L,
                lockoutUntil = 0L
            )
            val payload = initialProfile.toMap() + mapOf(
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            seedInitialFirestoreData(uid)
            return initialProfile
        }

        return UserProfile(userId = uid)
    }

    private fun todayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    private suspend fun checkAndUpdateStreak(profile: UserProfile) {
        val today = todayString()
        if (profile.lastActiveDate == today) return

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        var newStreak = profile.streakCount
        try {
            val lastDate = sdf.parse(profile.lastActiveDate)
            if (lastDate != null) {
                val diffDays = (Date().time - lastDate.time) / (1000 * 60 * 60 * 24)
                if (diffDays == 1L) {
                    newStreak += 1
                } else if (diffDays > 1L) {
                    newStreak = 1
                }
            }
        } catch (_: Exception) {
            newStreak = 1
        }

        val updatedProfile = profile.copy(
            streakCount = newStreak,
            lastActiveDate = today
        )

        if (localDb != null) {
            localDb.userProfileDao().insertOrUpdate(UserProfileEntity.fromModel(updatedProfile))
        }

        if (db != null && auth.currentUser != null) {
            try {
                val uid = requireUserId()
                db.collection("users").document(uid).update(
                    mapOf(
                        "streakCount" to newStreak,
                        "lastActiveDate" to today,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (_: Exception) {
                // Offline fallback
            }
        }
    }

    suspend fun awardXp(baseAmount: Long, reason: String = ""): Long {
        val uid = requireUserId()

        var currentProfile: UserProfile? = null
        if (localDb != null) {
            currentProfile = localDb.userProfileDao().getProfileSync(uid)?.toModel()
        }

        if (currentProfile == null && db != null) {
            try {
                val snap = db.collection("users").document(uid).get().await()
                currentProfile = snap.toObject(UserProfile::class.java)
            } catch (_: Exception) {
                // Offline fallback
            }
        }

        val profile = currentProfile ?: UserProfile(userId = uid)
        val multiplier = calculateStreakMultiplier(profile.streakCount)
        val multipliedXp = (baseAmount.toFloat() * multiplier).toLong().coerceAtLeast(baseAmount)
        val newXp = profile.xp + multipliedXp
        var newLevel = profile.level
        val updatedBadges = profile.badges.toMutableList()

        val levelThresholds = listOf(
            0L to 1L,
            250L to 2L,
            600L to 3L,
            1200L to 4L,
            2000L to 5L,
            3200L to 6L,
            5000L to 7L,
            7500L to 8L,
            10500L to 9L,
            15000L to 10L
        )
        for ((threshold, lvl) in levelThresholds.reversed()) {
            if (newXp >= threshold) {
                newLevel = maxOf(newLevel, lvl)
                break
            }
        }

        if (newLevel >= 3 && !updatedBadges.contains("blueprint_master")) {
            updatedBadges.add("blueprint_master")
        }
        if (newXp >= 1000 && !updatedBadges.contains("xp_1000")) {
            updatedBadges.add("xp_1000")
        }

        val newProfile = profile.copy(
            xp = newXp,
            level = newLevel,
            badges = updatedBadges
        )

        if (localDb != null) {
            localDb.userProfileDao().insertOrUpdate(UserProfileEntity.fromModel(newProfile))
        }

        if (db != null && auth.currentUser != null) {
            try {
                db.collection("users").document(uid).update(
                    mapOf(
                        "xp" to newXp,
                        "level" to newLevel,
                        "badges" to updatedBadges,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (_: Exception) {
                // Offline
            }
        }

        return newXp
    }

    // ==========================================
    // 4-DIGIT PIN SECURITY & LOCKOUT
    // ==========================================

    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun setPin(pin: String) {
        val uid = requireUserId()
        val hash = hashPin(pin)

        if (localDb != null) {
            val existing = localDb.userProfileDao().getProfileSync(uid)
            val updated = (existing?.toModel() ?: UserProfile(userId = uid)).copy(
                pinHash = hash,
                failedPinAttempts = 0L,
                lockoutUntil = 0L
            )
            localDb.userProfileDao().insertOrUpdate(UserProfileEntity.fromModel(updated))
        }

        if (db != null && auth.currentUser != null) {
            try {
                db.collection("users").document(uid).update(
                    mapOf(
                        "pinHash" to hash,
                        "failedPinAttempts" to 0L,
                        "lockoutUntil" to 0L,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (_: Exception) {}
        }
    }

    suspend fun verifyPin(pin: String): PinVerificationResult {
        val uid = requireUserId()
        var profile: UserProfile? = null

        if (localDb != null) {
            profile = localDb.userProfileDao().getProfileSync(uid)?.toModel()
        }
        if (profile == null && db != null) {
            try {
                val snap = db.collection("users").document(uid).get().await()
                profile = snap.toObject(UserProfile::class.java)
            } catch (_: Exception) {}
        }

        if (profile == null) return PinVerificationResult.Success

        val now = System.currentTimeMillis()
        if (profile.lockoutUntil > now) {
            val remainingSec = ((profile.lockoutUntil - now) / 1000).toInt()
            return PinVerificationResult.LockedOut(remainingSec)
        }

        val inputHash = hashPin(pin)
        if (profile.pinHash.isBlank() || profile.pinHash == inputHash) {
            val updated = profile.copy(failedPinAttempts = 0L, lockoutUntil = 0L)
            if (localDb != null) localDb.userProfileDao().insertOrUpdate(UserProfileEntity.fromModel(updated))
            return PinVerificationResult.Success
        } else {
            val nextFailed = profile.failedPinAttempts + 1
            if (nextFailed >= 3) {
                val lockUntil = now + (30 * 1000)
                val updated = profile.copy(failedPinAttempts = nextFailed, lockoutUntil = lockUntil)
                if (localDb != null) localDb.userProfileDao().insertOrUpdate(UserProfileEntity.fromModel(updated))
                return PinVerificationResult.LockedOut(30)
            } else {
                val attemptsLeft = (3 - nextFailed).toInt()
                val updated = profile.copy(failedPinAttempts = nextFailed)
                if (localDb != null) localDb.userProfileDao().insertOrUpdate(UserProfileEntity.fromModel(updated))
                return PinVerificationResult.Failed(attemptsLeft)
            }
        }
    }

    // ==========================================
    // DICTIONARY
    // ==========================================

    fun observeDictionary(): Flow<List<DictionaryTerm>> = flow {
        if (localDb != null) {
            // Seed starter dictionary if empty
            if (localDb.dictionaryDao().getCount() == 0) {
                seedInitialLocalData(requireUserId())
            }
            emitAll(
                localDb.dictionaryDao().getAllTerms().map { list ->
                    list.map { it.toModel() }
                }
            )
        } else if (db != null) {
            val uid = requireUserId()
            val path = "users/$uid/dictionary"
            emitAll(
                db.collection("users").document(uid).collection("dictionary")
                    .snapshots()
                    .map { snapshot -> snapshot.toObjects(DictionaryTerm::class.java) }
                    .catch { error ->
                        if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                        throw error
                    }
            )
        } else {
            emit(emptyList())
        }
    }

    suspend fun addDictionaryTerm(term: DictionaryTerm): String {
        val uid = requireUserId()
        val docId = if (term.id.isNotBlank()) term.id else UUID.randomUUID().toString()
        val finalTerm = term.copy(id = docId, userId = uid)

        if (localDb != null) {
            localDb.dictionaryDao().insertOrUpdate(DictionaryTermEntity.fromModel(finalTerm))
        }

        if (db != null && auth.currentUser != null) {
            try {
                val docRef = db.collection("users").document(uid).collection("dictionary").document(docId)
                val payload = finalTerm.toMap() + mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } catch (_: Exception) {}
        }

        awardXp(15L, "Added dictionary command")
        return docId
    }

    suspend fun updateDictionaryTerm(term: DictionaryTerm) {
        val uid = requireUserId()
        require(term.id.isNotBlank())
        val finalTerm = term.copy(userId = uid)

        if (localDb != null) {
            localDb.dictionaryDao().insertOrUpdate(DictionaryTermEntity.fromModel(finalTerm))
        }

        if (db != null && auth.currentUser != null) {
            try {
                val docRef = db.collection("users").document(uid).collection("dictionary").document(term.id)
                val payload = finalTerm.toMap() + mapOf(
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(payload).await()
            } catch (_: Exception) {}
        }
    }

    suspend fun toggleDictionaryMastered(term: DictionaryTerm) {
        val newMastered = !term.isMastered
        updateDictionaryTerm(term.copy(isMastered = newMastered))
        if (newMastered) {
            awardXp(25L, "Mastered dictionary term")
        }
    }

    suspend fun deleteDictionaryTerm(termId: String) {
        val uid = requireUserId()
        if (localDb != null) {
            localDb.dictionaryDao().deleteById(termId)
        }
        if (db != null && auth.currentUser != null) {
            try {
                db.collection("users").document(uid).collection("dictionary").document(termId).delete().await()
            } catch (_: Exception) {}
        }
    }

    // ==========================================
    // JOURNAL & LAB NOTES
    // ==========================================

    fun observeJournal(): Flow<List<JournalEntry>> = flow {
        if (localDb != null) {
            emitAll(
                localDb.journalDao().getAllEntries().map { list ->
                    list.map { it.toModel() }
                }
            )
        } else if (db != null) {
            val uid = requireUserId()
            val path = "users/$uid/journal"
            emitAll(
                db.collection("users").document(uid).collection("journal")
                    .snapshots()
                    .map { snapshot -> snapshot.toObjects(JournalEntry::class.java) }
                    .catch { error ->
                        if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                        throw error
                    }
            )
        } else {
            emit(emptyList())
        }
    }

    suspend fun addJournalEntry(entry: JournalEntry): String {
        val uid = requireUserId()
        val docId = if (entry.id.isNotBlank()) entry.id else UUID.randomUUID().toString()
        val finalEntry = entry.copy(id = docId, userId = uid)

        if (localDb != null) {
            localDb.journalDao().insertOrUpdate(JournalEntryEntity.fromModel(finalEntry))
        }

        if (db != null && auth.currentUser != null) {
            try {
                val docRef = db.collection("users").document(uid).collection("journal").document(docId)
                val payload = finalEntry.toMap() + mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } catch (_: Exception) {}
        }

        awardXp(30L, "Logged verified lab note")
        return docId
    }

    suspend fun updateJournalEntry(entry: JournalEntry) {
        val uid = requireUserId()
        require(entry.id.isNotBlank())
        val finalEntry = entry.copy(userId = uid)

        if (localDb != null) {
            localDb.journalDao().insertOrUpdate(JournalEntryEntity.fromModel(finalEntry))
        }

        if (db != null && auth.currentUser != null) {
            try {
                val docRef = db.collection("users").document(uid).collection("journal").document(entry.id)
                val payload = finalEntry.toMap() + mapOf(
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(payload).await()
            } catch (_: Exception) {}
        }
    }

    suspend fun toggleJournalVerified(entry: JournalEntry) {
        val newVerified = !entry.isVerified
        updateJournalEntry(entry.copy(isVerified = newVerified))
        if (newVerified) {
            awardXp(40L, "Verified working solution")
        }
    }

    suspend fun deleteJournalEntry(entryId: String) {
        val uid = requireUserId()
        if (localDb != null) {
            localDb.journalDao().deleteById(entryId)
        }
        if (db != null && auth.currentUser != null) {
            try {
                db.collection("users").document(uid).collection("journal").document(entryId).delete().await()
            } catch (_: Exception) {}
        }
    }

    // ==========================================
    // PORTFOLIO & CASE STUDIES
    // ==========================================

    fun observePortfolio(): Flow<List<PortfolioProject>> = flow {
        if (localDb != null) {
            emitAll(
                localDb.portfolioDao().getAllProjects().map { list ->
                    list.map { it.toModel() }
                }
            )
        } else if (db != null) {
            val uid = requireUserId()
            val path = "users/$uid/projects"
            emitAll(
                db.collection("users").document(uid).collection("projects")
                    .snapshots()
                    .map { snapshot -> snapshot.toObjects(PortfolioProject::class.java) }
                    .catch { error ->
                        if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                        throw error
                    }
            )
        } else {
            emit(emptyList())
        }
    }

    suspend fun addPortfolioProject(project: PortfolioProject): String {
        val uid = requireUserId()
        val docId = if (project.id.isNotBlank()) project.id else UUID.randomUUID().toString()
        val finalProject = project.copy(id = docId, userId = uid)

        if (localDb != null) {
            localDb.portfolioDao().insertOrUpdate(PortfolioProjectEntity.fromModel(finalProject))
        }

        if (db != null && auth.currentUser != null) {
            try {
                val docRef = db.collection("users").document(uid).collection("projects").document(docId)
                val payload = finalProject.toMap() + mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            } catch (_: Exception) {}
        }

        awardXp(75L, "Published employer case study")
        return docId
    }

    suspend fun updatePortfolioProject(project: PortfolioProject) {
        val uid = requireUserId()
        require(project.id.isNotBlank())
        val finalProject = project.copy(userId = uid)

        if (localDb != null) {
            localDb.portfolioDao().insertOrUpdate(PortfolioProjectEntity.fromModel(finalProject))
        }

        if (db != null && auth.currentUser != null) {
            try {
                val docRef = db.collection("users").document(uid).collection("projects").document(project.id)
                val payload = finalProject.toMap() + mapOf(
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.update(payload).await()
            } catch (_: Exception) {}
        }
    }

    suspend fun togglePortfolioMastered(project: PortfolioProject) {
        val newMastered = !project.isMastered
        updatePortfolioProject(project.copy(isMastered = newMastered))
        if (newMastered) {
            awardXp(50L, "Mastered Case Study")
        }
    }

    suspend fun deletePortfolioProject(projectId: String) {
        val uid = requireUserId()
        if (localDb != null) {
            localDb.portfolioDao().deleteById(projectId)
        }
        if (db != null && auth.currentUser != null) {
            try {
                db.collection("users").document(uid).collection("projects").document(projectId).delete().await()
            } catch (_: Exception) {}
        }
    }

    // ==========================================
    // QUESTS & CERTIFICATION ROADMAPS
    // ==========================================

    fun observeQuests(): Flow<List<Quest>> = flow {
        if (localDb != null) {
            if (localDb.questDao().getCount() == 0) {
                seedInitialLocalData(requireUserId())
            }
            emitAll(
                localDb.questDao().getAllQuests().map { list ->
                    list.map { it.toModel() }
                }
            )
        } else if (db != null) {
            val uid = requireUserId()
            val path = "users/$uid/quests"
            emitAll(
                db.collection("users").document(uid).collection("quests")
                    .snapshots()
                    .map { snapshot -> snapshot.toObjects(Quest::class.java) }
                    .catch { error ->
                        if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                        throw error
                    }
            )
        } else {
            emit(emptyList())
        }
    }

    suspend fun completeQuest(quest: Quest) {
        val uid = requireUserId()
        require(quest.id.isNotBlank())
        val updatedQuest = quest.copy(status = "COMPLETED", isVerified = true)

        if (localDb != null) {
            localDb.questDao().update(QuestEntity.fromModel(updatedQuest))
        }

        if (db != null && auth.currentUser != null) {
            try {
                db.collection("users").document(uid).collection("quests").document(quest.id).update(
                    mapOf(
                        "status" to "COMPLETED",
                        "isVerified" to true,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } catch (_: Exception) {}
        }

        awardXp(quest.xpReward, "Completed quest: ${quest.title}")
    }

    // ==========================================
    // ACTIVITY LOG TRACKER
    // ==========================================

    fun observeActivityLogs(): Flow<List<ActivityLog>> = flow {
        if (localDb != null) {
            emitAll(
                localDb.activityLogDao().getAllLogs().map { list ->
                    list.map { it.toModel() }
                }
            )
        } else {
            emit(emptyList())
        }
    }

    suspend fun logActivity(log: ActivityLog): String {
        val docId = if (log.id.isNotBlank()) log.id else UUID.randomUUID().toString()
        val finalLog = log.copy(id = docId)

        if (localDb != null) {
            localDb.activityLogDao().insert(ActivityLogEntity.fromModel(finalLog))
        }

        // Award XP for completion
        if (finalLog.status.equals("Completed", ignoreCase = true)) {
            val xpGain = when (finalLog.moduleType) {
                "TEST" -> 100L
                "WORKSHEET" -> 60L
                "FLASHCARDS" -> 40L
                "PROJECT" -> 120L
                else -> 50L
            }
            awardXp(xpGain, "Completed ${finalLog.title}")
        }

        return docId
    }

    suspend fun getRecentIncompleteActivities(): List<ActivityLog> {
        return localDb?.activityLogDao()?.getIncompleteActivities()?.map { it.toModel() } ?: emptyList()
    }

    // ==========================================
    // NON-DESTRUCTIVE INITIAL SEEDING
    // ==========================================

    private suspend fun seedInitialLocalData(uid: String) {
        if (localDb == null) return

        if (localDb.dictionaryDao().getCount() == 0) {
            val starterTerms = getStarterTerms(uid)
            localDb.dictionaryDao().insertAll(starterTerms.map { DictionaryTermEntity.fromModel(it) })
        }

        if (localDb.questDao().getCount() == 0) {
            val starterQuests = getStarterQuests(uid)
            localDb.questDao().insertAll(starterQuests.map { QuestEntity.fromModel(it) })
        }

        if (localDb.journalDao().getCount() == 0) {
            val sampleJournal = JournalEntry(
                id = UUID.randomUUID().toString(),
                userId = uid,
                title = "Wireshark TCP SYN Analysis Lab",
                notes = "Filtered live capture on interface eth0 with 'tcp.flags.syn == 1'. Verified sequence number randomization to mitigate blind spoofing attacks.",
                solution = "sudo tcpdump -i eth0 -nn 'tcp[tcpflags] & tcp-syn != 0' -c 20",
                tags = listOf("wireshark", "tcpdump", "security", "packet-analysis"),
                category = "Networking",
                isVerified = true,
                isMastered = true
            )
            localDb.journalDao().insertOrUpdate(JournalEntryEntity.fromModel(sampleJournal))
        }

        if (localDb.portfolioDao().getCount() == 0) {
            val sampleProject = PortfolioProject(
                id = UUID.randomUUID().toString(),
                userId = uid,
                title = "Enterprise VLAN Segmentation & Subnet Hardening",
                executiveSummary = "Designed and deployed isolated multi-tenant VLANs across core switches, reducing broadcast traffic by 42% and implementing 802.1Q trunking with native VLAN security tagging.",
                problemBusinessNeed = "High broadcast storm latency in shared guest and operational networks, causing packet drops in VoIP phones.",
                toolsTechnologies = listOf("Wireshark", "Nmap", "Angry IP Scanner", "Cisco IOS", "PowerShell"),
                architectureSteps = "1. Designed VLSM /26 address blocks.\n2. Configured 802.1Q switchport mode trunk.\n3. Segmented VoIP onto VLAN 20 and corporate on VLAN 10.\n4. Verified with Wireshark latency capture.",
                resultsMetrics = "Broadcast collision reduction: 42%\nZero packet loss on VoIP VLAN\nAudit passed 100%",
                verifiedEvidence = "PCAP traces clean; show vlan brief verified with no rogue broadcast domains.",
                lessonsLearned = "Always prune unused VLANs on trunk uplinks to prevent unnecessary flooding.",
                isVerified = true,
                isMastered = true
            )
            localDb.portfolioDao().insertOrUpdate(PortfolioProjectEntity.fromModel(sampleProject))
        }
    }

    private suspend fun seedInitialFirestoreData(uid: String) {
        if (db == null) return
        val dictRef = db.collection("users").document(uid).collection("dictionary")
        val starterTerms = getStarterTerms(uid)
        for (item in starterTerms) {
            val docId = item.id.ifBlank { UUID.randomUUID().toString() }
            dictRef.document(docId).set(
                item.copy(id = docId, userId = uid).toMap() + mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }

        val questRef = db.collection("users").document(uid).collection("quests")
        val starterQuests = getStarterQuests(uid)
        for (q in starterQuests) {
            val docId = q.id.ifBlank { UUID.randomUUID().toString() }
            questRef.document(docId).set(
                q.copy(id = docId, userId = uid).toMap() + mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    private fun getStarterTerms(uid: String): List<DictionaryTerm> = listOf(
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "cmd",
            command = "hostname",
            description = "Find your computer name",
            category = "Networking",
            difficulty = "Beginner",
            isMastered = true
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "cmd",
            command = "ipconfig /all",
            description = "Find your IP, Gateway, DNS, DHCP.",
            category = "Networking",
            difficulty = "Beginner",
            isMastered = true
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "cmd",
            command = "Get-NetAdapter",
            description = "List network adapters and status in PowerShell.",
            category = "Networking",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "cmd",
            command = "systeminfo",
            description = "Detailed OS and hardware configuration summary.",
            category = "Networking",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "cmd",
            command = "getmac /v",
            description = "Find your MAC Address.",
            category = "Networking",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "cmd",
            command = "Get-NetIPAddress",
            description = "List IP Addresses and Interfaces.",
            category = "Networking",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "termux",
            command = "getprop ro.product.model",
            description = "Show Android device hardware model property.",
            category = "Linux",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "termux",
            command = "getprop net.hostname",
            description = "Show Android device network hostname.",
            category = "Linux",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "cmd",
            command = "Get-DnsClientServerAddress",
            description = "Find configured DNS server addresses.",
            category = "Networking",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "termux",
            command = "getprop | grep -i dns",
            description = "Show DNS configuration properties in Linux shell.",
            category = "Linux",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "termux",
            command = "ip neigh show",
            description = "Show discovered neighbor devices and ARP table entries.",
            category = "Linux",
            difficulty = "Beginner",
            isMastered = false
        ),
        DictionaryTerm(
            id = UUID.randomUUID().toString(),
            userId = uid,
            term = "BGP Multi-Exit Discriminator (MED)",
            command = "neighbor 192.168.1.1 route-map SET-MED out",
            description = "Non-transitive attribute suggesting exit preference to neighboring AS.",
            category = "Networking",
            difficulty = "Advanced",
            isMastered = false,
            isAiGenerated = true,
            aiExplanation = "Unlocked at Level 3: In BGP path selection, lower MED values are preferred over higher ones. Passed between ASs but not propagated further."
        )
    )

    private fun getStarterQuests(uid: String): List<Quest> = listOf(
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "CompTIA A+ Hardware & Network Fundamentals",
            description = "Identify motherboard components, RAID types, TCP vs UDP ports, and 802.11 standards.",
            questType = "MAIN",
            category = "A_PLUS",
            xpReward = 150L,
            status = "ACTIVE"
        ),
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Cisco CCNA Subnetting & VLAN Trunking",
            description = "Calculate VLSM /28 to /26 subnets, configure IEEE 802.1Q trunking on switches, and verify with show vlan brief.",
            questType = "MAIN",
            category = "CCNA",
            xpReward = 200L,
            status = "ACTIVE"
        ),
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Python for Network Automation",
            description = "Write a Python script using Netmiko or Paramiko to pull running-config from 5 Cisco devices simultaneously.",
            questType = "MAIN",
            category = "PYTHON",
            xpReward = 180L,
            status = "ACTIVE"
        ),
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Ethical Hacking & Penetration Testing",
            description = "Perform reconnaissance using Nmap, scan for SMB vulnerabilities, and document remediation in your journal.",
            questType = "MAIN",
            category = "SECURITY_PLUS",
            xpReward = 220L,
            status = "ACTIVE"
        ),
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Daily Challenge: Subnet Mask Quick Drill",
            description = "Calculate the network ID and broadcast address for 192.168.10.85/26.",
            questType = "DAILY",
            category = "NETWORKING",
            xpReward = 50L,
            status = "ACTIVE"
        ),
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Daily Challenge: Log 1 Verified Lab",
            description = "Document a troubleshooting lab or device setup in the IT Lab Journal with verified working solution.",
            questType = "DAILY",
            category = "LINUX",
            xpReward = 60L,
            status = "ACTIVE"
        ),
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Side Quest: Wireshark 3-Way Handshake Capture",
            description = "Requires Outside Learning: Open Wireshark on your PC, filter by 'tcp.flags.syn == 1', initiate an HTTPS connection, and capture SYN, SYN-ACK, ACK.",
            questType = "SIDE",
            category = "NETWORKING",
            xpReward = 120L,
            status = "ACTIVE",
            outsideLearningRequirement = "Capture raw packet trace on your home Wi-Fi and verify sequence/acknowledgment numbers."
        ),
        Quest(
            id = UUID.randomUUID().toString(),
            userId = uid,
            title = "Side Quest: Local SSH Keypair Bastion Setup",
            description = "Requires Outside Learning: Generate an ed25519 key-pair, copy public key to remote host, and disable password authentication in sshd_config.",
            questType = "SIDE",
            category = "LINUX",
            xpReward = 140L,
            status = "ACTIVE",
            outsideLearningRequirement = "Configure key-based authentication on a Linux VM or Raspberry Pi and test passwordless login."
        )
    )
}

sealed class PinVerificationResult {
    object Success : PinVerificationResult()
    data class Failed(val attemptsRemaining: Int) : PinVerificationResult()
    data class LockedOut(val secondsRemaining: Int) : PinVerificationResult()
}
