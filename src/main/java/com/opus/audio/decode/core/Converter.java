package com.opus.audio.decode.core;

//import it.sauronsoftware.jave.AudioAttributes;
//import it.sauronsoftware.jave.Encoder;
//import it.sauronsoftware.jave.EncoderException;
//import it.sauronsoftware.jave.EncodingAttributes;

import de.sciss.jump3r.Main;
import de.sciss.jump3r.lowlevel.LameEncoder;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static de.sciss.jump3r.lowlevel.LameEncoder.CHANNEL_MODE_STEREO;


public class Converter {
    //    public void conversao() throws EncoderException {
    //        File source = new File("C:\\Users\\ferna\\OneDrive\\Documentos\\Download.ogg");
    //        File target = new File("C:\\Users\\ferna\\OneDrive\\Documentos\\originalconvertido.mp3");
    //
    //        AudioAttributes audio = new AudioAttributes();
    //        audio.setCodec("libmp3lame");
    //        audio.setBitRate(128000);
    //        audio.setChannels(2);
    //        audio.setSamplingRate(44100);
    //        EncodingAttributes attrs = new EncodingAttributes();
    //        attrs.setFormat("mp3");
    //        attrs.setAudioAttributes(audio);
    //        Encoder encoder = new Encoder();
    //        try {
    //            encoder.encode(source, target, attrs);
    //        } catch (Exception e) {
    //            System.out.println("Encoding Failed");
    //        }
    //
    //    }

    public void ConvertFileToAIFF(String inputPath, String outputPath) {
        AudioFileFormat inFileFormat;
        File inFile;
        File outFile;
        try {
            inFile = new File(inputPath);
            outFile = new File(outputPath);
        } catch (NullPointerException ex) {
            System.out.println("Error: one of the ConvertFileToAIFF" + " parameters is null!");
            return;
        }
        try {
            // query file type
            inFileFormat = AudioSystem.getAudioFileFormat(inFile);
            if (inFileFormat.getType() != AudioFileFormat.Type.AIFF) {
                // inFile is not AIFF, so let's try to convert it.
                AudioInputStream inFileAIS = AudioSystem.getAudioInputStream(inFile);
                inFileAIS.reset(); // rewind
                if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.AIFF, inFileAIS)) {
                    // inFileAIS can be converted to AIFF.
                    // so write the AudioInputStream to the
                    // output file.
                    AudioSystem.write(inFileAIS, AudioFileFormat.Type.AIFF, outFile);
                    System.out.println("Successfully made AIFF file, " + outFile.getPath() + ", from " + inFileFormat.getType() + " file, " + inFile.getPath() + ".");
                    inFileAIS.close();
                    return; // All done now
                } else System.out.println("Warning: AIFF conversion of " + inFile.getPath() + " is not currently supported by AudioSystem.");
            } else System.out.println("Input file " + inFile.getPath() + " is AIFF." + " Conversion is unnecessary.");
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: " + inFile.getPath() + " is not a supported audio file type!");
            return;
        } catch (IOException e) {
            System.out.println("Error: failure attempting to read " + inFile.getPath() + "!");
            return;
        }
    }

    public static void main(String[] args) throws Exception {
        Converter converter = new Converter();
        String inputPathOGG = "C:\\Users\\ferna\\OneDrive\\Documentos\\fileOGG.ogg";
        String inputPath = "C:\\Users\\ferna\\OneDrive\\Documentos\\teste.ogg";
        String wavPath = "C:\\Users\\ferna\\OneDrive\\Documentos\\teste.wav";
        String outputPath = "C:\\Users\\ferna\\OneDrive\\Documentos\\originalconvertido.mp3";
        //        AudioFormat formato = new AudioFormat(8000f, 16, 2, true, false);
        //        converter.convertWavFileToMp3File(new File(inputPath), new File(outputPath));

        File file = new File(inputPathOGG);


//        AudioInputStream stream = converter.createOgg(file);

        byte[] bytes = getByteArrayFromFile(file);

        File tempFile = File.createTempFile("temp", null, null);
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bytes);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        File source = new File("C:\\Users\\jmoura\\Documents\\Bradesco\\temp");

