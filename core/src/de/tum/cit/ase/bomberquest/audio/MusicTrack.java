package de.tum.cit.ase.bomberquest.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 * This enum is used to manage the music tracks in the game.
 * Currently, only one track is used, but this could be extended to include multiple tracks.
 * Using an enum for this purpose is a good practice, as it allows for easy management of the music tracks
 * and prevents the same track from being loaded into memory multiple times.
 * See the assets/audio folder for the actual music files.
 * Feel free to add your own music tracks and use them in the game!
 */
public enum MusicTrack {
    
    BACKGROUND("background.mp3", 0.2f,true),
    BOMBEXPLODE("bombexplode.mp3",0.2f,false),
    ENEMYDIE("enemydie.mp3",0.2f,false),
    BUTTONSOUND("menubuttonsound.mp3",0.2f,false),
    POWERUPSOUND("powerup.mp3",0.2f,false),
    GAMEOVERSOUND("gameoversound.mp3",0.07f,false),
    MENUMUSIC("menumusic.mp3",0.7f,true),
    WINSOUND("winsound.mp3",0.8f,false),
    PLAYERDIE("playerdie.mp3",0.1f,false),
    PAUSEMUSIC("intro.mp3",0.3f,true)
    ;

    
    /** The music file owned by this variant. */
    private final Music music;
    
    MusicTrack(String fileName, float volume, boolean isLooping) {
        this.music = Gdx.audio.newMusic(Gdx.files.internal("audio/" + fileName));
        this.music.setLooping(isLooping);
        this.music.setVolume(volume);
    }
    
    /**
     * Play this music track.
     * This will not stop other music from playing - if you add more tracks, you will have to handle that yourself.
     */
    public void play() {
        this.music.play();
    }
    public void dispose() {this.music.dispose();}
    public void stop(){this.music.pause();}
}
