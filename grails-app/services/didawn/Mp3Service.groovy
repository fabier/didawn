package didawn

import didawn.gson.Track
import grails.transaction.Transactional
import org.apache.commons.io.FileUtils
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.id3.ID3v24Tag
import org.jaudiotagger.tag.images.Artwork
import org.jaudiotagger.tag.images.StandardArtwork

@Transactional
class Mp3Service {

    def addTags(File file, Track track) {
        MP3File f = (MP3File) AudioFileIO.read(file)
        ID3v24Tag tag = new ID3v24Tag()

        track.artists?.each {
            tag.addField(FieldKey.ARTIST, it.getART_NAME())
        }

        if (track.getART_NAME()) {
            tag.addField(FieldKey.ALBUM_ARTIST, track.getART_NAME())
        }
        if (track.getTitle()) {
            tag.addField(FieldKey.TITLE, track.getTitle())
        }
        if (track.getTRACK_NUMBER() > 0) {
            tag.addField(FieldKey.TRACK, Integer.toString(track.getTRACK_NUMBER()))
        }
        if (track.getDISK_NUMBER() > 0) {
            tag.addField(FieldKey.DISC_NO, Integer.toString(track.getDISK_NUMBER()))
        }
        if (track.getALB_TITLE()) {
            tag.addField(FieldKey.ALBUM, track.getALB_TITLE())
        }
        if (track.getPHYSICAL_RELEASE_DATE()) {
            tag.addField(FieldKey.YEAR, track.getPHYSICAL_RELEASE_DATE())
        }
        // tag.addField(FieldKey.GENRE, track.getAlbum().getGenres().getGenres().first().name)
        if (track.getISRC()) {
            tag.addField(FieldKey.ISRC, track.getISRC())
        }
        if (track.getCOMPOSER()) {
            tag.addField(FieldKey.COMPOSER, track.getCOMPOSER())
        }
        if (track.getBPM() > 0) {
            tag.addField(FieldKey.BPM, Float.toString(track.getBPM()))
        }
        if (track.getAlbum().getNbTracks() > 0) {
            tag.addField(FieldKey.TRACK_TOTAL, Integer.toString(track.getAlbum().getNbTracks()))
        }
        if (track.getAlbum().getLabel()) {
            tag.addField(FieldKey.RECORD_LABEL, track.getAlbum().getLabel())
        }
        if (track.getAlbum().getCoverXl()) {
            tag.addField(getAlbumArtwork(track.getAlbum().getCoverXl()))
        }

        f.setTag(tag)
        f.commit()
    }

    Artwork getAlbumArtwork(String coverURL) throws IOException {
        StandardArtwork a = new StandardArtwork()
        if (coverURL == null) {
            return a
        } else {
            File f = File.createTempFile("tmp", ".jpg")
            FileUtils.copyURLToFile(new URL(coverURL), f)
            a.setFromFile(f)
            if (!f.delete()) {
                log.info "Cannot delete file: ${f.getAbsolutePath()}"
            }
            a.setImageUrl(coverURL)
            return a
        }
    }
}
