package com.opus.audio.decode.core;

import de.sciss.jump3r.lowlevel.LameEncoder;
import org.jitsi.impl.neomedia.codec.audio.opus.Opus;

import javax.sound.sampled.AudioFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author janayna.fernandes@bradesco.com.br
 */

public class OpusAudioDecoder {
    private static int BUFFER_SIZE = 1024 * 1024;
    private static int INPUT_BITRATE = 48000;
    private static int OUTPUT_BITRATE = 48000;

    private FileStreamCustom oggFile;
    private long opusState;

    private ByteBuffer decodeBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private AudioFormat audioFormat = new AudioFormat(OUTPUT_BITRATE, 16, 1, true, false);

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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            for (LogicalOggStreamCustomImpl stream : (Collection<LogicalOggStreamCustomImpl>) oggFile.getLogicalStreams()) {
                byte[] nextPacket = stream.getNextOggPacket();
                while (nextPacket != null) {
                    byte[] decodedData = decode(nextPacket);
                    if (decodedData != null) {
                        outputStream.write(decodedData);
                    }
                    try {
                        nextPacket = stream.getNextOggPacket();
                    } catch (IOException | IndexOutOfBoundsException e) {
                        nextPacket = null;
                    }
                }
            }
            return encodePcmToMp3(outputStream.toByteArray(), audioFormat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
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
}
