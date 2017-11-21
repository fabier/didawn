package didawn.json

import com.google.gson.annotations.SerializedName

import static java.util.Collections.unmodifiableList

public class SearchResponse {

    @SerializedName("tracks")
    private Datas tracks

    @SerializedName("genres")
    private Datas genres

    @SerializedName("artist")
    private Artist artist

    @SerializedName("data")
    private List<Data> data

    @SerializedName("title")
    private String title

    @SerializedName("label")
    private String label

    @SerializedName("nb_tracks")
    private String nbTracks

    @SerializedName("next")
    private String next

    @SerializedName("cover_xl")
    private String coverXL

    @SerializedName("cover_big")
    private String coverBig

    @SerializedName("error")
    private Error error


    public Datas getTracks() {
        return tracks
    }

    public void setTracks(Datas tracks) {
        this.tracks = tracks
    }

    public Datas getGenres() {
        return genres
    }

    public void setGenres(Datas genres) {
        this.genres = genres
    }

    public Artist getArtist() {
        return artist
    }

    public void setArtist(Artist artist) {
        this.artist = artist
    }


    public List<Data> getData() {
        return unmodifiableList(data)
    }

    public void setData(List<Data> data) {
        this.data = data
    }

    public String getTitle() {
        return title
    }

    public void setTitle(String title) {
        this.title = title
    }

    public String getLabel() {
        return label
    }

    public void setLabel(String label) {
        this.label = label
    }

    public String getNbTracks() {
        return nbTracks
    }

    public void setNbTracks(String nbTracks) {
        this.nbTracks = nbTracks
    }

    public String getCoverBig() {
        return coverBig
    }

    public void setCoverBig(String coverBig) {
        this.coverBig = coverBig
    }

    public String getCoverXL() {
        return coverXL
    }

    public void setCoverXL(String coverXL) {
        this.coverXL = coverXL
    }

    public String getNext() {
        return next
    }

    public void setNext(String next) {
        this.next = next
    }

    public Error getError() {
        return error
    }

    public void setError(Error error) {
        this.error = error
    }
}
