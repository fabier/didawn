package didawn

import grails.transaction.Transactional
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.RandomStringUtils

import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.text.MessageFormat

@Transactional
class CryptoService {

    def grailsApplication

    String dawn() {
        grailsApplication.config.di.dawn
    }

    String dawnDotCom() {
        grailsApplication.config.di.dawndotcom
    }

    String key1() {
        grailsApplication.config.di.key1
    }

    String key2() {
        grailsApplication.config.di.key2
    }

    protected byte[] getBlowfishKey(Long songId) {
        if (songId < 0L) {
            songId *= -1L
        }

        String hash = DigestUtils.md5Hex(iso88591bytes(songId.toString()))
        String part1 = hash.substring(0, 16)
        String part2 = hash.substring(16, 32)
        String[] data = [key1(), part1, part2]
        String keyStr = getXor(data, 16)
        return keyStr.getBytes()
    }

    private String getXor(String[] data, int len) {
        StringBuilder result = new StringBuilder()

        for (int i = 0; i < len; i++) {
            char character = data[0].charAt(i)

            for (int j = 1; j < data.length; j++) {
                char c = data[j].charAt(i)
                character = (character as int) ^ (c as int)
            }

            result.append(character)
        }

        return result.toString()
    }

    protected byte[] decryptBlowfish(byte[] data, byte[] key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish")
            Cipher cipher = Cipher.getInstance("Blowfish/CBC/NoPadding")
            cipher.init(2, keySpec, new IvParameterSpec([0, 1, 2, 3, 4, 5, 6, 7] as byte[]))
            return cipher.doFinal(data)
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            log.warn "Impossible to decrypt Blowfish", e
            return new byte[data.length]
        }
    }

    public String getDownloadURL(String md5, int format, long id, int mediaVersion) {
        String data = getDownloadUrlEnd(md5, format, id, mediaVersion)
        return getDownloadUrlFromData(data)
    }

    public String getDownloadURL(String md5, int format, String id, int mediaVersion) {
        String data = getDownloadUrlEnd(md5, format, id, mediaVersion)
        return getDownloadUrlFromData(data)
    }

    public String getDownloadUrlFromData(String data) {
        String proxyLetter = RandomStringUtils.random(1, "0123456789abcdef")
        return data != null ? MessageFormat.format("http://e-cdn-proxy-{0}." + dawnDotCom() + "/mobile/1/{1}",
                proxyLetter, data) : null
    }

    public String getDownloadUrlEnd(String md5, int format, String id, int mediaVersion) {
        try {
            String separator = "¤"
            String data = md5 + separator + format + separator + id + separator + mediaVersion
            String dataHash = DigestUtils.md5Hex(iso88591bytes(data))
            return aes(dataHash + separator + data + separator)
        } catch (Exception e) {
            throw new IllegalStateException("Impossible to get download URL", e)
        }
    }

    public String getDownloadUrlEnd(String md5, int format, long id, int mediaVersion) {
        try {
            String separator = "¤"
            String data = md5 + separator + format + separator + id + separator + mediaVersion
            String dataHash = DigestUtils.md5Hex(iso88591bytes(data))
            return aes(dataHash + separator + data + separator)
        } catch (Exception e) {
            throw new IllegalStateException("Impossible to get download URL", e)
        }
    }

    private String aes(String clearText) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key2().getBytes(), "AES")
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(1, skeySpec)
        byte[] encrypted = cipher.doFinal(iso88591bytes(clearText))
        return DatatypeConverter.printHexBinary(encrypted).toLowerCase()
    }

    private byte[] iso88591bytes(String s) {
        try {
            return s.getBytes("ISO-8859-1")
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Impossible to get bytes as ISO-8859-1", ex)
        }
    }
}
