package org.example.app.data

/**
 * Simple parser for radar points payload.
 * Accepts either:
 * - CSV lines: "x,y,z,intensity" per line
 * - JSON array of objects: [{"x":..,"y":..,"z":..,"i":..}, ...]
 */
// PUBLIC_INTERFACE
object RadarPointParser {
    fun parse(payload: String): List<RadarPoint> {
        val trimmed = payload.trim()
        if (trimmed.isEmpty()) return emptyList()
        return try {
            if (trimmed.startsWith("[")) parseJsonArray(trimmed)
            else parseCsv(trimmed)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun parseCsv(text: String): List<RadarPoint> {
        val result = ArrayList<RadarPoint>()
        text.lines().forEach { line ->
            val parts = line.split(",")
            if (parts.size >= 3) {
                val x = parts[0].toFloatOrNull() ?: return@forEach
                val y = parts[1].toFloatOrNull() ?: return@forEach
                val z = parts[2].toFloatOrNull() ?: 0f
                val i = if (parts.size >= 4) parts[3].toFloatOrNull() ?: 1f else 1f
                result.add(RadarPoint(x, y, z, i))
            }
        }
        return result
    }

    private fun parseJsonArray(text: String): List<RadarPoint> {
        // Minimal JSON parse without adding heavy dependencies
        // Expect objects with fields x,y,z,i (intensity) - naive parsing
        val result = ArrayList<RadarPoint>()
        val items = text.removePrefix("[").removeSuffix("]").split("},")
        for (raw in items) {
            val obj = raw.trim().trimStart('{').trimEnd('}', ']')
            val pairs = obj.split(",")
            var x = 0f; var y = 0f; var z = 0f; var i = 1f
            for (p in pairs) {
                val kv = p.split(":").map { it.trim().trim('"') }
                if (kv.size == 2) {
                    when (kv[0]) {
                        "x" -> x = kv[1].toFloatOrNull() ?: x
                        "y" -> y = kv[1].toFloatOrNull() ?: y
                        "z" -> z = kv[1].toFloatOrNull() ?: z
                        "i", "intensity" -> i = kv[1].toFloatOrNull() ?: i
                    }
                }
            }
            result.add(RadarPoint(x, y, z, i))
        }
        return result
    }
}
