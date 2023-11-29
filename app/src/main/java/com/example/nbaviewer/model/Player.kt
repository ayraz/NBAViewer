package com.example.nbaviewer.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.lang.reflect.Type

@Serializable
class Player(
    val id: Long = 0,
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    val position: String = "",
    @SerialName("height_feet")
    val heightFeet: Long? = null,
    @SerialName("height_inches")
    val heightInches: Long? = null,
    @SerialName("weight_pounds")
    val weightPounds: Long? = null,
    val team: Team = Team(),
)

class TeamNameDeserializer : JsonDeserializer<String> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): String {
        return json?.asJsonObject?.get("team")?.asJsonObject?.get("name")?.asString ?: ""
    }

}
