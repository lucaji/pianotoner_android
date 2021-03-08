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

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;

import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;

import org.lucaji.pianotoner.pianoview.listener.OnLoadAudioListener;
import org.lucaji.pianotoner.pianoview.listener.OnPianoAutoPlayListener;
import org.lucaji.pianotoner.pianoview.listener.OnPianoListener;
import org.lucaji.pianotoner.pianoview.view.PianoView;
import org.lucaji.pianotoner.pianoview.entity.Piano;

import java.util.Arrays;
import java.util.Locale;



public class MainActivity extends AppCompatActivity
        implements OnPianoListener, OnLoadAudioListener,
        SeekBar.OnSeekBarChangeListener,
        View.OnClickListener, OnPianoAutoPlayListener {



    /**
     * Dp to px
     *
     * @param dp dp值
     * @return px 值
     */
    private float convertDpToPixel(float dp) {
        Resources resources = this.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private PianoView pianoView;
    private SeekBar seekBar;
    private Button leftArrow;
    private Button rightArrow;
    private Button btnMute;
    private int scrollProgress = 0;
    private final static float SEEKBAR_OFFSET_SIZE = -12;

    private TextView noteNameTextView;
    private TextView noteFrequencyTextView;

    //
    private boolean isPlaying = false;

    private int currentNoteNumber = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteNameTextView = findViewById((R.id.textViewNoteName));
        noteFrequencyTextView = findViewById((R.id.textViewNoteFrequency));

        //view
        pianoView = findViewById(R.id.pv);
        pianoView.setSoundPollMaxStream(10);
        seekBar = findViewById(R.id.sb);
        seekBar.setThumbOffset((int) convertDpToPixel(SEEKBAR_OFFSET_SIZE));
        leftArrow = findViewById(R.id.iv_left_arrow);
        rightArrow = findViewById(R.id.iv_right_arrow);
        btnMute = findViewById(R.id.iv_muteButton);
        //listener
        pianoView.setPianoListener(this);
        pianoView.setAutoPlayListener(this);
        pianoView.setLoadAudioListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        rightArrow.setOnClickListener(this);
        leftArrow.setOnClickListener(this);
        btnMute.setOnClickListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

        final Spinner spinnerSourceGet = (findViewById(R.id.spinnerReferenceAFrequency));
        spinnerSourceGet.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 415
                        TTPitchedNote.currentAFrequency = 415.0;
                        break;
                    case 1:
                        // 435
                        TTPitchedNote.currentAFrequency = 435.0;
                        break;
                    case 2:
                        // 440
                        TTPitchedNote.currentAFrequency = 440.0;
                        break;
                    case 3:
                        // 445
                        TTPitchedNote.currentAFrequency = 445.0;
                        break;
                }
                updateFreq();
            }

            @Override public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        //MuteAudio();
        // Connect synth io

        synth.add(lineOut = new LineOut());
        synth.add(toneGenerator = new TestToneGenerator());
        toneGenerator.output.connect(0, lineOut.input, 0);
        toneGenerator.output.connect(0, lineOut.input, 1);
    }

    @Override protected void onPause() {
        super.onPause(); // Always call the superclass method first
        stopOscillator();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //MuteAudio();
        //startOscillator();

        super.onResume();
    }

    @Override protected void onDestroy() {
        if (pianoView != null) {
            pianoView.releaseAutoPlay();
        }
        stopOscillator();

        super.onDestroy();
    }

    private static LineOut lineOut;
    public static TestToneGenerator toneGenerator;
    Synthesizer synth = JSyn.createSynthesizer(new JSynAndroidAudioDevice());


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onPianoInitFinish() {

    }

    @Override public void onPianoClick(Piano.PianoKeyType type, Piano.PianoVoice voice, int group,
                                       int positionOfGroup) {
        int noteNumber = 0;
        switch (voice) {

            case DO:
                break;
            case RE:
                noteNumber += 2;
                break;
            case MI:
                noteNumber += 4;
                break;
            case FA:
                noteNumber += 5;
                break;
            case SO:
                noteNumber += 7;
                break;
            case LA:
                noteNumber += 9;
                break;
            case SI:
                noteNumber += 11;
                break;
        }
        noteNumber += ((group + 1) * 12) + (type == Piano.PianoKeyType.BLACK ? 1 : 0);
        currentNoteNumber = noteNumber;
        updateFreq();
        if (!isPlaying) {
            Snackbar.make(this.noteNameTextView, Html.fromHtml("<font color=\"#ffffff\">Use the MUTE button to control the audio output.</font>"), Snackbar.LENGTH_LONG).setAction("UNMUTE", view -> {
                startOscillator();
            }).show();
        }
    }

    void updateFreq() {
        Log.e("onPianoClick", "MIDI=" + currentNoteNumber);
        TTPitchedNote theNote = new TTPitchedNote(currentNoteNumber);
        double frequency = theNote.getFrequency();

        noteFrequencyTextView.setText(String.format(Locale.ENGLISH, "%1$,.2f", frequency) + "Hz");
        noteNameTextView.setText(theNote.noteName());

        toneGenerator.frequency.set(frequency);

//        final float[] audioData = frequencyConverter.convert(frequency);
//        audioPlayer.setBuffer(audioData);
//        audioPlayer.play();
    }

    void startOscillator() {
        if (!synth.isRunning()) {
            synth.start();
            lineOut.start();
            isPlaying = true;
            btnMute.setBackgroundResource(R.drawable.ic_mutewhite);
        }
    }

    void stopOscillator() {
        lineOut.stop();
        synth.stop();
        isPlaying = false;
        btnMute.setBackgroundResource(R.drawable.ic_mutered);
    }

    public void MuteAudio(){
        AudioManager mAlramMAnager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, true);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        }
    }

    public void UnMuteAudio(){
        AudioManager mAlramMAnager=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        } else {
            mAlramMAnager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_ALARM, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_RING, false);
            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        }
    }
