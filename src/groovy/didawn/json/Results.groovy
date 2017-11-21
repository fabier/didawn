package didawn.json

import com.google.gson.annotations.SerializedName

import static java.util.Collections.unmodifiableList

public class Results {

    @SerializedName("tracks")
    private List<Track> tracks

    @SerializedName("data")
    private List<Data> data

    @SerializedName("count")
    private int count


    public List<Track> getTracks() {
        return unmodifiableList(tracks)
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks
    }

    public List<Data> getData() {
        return unmodifiableList(data)
    }

    public void setData(List<Data> data) {
        this.data = data
    }

    public int getCount() {
        return count
    }

    public void setCount(int count) {
        this.count = count
    }
}
