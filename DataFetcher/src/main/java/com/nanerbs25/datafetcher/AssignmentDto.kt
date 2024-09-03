package com.nanerbs25.datafetcher

import com.google.gson.annotations.SerializedName

data class AssignmentDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl1") val imageUrl1: String?,
    @SerializedName("imageUrl2") val imageUrl2: String?,
    @SerializedName("imageUrl3") val imageUrl3: String?,
    @SerializedName("imageUrl4") val imageUrl4: String?,
    @SerializedName("imageUrl5") val imageUrl5: String?,
    @SerializedName("imageUrl6") val imageUrl6: String?,
    @SerializedName("imageUrl7") val imageUrl7: String?,
    @SerializedName("imageUrl8") val imageUrl8: String?,
    @SerializedName("imageUrl9") val imageUrl9: String?,
    @SerializedName("imageUrl10") val imageUrl10: String?,
    @SerializedName("videoUrl") val videoUrl: String?,
    @SerializedName("fileUrl") val fileUrl: String?
) {
    fun toAssignment(): Assignment {
        return Assignment(
            id = this.id,
            title = this.title,
            description = this.description,
            imageUrl1 = this.imageUrl1,
            imageUrl2 = this.imageUrl2,
            imageUrl3 = this.imageUrl3,
            imageUrl4 = this.imageUrl4,
            imageUrl5 = this.imageUrl5,
            imageUrl6 = this.imageUrl6,
            imageUrl7 = this.imageUrl7,
            imageUrl8 = this.imageUrl8,
            imageUrl9 = this.imageUrl9,
            imageUrl10 = this.imageUrl10,
            videoUrl = this.videoUrl,
            fileUrl = this.fileUrl
        )
    }
}
