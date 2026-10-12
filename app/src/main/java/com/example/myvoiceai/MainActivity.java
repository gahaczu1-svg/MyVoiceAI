package com.example.myvoiceai;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQ_AUDIO = 41;
    private TextView status, transcript;
    private SpeechRecognizer recognizer;
    private TextToSpeech tts;
    private boolean ttsReady = false;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildUI();
        tts = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                ttsReady = true;
                tts.setLanguage(new Locale("hi", "IN"));
            }
        });
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO);
        } else setupRecognizer();
    }

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 32, 24, 24);
        root.setBackgroundColor(0xFF171323);

        TextView title = new TextView(this);
        title.setText("🎙 My Voice AI");
        title.setTextSize(28);
        title.setTextColor(0xFFFFFFFF);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 12, 0, 28);
        root.addView(title);

        status = new TextView(this);
        status.setText("तैयार हूँ — माइक दबाकर बोलें");
        status.setTextSize(17);
        status.setTextColor(0xFFE0D7FF);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0, 12, 0, 20);
        root.addView(status);

        Button listen = new Button(this);
        listen.setText("🎤  बोलें");
        listen.setTextSize(20);
        listen.setOnClickListener(v -> startListening());
        root.addView(listen, new LinearLayout.LayoutParams(-1, -2));

        Button repeat = new Button(this);
        repeat.setText("🔊  जवाब सुनें");
        repeat.setOnClickListener(v -> {
            String text = transcript.getText().toString();
            if (ttsReady && !text.isEmpty()) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "reply");
            else Toast.makeText(this, "पहले माइक से कुछ बोलें", Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, -2);
        bp.topMargin = 12;
        root.addView(repeat, bp);

        TextView label = new TextView(this);
        label.setText("आपकी आवाज़ से पहचाना गया टेक्स्ट:");
        label.setTextColor(0xFFFFFFFF);
        label.setTextSize(16);
        label.setPadding(0, 28, 0, 10);
        root.addView(label);

        transcript = new TextView(this);
        transcript.setText("यहाँ आपका बोला हुआ टेक्स्ट आएगा।");
        transcript.setTextColor(0xFFFFFFFF);
        transcript.setTextSize(19);
        transcript.setPadding(16, 18, 16, 18);
        transcript.setBackgroundColor(0xFF292438);
        root.addView(transcript, new LinearLayout.LayoutParams(-1, -2));

        TextView note = new TextView(this);
        note.setText("यह शुरुआती वॉइस ऐप है: आवाज़ को टेक्स्ट में बदलता है और टेक्स्ट बोलकर सुनाता है।");
        note.setTextColor(0xFFBDB5D4);
        note.setTextSize(13);
        note.setPadding(0, 24, 0, 0);
        root.addView(note);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        setContentView(scroll);
    }

    private void setupRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            status.setText("इस फोन में speech recognition उपलब्ध नहीं है। Google app/Speech Services अपडेट करें।");
            return;
        }
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle params) { status.setText("सुन रहा हूँ… बोलिए"); }
            public void onBeginningOfSpeech() { status.setText("आप बोल रहे हैं…"); }
            public void onRmsChanged(float rmsdB) {}
            public void onBufferReceived(byte[] buffer) {}
            public void onEndOfSpeech() { status.setText("आवाज़ समझ रहा हूँ…"); }
            public void onError(int error) {
                status.setText("आवाज़ नहीं समझ आई। फिर से कोशिश करें।");
            }
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    transcript.setText(matches.get(0));
                    status.setText("हो गया! नीचे टेक्स्ट देखें।");
                } else status.setText("कुछ सुनाई नहीं दिया, फिर से बोलें।");
            }
            public void onPartialResults(Bundle partialResults) {}
            public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startListening() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO);
            return;
        }
        if (recognizer == null) setupRecognizer();
        if (recognizer == null) return;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        try { recognizer.startListening(intent); }
        catch (Exception e) { status.setText("माइक शुरू नहीं हुआ। ऐप दोबारा खोलें।"); }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_AUDIO && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupRecognizer();
            status.setText("माइक की अनुमति मिल गई — बोलें");
        } else status.setText("वॉइस फीचर के लिए माइक अनुमति ज़रूरी है।");
    }

    @Override protected void onDestroy() {
        if (recognizer != null) recognizer.destroy();
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
