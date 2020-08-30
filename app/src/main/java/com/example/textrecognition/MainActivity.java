package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
private TextToSpeech mtts;
private Button speak;
private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speak = findViewById(R.id.speak);

        Button ret;


mtts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
           int res =  mtts.setLanguage(Locale.ENGLISH);

           if (res == TextToSpeech.LANG_MISSING_DATA || res ==TextToSpeech.LANG_NOT_SUPPORTED){
               Toast.makeText(MainActivity.this , "Language not available!",Toast.LENGTH_LONG).show();


           }else{

               speak.setEnabled(true);
           }

        }


    }
});



speak.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        speakText();
    }
});



        ret = findViewById(R.id.button);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent ,10);

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 10)
        {
            if(resultCode == RESULT_OK){

                Bitmap image1 = (Bitmap)data.getExtras().get("data");
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(image1);

                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();






                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                Toast.makeText(MainActivity.this , " Boom" ,Toast.LENGTH_SHORT).show();
                                String resultText = result.getText();

                                text = findViewById(R.id.textView);
                                text.setMovementMethod(new ScrollingMovementMethod());
                                text.setText(resultText);

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this , " Task Failed" ,Toast.LENGTH_SHORT).show();
                                    }
                                });








            }

            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this , "Cancelled" ,Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void speakText(){

        String textToBeSpoken = text.getText().toString();
       textToBeSpoken = textToBeSpoken.replace('\n' , ' ');
        float speechRate = (float)1.2;
        float pitch = (float)1.1;


        mtts.setSpeechRate(speechRate);
        mtts.setPitch(pitch);

        mtts.speak(textToBeSpoken,TextToSpeech.QUEUE_FLUSH,null);

    }

    @Override
    protected void onDestroy() {
        if(mtts != null){
            mtts.stop();
            mtts.shutdown();
        }
        super.onDestroy();
    }
}

