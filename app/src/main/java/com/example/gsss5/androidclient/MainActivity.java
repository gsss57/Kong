package com.example.gsss5.androidclient;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.String;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    BufferedReader mReader;
    BufferedWriter mWriter;
    TextView textResponse,textMessageBox,myInputText,myInputFile ;
    EditText editTextAddress, editTextPort,mEditSend;
    ImageView img;
    Socket socket = null;
    String message = "";
    int bodysize=0;
    String strSend="";
    String dstAddress;
    int dstPort;
    String response = "";
    String myData;
    String myData2;
    String path;
    String header="";
    FileInputStream fis;
    DataInputStream dis;
    DataOutputStream dos;
    private String TestFile = "sample.jpg";
    private String filepath = "MyFile";
    File ExtFile;
    byte[] b;
    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), TestFile);
    int current;
    private boolean connected = false;
    int filesize;
    int cnt=0;
    int num[];
    // byte[] buf;
    int test=0;
    String pl="";
    int strToInt =0;
    String intToStr="ab";
    byte[] buffer = new byte[1];
    byte [] mybytearray  = new byte [5];
    OutputStream os;
    public final static int FILE_SIZE = Integer.MAX_VALUE;
    Drawable draw_image;
    MyClientTask myClientTask;
    InputStream inputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        mEditSend= (EditText)findViewById(R.id.message);

        textResponse = (TextView)findViewById(R.id.response);
        textMessageBox = (TextView)findViewById(R.id.messageBox);
        myInputText = (TextView)findViewById(R.id.inputMessageBox);
        img=(ImageView)findViewById(R.id.icon);
        myInputFile = (TextView)findViewById(R.id.inputFileBox);
        ContextWrapper contextWrapper = new ContextWrapper( getApplicationContext());
        File directory = contextWrapper.getDir(filepath, Context.MODE_PRIVATE);
        Button save = (Button) findViewById(R.id.saveFile);
        //  save.setOnClickListener(R.id.save);
        Button get = (Button) findViewById(R.id.getFile);
        //  get.setOnClickListener(this);
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            save.setEnabled(false);
            Log.d("tag", "file save error.");}
        else {
            ExtFile = new File(getExternalFilesDir(filepath), TestFile); }
    }

    public void onBtnConnect() {
        try {
           myClientTask = new MyClientTask(
                    editTextAddress.getText().toString(),
                    Integer.parseInt(editTextPort.getText().toString()),mEditSend.getText().toString());
            myClientTask.execute();

        } catch(Exception e) {
            Log.d("tag", "connect error.");
        }
    }
    public void onBtnClose() {
        try {
            socket.close();
        } catch(Exception e) {
            Log.d("tag", "close error.");
        }
    }
    public void onBtnSend() {
        try {
            // EditText 에서 메시지를 구한 다음 기존 메시지를 삭제한다

            strSend = mEditSend.getText().toString();
            mWriter.write(strSend);
            mWriter.flush();
            mEditSend.setText("");
            // 서버로 메시지 전송
            Update();
        } catch(Exception e) {
            Log.d("tag", "Data send error.");
        }
    }
    public void onBtnClear() {
        try {
            textResponse.setText("");
        } catch(Exception e) {
            Log.d("tag", "clear error.");
        }
    }
    public void Update() {
        try {
            intToStr=Integer.toString(bodysize);
            textMessageBox.setText(intToStr);
        } catch(Exception e) {
            Log.d("tag", "update error.");
        }
    }
    public void fileUpload(View view) {
        Intent intent = new Intent(this, FileActivity.class);
        startActivityForResult(intent, 0);
    }
    public void onBtnFileUpload() {
        try {
            fis = new FileInputStream(path);
            int n = 0;
            while((n = fis.read(buffer)) != -1) {
                dos.write(buffer);
            }
            fis.close();
        } catch(Exception e) {
            Log.d("tag", "fileupload error.");
        }
    }
    public void onBtnFileSave() {
        try {
            OutputStream os = new FileOutputStream(file);
            //   os.write(textMessageBox.getText().toString().getBytes());
            os.write(b);
            os.close(); }
        catch (IOException e) {
            e.printStackTrace(); }
    }
    public void onBtnFileGet() {
        try {
            FileInputStream fis = new
                    FileInputStream(ExtFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader( new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                myData = myData + strLine; } in.close(); }
        catch (IOException e) { e.printStackTrace(); }
        myInputText.setText(myData);
    }
    public void onBtnFileSaveImage() {
        try {
            FileOutputStream fos2 = new FileOutputStream(ExtFile);
            String strLine="";
            for(int i=0;i<7000;i++) {
                strLine=strLine+mReader.read();
            }
            fos2.write(strLine.getBytes());
            fos2.close(); }
        catch (IOException e) {
            e.printStackTrace(); }
    }
    public void onBtnFileGetImage() {
        try {
            FileInputStream fis2 = new
                    FileInputStream(ExtFile);
            DataInputStream in2 = new DataInputStream(fis2);
            BufferedReader br2 = new BufferedReader( new InputStreamReader(in2));
            String strLine2;
            while ((strLine2 = br2.readLine()) != null) {
                myData2 = myData2+ strLine2; } in2.close(); }
        catch (IOException e) { e.printStackTrace(); }
        myInputFile.setText(myData2);
    }
    public void onClick(View v) {
        switch( v.getId() ) {
            case R.id.connect:
                // 접속 시작
                onBtnConnect();
                break;
            case R.id.close:
                // 접속 시작
                onBtnClose();
                break;
            case R.id.send:
                // 접속 시작
                onBtnSend();
                break;
            case R.id.clear:
                // 접속 시작
                onBtnClear();
                break;
            case R.id.fileSend:
                // 접속 시작
                onBtnFileUpload();
                break;
            case R.id.saveFile:
                // 접속 시작
                onBtnFileSave();
                break;
            case R.id.getFile:
                // 접속 시작
                onBtnFileGet();
                break;
        }}
    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true; }
        return false; }
    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        } return false;
    }

    public int byteArrayToInt(byte[] b)
    {
        return   b[3] & 0xFF << 24|
                (b[2] & 0xFF) << 16 |
                (b[1] & 0xFF) << 8 |
                (b[0] & 0xFF) ;
    }
    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        MyClientTask(String addr, int port, String msg) {
            dstAddress = addr;
            dstPort = port;
            strSend = msg;
        }
        @Override
        protected void onPreExecute() {
        //    textResult.setText("Background 작업 시작 ");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
            while(true){
                socket = new Socket(dstAddress, dstPort);

                mWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                mReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                 /*
     * notice:
     * inputStream.read() will block if no data return
     */
//오류볼때만 사용
                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                FileOutputStream fos = new FileOutputStream(file.getPath());
                os = new FileOutputStream(file.getPath());
                inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                FileInputStream fis = new FileInputStream(file.getPath());
                while(true) {
                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), TestFile);

                    inputStream.read(mybytearray, 0, 5);
                    header = new String(mybytearray, 0, 5);
                    bodysize = Integer.parseInt(header);
                    int readsize = 0;
                    int rsize = 0;
                    // read body
                    int n = 0;
                    if (bodysize != 0) {
                        mWriter.write("filesizeok");
                        mWriter.flush();

                        // 서버로 메시지 전송
                        publishProgress();
                    }
                /*
                while(readsize < bodysize){
                    n=dis.read(buffer);
                    fos.write(buffer,0,n);
                }
                /*
                while((n=dis.read(buffer))!=-1){
                    fos.write(buffer,0,n);
                }
                */
/*
                while(readsize < bodysize)
                {
                   //while((rsize=inputStream.read(buffer))!=-1)
                    rsize = inputStream.read(buffer);
                    byteArrayOutputStream.write(buffer,0,rsize);
                    fos.write(buffer);
                    readsize ++;
                }
*/


                    while (readsize < bodysize) {
                        rsize = inputStream.read(buffer);
                        byteArrayOutputStream.write(buffer, 0, rsize);
                        os.write(buffer, 0, rsize);
                        readsize++;

                    }
                    draw_image = Drawable.createFromPath(file.getPath());
                    if (draw_image != null) {

                        mWriter.write("filesizeok");
                        mWriter.flush();

                        // 서버로 메시지 전송

                        publishProgress();
                   draw_image.clearColorFilter();
                    }

                    for (int i = 0; i < buffer.length; i++) {
                        buffer[i] = 0x00;
                    }
                    for (int i = 0; i < mybytearray.length; i++) {
                        mybytearray[i] = 0x00;
                    }
                    //  file.createNewFile();
                    readsize = 0;
                    rsize = 0;
                    // read body
                    n = 0;
                  //  boolean f=file.delete();
                    //file.delete();
                }
                //os.flush();
                //   inputStream.reset();
                //  Update();

                //   mReader.close();
            }
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {

                        socket.close();
                        mReader.close();
                        mWriter.close();


                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            textResponse.setText(response);
            intToStr=Integer.toString(bodysize);
            textMessageBox.setText(intToStr);
            draw_image = Drawable.createFromPath(file.getPath());
            img.invalidateDrawable(draw_image);
            img.jumpDrawablesToCurrentState();
            img.setImageDrawable(draw_image);
          //  img.invalidate();
            super.onPostExecute(result);

            myClientTask.execute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            //      strToInt =Integer.parseInt(message);
            intToStr=Integer.toString(bodysize);
            textMessageBox.setText(intToStr);
            // Update();
            draw_image = Drawable.createFromPath(file.getPath());
            img.invalidateDrawable(draw_image);
            img.jumpDrawablesToCurrentState();
            img.setImageDrawable(draw_image);
            img.jumpDrawablesToCurrentState();
            super.onProgressUpdate(values);
            // b = message.getBytes();
            // onBtnFileSave();
            //onBtnFileGet();
            //   onBtnFileSaveImage();
            //  onBtnFileGetImage();
        }
    }


}
