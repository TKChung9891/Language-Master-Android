package com.example.languagemaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;

import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import android.widget.CompoundButton;
import android.widget.ProgressBar;

import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Locale;

public class ExpertJPActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecognitionListener {

    TextView mName, mEmail;
    View mView;
    private DrawerLayout mDrawerLayout;

    private static final int REQUEST_RECORD_PERMISSION = 100;
    private TextView txtSpeech, confidenceLabel;
    private EditText txtListen;
    private ToggleButton toggleBtnSpeak;
    private Button btnListen;
    private TextToSpeech textToSpeech;
    private ProgressBar progressBarSpeech;
    private SpeechRecognizer speechRecognizer = null;
    private Intent intentRecognizer;
    private String LOG_TAG = "VoiceRecognitionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expertjp);
        /// Nav Drawer
        Toolbar mToolbar = findViewById(R.id.iToolbar);
        setSupportActionBar(mToolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mView = mNavigationView.inflateHeaderView(R.layout.nav_header);
        mName = mView.findViewById(R.id.hName);
        mEmail = mView.findViewById(R.id.hEmail);

        // The following codes link the variables to the widgets on the screen so that
        // we can manipulate them in our code here.

        txtSpeech = findViewById(R.id.txtSpeech);
        progressBarSpeech = findViewById(R.id.progressBarSpeech);
        toggleBtnSpeak = findViewById(R.id.toggleBtnSpeak);
        progressBarSpeech.setVisibility(View.INVISIBLE);
        txtListen = (EditText)findViewById(R.id.txtListen);
        confidenceLabel = findViewById(R.id.confidenceLabel);
        btnListen = findViewById(R.id.btnListen);

        // Create new instance of TTS with Language set to Japanese
        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.JAPANESE);
                    textToSpeech.setSpeechRate(Float.parseFloat(getString(R.string.speech_rate)));
                }
            }
        });

        // On click listener set for Listen Button to output text to speech
        btnListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = txtListen.getText().toString();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });   

        // The following codes configure the SpeechRecognizer such as setting Language
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speechRecognizer.setRecognitionListener(this);
        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ja-JP");
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "ja-JP");
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        // The following codes add instructions for the Toggle Button when it is ON or OFF
         toggleBtnSpeak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) // If button is ON, get permission from user to record audio with the mic
                {
                    progressBarSpeech.setVisibility(View.VISIBLE);
                    progressBarSpeech.setIndeterminate(true);
                    ActivityCompat.requestPermissions(ExpertJPActivity.this,
                            new String[] { Manifest.permission.RECORD_AUDIO }, REQUEST_RECORD_PERMISSION);
                } else // If button is OFF, stop listening through the mic
                {
                    progressBarSpeech.setIndeterminate(false);
                    progressBarSpeech.setVisibility(View.INVISIBLE);
                    speechRecognizer.stopListening();
                }
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.nav_draw_open,
                R.string.nav_draw_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_a:
                Intent m1 = new Intent(this, MainActivity.class);
                startActivity(m1);
                finish();
                break;
            case R.id.nav_b:
                Intent m2 = new Intent(this, BeginnerJPActivity.class);
                startActivity(m2);
                finish();
                break;
            case R.id.nav_c:
                Intent m3 = new Intent(this, BeginnerKRActivity.class);
                startActivity(m3);
                finish();
                break;
            case R.id.nav_d:
//                Intent m4 = new Intent(this, ExpertJPActivity.class);
//                startActivity(m4);
//                finish();
                break;
            case R.id.nav_e:
                Intent m5 = new Intent(this, ExpertKRActivity.class);
                startActivity(m5);
                finish();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Return to Home screen upon Back Button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            Log.i(LOG_TAG, "destroy");
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        Intent m1 = new Intent(this, MainActivity.class);
        startActivity(m1);
    }


    // This method will capture the user's permission when he/she is prompted to
    // allow permission to use the mic for audio recording.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If user allows permission, start recording audio.
                    speechRecognizer.startListening(intentRecognizer);
                } else // If user does not allow, pop up a message.
                {
                    Toast.makeText(ExpertJPActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBarSpeech.setIndeterminate(false);
        progressBarSpeech.setMax(10);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        // The (int) below converts the float value to integer.
        // This is known as casting.
        progressBarSpeech.setProgress((int) rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBarSpeech.setIndeterminate(true);
        toggleBtnSpeak.setChecked(false);
    }

    @Override
    public void onError(int error) {
        String errorMessage = getErrorMessage(error);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        txtSpeech.setText(errorMessage);
        toggleBtnSpeak.setChecked(false);
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");

        // Best result from speech recognizer is stored in the first item of array list
        String bestResult = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
        txtSpeech.setText(bestResult);
        // Best confidence score corresponding to the result string is in first item of float array
        float [] confidence = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        float bestScore = confidence[0];
        String bestScoreString = String.format ("%.2f", bestScore);
        confidenceLabel.setText(bestScoreString);
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(LOG_TAG, "onEvent");
    }

    public static String getErrorMessage(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
