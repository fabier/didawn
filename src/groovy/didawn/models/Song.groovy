package didawn.models

import static java.util.concurrent.TimeUnit.MILLISECONDS
import static java.util.concurrent.TimeUnit.MINUTES

public class Song {

    private String id
    private String md5
    private int mediaVersion
    private String albumPicture
    private String name
    private ArtistList artists
    private String album
    private long duration
    private String year
    private long trackNum
    private long fileSizeMp3_320
    private long fileSizeMp3_256
    private String coverURL
    private String alternativeID
    private int diskNumber
    private String genre
    private int genreID
    private String albumArtist
    private String isrc
    private String composer
    private String bpm
    private String albumTrackCount
    private String label
    private String data

    public Song(String id, String md5, String name, ArtistList artists, int mediaVersion, String albumPicture) {
        this(id, md5, name, artists, "", 0L, 0L, "", mediaVersion, albumPicture)
    }

    public Song(String id, String md5, int mediaVersion, String albumPicture) {
        this(id, md5, "", new ArtistList(), "", 0L, 0L, "", mediaVersion, albumPicture)
    }

    public Song(String id, String md5, String name, ArtistList artists, String album, long duration, long trackNum, String year, int mediaVersion, String albumPicture) {
        this.id = id
        this.md5 = md5
        this.name = name.trim()
        this.artists = artists
        this.album = album.trim()
        this.duration = duration
        this.year = year
        this.mediaVersion = mediaVersion
        this.albumPicture = albumPicture
        this.trackNum = trackNum
        this.genre = ""
        if (this.artists.isEmpty()) {
            artists.add("")
        }

        this.albumArtist = this.artists.get(0)
        this.diskNumber = 1
        this.isrc = ""
        this.composer = ""
        this.bpm = ""
        this.alternativeID = ""
        this.albumTrackCount = ""
        this.label = ""
    }

    String getMd5() {
        return md5
    }

    void setMd5(String md5) {
        this.md5 = md5
    }

    String getAlbumPicture() {
        return albumPicture
    }

    void setAlbumPicture(String albumPicture) {
        this.albumPicture = albumPicture
    }

    int getMediaVersion() {
        return mediaVersion
    }

    void setMediaVersion(int mediaVersion) {
        this.mediaVersion = mediaVersion
    }

    public String getGenre() {
        return this.genre
    }

    public void setGenre(String genre) {
        this.genre = genre
    }

    public String getId() {
        return this.id
    }

    public void setId(String id) {
        this.id = id
    }

    public String getTitle() {
        return this.name
    }

    public void setTitle(String name) {
        this.name = name
    }

    public ArtistList getArtists() {
        return this.artists
    }

    public void setArtists(ArtistList artist) {
        this.artists = artist
    }

    public String getAlbum() {
        return this.album
    }

    public void setAlbum(String album) {
        this.album = album
    }

    public String getReadableDuration() {
        return this.duration > 0L ? String.format("%d:%02d", MILLISECONDS.toMinutes(this.duration), MILLISECONDS.toSeconds(this.duration) - MINUTES.toSeconds(MILLISECONDS.toMinutes(this.duration))) : "0:00"
    }

    public long getDuration() {
        return this.duration
    }

    public void setDuration(long duration) {
        this.duration = duration
    }

    public String getYear() {
        return !this.year.isEmpty() ? this.year : ""
    }

    public void setYear(String year) {
        this.year = year
    }

    public Long getTrackNumber() {
        return this.trackNum
    }

    public void setTrackNum(long trackNum) {
        this.trackNum = trackNum
    }

    @Override
    public int hashCode() {
        return super.hashCode()
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false
        } else if (this.getClass() != obj.getClass()) {
            return false
        } else {
            Song other = (Song) obj
            return Objects.equals(this.id, other.id) &&
                    Objects.equals(this.name, other.name) &&
                    Objects.deepEquals(this.albumArtist, other.albumArtist) &&
                    Objects.equals(this.album, other.album) &&
                    Objects.equals(this.year, other.year) &&
                    Objects.equals(this.trackNum, other.trackNum)
        }
    }

    public String getAlternativeID() {
        return this.alternativeID
    }

    public void setAlternativeID(String alternativeID) {
        this.alternativeID = alternativeID
    }

    public String getCoverURL() {
        return this.coverURL
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL
    }

    public int getDiskNumber() {
        return this.diskNumber
    }

    public void setDiskNumber(int diskNumber) {
        this.diskNumber = diskNumber
    }

    public int getGenreID() {
        return this.genreID
    }

    public void setGenreID(int genreID) {
        this.genreID = genreID
    }

    @Override
    public String toString() {
        return "Song{id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", artists='" + this.artists +
                '\'' + ", album='" + this.album + '\'' + ", duration=" + this.duration + ", year='" + this.year +
                '\'' + ", trackNum=" + this.trackNum + ", coverURL='" + this.coverURL +
                '\'' + ", alternativeID='" + this.alternativeID + '\'' + ", diskNumber=" + this.diskNumber +
                ", genre='" + this.genre + '\'' + ", genreID=" + this.genreID + '}'
    }

    public String getAlbumArtist() {
        return this.albumArtist
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist
    }

    public String getIsrc() {
        return this.isrc
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc
    }

    public String getComposer() {
        return this.composer
    }

    public void setComposer(String composer) {
        this.composer = composer
    }

    public String getBpm() {
        return this.bpm
    }

    public void setBpm(String bpm) {
        this.bpm = bpm
    }

    public String getAlbumTrackCount() {
        return this.albumTrackCount
    }

    public void setAlbumTrackCount(String albumTrackCount) {
        this.albumTrackCount = albumTrackCount
    }

    public String getLabel() {
        return this.label
    }

    public void setLabel(String label) {
        this.label = label
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    long getTrackNum() {
        return trackNum
    }

    long getFileSizeMp3_320() {
        return fileSizeMp3_320
    }

    void setFileSizeMp3_320(long fileSizeMp3_320) {
        this.fileSizeMp3_320 = fileSizeMp3_320
    }

    long getFileSizeMp3_256() {
        return fileSizeMp3_256
    }

    void setFileSizeMp3_256(long fileSizeMp3_256) {
        this.fileSizeMp3_256 = fileSizeMp3_256
    }

    int getFormat() {
        if (isUserTrack()) {
            return 0;
        } else {
            return getFileSizeMp3_320() > 0L ? 3 : getFileSizeMp3_256() > 0L ? 5 : 1;
        }
    }

    boolean isUserTrack() {
        return id.startsWith("-")
    }

    void setData(String data) {
        this.data = data
    }

    String getData() {
        return data
    }
}
