package com.example.data.service

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private fun getApiKey(): String {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else ""
    }

    /**
     * Ask Gemini IT Architect with multi-turn chat, optional high thinking, optional search grounding, and optional image!
     */
    suspend fun chatWithArchitect(
        messages: List<Pair<String, String>>, // role ("user" / "model") to text
        userPrompt: String,
        imageBitmap: Bitmap? = null,
        enableHighThinking: Boolean = false,
        useSearchGrounding: Boolean = false,
        useLowLatency: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext "Gemini API key is not configured. Please set your GEMINI_API_KEY in AI Studio Secrets."
        }

        val modelName = when {
            useLowLatency -> "gemini-3.1-flash-lite-preview"
            enableHighThinking -> "gemini-4-argon"
            useSearchGrounding -> "gemini-3.8-flash"
            else -> "gemini-3.8-flash"
        }

        try {
            val rootJson = JSONObject()

            // System instruction
            val sysInstruction = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply {
                        put("text", "You are the NetQuest Senior Principal IT, Network, and Cloud Infrastructure Architect. You mentor the user in CompTIA A+, Cisco CCNA, CompTIA Security+, Python NetDevOps, and Linux administration. Provide concise, high-value, production-grade technical guidance, verify packet structures, command syntax, and troubleshooting steps. Encourage continuous learning and gamified mastery.")
                    })
                })
            }
            rootJson.put("systemInstruction", sysInstruction)

            // Contents array
            val contentsArray = JSONArray()
            for ((role, text) in messages) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (role == "user") "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", text) })
                    })
                })
            }

            // Current prompt parts
            val currentParts = JSONArray()
            currentParts.put(JSONObject().apply { put("text", userPrompt) })

            // Image attachment if available
            imageBitmap?.let { bmp ->
                val base64Str = bmpToBase64(bmp)
                val inlineData = JSONObject().apply {
                    put("mimeType", "image/jpeg")
                    put("data", base64Str)
                }
                currentParts.put(JSONObject().apply {
                    put("inlineData", inlineData)
                })
            }

            contentsArray.put(JSONObject().apply {
                put("role", "user")
                put("parts", currentParts)
            })
            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject()
            if (enableHighThinking) {
                genConfig.put("thinkingConfig", JSONObject().apply {
                    put("thinkingLevel", "HIGH")
                })
            }
            rootJson.put("generationConfig", genConfig)

            // Google Search Grounding if requested
            if (useSearchGrounding) {
                val toolsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("googleSearch", JSONObject())
                    })
                }
                rootJson.put("tools", toolsArray)
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"
            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder().url(url).post(requestBody).build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e("GeminiService", "API error: ${response.code} $respBody")
                return@withContext "AI Mentor is currently busy (${response.code}). Please check command manually or retry."
            }

            val respJson = JSONObject(respBody)
            val candidates = respJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            val sb = StringBuilder()
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    if (part.has("text")) {
                        sb.append(part.getString("text"))
                    }
                }
            }

            if (sb.isNotBlank()) sb.toString().trim() else "No response generated."
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception calling Gemini", e)
            "AI Assistant encountered an issue: ${e.message}"
        }
    }

    /**
     * AI Syntax Clarifier for the 95% user-entered dictionary terms:
     * Checks user commands for flags, typos, or security warnings without replacing user's personal thought.
     */
    suspend fun clarifyDictionaryCommand(term: String, command: String, userDescription: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) return@withContext ""

        val prompt = """
            Review this IT/Networking command entered by a student:
            Term/Utility: $term
            Command: $command
            User Note: $userDescription

            Task: Provide a very brief (1-2 sentence) technical clarification or syntax verification. 
            Confirm whether the syntax is valid, highlight any key flags (e.g. -v, /all, sudo, -sV), or mention common pitfalls. 
            Keep it strictly educational and concise.
        """.trimIndent()

        val rootJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite-preview:generateContent?key=$apiKey"
            val request = Request.Builder().url(url).post(rootJson.toString().toRequestBody(jsonMediaType)).build()
            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: return@withContext ""
            val respJson = JSONObject(respBody)
            val candidates = respJson.optJSONArray("candidates")
            val text = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
            text?.trim() ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Auto-synthesizes an employer-ready Portfolio Case Study from verified journal lab notes.
     */
    suspend fun synthesizeCaseStudyFromJournal(
        title: String,
        notes: String,
        solution: String,
        category: String
    ): JSONObject = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val defaultResult = JSONObject().apply {
            put("executiveSummary", "Implemented and verified $title in a production simulation environment, establishing stable network connectivity.")
            put("problemBusinessNeed", "Business required robust $category infrastructure with zero downtime and strict security segmentation.")
            put("toolsTechnologies", "Cisco Catalyst, Wireshark, Bash, Linux")
            put("architectureSteps", "1. Verified initial state.\n2. Configured device parameters:\n$solution\n3. Tested packet propagation.")
            put("resultsMetrics", "99.99% simulated uptime, zero packet loss, verified RFC compliance.")
            put("lessonsLearned", "Configuration verification and automated backup of startup-config are essential.")
        }
        if (apiKey.isBlank()) return@withContext defaultResult

        val prompt = """
            You are a Senior Technical Recruiter and Principal Infrastructure Architect.
            Transform these raw IT lab troubleshooting notes into a prestigious, employer-ready Case Study for a portfolio:
            Lab Title: $title
            Category: $category
            Lab Notes: $notes
            Working Solution / Commands: $solution

            Respond ONLY with a JSON object containing these exact string fields:
            {
              "executiveSummary": "Concise high-level summary demonstrating business impact and engineering competence",
              "problemBusinessNeed": "The underlying technical bottleneck or organizational business requirement",
              "toolsTechnologies": "Comma-separated list of tools, protocols, and hardware/software",
              "architectureSteps": "Numbered step-by-step technical implementation methodology",
              "resultsMetrics": "Measurable outcomes, uptime, latency reduction, or security benchmarks",
              "lessonsLearned": "Key engineering takeaways and architectural improvements"
            }
        """.trimIndent()

        val rootJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent?key=$apiKey"
            val request = Request.Builder().url(url).post(rootJson.toString().toRequestBody(jsonMediaType)).build()
            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: return@withContext defaultResult
            val respJson = JSONObject(respBody)
            val candidates = respJson.optJSONArray("candidates")
            val jsonText = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
            if (jsonText != null) {
                JSONObject(jsonText)
            } else {
                defaultResult
            }
        } catch (e: Exception) {
            defaultResult
        }
    }

    /**
     * Generate an adaptive 3-question quiz for daily challenges to test retention.
     */
    suspend fun generateDailyQuiz(topic: String): List<QuizQuestion> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val defaultList = listOf(
            QuizQuestion(
                question = "In a /26 subnet, what is the total number of usable host IP addresses?",
                options = listOf("62 hosts", "64 hosts", "30 hosts", "126 hosts"),
                correctIndex = 0,
                explanation = "A /26 mask has 6 host bits (32 - 26 = 6). 2^6 - 2 = 62 usable host addresses."
            ),
            QuizQuestion(
                question = "Which default port does Secure Shell (SSH) utilize for encrypted remote administration?",
                options = listOf("Port 21", "Port 22", "Port 23", "Port 443"),
                correctIndex = 1,
                explanation = "Port 22 is assigned by IANA for standard SSH connections."
            ),
            QuizQuestion(
                question = "Which command in Linux displays kernel IP routing tables?",
                options = listOf("ip route show", "route -k", "netstat -kernel", "show ip route"),
                correctIndex = 0,
                explanation = "The modern iproute2 suite uses 'ip route' or 'ip route show'."
            )
        )
        if (apiKey.isBlank()) return@withContext defaultList

        val prompt = """
            Create an adaptive 3-question multiple choice quiz on the topic: $topic.
            Respond ONLY with a JSON array where each object has:
            {
              "question": "string question",
              "options": ["option 1", "option 2", "option 3", "option 4"],
              "correctIndex": integer (0 to 3),
              "explanation": "clear 1-sentence technical explanation"
            }
        """.trimIndent()

        val rootJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseMimeType", "application/json")
            })
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-lite-preview:generateContent?key=$apiKey"
            val request = Request.Builder().url(url).post(rootJson.toString().toRequestBody(jsonMediaType)).build()
            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: return@withContext defaultList
            val respJson = JSONObject(respBody)
            val candidates = respJson.optJSONArray("candidates")
            val jsonText = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")
            if (jsonText != null) {
                val array = JSONArray(jsonText)
                val result = mutableListOf<QuizQuestion>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val opts = mutableListOf<String>()
                    val optsArray = obj.getJSONArray("options")
                    for (j in 0 until optsArray.length()) {
                        opts.add(optsArray.getString(j))
                    }
                    result.add(
                        QuizQuestion(
                            question = obj.getString("question"),
                            options = opts,
                            correctIndex = obj.getInt("correctIndex"),
                            explanation = obj.getString("explanation")
                        )
                    )
                }
                if (result.isNotEmpty()) result else defaultList
            } else {
                defaultList
            }
        } catch (e: Exception) {
            defaultList
        }
    }

    private fun bmpToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
