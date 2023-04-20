package ie.setu.fooddiary.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class ExperienceModel(
    var uid: String,            // unique identifier for the experience
    val userId: String,         // identifier for the user who created the experience
    val restaurantName: String, // name of the restaurant
    val cuisine: String,        // type of cuisine
    val dishName: String,       // name of the dish
    val rating: Int,            // rating given by the user (out of 5)
    val comment: String?,       // optional comment provided by the user
    val imageUrl: String?,      // optional URL for an image of the dish or restaurant
    val latitude: Double,       // latitude of the restaurant's location
    val longitude: Double,      // longitude of the restaurant's location
    val creationTime: Long,     // timestamp for when the experience was created
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
            "rating" to rating,
            "comment" to comment,
            "imageUrl" to imageUrl,
            "latitude" to latitude,
            "longitude" to longitude,
            "creationTime" to creationTime
        )
    }
}
