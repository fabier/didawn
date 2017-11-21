package didawn.json

import com.google.gson.annotations.SerializedName
import didawn.models.ArtistList
import didawn.models.Song

import static java.lang.String.format

public class Track {

    @SerializedName("SNG_ID")
    private String songID

    @SerializedName("MD5_ORIGIN")
    private String puid

    @SerializedName("MEDIA_VERSION")
    private int mediaVersion

    @SerializedName("SNG_TITLE")
    private String title

    @SerializedName("ALB_TITLE")
    private String albumTitle

    @SerializedName("ART_NAME")
    private String artistName

    @SerializedName("DURATION")
    private long duration

    @SerializedName("TRACK_NUMBER")
    private long trackNumber

    @SerializedName("DISK_NUMBER")
    private int diskNumber

    @SerializedName("ALB_PICTURE")
    private String albumPicture

    // SPECIFIC NON USER TRACKS
    @SerializedName("FILESIZE_MP3_320")
    private long fileSizeMp3_320

    // SPECIFIC NON USER TRACKS
    @SerializedName("FILESIZE_MP3_256")
    private long fileSizeMp3_256

    // SPECIFIC NON USER TRACKS
    @SerializedName("VERSION")
    private String version

    // SPECIFIC NON USER TRACKS
    @SerializedName("ARTISTS")
    private List<Artist> artists

    // SPECIFIC NON USER TRACKS
    @SerializedName("DIGITAL_RELEASE_DATE")
    private String year

    // SPECIFIC NON USER TRACKS
    @SerializedName("ISRC")
    private String isrc

    // SPECIFIC NON USER TRACKS
    @SerializedName("COMPOSER")
    private String composer

    // SPECIFIC NON USER TRACKS
    @SerializedName("BPM")
    private String bpm

    public String getSongID() {
        return songID
    }

    public void setSongID(String songID) {
        this.songID = songID
    }

    public int getMediaVersion() {
        return mediaVersion
    }

    public void setMediaVersion(int mediaVersion) {
        this.mediaVersion = mediaVersion
    }

    public String getPuid() {
        return puid
    }

    public void setPuid(String puid) {
        this.puid = puid
    }

    public String getTitle() {
        return title
    }

    public void setTitle(String title) {
        this.title = title
    }

    public String getAlbumTitle() {
        return albumTitle
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle
    }

    public String getArtistName() {
        return artistName
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName
    }

    public long getDuration() {
        return duration
    }

    public void setDuration(long duration) {
        this.duration = duration
    }

    public long getTrackNumber() {
        return trackNumber
    }

    public void setTrackNumber(long trackNumber) {
        this.trackNumber = trackNumber
    }

    public int getDiskNumber() {
        return diskNumber
    }

    public void setDiskNumber(int diskNumber) {
        this.diskNumber = diskNumber
    }

    public String getAlbumPicture() {
        return albumPicture
    }

    public void setAlbumPicture(String albumPicture) {
        this.albumPicture = albumPicture
    }

    public long getFileSizeMp3_320() {
        return fileSizeMp3_320
    }

    public void setFileSizeMp3_320(long fileSizeMp3_320) {
        this.fileSizeMp3_320 = fileSizeMp3_320
    }

    public long getFileSizeMp3_256() {
        return fileSizeMp3_256
    }

    public void setFileSizeMp3_256(long fileSizeMp3_256) {
        this.fileSizeMp3_256 = fileSizeMp3_256
    }

    public String getVersion() {
        return version
    }

    public void setVersion(String version) {
        this.version = version
    }

    public List<Artist> getArtists() {
        return unmodifiableList(artists)
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists
    }

    public String getYear() {
        return year
    }

    public void setYear(String year) {
        this.year = year
    }

    public String getIsrc() {
        return isrc
    }

    public void setIsrc(String isrc) {
        this.isrc = isrc
    }

    public String getComposer() {
        return composer
    }

    public void setComposer(String composer) {
        this.composer = composer
    }

    public String getBpm() {
        return bpm
    }

    public void setBpm(String bpm) {
        this.bpm = bpm
    }

    private int getFormat() {
        if (isUserTrack()) {
            return 0
        } else {
            return fileSizeMp3_320 > 0L ? 3 : fileSizeMp3_256 > 0L ? 5 : 1
        }
    }

    private boolean isUserTrack() {
        return getSongID().startsWith("-")
    }

    private String getDownloadUrl() {
        if (isUserTrack()) {
            return getDownloadURL(puid, 0, songID.substring(1), mediaVersion)
        } else {
            return getDownloadURL(puid, getFormat(), songID, mediaVersion)
        }
    }


    public Song toSong() {
        Song song = new Song(songID, puid, title, getArtistList(), albumTitle, duration, trackNumber, year, mediaVersion, albumPicture)
        song.setDiskNumber(diskNumber)
        song.setAlbumArtist(this.artistName)
        song.setIsrc(this.isrc)
        song.setComposer(composer)
        song.setBpm(bpm)
        return song
    }

    public ArtistList getArtistList() {
        ArtistList artistList = new ArtistList()
        artistList.add(this.artistName)

        if (this.artists != null) {
            this.artists.each {
                def artist = it.getArtistName();
                if (artist.contains(" feat. ")) {
                    for (String tmpArtist : artist.split(" feat. ")) {
                        if (!artistList.contains(tmpArtist)) {
                            artistList.add(tmpArtist)
                        }
                    }
                } else if (!artistList.contains(artist)) {
                    artistList.add(artist)
                }
            }
        }

        return artistList
    }
}
