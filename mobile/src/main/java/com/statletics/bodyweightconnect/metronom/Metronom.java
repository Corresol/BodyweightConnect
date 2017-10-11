package com.statletics.bodyweightconnect.metronom;

/**
 * Created by Tonni on 02.08.2016.
 */
public class Metronom {

    private double bpm;
    private int beat = 0;
    private int noteValue;
    private int silence;

    private int tick = 150; // samples of tick

    private boolean play = true;

    private AudioGenerator audioGenerator = new AudioGenerator(16000);

    public Metronom() {

    }

    public void calcSilence() {
        silence = (int) (((60/bpm)*16000)-tick);
    }

    public void play() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                play=true;
                audioGenerator.createPlayer();
                double[] tick =
                        //audioGenerator.getSineWave(this.tick, 8000, beatSound);
                        audioGenerator.getTockFromFile();
                double[] tock =
                        audioGenerator.getTockFromFile();
                //audioGenerator.getSineWave(this.tick, 8000, sound);
                Metronom.this.tick=tock.length;
                calcSilence();
                double silence = 0;
                double[] sound = new double[16000];
                int t = 0,s = 0,b = 0;
                do {
                    for(int i=0;i<sound.length&&play;i++) {
                        if(t<Metronom.this.tick) {
                            if(b == 0)
                                sound[i] = tock[t];
                            else
                                sound[i] = tick[t];
                            t++;
                        } else {
                            sound[i] = silence;
                            s++;
                            if(s >= Metronom.this.silence) {
                                t = 0;
                                s = 0;
                                b++;
                                if(b > (Metronom.this.beat-1))
                                    b = 0;
                            }
                        }
                    }
                    audioGenerator.writeSound(sound);
                    //System.out.println("write sound: silence("+Metronom.this.silence+")");
                } while(play);
            }
        });
        thread.setDaemon(true);
        thread.start();

    }

    public void stop() {
        play = false;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        audioGenerator.destroyAudioTrack();
    }

    public double getBpm() {
        return bpm;
    }

    public void setBpm(double bpm) {
        this.bpm = bpm;
        //System.out.println("Set BPM : "+bpm);
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getTick() {
        return tick;
    }
}
