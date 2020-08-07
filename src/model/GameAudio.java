package model;

import java.applet.Applet;
import java.applet.AudioClip;

/**
 * @author Yhaobo
 * @date 2020/7/12
 */
public class GameAudio {
    /**
     * 惨叫声
     */
    private static AudioClip screech;
    /**
     * 肚子好饿周星驰
     */
    private static AudioClip hungry;
    /**
     * 婴儿开心笑声
     */
    private static AudioClip laughter;
    /**
     * 背景音乐
     */
    private static AudioClip music;
    /**
     * 狼叫声
     */
    private static AudioClip wolfAudio;
    /**
     * 羊叫声
     */
    private static AudioClip sheepAudio;

    static {
        try {
            music = Applet.newAudioClip(GameAudio.class.getResource("/resource/背景音乐.wav"));
            music.loop();
            screech = Applet.newAudioClip(GameAudio.class.getResource("/resource/惨叫声.wav"));
            hungry = Applet.newAudioClip(GameAudio.class.getResource("/resource/肚子好饿周星驰.wav"));
            laughter = Applet.newAudioClip(GameAudio.class.getResource("/resource/婴儿开心笑声.wav"));
            wolfAudio = Applet.newAudioClip(GameAudio.class.getResource("/resource/狼叫声.wav"));
            sheepAudio = Applet.newAudioClip(GameAudio.class.getResource("/resource/羊叫声.wav"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static AudioClip getScreech() {
        return screech;
    }

    public static AudioClip getHungry() {
        return hungry;
    }

    public static AudioClip getLaughter() {
        return laughter;
    }

    public static AudioClip getMusic() {
        return music;
    }

    public static AudioClip getWolfAudio() {
        return wolfAudio;
    }

    public static AudioClip getSheepAudio() {
        return sheepAudio;
    }

    public static void stopAudio() {
        screech.stop();
        hungry.stop();
        laughter.stop();
        music.stop();
        wolfAudio.stop();
        sheepAudio.stop();
    }
}
