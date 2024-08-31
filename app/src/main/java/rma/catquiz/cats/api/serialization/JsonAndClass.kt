package rma.catquiz.cats.api.serialization

import kotlinx.serialization.json.Json

val JsonAndClass = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    encodeDefaults = true
}

