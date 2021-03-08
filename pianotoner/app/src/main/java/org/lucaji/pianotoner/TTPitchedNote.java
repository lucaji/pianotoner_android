/*
    Copyright 2021 Luca Cipressi - lookaji - themilletgrainfromouterspace

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

package org.lucaji.pianotoner;

import static java.lang.Math.exp;
import static java.lang.StrictMath.abs;

public class TTPitchedNote {

    // FREQUENCY
    public static double f(int midiNoteNumber) {
        // problem with range from c-2 to c8
        if (midiNoteNumber < 0) {
            return currentAFrequency * exp(0.057762265 * 69.0);
        }
        return currentAFrequency * exp(0.057762265 * (midiNoteNumber - 69));
    }

    String noteName() {
        return TTPitchedNote.midinumberToNoteName(this.number);
    }

    static String midinumberToNoteName(int midiNote) {

        // StuffLogger.print("midinumberToNoteName \(midiNote)")

        int octave = (midiNote / 12) - 1;
        int noteIndex = abs(midiNote % 12);

        String note;
        if (usingSharps) {
            note = sharps[noteIndex];
        } else {
            note = flats[noteIndex];
        }
        return note + octave;
    }

    static String noteNameByName(String noteName) {
        int offset = noteName.length() == 3 ? 2 : 1;
        return ""; //String(noteName.prefix(offset));
    }

    static int noteOctaveByName(String noteName) {
        int octave = -2;
        //for i in -2...8 {
        //    if noteName.hasSuffix(String(i)) { octave = i }
        //}
        return octave;
    }


    // REFERENCE A
    public static double currentAFrequency = 440.0;
    // public static double[] aTunings = { 415.0, 432.0, 435.0, 440.0, 444.0 };
    public static double[] aTunings = { 415.0, 435.0, 440.0, 445.0 };

    final static int gTuningsCount = aTunings.length;

    public static void changeReferenceA(int indexxo) {
        if ((indexxo < gTuningsCount) && (indexxo >= 0)) {
            currentAFrequency = aTunings[indexxo];
        }
    }

    public static boolean usingSharps = true;
    //public static String[] flats = {"C", "D♭","D","E♭","E","F","G♭","G","A♭","A","B♭","B"};
    //public static String[] sharps = {"C", "C♯","D","D♯","E","F","F♯","G","G♯","A","A♯","B"};
    public static String[] flats = {"C", "Db","D","Eb","E","F","Gb","G","Ab","A","Bb","B"};
    public static String[] sharps = {"C", "C#","D","D#","E","F","F#","G","G#","A","A#","B"};

    public TTPitchedNote(int number) {
        this.number = number;
    }

    public TTPitchedNote(int octave, int semitone) {
        this.number = 12 * octave + semitone;
    }

    public TTPitchedNote(String name) {
        String uppercased = name.toUpperCase();
        boolean success = false;
    }

    public int number = 0;
    private int octave = 0;
    public int getOctave() {
        return this.octave / 12 - 2;
    }
    public void setOctave(int value) {
        this.octave = value;
    }

    private int semitone = 0;
    public int getSemitone() {
        return this.semitone % 12;
    }
    public void setSemitone(int value) {
        this.semitone = value;
    }

    public double getFrequency() {
        return f(this.number);
    }


}
