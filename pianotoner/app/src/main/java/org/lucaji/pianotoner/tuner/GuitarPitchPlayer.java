package org.lucaji.pianotoner.tuner;

import org.lucaji.pianotoner.tuner.converter.FrequencyConverter;
import org.lucaji.pianotoner.tuner.player.AudioPlayer;

import io.reactivex.rxjava3.core.Completable;

/**
 * An implementation of the {@link PitchPlayer} interface.
 */
public class GuitarPitchPlayer implements PitchPlayer {

    private final AudioPlayer audioPlayer;
    private final FrequencyConverter frequencyConverter;

    public GuitarPitchPlayer(final AudioPlayer audioPlayer, final FrequencyConverter frequencyConverter) {
        this.audioPlayer = audioPlayer;
        this.frequencyConverter = frequencyConverter;
    }

    @Override
    public Completable startPlaying(double frequency) {

        return Completable.create(emitter -> {
            try {
                final float[] audioData = frequencyConverter.convert(frequency);

                audioPlayer.setBuffer(audioData);

                audioPlayer.play();
            } catch (Exception exception) {
                emitter.tryOnError(exception);
            }
        }).doOnEvent(event -> audioPlayer.stop())
                .doOnDispose(this::stopPlayingAndRelease);
    }

    private void stopPlayingAndRelease() {
        audioPlayer.stop();
        audioPlayer.release();
    }
}
