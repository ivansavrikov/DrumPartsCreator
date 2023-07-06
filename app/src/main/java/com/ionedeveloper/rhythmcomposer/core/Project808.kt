package com.ionedeveloper.rhythmcomposer.core
import kotlinx.serialization.Serializable

@Serializable
data class Project808(val title: String, val bpm: Int, var bars: Int, var stepsInBeat: Int ,var patterns: Array<MutableList<Boolean>>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Project808

        if (bpm != other.bpm) return false
        if (!patterns.contentEquals(other.patterns)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bpm
        result = 31 * result + patterns.contentHashCode()
        return result
    }
}