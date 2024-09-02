package rma.catquiz.user

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import rma.catquiz.cats.api.serialization.JsonAndClass
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets

class UserDataSerializer() : Serializer<UserData> {

    private val json: Json = JsonAndClass

    override val defaultValue: UserData = UserData.EMPTY

    override suspend fun readFrom(input: InputStream): UserData {
        val text = String(input.readBytes(), charset = StandardCharsets.UTF_8)
        return try {
            json.decodeFromString<UserData>(text)
        } catch (error: SerializationException) {
            throw CorruptionException(message = "Unable to deserialize file.", cause = error)
        } catch (error: IllegalArgumentException) {
            throw CorruptionException(message = "Unable to deserialize file.", cause = error)
        }
    }

    override suspend fun writeTo(t: UserData, output: OutputStream) {
        val text = json.encodeToString(t)
        output.write(text.toByteArray())
    }
}