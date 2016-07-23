package org.t_robop.y_ogawara.ev3control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter; // BTアダプタ
    private BluetoothDevice mBtDevice; // BTデバイス
    private BluetoothSocket mBtSocket; // BTソケット
    private OutputStream mOutput; // 出力ストリーム
    //private Button btn1; // 送信ボタン
    //byte[] tele;

    //00:16:53:44:69:AB   ev3 青
    //00:16:53:44:59:C0
    String macAddress = "00:16:53:44:59:C0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerSetting();
        connection();

    }
    byte[] sendMessage(int num)
    {
        byte[] tele = new byte[21];
        //tele = new byte[21];
        tele[0] = (byte)19;
        tele[1] = (byte)0;
        tele[2] = (byte)0;
        tele[3] = (byte)0;
        tele[4] = (byte)0;
        tele[5] = (byte)0;
        tele[6] = (byte)0;
//        tele[7] = (byte)0;     //OUTPUT_POWER
//        tele[8] = (byte)0;
//        tele[9] = (byte)0;     //Motor ID = PortC
//        tele[10] = (byte)0;     //Motor Power
//        tele[11] = (byte)0;    //OUTPUT_START
//        tele[12] = (byte)0;
//        tele[13] = (byte)0;     //Motor ID = PortC
//        tele[14] = (byte)0;     //OUTPUT_POWER
//        tele[15] = (byte)0;
//        tele[16] = (byte)0;     //Motor ID = PortD
//        tele[17] = (byte)0;     //Motor Power
//        tele[18] = (byte)0;    //OUTPUT_START
//        tele[19] = (byte)0;
//        tele[20] = (byte)0;


        if (num == 0) {   //Stop Motors at PortC & D
            tele[7] = (byte)0xA4;     //OUTPUT_POWER
            tele[8] = (byte)0;
            tele[9] = (byte)4;     //Motor ID = PortC
            tele[10] = (byte)0;     //Motor Power
            tele[11] = (byte)0xA6;    //OUTPUT_START
            tele[12] = (byte)0;
            tele[13] = (byte)4;     //Motor ID = PortC

            tele[14] = (byte)0xA4;     //OUTPUT_POWER
            tele[15] = (byte)0;
            tele[16] = (byte)8;     //Motor ID = PortD
            tele[17] = (byte)0;     //Motor Power
            tele[18] = (byte)0xA6;    //OUTPUT_START
            tele[19] = (byte)0;
            tele[20] = (byte)8;     //Motor ID = PortD
        }

        if (num == 1) {    //Forward Motors at PortC & D
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)68;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)68;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)8;
        }
        if (num == 2) {    //Backward Motors at PortC & D
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)40;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)40;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)9;
        }

        if (num == 3) {    //Turn Right = Forward Motor at PortC(Left) and Stop PortD(Right)
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)0;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)70;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)8;
        }

        if (num == 4) {    //Turn Left = Forward Motor at PortD(Right) and Stop PortC(Left)
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)70;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)0;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)8;
        }

        //Log.d("sending","");
        return tele;
    }
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
        switch (view.getId()){
            case R.id.front:
                sendBluetooth(1);
                break;
            case R.id.stop:
                sendBluetooth(0);
                break;
            case R.id.left:
                sendBluetooth(4);
                break;
            case R.id.right:
                sendBluetooth(3);
                break;
            case R.id.back:
                sendBluetooth(2);
                break;
            case R.id.connect:
                connection();
                break;
        }
    }
    void sendBluetooth(int num){
        //ここで送信
        try {
            //Log.d("aaaaaaaaaaaaaaaaaa",String.valueOf(sendMessage(num)));
            //sendMessage(num);
            mOutput.write(sendMessage(num));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    void connection(){
        // BTの準備 --------------------------------------------------------------
        // BTアダプタのインスタンスを取得
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 相手先BTデバイスのインスタンスを取得
        mBtDevice = mBluetoothAdapter.getRemoteDevice(macAddress);

        // BTソケットのインスタンスを取得
        try {
            // 接続に使用するプロファイルを指定
            mBtSocket = mBtDevice.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int connectFlag = 0;
        // ソケットを接続する
        try {

            mBtSocket.connect();
            mOutput = mBtSocket.getOutputStream(); // 出力ストリームオブジェクトを得る
        } catch (IOException e) {
            Toast.makeText(this, "接続に失敗しました", Toast.LENGTH_LONG).show();
            connectFlag = 1;
            e.printStackTrace();
        }finally {
            if (connectFlag == 0){
                Toast.makeText(this, "接続に成功！！！", Toast.LENGTH_LONG).show();

            }
        }
    }
    void spinnerSetting (){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        adapter.add("red");
        adapter.add("green");
        adapter.add("blue");
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // アダプターを設定します
        spinner.setAdapter(adapter);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                Toast.makeText(MainActivity.this, item, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        adapter.add("00:16:53:44:59:C0,eb3青");
        adapter.add("00:16:53:44:69:AB,ev3緑");
        adapter.add("blue");
        spinner = (Spinner) findViewById(R.id.spinner);
        // アダプターを設定します
        spinner.setAdapter(adapter);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                String item = (String) spinner.getSelectedItem();
                Log.d("aaaaaaaaaaaaa",item);
                Toast.makeText(MainActivity.this, item, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                
            }
        });
    }
}
