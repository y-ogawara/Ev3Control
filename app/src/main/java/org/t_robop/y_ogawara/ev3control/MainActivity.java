package org.t_robop.y_ogawara.ev3control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.t_robop.y_ogawara.ev3control.ev3.AndroidComm;
import org.t_robop.y_ogawara.ev3control.ev3.EV3Command;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBtAdapter; // BTアダプタ
    private BluetoothDevice mBtDevice; // BTデバイス
    private BluetoothSocket mBtSocket; // BTソケット
    private OutputStream mOutput; // 出力ストリーム

    int STOP = 0;
    int FRONT = 1;
    int BACK = 2;
    int RIGHT = 3;
    int LEFT = 4;
    int TEST = 5;

    int i = 0;

    //00:16:53:44:69:AB   ev3 青
    //00:16:53:44:59:C0   ev3 緑
    //00:16:53:43:DE:A0   ev3 灰色
    String macAddress = "00:16:53:44:59:C0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerSetting();
    }
    //送信データの生成
    byte[] sendMessage(int num) {
        i++;
        byte[] tele = new byte[21];
        tele[0] = (byte)19;
        tele[1] = (byte)0;
        tele[2] = (byte)0;
        tele[3] = (byte)0;
        tele[4] = (byte)0x80;
        tele[5] = (byte)0;
        tele[6] = (byte)0;

        //止まるとき
        if (num == 0) {   //Stop Motors at PortC & D
            tele[7] = (byte)0xA4;     //OUTPUT_POWER
            tele[8] = (byte)0;
            tele[9] = (byte)2;     //Motor ID = PortC
            tele[10] = (byte)0;     //Motor Power
            tele[11] = (byte)0xA6;    //OUTPUT_START
            tele[12] = (byte)0;
            tele[13] = (byte)2;     //Motor ID = PortC

            tele[14] = (byte)0xA4;     //OUTPUT_POWER
            tele[15] = (byte)0;     //Layer master
            tele[16] = (byte)4;     //Motor ID = PortD
            tele[17] = (byte)0;     //Motor Power
            tele[18] = (byte)0xA6;    //OUTPUT_START
            tele[19] = (byte)0;     //Layer master
            tele[20] = (byte)4;     //Motor ID = PortD
        }

        //進むとき
        if (num == 1) {    //Forward Motors at PortC & D
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)0x02;
            tele[10] = (byte)0x1F; //速度
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0x00;
            tele[13] = (byte)0x02;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)0x04;
            tele[17] = (byte)0x1F;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0x00;
            tele[20] = (byte)0x04;
            Log.d("iの値は",i+"  です");
        }
        //バック
        if (num == 2) {    //Backward Motors at PortC & D
            Log.d("iの数は",i+"  です");
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)2;
            tele[10] = (byte)32;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)2;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)4;
            tele[17] = (byte)32;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)4;


        }

        //右回転
        if (num == 3) {    //Turn Right = Forward Motor at PortC(Left) and Stop PortD(Right)
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)2;
            tele[10] = (byte)0;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)2;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)4;
            tele[17] = (byte)68;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)4;
        }
        //左回転
        if (num == 4) {    //Turn Left = Forward Motor at PortD(Right) and Stop PortC(Left)
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)2;
            tele[10] = (byte)68;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)2;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)4;
            tele[17] = (byte)0;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)4;
        }
        //byte配列を返す
        return tele;
    }


    //アプリを終了した時
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ソケットを閉じる
        try {
            mBtSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void btn(View view) {
        //バイブレーションの宣言
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //20ミリセックバイブする
        vibrator.vibrate(50);
        switch (view.getId()){
            //前進するとき
            case R.id.front:
                sendBluetooth(1);
                break;
            //止まるとき
            case R.id.stop:
                sendBluetooth(0);
                break;
            //左回転するとき
            case R.id.left:
                sendBluetooth(4);
                break;
            //右回転するとき
            case R.id.right:
                sendBluetooth(3);
                break;
            //後ろに行くとき
            case R.id.back:
                sendBluetooth(2);
                break;
            //接続するとき
            case R.id.connect:
                connection();
                break;

        }
    }
    //Bluetooth接続先に送信
    void sendBluetooth(int num){
        //ここで送信
        try {
            //ここでBluetooth送信してる
            AndroidComm.mOutputStream.write(sendMessage(num));

            //mOutput.write(sendMessage(num));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    //接続を確立させる
    void connection(){



        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice device = mBtAdapter.getRemoteDevice(macAddress);

        AndroidComm.getInstance().setDevice(device); // Set device

// Connect to EV3
        try {
            EV3Command.open();
            Toast.makeText(MainActivity.this, "接続成功！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "エラーです", Toast.LENGTH_LONG).show();
            //接続が失敗したらnullに
            mBtAdapter = null;
        }






//        // BTの準備 --------------------------------------------------------------
//        // BTアダプタのインスタンスを取得
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        // 相手先BTデバイスのインスタンスを取得
//        mBtDevice = mBluetoothAdapter.getRemoteDevice(macAddress);
//
//        // BTソケットのインスタンスを取得
//        try {
//            // 接続に使用するプロファイルを指定
//            mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(
//                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //接続に成功したかどうか
//        int connectFlag = 0;
//        try {
//            // ソケットを接続する
//            mBtSocket.connect();
//            mOutput = mBtSocket.getOutputStream(); // 出力ストリームオブジェクトを得る
//        } catch (IOException e) {
//            Toast.makeText(this, "接続に失敗しました", Toast.LENGTH_LONG).show();
//            connectFlag = 1;
//            e.printStackTrace();
//        }finally {
//            if (connectFlag == 0){
//                Toast.makeText(this, "接続に成功！！！", Toast.LENGTH_LONG).show();
//            }
//        }
    }
    //spinner設定用メソッド
    void spinnerSetting (){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        adapter.add("00:16:53:44:59:C0,eb3緑");
        adapter.add("00:16:53:44:69:AB,ev3青");
        adapter.add("00:16:53:43:DE:A0,ev3灰色");

        //スピナーの関連付け
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // アダプターを設定します
        spinner.setAdapter(adapter);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //スピナーを宣言
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得
                String item = (String) spinner.getSelectedItem();
                // , で文字を分割して保存
                String addressArray[] = item.split(",",0);
                //macAddressにaddressを入れる
                macAddress =addressArray[0];
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
    byte[] createByte(){
        Log.d("iは",i+ "です");
        byte[] msg = new byte[18];
        msg[0] = (byte)16;
        msg[1] = (byte)0;
        msg[2] = (byte)0;//outputスピード
        msg[3] = (byte)0;//レイヤーmaster
        msg[4] = (byte)0x80;//
        msg[5] = (byte)0;
        msg[6] = (byte)0;

        msg[7] = (byte)0xA4;     //OUTPUT_POWER
        msg[8] = (byte)0x00;     //Layer master
        msg[9] = (byte)6;     //Motor ID = PortD
        msg[10] = (byte)0x00;     //Motor Power

        msg[11] = (byte)0xA6;
        msg[12] = (byte)0x000;
        msg[13] = (byte)6;

        msg[14] = (byte)0xA5;
        msg[15] = (byte)0x00;
        msg[16] = (byte)6;
        //msg[17] = (byte)0x81;
        msg[17] = (byte)31;


//        msg[14] = (byte)0xA4;
//        msg[15] = (byte)0x00;
//        msg[16] = (byte)4;
//        msg[17] = (byte)100;
//        msg[18] = (byte)0xA6;
//        msg[19] = (byte)0;
//        msg[20] = (byte)4;
        return msg;
    }
//    public void test(View v ){
//        //ここで送信
//        try {
//            //ここでBluetooth送信してる
//            AndroidComm.mOutputStream.write(speedByte());
//
//            //mOutput.write(sendMessage(num));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
    byte[] stopByte(){
        Log.d("iは",i+ "です");
        byte[] msg = new byte[11];
        msg[0] = (byte)9;
        msg[1] = (byte)0;//空白
        msg[2] = (byte)0;//空白
        msg[3] = (byte)0;//レイヤーmaster
        msg[4] = (byte)0x80;//NO_Reply
        msg[5] = (byte)0;//空白
        msg[6] = (byte)0;//空白

        msg[7] = (byte)0xA3;     //OUTPUT_STOP
        msg[8] = (byte)0;     //Layer master
        msg[9] = (byte)6;     //Motor ID = PortB+C
        msg[10] = 1;     //break
        return msg;
    }
    byte[] speedByte(){
        byte[] msg = new byte[16];
        msg[0] = (byte)14;
        msg[1] = (byte)0x00;//空白
        msg[2] = (byte)0x00;//空白
        msg[3] = (byte)0x00;//レイヤーmaster
        msg[4] = (byte)0x80;//NO_Reply
        msg[5] = (byte)0x00;//空白
        msg[6] = (byte)0x00;//空白

        msg[7] = (byte)0xA5;
        msg[8] = (byte)0x00;
        msg[9] = (byte)6;


        msg[10] = (byte)0x81;     //
        msg[11] = (byte)31;     //Layer master
        msg[12] = (byte)0xA6;     //Motor ID = PortB+C
        msg[14] = (byte)0x00;
        msg[15] = (byte)2;

        return msg;
    }
}