//        try {
//            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//            InputStream stream = new FileInputStream(inputPathOGG);
//            File mp3 = new File(outputPath);
//            OutputStream os = new FileOutputStream(mp3);
//
//            byte[] buffer = new byte[4096];
//
//            int len;
//            while ((len = stream.read(buffer)) > 0) {
//                os.write(buffer, 0, len);
//            }
//
//            byteBuffer.close();
//            stream.close();
//            os.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //        InputStream inputStream = new ByteArrayInputStream(bytes);
        //        converter.uploadFiles("teste.wav", bytes);


        //        VorbisFile vf = new VorbisFile(inputPathOGG);
        //        AudioFileFormat baseFileFormat= AudioSystem.getAudioFileFormat(file);


        //        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);


        //        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(wavPath));
//        AudioFormat format;
//        format = stream.getFormat();
//                AudioFormat formato = new AudioFormat(format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), format.getFrameRate(), format.isBigEndian());
//        AudioFormat formato = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100f, 16, 2, 4, // frameSize
//                44100f,// frameRate
//                false);

//        byte[] pcmToMp3 = converter.encodePcmToMp3(bytes, formato);

//        Files.write(new File(outputPath).toPath(), pcmToMp3);
    }


//    public AudioInputStream createOgg(File fileIn) throws IOException, Exception {
//        AudioInputStream audioInputStream = null;
//        AudioFormat targetFormat = null;
//        try {
//            AudioInputStream in = null;
//            VorbisAudioFileReader vb = new VorbisAudioFileReader();
//            byte[] bytes = getByteArrayFromFile(fileIn);
//            InputStream inputStream = new ByteArrayInputStream(bytes);
//            in = vb.getAudioInputStream(inputStream);
//
//            AudioFormat baseFormat = in.getFormat();
//            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
//            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, in);
//        } catch (UnsupportedAudioFileException ue) {
//            System.out.println("\nUnsupported Audio");
//        }
//        return audioInputStream;
//    }

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

    public void encodePcmToMp3(byte[] pcm) {
        //LameEncoder encoder = new LameEncoder(new javax.sound.sampled.AudioFormat(44100.0f, 16, 2, true, false), 256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);
        //fast  gmm2  LameEncoder encoder = new LameEncoder(new javax.sound.sampled.AudioFormat(88200.0f, 16, 2, true, false), 256, MPEGMode.STEREO, Lame.QUALITY_HIGHEST, false);

        LameEncoder encoder = new LameEncoder(new AudioFormat(44100.0f, 8, 1, true, false), 256, CHANNEL_MODE_STEREO, 1, false);
        //        LameEncoder encoder = new LameEncoder(new AudioFormat(44100.0f, 8, 1, true, false),);


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

        byte[] bytes = mp3.toByteArray();

        File file = new File("C:\\Users\\ferna\\OneDrive\\Documentos\\originalconvertido.mp3");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream("C:\\Users\\ferna\\OneDrive\\Documentos\\originalconvertido.mp3");
                stream.write(mp3.toByteArray());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        //   return mp3.toByteArray();
    }

    public static byte[] fileToByteArray(String name) {
        Path path = Paths.get(name);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void convertWavFileToMp3File(File source, File target) throws IOException {
        String[] mp3Args = {"--preset", "standard", "-q", "0", "-m", "s", source.getAbsolutePath(), target.getAbsolutePath()};
        (new Main()).run(mp3Args);
    }

    public void uploadFiles(String fileName, byte[] bFile) throws IOException, UnsupportedAudioFileException {
        String uploadedFileLocation = "c:\\";

        AudioInputStream source;
        AudioInputStream pcm;
        InputStream b_in = new ByteArrayInputStream(bFile);
        source = AudioSystem.getAudioInputStream(new BufferedInputStream(b_in));
        pcm = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, source);
        File newFile = new File(uploadedFileLocation + fileName);
        AudioSystem.write(pcm, AudioFileFormat.Type.WAVE, newFile);

        source.close();
        pcm.close();
    }

}

