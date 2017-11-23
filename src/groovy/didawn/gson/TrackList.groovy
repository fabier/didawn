package didawn.gson

import com.google.gson.annotations.SerializedName

class TrackList extends BaseJSON {

    @SerializedName("data")
    List<Track> tracks

    @SerializedName("checksum")
    String checksum
}
