package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
private TextToSpeech mtts;
private Button speak;
private TextView text;
private  TextView cloudText;
private EditText textSpeed;
private Button share;
private ImageButton search;
private EditText searchText;
private  Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speak = findViewById(R.id.speak);
        share = findViewById(R.id.share);
        save = findViewById(R.id.save);
        search = findViewById(R.id.search);
        searchText = findViewById(R.id.searchText);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String  searchTextStr = searchText.getText().toString();
              String corpora = text.getText().toString();
              if (corpora.isEmpty()){
                  Toast.makeText(MainActivity.this , "Please take a clear photo and try again.",Toast.LENGTH_LONG).show();
                            return;
              }

                if (searchTextStr.isEmpty()){
                    Toast.makeText(MainActivity.this , "Enter keyword to be searched",Toast.LENGTH_LONG).show();
                            return;
                }


                if(!corpora.contains(searchTextStr)){
                    Toast.makeText(MainActivity.this , "Keyword not found",Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(MainActivity.this , "Keyword found! ",Toast.LENGTH_LONG).show();
                    String newString = corpora.replaceAll(searchTextStr, "<b><font color='yellow'>"+searchTextStr+"</font></b>");
                    text.setText(Html.fromHtml(newString));


                }

            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        Button ret;
        textSpeed = findViewById(R.id.speed);

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




        save.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (!text.getText().toString().isEmpty()) {

               File file = new File(MainActivity.this.getFilesDir(), "text");
               if (!file.exists()) {
                  file.mkdir();
               }
               try {
                  File gpxfile = new File(file, "adz0612adz.txt");
                  FileWriter writer = new FileWriter(gpxfile);
                  writer.append(text.getText().toString());
                  writer.flush();
                  writer.close();
                  Toast.makeText(MainActivity.this, "Saved your text", Toast.LENGTH_LONG).show();
               } catch (Exception e) { }
            }
             return;
         }
      });









        share.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        String shareText = text.getText().toString();
                                        if (shareText.isEmpty()){
                                            Toast.makeText(MainActivity.this, "Please take a clear photo and try again.", Toast.LENGTH_LONG).show();

                                            return;
                                        }


                                        Intent myintent = new Intent(Intent.ACTION_SEND);

                                        myintent.setType("text/plain");
                                        String sharebody = ""+ shareText;
                                        String sharesub = "My note";
                                        myintent.putExtra(Intent.EXTRA_TEXT, sharebody);
                                        myintent.putExtra(Intent.EXTRA_SUBJECT, sharesub);
startActivity(Intent.createChooser(myintent,"Share Note Text using..."));

                                    }
                                });









        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String speedStr =   textSpeed.getText().toString();
              if (speedStr.isEmpty()){
                  Toast.makeText(MainActivity.this , " Please enter speed of speech " , Toast.LENGTH_LONG).show();
                  return;
                }
              int speedInt = Integer.parseInt(speedStr);
                speakText(speedInt);
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


                // <! ------               On Device BASED MODEL        STARTS here                ----->

                Bitmap image1 = (Bitmap)data.getExtras().get("data");
                FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(image1);

                FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance()
                        .getOnDeviceTextRecognizer();





                textRecognizer.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText result) {
                                Toast.makeText(MainActivity.this , "Text Extracted" ,Toast.LENGTH_SHORT).show();
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



                // <! ------               On Device BASED MODEL     ENDS here                   ----->






                // <! ------               CLOUD BASED MODEL          STARTS here              ----->
/*
                FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                        .getCloudTextRecognizer();


                        detector.processImage(image)
                                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                                    @Override
                                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                        Toast.makeText(MainActivity.this , " Boom" ,Toast.LENGTH_SHORT).show();
                                        String cloudStr = firebaseVisionText.getText();
                                        cloudText.setMovementMethod(new ScrollingMovementMethod());
                                        cloudText.setText(cloudStr);


                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Task failed with an exception
                                                // ...
                                            }
                                        });


*/


                // <! ------               CLOUD BASED MODEL       ENDS here                   ----->










            }

            else if (resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this , "Cancelled" ,Toast.LENGTH_SHORT).show();
            }
        }

    }
    private void speakText(int speed){
        try {
            String textToBeSpoken = text.getText().toString();
            textToBeSpoken = textToBeSpoken.replace('\n' , ' ');
            float speechRate = (float)1.0 + (speed)/10;
            float pitch = (float)1.1;


            mtts.setSpeechRate(speechRate);
            mtts.setPitch(pitch);

            mtts.speak(textToBeSpoken,TextToSpeech.QUEUE_FLUSH,null);
        }
        catch (Exception e){
            Toast.makeText(MainActivity.this , "Please take a clear photo and try again." ,Toast.LENGTH_SHORT).show();

        }



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

