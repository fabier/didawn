package didawn.json

import com.google.gson.annotations.SerializedName
import didawn.models.ArtistList
import didawn.models.Song

public class Data {

    @SerializedName("link")
    private String link

    @SerializedName("SNG_ID")
    private String songID

    @SerializedName("id")
    private String id

    @SerializedName("title")
    private String title

    @SerializedName("album")
    private Album album

    @SerializedName("artist")
    private Artist artist

    @SerializedName("duration")
    private long duration

    @SerializedName("alternative")
    private Alternative alternative

    @SerializedName("name")
    private String name

    @SerializedName("SNG_TITLE")
    private String songTitle

    @SerializedName("ALB_TITLE")
    private String albumTitle

    @SerializedName("MEDIA_VERSION")
    private int mediaVersion

    @SerializedName("ART_NAME")
    private String artistName

    @SerializedName("ARTISTS")
    private List<Artist> artists

    @SerializedName("MD5_ORIGIN")
    private String md5

    @SerializedName("FILESIZE_MP3_320")
    private long fileSize320kMp3

    @SerializedName("FILESIZE_MP3_256")
    private long fileSize256kMp3

    @SerializedName("DISK_NUMBER")
    private int diskNumber

    @SerializedName("ISRC")
    private String isrc

    @SerializedName("COMPOSER")
    private String composer

    @SerializedName("BPM")
    private String bpm

    @SerializedName("ALB_PICTURE")
    private String albumPicture

    @SerializedName("TRACK_NUMBER")
    private long trackNumber

    @SerializedName("DIGITAL_RELEASE_DATE")
    private String year

    String getMd5() {
        return md5
    }

    void setMd5(String md5) {
        this.md5 = md5
    }

    public String getLink() {
        return link
    }

    public void setLink(String link) {
        this.link = link
    }

    int getMediaVersion() {
        return mediaVersion
    }

    void setMediaVersion(int mediaVersion) {
        this.mediaVersion = mediaVersion
    }

    String getAlbumPicture() {
        return albumPicture
    }

    void setAlbumPicture(String albumPicture) {
        this.albumPicture = albumPicture
    }

    public String getSongID() {
        return songID
    }

    public void setSongID(String songID) {
        this.songID = songID
    }


    public String getId() {
        return id
    }

    public void setId(String id) {
        this.id = id
    }


    public String getTitle() {
        return title
    }

    public void setTitle(String title) {
        this.title = title
    }


    public Album getAlbum() {
        return album
    }

    public void setAlbum(Album album) {
        this.album = album
    }


    public Artist getArtist() {
        return artist
    }

    public void setArtist(Artist artist) {
        this.artist = artist
    }


    public long getDuration() {
        return duration
    }

    public void setDuration(long duration) {
        this.duration = duration
    }

    public Alternative getAlternative() {
        return alternative
    }

    public void setAlternative(Alternative alternative) {
        this.alternative = alternative
    }

    String getSongTitle() {
        return songTitle
    }

    void setSongTitle(String songTitle) {
        this.songTitle = songTitle
    }

    String getAlbumTitle() {
        return albumTitle
    }

    void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle
    }

    String getArtistName() {
        return artistName
    }

    void setArtistName(String artistName) {
        this.artistName = artistName
    }

    List<Artist> getArtists() {
        return artists
    }

    void setArtists(List<Artist> artists) {
        this.artists = artists
    }

    long getFileSize320kMp3() {
        return fileSize320kMp3
    }

    void setFileSize320kMp3(long fileSize320kMp3) {
        this.fileSize320kMp3 = fileSize320kMp3
    }

    long getFileSize256kMp3() {
        return fileSize256kMp3
    }

    void setFileSize256kMp3(long fileSize256kMp3) {
        this.fileSize256kMp3 = fileSize256kMp3
    }

    int getDiskNumber() {
        return diskNumber
    }

    void setDiskNumber(int diskNumber) {
        this.diskNumber = diskNumber
    }

    String getIsrc() {
        return isrc
    }

    void setIsrc(String isrc) {
        this.isrc = isrc
    }

    String getComposer() {
        return composer
    }

    void setComposer(String composer) {
        this.composer = composer
    }

    String getBpm() {
        return bpm
    }

    void setBpm(String bpm) {
        this.bpm = bpm
    }

    long getTrackNumber() {
        return trackNumber
    }

    void setTrackNumber(long trackNumber) {
        this.trackNumber = trackNumber
    }

    String getYear() {
        return year
    }

    void setYear(String year) {
        this.year = year
    }

    public String getName() {
        return name
    }

    public void setName(String name) {
        this.name = name
    }

    private int getFormat() {
        if (isUserTrack()) {
            return 0
        } else {
            int formatIf256k = fileSize256kMp3 > 0L ? 5 : 1
            return fileSize320kMp3 > 0L ? 3 : formatIf256k
        }
    }

    private boolean isUserTrack() {
        return getSongID().startsWith("-")
    }

    private String getDownloadUrl() {
        if (isUserTrack()) {
            return getDownloadURL(md5, 0, songID.substring(1), mediaVersion)
        } else {
            return getDownloadURL(md5, getFormat(), songID, mediaVersion)
        }
    }


    public Song toSong() {
        Song song = new Song(songID, md5, songTitle, getArtistList(),
                albumTitle, duration, trackNumber, year, mediaVersion, albumPicture)
        song.setDiskNumber(diskNumber)
        song.setAlbumArtist(this.artistName)
        song.setIsrc(this.isrc)
        song.setComposer(composer)
        song.setBpm(bpm)
        return song
    }

    public didawn.models.Album toAlbum() {
        didawn.models.Album album = new didawn.models.Album(id, md5, songTitle, getArtistList(),
                albumTitle, duration, trackNumber, year, mediaVersion, albumPicture)
        album.setDiskNumber(diskNumber)
        album.setAlbumArtist(this.artistName)
        album.setIsrc(this.isrc)
        album.setComposer(composer)
        album.setBpm(bpm)
        return album
    }

    public ArtistList getArtistList() {
        ArtistList artistList = new ArtistList()
        artistList.add(this.artistName)

        if (this.artists != null) {
            this.artists.each {
                def artist = it.getArtistName()
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
