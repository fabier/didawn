package didawn

import didawn.gson.Track
import org.jaudiotagger.audio.exceptions.CannotReadException
import org.jaudiotagger.audio.exceptions.CannotWriteException
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.TagException
import org.jaudiotagger.tag.id3.ID3v23Tag
import org.jaudiotagger.tag.images.Artwork
import org.jaudiotagger.tag.images.StandardArtwork

import java.util.logging.Level
import java.util.logging.Logger

import static java.io.File.createTempFile
import static java.lang.String.valueOf
import static java.util.logging.Level.INFO
import static java.util.logging.Logger.getLogger
import static org.apache.commons.io.FileUtils.copyURLToFile
import static org.jaudiotagger.audio.AudioFileIO.read
import static org.jaudiotagger.tag.FieldKey.*

public class Utils {

    private static final Logger logger = getLogger(Utils.class.getName());

    private static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static void writeTrackInfo(Track track, File fileIn) throws IOException {
        try {
            MP3File f = (MP3File) read(fileIn);
            ID3v23Tag tag = new ID3v23Tag();

            for (String artist : track.getArtists()) {
                tag.addField(ARTIST, artist);
            }

            tag.addField(ALBUM_ARTIST, track.getAlbum().getArtist().getName());
            tag.addField(TITLE, track.getTitle());
            tag.addField(TRACK, valueOf(track.getTRACK_NUMBER()));
            tag.addField(DISC_NO, valueOf(track.getDISK_NUMBER()));
            tag.addField(ALBUM, track.getAlbum().getTitle());
            tag.addField(YEAR, track.getDIGITAL_RELEASE_DATE());
            tag.addField(GENRE, track.getGENRE_ID());
            tag.addField(ISRC, track.getISRC());
            tag.addField(COMPOSER, track.getCOMPOSER());
            tag.addField(BPM, Float.toString(track.getBPM()));
            tag.addField(TRACK_TOTAL, Integer.toString(track.getAlbum().getNbTracks()));
            tag.addField(RECORD_LABEL, Long.toString(track.getLABEL_ID()));
            tag.addField(getAlbumArtwork(track.getAlbum().getCover()));
            f.setTag(tag);
            f.commit();
        } catch (CannotWriteException | InvalidAudioFrameException | ReadOnlyFileException | TagException | CannotReadException e) {
            logger.log Level.WARNING, "Impossible to write track info", e
        }
    }

    private static Artwork getAlbumArtwork(String coverURL) throws IOException {
        StandardArtwork a = new StandardArtwork();
        if (coverURL == null) {
            return a;
        } else {
            File f = createTempFile("tmp", ".jpg");
            copyURLToFile(new URL(coverURL), f);
            a.setFromFile(f);
            deleteFile(f);
            a.setImageUrl(coverURL);
            return a;
        }
    }

    public static void deleteFile(File f) {
        if (!f.delete()) {
            logger.log(INFO, "Cannot delete file: {0}", f.getAbsolutePath());
        }
    }

    private Utils() {
    }
}
