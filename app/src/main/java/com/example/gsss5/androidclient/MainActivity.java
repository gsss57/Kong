package com.example.gsss5.androidclient;
import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
import java.io.PrintWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
        import java.net.UnknownHostException;

        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
import android.util.Log;
import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

public class MainActivity extends Activity {

    TextView textResponse,textMessageBox ;
    EditText editTextAddress, editTextPort,mEditSend;
    Socket socket = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextAddress = (EditText)findViewById(R.id.address);
        editTextPort = (EditText)findViewById(R.id.port);
        mEditSend= (EditText)findViewById(R.id.message);
        textResponse = (TextView)findViewById(R.id.response);
        textMessageBox = (TextView)findViewById(R.id.messageBox);

    }

    public void onBtnConnect() {
        try {
            MyClientTask myClientTask = new MyClientTask(
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
            MyClientTask myClientTask = new MyClientTask(
                    editTextAddress.getText().toString(),
                    Integer.parseInt(editTextPort.getText().toString()),mEditSend.getText().toString());
            myClientTask.execute();
            mEditSend.setText("");
            // 서버로 메시지 전송
            //    PrintWriter out = new PrintWriter(mWriter, true);
            //  out.println(strSend);
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
           }}
    public class MyClientTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";
        String strSend ;
        MyClientTask(String addr, int port,String msg){
            dstAddress = addr;
            dstPort = port;
           strSend=msg;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();

    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }
                DataOutputStream  dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                new Receiver(dis).start();

                while(true) {

                    dos.writeUTF(strSend);
                }
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
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
            super.onPostExecute(result);
        }

    }
    class Receiver extends Thread {
        private DataInputStream dis;

        public Receiver(DataInputStream dis) {
            this.dis = dis;
        }

        @Override
        public void run() {
            super.run();

            try {
                while(true) {
                    String message = dis.readUTF();
                    textMessageBox.setText(message);
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

