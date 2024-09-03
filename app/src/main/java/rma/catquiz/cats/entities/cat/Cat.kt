package rma.catquiz.cats.entities.cat

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "cats")
@Serializable
data class Cat (
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val origin: String,
    val temperament: String,
    @SerialName("life_span")
    val life: String,
    @Embedded
    val weight: CatWeight,
    @Embedded
    val image: CatImage? = null,
) {

    fun doesMatchSearchQuery(query: String): Boolean {
        return name.contains(query, true)
    }

    fun averageWeight():Double {
        val lines = weight.metric.replace(" ", "").split("-")
        return lines[0].toDouble() / lines[1].toDouble()
    }

    fun averageLife(): Double {
        val lines = life.replace(" ", "").split("-")
        return lines[0].toDouble() / lines[1].toDouble()
    }
}

@Serializable
data class CatWeight (
    val metric: String
)

@Serializable
data class CatImage(
    val url: String
)