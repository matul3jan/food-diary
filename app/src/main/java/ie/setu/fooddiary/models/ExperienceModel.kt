package ie.setu.fooddiary.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class ExperienceModel(
    var uid: String? = "",
    var userId: String? = "",
    var restaurantName: String = "",
    var cuisine: String = "",
    var dishName: String = "",
    var rating: Float = 0F,
    var comment: String = "",
    var imageUrl: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var creationTime: String = "",
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "userId" to userId,
            "restaurantName" to restaurantName,
            "cuisine" to cuisine,
            "dishName" to dishName,
            "rating" to rating,
            "comment" to comment,
            "imageUrl" to imageUrl,
            "latitude" to latitude,
            "longitude" to longitude,
            "creationTime" to creationTime,
        )
    }
}
