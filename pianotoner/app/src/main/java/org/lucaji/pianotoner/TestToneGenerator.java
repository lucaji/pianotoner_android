package org.lucaji.pianotoner;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.PassThrough;
import com.jsyn.unitgen.SawtoothOscillatorBL;
import com.jsyn.unitgen.Select;
import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.SquareOscillatorBL;
import com.jsyn.unitgen.UnitVoice;
import com.jsyn.unitgen.WhiteNoise;
import com.softsynth.shared.time.TimeStamp;

// Define Jsyn synthesis class, import from Syntona

public class TestToneGenerator extends Circuit implements UnitVoice {
    // Declare units and ports.
    PassThrough mFrequencyPassThrough;
    public UnitInputPort frequency;
    PassThrough mAmplitudePassThrough;
    public UnitInputPort amplitude;
    PassThrough mOutputPassThrough;
    public UnitOutputPort output;
    //WhiteNoise mWhiteNoise;
    //Select mSelectSineSaw;
    SineOscillator mSineOsc;
    //SawtoothOscillatorBL mSawOscBL;
    //SquareOscillatorBL mSquareOscBL;
    //Select mSelectSquareWhite;
    Select mSelectFinalSource;

    // Declare inner classes for any child circuits.

    public TestToneGenerator() {
        // Create unit generators.
        add(mFrequencyPassThrough = new PassThrough());
        addPort(frequency = mFrequencyPassThrough.input, "frequency");
        add(mAmplitudePassThrough = new PassThrough());
        addPort(amplitude = mAmplitudePassThrough.input, "amplitude");
        add(mOutputPassThrough = new PassThrough());
        addPort( output = mOutputPassThrough.output, "output");
        //add(mWhiteNoise = new WhiteNoise());
        //add(mSelectSineSaw = new Select());
        add(mSineOsc = new SineOscillator());
        //add(mSawOscBL = new SawtoothOscillatorBL());
        //add(mSquareOscBL = new SquareOscillatorBL());
        //add(mSelectSquareWhite = new Select());
        add(mSelectFinalSource = new Select());
        // Connect units and ports.
        mFrequencyPassThrough.output.connect(mSineOsc.frequency);
        //mFrequencyPassThrough.output.connect(mSawOscBL.frequency);
        //mFrequencyPassThrough.output.connect(mSquareOscBL.frequency);
        //mAmplitudePassThrough.output.connect(mWhiteNoise.amplitude);
        mAmplitudePassThrough.output.connect(mSineOsc.amplitude);
        //mAmplitudePassThrough.output.connect(mSawOscBL.amplitude);
        //mAmplitudePassThrough.output.connect(mSquareOscBL.amplitude);
        //mWhiteNoise.output.connect(mSelectSquareWhite.inputB);
        //mSelectSineSaw.output.connect(mSelectFinalSource.inputA);
       // mSineOsc.output.connect(mSelectSineSaw.inputA);
        mSineOsc.output.connect(mSelectFinalSource.inputA);

        //mSawOscBL.output.connect(mSelectSineSaw.inputB);
        //mSquareOscBL.output.connect(mSelectSquareWhite.inputA);
        //mSelectSquareWhite.output.connect(mSelectFinalSource.inputB);
        mSelectFinalSource.output.connect(mOutputPassThrough.input);
        // Setup
        frequency.setup(20.0, 440.0, 22000.0);
        amplitude.setup(0.0, 1.0, 1.0);
        //mSelectSineSaw.select.set(1.0);
        //mSelectSquareWhite.select.set(0.0);
        mSelectFinalSource.select.set(0.0);
    }

    public void noteOn(double frequency, double amplitude, TimeStamp timeStamp) {
        this.frequency.set(frequency, timeStamp);
        this.amplitude.set(amplitude, timeStamp);
    }

    public void noteOff(TimeStamp timeStamp) {
    }

    public UnitOutputPort getOutput() {
        return output;
    }
}