//    private final AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//    private final AudioConfig audioConfig = new AndroidAudioConfig();
//    private final AudioPlayer audioPlayer = new AndroidAudioPlayer(audioConfig);
//    private final FrequencyConverter frequencyConverter = new SineWaveFrequencyConverter(audioConfig);


    @Override public void loadPianoAudioStart() {
        //Toast.makeText(getApplicationContext(), "loadPianoMusicStart", Toast.LENGTH_SHORT).show();
    }

    @Override public void loadPianoAudioFinish() {
        //Toast.makeText(getApplicationContext(), "loadPianoMusicFinish", Toast.LENGTH_SHORT).show();
    }

    @Override public void loadPianoAudioError(Exception e) {
       // Toast.makeText(getApplicationContext(), "loadPianoMusicError", Toast.LENGTH_SHORT).show();
    }

    @Override public void loadPianoAudioProgress(int progress) {
        Log.e("TAG", "progress:" + progress);
    }

    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        pianoView.scroll(i);
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {

    }



    @Override public void onClick(View view) {
        if (scrollProgress == 0) {
            try {
                scrollProgress = (pianoView.getLayoutWidth() * 100) / pianoView.getPianoWidth();
            } catch (Exception e) {
                return;
            }
        }
        int progress;
        switch (view.getId()) {
            case R.id.iv_left_arrow:
                if (scrollProgress == 0) {
                    progress = 0;
                } else {
                    progress = seekBar.getProgress() - scrollProgress;
                    if (progress < 0) {
                        progress = 0;
                    }
                }
                seekBar.setProgress(progress);
                break;
            case R.id.iv_right_arrow:
                if (scrollProgress == 0) {
                    progress = 100;
                } else {
                    progress = seekBar.getProgress() + scrollProgress;
                    if (progress > 100) {
                        progress = 100;
                    }
                }
                seekBar.setProgress(progress);
                break;
            case R.id.iv_muteButton:
                if (!isPlaying) {
                    startOscillator();
                } else {
                    stopOscillator();
                }
                break;
        }
    }

    @Override public void onPianoAutoPlayStart() {
        Toast.makeText(this, "onPianoAutoPlayStart", Toast.LENGTH_SHORT).show();
    }

    @Override public void onPianoAutoPlayEnd() {
        Toast.makeText(this, "onPianoAutoPlayEnd", Toast.LENGTH_SHORT).show();
    }


}
