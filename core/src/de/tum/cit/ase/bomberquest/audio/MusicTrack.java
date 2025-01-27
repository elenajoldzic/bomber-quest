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

    /** Enum constants for different music and sound effects in the game. Each constant maps to a specific file. */
    BACKGROUND("electronicgameplaymusic2.mp3", 0.16f, true),
    BOMBEXPLODE("bombexplode.mp3", 0.2f, false),
    ENEMYDIE("FX061.mp3", 0.3f, false),
    BUTTONSOUND("menubuttonsound.mp3", 0.2f, false),
    POWERUPSOUND("electronicpowerup.mp3", 0.1f, false),
    GAMEOVERSOUND("electronicdeath.mp3", 0.07f, false),
    MENUMUSIC("electronicmenumusic.mp3", 0.3f, true),
    WINSOUND("winsound.mp3", 0.8f, false),
    PLAYERDIE("playerdie.mp3", 0.1f, false),
    PAUSEMUSIC("intro.mp3", 0.3f, true),
    BOMBPLACE("bombdropsound.mp3", 0.06f, false),
    EXITREVEAL("exitreveal.mp3", 0.4f, false),
    WALKING("walking.mp3", 0.1f, true);

    /**
     * The `Music` object associated with the track.
     * This field holds the loaded music file and its properties (looping, volume).
     */
    private final Music music;

    /**
     * Constructor for the enum constants. It initializes the `Music` object for each track.
     * @param fileName The name of the music file in the "audio" folder.
     * @param volume The volume level of the music (0.0f to 1.0f).
     * @param isLooping Whether the music should loop continuously.
     */
    MusicTrack(String fileName, float volume, boolean isLooping) {
        this.music = Gdx.audio.newMusic(Gdx.files.internal("audio/" + fileName)); // Load the music file.
        this.music.setLooping(isLooping); // Set whether the music should loop.
        this.music.setVolume(volume); // Set the initial volume for the track.
    }

    /**
     * Play this music track.
     * This method starts playback of the track. It does not stop or pause any other music that might already
     * be playing in the game. Managing simultaneous playback of multiple tracks is the caller's responsibility.
     */
    public void play() {
        this.music.play();
    }

    /**
     * Dispose of the `Music` object when it is no longer needed.
     * This method releases the resources associated with the music track to prevent memory leaks.
     */
    public void dispose() {
        this.music.dispose();
    }

    /**
     * Stop or pause the music track.
     * This method halts playback of the current track. Playback can be resumed later with the `play()` method.
     */
    public void stop() {
        this.music.pause();
    }
}