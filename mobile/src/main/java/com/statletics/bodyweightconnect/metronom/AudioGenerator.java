package com.statletics.bodyweightconnect.metronom;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

/**
 * Created by Tonni on 02.08.2016.
 */
public class AudioGenerator {
    private int sampleRate;
    private AudioTrack audioTrack;

    public AudioGenerator(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public double[] getSineWave(int samples, int sampleRate, double frequencyOfTone) {
        double[] sample = new double[samples];
        for (int i = 0; i < samples; i++) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequencyOfTone));
        }
        return sample;
    }

    public double[] getTockFromFile(){
        double[] left = new double[0],right;

        //Get audio file into array
        try {
            byte[] wav = IOUtils.toByteArray(AudioGenerator.this.getClass().getResourceAsStream("/assets/audio/tick.wav"));
            // Determine if mono or stereo
            int channels = wav[22];     // Forget byte 23 as 99.999% of WAVs are 1 or 2 channels

            // Get past all the other sub chunks to get to the data subchunk:
            int pos = 12;   // First Subchunk ID from 12 to 16

            // Keep iterating until we find the data chunk (i.e. 64 61 74 61 ...... (i.e. 100 97 116 97 in decimal))
            while(!(wav[pos]==100 && wav[pos+1]==97 && wav[pos+2]==116 && wav[pos+3]==97)) {
                pos += 4;
                int chunkSize = wav[pos] + wav[pos + 1] * 256 + wav[pos + 2] * 65536 + wav[pos + 3] * 16777216;
                pos += 4 + chunkSize;
            }
            pos += 8;

            // Pos is now positioned to start of actual sound data.
            int samples = (wav.length - pos)/2;     // 2 bytes per sample (16 bit sound mono)
            if (channels == 2) samples /= 2;        // 4 bytes per sample (16 bit stereo)

            // Allocate memory (right will be null if only mono sound)
            left = new double[samples];
            if (channels == 2) right = new double[samples];
            else right = null;

            // Write to double array/s:
            int i=0;
            while (pos < wav.length) {
                left[i] = bytesToDouble(wav[pos], wav[pos + 1]);
                pos += 2;
                if (channels == 2) {
                    right[i] = bytesToDouble(wav[pos], wav[pos + 1]);
                    pos += 2;
                }
                i++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


       // double[] sample = new double[samples];



        return left;
    }

    // convert two bytes to one double in the range -1 to 1
    static double bytesToDouble(byte firstByte, byte secondByte) {
        // convert two bytes to one short (little endian)
        short s = (short) ((secondByte << 8) | firstByte);
        // convert to range from -1 to (just below) 1
        return s / 32768.0;
    }


    public byte[] get16BitPcm(double[] samples) {
        byte[] generatedSound = new byte[2 * samples.length];
        int index = 0;
        for (double sample : samples) {
            // scale to maximum amplitude
            short maxSample = (short) ((sample * Short.MAX_VALUE));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[index++] = (byte) (maxSample & 0x00ff);
            generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);

        }
        return generatedSound;
    }

    public void createPlayer() {
        if(audioTrack==null) {
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, sampleRate,
                    AudioTrack.MODE_STREAM);
        }
        audioTrack.play();
    }

    public void writeSound(double[] samples) {
        byte[] generatedSnd = get16BitPcm(samples);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
    }

    public void destroyAudioTrack() {
        audioTrack.stop();
        audioTrack.flush();
        audioTrack.release();

        audioTrack = null;
    }

}
