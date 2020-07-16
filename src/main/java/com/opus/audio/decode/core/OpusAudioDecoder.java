package com.opus.audio.decode.core;

import de.sciss.jump3r.lowlevel.LameEncoder;
import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

import javax.sound.sampled.AudioFormat;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collection;

public class OpusAudioDecoder {
    private static int BUFFER_SIZE = 1024 * 1024;
    private static int INPUT_BITRATE = 48000;
    private static int OUTPUT_BITRATE = 48000;

    private FileStreamCustom oggFile;
    private long opusState;

    private ByteBuffer decodeBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private AudioFormat audioFormat = new AudioFormat(OUTPUT_BITRATE, 16, 1, true, false);

    public static void main(String[] args) {
        try {
            File source = new File("C:\\Users\\ferna\\OneDrive\\Documentos\\audio.ogg");
            byte[] bytes = getByteArrayFromFile(source);

            File tempFile = File.createTempFile("temp", null, null);
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(bytes);

            OpusAudioDecoder opusAudioDecoder = new OpusAudioDecoder(tempFile);
            byte[] decode = opusAudioDecoder.decode();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public OpusAudioDecoder(File audioFile) throws IOException {
        oggFile = new FileStreamCustom(new RandomAccessFile(audioFile, "r"));
        opusState = Opus.decoder_create(INPUT_BITRATE, 1);
    }

    private byte[] decode(byte[] packetData) {
        int frameSize = Opus.decoder_get_nb_samples(opusState, packetData, 0, packetData.length);
        int decodedSamples = Opus.decode(opusState, packetData, 0, packetData.length, decodeBuffer.array(), 0, frameSize, 0);
        if (decodedSamples < 0) {
            decodeBuffer.clear();
            return null;
        }
        decodeBuffer.position(decodedSamples * 2); // 2 bytes per sample
        decodeBuffer.flip();

        byte[] decodedData = new byte[decodeBuffer.remaining()];
        decodeBuffer.get(decodedData);
        decodeBuffer.flip();
        return decodedData;
    }

    public byte[] decode() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        int cont = 0;
        byte[] pcmToMp3 = new byte[0];
        try {
            for (LogicalOggStreamCustomImpl stream : (Collection<LogicalOggStreamCustomImpl>) oggFile.getLogicalStreams()) {
                byte[] nextPacket = stream.getNextOggPacket();
                while (nextPacket != null) {
                    System.out.println(cont++ +" e "+ nextPacket);
                    byte[] decodedData = decode(nextPacket);
                    if (decodedData != null) {
                        outputStream.write(decodedData);
                    }
                    try {
                        nextPacket = stream.getNextOggPacket();
                        System.out.println(nextPacket);
                    } catch (IOException | IndexOutOfBoundsException e) {
                        nextPacket = null;
                    }
                }
            }
            pcmToMp3 = encodePcmToMp3(outputStream.toByteArray(), audioFormat);
//            Files.write(new File(outputPath).toPath(), pcmToMp3);
            String encodeding = Base64.getEncoder().encodeToString(pcmToMp3);
            System.out.println(encodeding);
            // acredito que não será necessário salvar o arquivo apenas retornar o pcmToMp3, mas lerá da onde?
            // 2 dependencias maven e 3 pacotes jar
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pcmToMp3;
    }

    public byte[] encodePcmToMp3(byte[] pcm, AudioFormat audioFormat) {
        LameEncoder encoder = new LameEncoder(audioFormat);
        ByteArrayOutputStream mp3 = new ByteArrayOutputStream();
        byte[] buffer = new byte[encoder.getPCMBufferSize()];

        int bytesToTransfer = Math.min(buffer.length, pcm.length);
        int bytesWritten;
        int currentPcmPosition = 0;
        while (0 < (bytesWritten = encoder.encodeBuffer(pcm, currentPcmPosition, bytesToTransfer, buffer))) {
            currentPcmPosition += bytesToTransfer;
            bytesToTransfer = Math.min(buffer.length, pcm.length - currentPcmPosition);

            mp3.write(buffer, 0, bytesWritten);
        }

        encoder.close();
        return mp3.toByteArray();
    }

    static byte[] getByteArrayFromFile(final File file) throws FileNotFoundException, IOException {
        final FileInputStream fis = new FileInputStream(file);
        try {
            final ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            final byte[] buffer = new byte[1024];
            int cnt;
            while ((cnt = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, cnt);
            }
            return bos.toByteArray();
        } finally {
            fis.close();
        }
    }
}




