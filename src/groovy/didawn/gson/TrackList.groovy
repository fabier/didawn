package didawn.gson

import com.google.gson.annotations.SerializedName

class TrackList {

    @SerializedName("data")
    List<Track> tracks

    @SerializedName("checksum")
    String checksum
}
