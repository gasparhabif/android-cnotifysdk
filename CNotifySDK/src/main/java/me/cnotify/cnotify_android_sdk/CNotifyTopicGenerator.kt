package me.cnotify.cnotify_android_sdk

class CNotifyTopicGenerator {
    private val baseTopic = "cnotify_"
    private val allUsersTopic = "-all_users"
    private val audienceSeparator = "_aud"

    fun getTopics(language: String, country: String, appVersion: String): List<String> {
        val topics = mutableListOf<String>()
        topics.add(buildTopic(language, soTopic()))
        topics.add(buildTopic(language, allUsersTopic))
        topics.add(buildTopic(language, countryTopic(country)))
        topics.add(buildTopic(language, versionTopic(appVersion)))
        return topics
    }

    private fun soTopic(): String {
        return "-os-android"
    }

    private fun countryTopic(country: String): String {
        return "-country-$country"
    }

    private fun versionTopic(version: String): String {
        return "-version-$version"
    }

    private fun langTopic(lang: String): String {
        return "lang-$lang"
    }

    private fun buildTopic(language: String, audience: String): String {
        val aud = "$audienceSeparator$audience"
        return "$baseTopic${langTopic(language)}$aud"
    }
}