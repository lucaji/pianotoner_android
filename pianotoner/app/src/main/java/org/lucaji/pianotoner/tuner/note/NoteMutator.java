package org.lucaji.pianotoner.tuner.note;

/**
 * An interface representing a mutable {@link Note}.
 * This interface extends the {@link Note} interface.
 */
interface NoteMutator extends Note {

    void setName(String name);

    void setFrequency(double frequency);

    void setPercentOffset(float percentOffset);
}
