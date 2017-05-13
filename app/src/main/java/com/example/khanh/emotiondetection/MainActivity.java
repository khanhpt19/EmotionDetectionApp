package com.example.khanh.emotiondetection;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.contract.Scores;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnProcess,btnLoad;
    int CODE=100;
    Bitmap mbitmap;
    public EmotionServiceClient emotionServiceClient = new EmotionServiceRestClient("17160e4f9907442c8ecd04d53fe5bfdf");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btnLoad= (Button) findViewById(R.id.btnLoad);
        btnProcess= (Button) findViewById(R.id.btnProcess);
        imageView= (ImageView) findViewById(R.id.imageView);

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                process();
            }
        });
    }

    private void process() {
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        mbitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        ByteArrayInputStream inputStream= new ByteArrayInputStream(outputStream.toByteArray());

        AsyncTask<InputStream,String ,List<RecognizeResult>>processAsync=new AsyncTask<InputStream, String, List<RecognizeResult>>() {
          ProgressDialog mDialog= new ProgressDialog(MainActivity.this);

            @Override
            protected void onPreExecute() {
                mDialog.show();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                mDialog.setMessage(values[0]);
            }

            @Override
            protected List<RecognizeResult> doInBackground(InputStream... params) {
                publishProgress("Please wait..");
                List<RecognizeResult>result=null;
                try{
                    result=emotionServiceClient.recognizeImage(params[0]);
                }catch(EmotionServiceException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<RecognizeResult> recognizeResults) {
                mDialog.dismiss();
                for(RecognizeResult res:recognizeResults){
                    String status  =getEmotion(res);
                    imageView.setImageBitmap(ImageHelper.drawRectOnBitmap(mbitmap,res.faceRectangle,status));
                }
            }
        };

        processAsync.execute(inputStream);
    }

    private String getEmotion(RecognizeResult res) {
        List<Double>list=new ArrayList<>();
        Scores scores= res.scores;
        list.add(scores.anger);
        list.add(scores.contempt);
        list.add(scores.disgust);
        list.add(scores.fear);
        list.add(scores.happiness);
        list.add(scores.neutral);
        list.add(scores.sadness);
        list.add(scores.surprise);

        Collections.sort(list);

        double max=list.get(list.size()-1);

        if(max==scores.anger)return "Anger";
        else if(max==scores.happiness)return "Happy";
        else if(max==scores.contempt)return "Contempt";
        else if(max==scores.disgust)return "Disgust";
        else if(max==scores.fear)return "Fear";
        else if(max==scores.neutral)return "Neutral";
        else if(max==scores.sadness)return "Sad";
        else if(max==scores.surprise)return "Surprise";
        else return "Can't detect";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==CODE){
//            Uri selectImage=data.getData();
//            InputStream in=null;
//            try{
//                in=getContentResolver().openInputStream(selectImage);
//
//            }catch(FileNotFoundException e){
//                e.printStackTrace();
//            }
//            mbitmap= BitmapFactory.decodeStream(in);
//            imageView.setImageBitmap(mbitmap);

            if(mbitmap != null){
                mbitmap.recycle();
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                mbitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                imageView.setImageBitmap(mbitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void load() {
        Intent intent= new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,CODE);
    }
}
