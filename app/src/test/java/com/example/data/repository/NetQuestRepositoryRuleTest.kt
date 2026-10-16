package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.DictionaryTerm
import com.example.data.model.JournalEntry
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class NetQuestRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun addDictionaryTerm_authenticatedOwner_createsDocument() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = NetQuestRepository(firestore)

        val termId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addDictionaryTerm(
                DictionaryTerm(
                    term = "netstat",
                    command = "netstat -tuln",
                    description = "Show listening ports and TCP sockets",
                    category = "Networking",
                    difficulty = "Intermediate"
                )
            )
        }

        assertTrue(termId.isNotBlank())

        val terms = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeDictionary().first { list -> list.any { it.id == termId } }
        }
        assertTrue(terms.any { it.command == "netstat -tuln" })
    }

    @Test
    fun addJournalEntry_authenticatedOwner_createsAndVerifies() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = NetQuestRepository(firestore)

        val entryId = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.addJournalEntry(
                JournalEntry(
                    title = "BGP Peer Configuration Lab",
                    notes = "Configured neighbor 10.0.0.2 remote-as 65001",
                    solution = "neighbor 10.0.0.2 remote-as 65001; neighbor 10.0.0.2 activate",
                    tags = listOf("bgp", "routing"),
                    category = "Networking",
                    isVerified = true
                )
            )
        }

        assertTrue(entryId.isNotBlank())

        val entries = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeJournal().first { list -> list.any { it.id == entryId } }
        }
        assertTrue(entries.any { it.title == "BGP Peer Configuration Lab" && it.isVerified })
    }

    @Test
    fun unauthenticatedAccess_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = NetQuestRepository(firestore)

        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                repository.addDictionaryTerm(
                    DictionaryTerm(term = "unauth", command = "whoami")
                )
            }
            fail("Expected exception for unauthenticated write")
        } catch (e: IllegalStateException) {
            // requireUserId() threw as expected
            assertTrue(e.message?.contains("User must be signed in") == true)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice_test@netquest.io"
        const val BOB_EMAIL = "bob_test@netquest.io"
        const val DEFAULT_TIMEOUT_MS = 6000L
        const val FLOW_TIMEOUT_MS = 6000L
    }
}
