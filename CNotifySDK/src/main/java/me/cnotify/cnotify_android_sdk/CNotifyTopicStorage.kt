package me.cnotify.cnotify_android_sdk

import android.content.Context
import android.content.SharedPreferences

class CNotifyTopicStorage(context: Context) {
    private val topicsKey = "cnotify_subscribed_topics"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CNotifyPreferences", Context.MODE_PRIVATE)

    fun getSubscribedTopics(): List<String> {
        // Get topics from SharedPreferences
        return sharedPreferences.getStringSet(topicsKey, emptySet())?.toList() ?: emptyList()
    }

    fun persistSubscribedTopics(topics: List<String>) {
        // Persist topics
        // Use SharedPreferences to store the topics
        sharedPreferences.edit().apply {
            putStringSet(topicsKey, topics.toSet())
            apply()
        }
    }
}
