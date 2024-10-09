package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class BuildingModelActivity extends AppCompatActivity {

    // final String[] layer_color_list={"#e6e6ff", "#e0e0ff", "#dbdbff","#d6d6ff", "#d1d1ff", "#ccccff", "#c7c7ff", "#c2c2ff", "#bdbdff","#b8b8ff"};

    final String[] this_data = {"MNIST"};
    private LinearLayout layout;
    int numLayer=0;
    int epoch_send=3;
    int batch_size_send=5;
    int numConvLayer=0;
    private final int layer_id=0;
    Vector<Integer> layertype=new Vector<Integer>();
    Vector<Integer> node_num=new Vector<Integer>();
    Vector<Integer> activation_func=new Vector<Integer>();
    Vector<Integer> kernel_size=new Vector<Integer>();
    Vector<Integer> pooling=new Vector<Integer>();
    Vector<Integer> unable_conv=new Vector<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildingmodel);

        // "데이터 선택" 버튼을 눌렀을 때
        // 현재 데이터명(this_data[0])을 들고 DataSelectingActivity.class로 이동
        // DataSelectingActivity.class에서 받은 값을 this_data[0]에 저장 (onActivityResult() 참고)
        Button data_selection=(Button) findViewById(R.id.data_selection);
        data_selection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), DataSelectingActivity.class);
                intent.putExtra("dataname",this_data[0]);
                startActivityForResult(intent, 201);
            }
        });

        // "레이어 추가" 버튼을 눌렀을 때
        // addLayer() 실행
        Button add_layer=(Button)findViewById(R.id.add_layer);
        add_layer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                layout=(LinearLayout)findViewById(R.id.layout);
                addLayer();
            }
        });

        // "레이어 삭제" 버튼을 눌렀을 때
        // deleteLayer() 실행
        Button delete_layer=(Button)findViewById(R.id.delete_layer);
        delete_layer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                layout=(LinearLayout)findViewById(R.id.layout);
                deleteLayer();
            }
        });


        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("1. 상위의 탭 중 ‘데이터 선택’탭을 클릭하여 사용할 데이터의 종류를 선택해줍니다.\n" +
                "2. 데이터를 선택해주면 학습 반복횟수와 Batch 크기를 입력할 수 있는 칸이 나오며, 해당 칸에 사용자가 원하는 값을 입력해줍니다.\n" +
                "3. 후에 학습 시킬 모델을 구성해주기 위해 상위의 탭 중에 ‘레이어 추가’를 클릭하여 줍니다.\n" +
                "4. 추가된 레이어를 클릭하면 해당 레이어를 세부적으로 설정할 수 있습니다. \n" +
                "5. 원한다면 상위의 탭 중 ‘레이어 삭제’를 클릭하여 레이어를 제거해줄 수 있습니다.\n" +
                "6. 모델 구성이 완료되면 상위의 탭 중 ‘모델실행’탭을 클릭하여 줍니다. 클릭하면 사용자가 구성한 모델이 정리되어 나타납니다.\n" +
                "7. 사용자가 구성한 모델이 맞다면 실행시켜줍니다.\n");
        alert_confirm.setPositiveButton("확인", null);
        final AlertDialog alert = alert_confirm.create();
        alert.setTitle("App 사용방법");

        Button explanation_building_model=(Button)findViewById(R.id.explanation_building_model);
        explanation_building_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.show();
            }
        });

        // Epoch와 Batch
        final EditText epoch=(EditText)findViewById(R.id.epoch);
        final EditText batch_size=(EditText)findViewById(R.id.batch_size);

        // "모델실행" 버튼을 눌렀을 때
        // 모델 설정값들을 정리해서 JSON 형태의 데이터로 변경한 후 Http 통신을 시도함
        Button run_model=(Button)findViewById(R.id.run_model);
        run_model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numLayer==0){
                    Toast.makeText(getApplicationContext(),"레이어를 추가하세요.", Toast.LENGTH_LONG).show();
                }else{
                    epoch_send=Integer.parseInt(epoch.getText().toString());
                    batch_size_send=Integer.parseInt(batch_size.getText().toString());
                    if(epoch_send>3){
                        Toast.makeText(getApplicationContext(),"현재 시스템 상 학습 반복횟수는 3까지만 허용됩니다.", Toast.LENGTH_LONG).show();
                    }else if(batch_size_send>10){
                        Toast.makeText(getApplicationContext(),"현재 시스템 상 Batch 크기는 10까지만 허용됩니다.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        JSONObject jObj = new JSONObject();
                        System.out.println("test numLayer: "+numLayer);
                        System.out.println("test layertype: "+layertype);
                        System.out.println("test node_num: "+node_num);
                        System.out.println("test activation_func: "+activation_func);
                        System.out.println("test kernel size: "+kernel_size);
                        System.out.println("test pooling: "+pooling);
                        System.out.println("test unable_conv: "+unable_conv);
                        // param_conv, dense layer 조정
                        String param_conv="";
                        String param_dense="";
                        String param_dense_act="";
                        if(layertype.get(0)==1){
                            if(numLayer==1){
                                // convolution layer의 filter 크기 조정
                                if(kernel_size.get(0)==1){
                                    param_conv="3";
                                }else{
                                    param_conv="5";
                                }
                            }else{
                                if(layertype.get(1)==2){
                                    //두 번째 레이어부터 fully connected layer
                                    //fully connected layer node수, activation function 조정
                                    param_dense=Integer.toString(node_num.get(1));
                                    if(activation_func.get(1)==0){
                                        param_dense_act="softmax";
                                    }else{
                                        param_dense_act="relu";
                                    }
                                    if(numLayer>2){
                                        for(int i=2;i<numLayer;i++){
                                            param_dense=param_dense+","+Integer.toString(node_num.get(i));
                                            if(activation_func.get(i)==0){
                                                param_dense_act=param_dense_act+",softmax";
                                            }else{
                                                param_dense_act=param_dense_act+",relu";
                                            }
                                        }
                                    }
                                    // convolution layer의 filter 크기 조정
                                    if(kernel_size.get(0)==1){
                                        param_conv="3";
                                    }else{
                                        param_conv="5";
                                    }
                                }else{
                                    //세 번째 레이어부터 fully connected layer
                                    //fully connected layer node수, activation function 조정
                                    if(numLayer>2){
                                        param_dense=Integer.toString(node_num.get(2));
                                        if(activation_func.get(2)==0){
                                            param_dense_act="softmax";
                                        }else{
                                            param_dense_act="relu";
                                        }
                                        if(numLayer>3){
                                            for(int i=3;i<numLayer;i++){
                                                param_dense=param_dense+","+Integer.toString(node_num.get(i));
                                                if(activation_func.get(i)==0){
                                                    param_dense_act=param_dense_act+",softmax";
                                                }else{
                                                    param_dense_act=param_dense_act+",relu";
                                                }
                                            }
                                        }
                                    }
                                    if(kernel_size.get(0)==1){
                                        param_conv="3";
                                    }else{
                                        param_conv="5";
                                    }
                                    if(kernel_size.get(1)==1){
                                        param_conv=param_conv+",3";
                                    }else{
                                        param_conv=param_conv+",5";
                                    }
                                }
                            }
                        }else{
                            //첫 번째 레이어부터 fully connected layer
                            //fully connected layer node수, activation function 조정
                            param_dense=Integer.toString(node_num.get(0));
                            if(activation_func.get(0)==0){
                                param_dense_act="softmax";
                            }else{
                                param_dense_act="relu";
                            }
                            if(numLayer>1){
                                for(int i=1;i<numLayer;i++){
                                    param_dense=param_dense+","+Integer.toString(node_num.get(i));
                                    if(activation_func.get(i)==0){
                                        param_dense_act=param_dense_act+",softmax";
                                    }else{
                                        param_dense_act=param_dense_act+",relu";
                                    }
                                }
                            }
                        }
                        try{
                            jObj.put("batch", Integer.toString(batch_size_send));
                            jObj.put("train_epch", Integer.toString(epoch_send));
                            jObj.put("param_conv", param_conv);
                            jObj.put("conv_active", "relu");
                            jObj.put("pooling", "Max");
                            jObj.put("param_dense", param_dense);
                            jObj.put("param_dense_act", param_dense_act);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        System.out.println("Test"+jObj.toString());
                        Toast.makeText(getApplicationContext(), "모델 실행중", Toast.LENGTH_LONG).show();

                        NetworkTask networkTask = new NetworkTask("http://10.0.2.2:80/test", jObj);
                        networkTask.execute();
                    }

                }
            }
        });

    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        String url;
        JSONObject values;

        NetworkTask(String url, JSONObject values){
            this.url = url;
            this.values = values;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result;
            HttpTest requestHttpURLConnection = new HttpTest();
            result = requestHttpURLConnection.request(url, values);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // "데이터 선택" 버튼을 눌렀을 때 현재 class가 받은 데이터는 선택된 데이터임
        // 따라서 this_data[0]를 다른 class에서 받은 데이터명으로 변경
        if(requestCode==201){
            this_data[0]=data.getStringExtra("dataname");
            super.onActivityResult(requestCode, resultCode, data);
        }
        // 추가된 레이어 버튼을 눌렀을 때
        else if(requestCode==202){
            int this_layer_num=data.getIntExtra("layernum",0);
            layertype.set(this_layer_num-1,data.getIntExtra("layertype", 1));
            node_num.set(this_layer_num-1,data.getIntExtra("nodenum", 1));
            activation_func.set(this_layer_num-1,data.getIntExtra("activationfunction",1));
            kernel_size.set(this_layer_num-1,data.getIntExtra("kernel_size",1));
            pooling.set(this_layer_num-1,data.getIntExtra("pooling",1));
            if(data.getIntExtra("layertype", 1)==2){
                for(int i=this_layer_num;i<numLayer;i++){
                    unable_conv.set(i, 1);
                    layertype.set(i, 2);
                }
            }else{
                if(this_layer_num==1&&numLayer>1){
                    unable_conv.set(1,0);
                }
//                if(this_layer_num<3){
//                    if(numLayer<3){
//                        for(int i=this_layer_num;i<numLayer;i++){
//                            unable_conv.set(i, 0);
//                        }
//                    }else{
//                        for(int i=this_layer_num;i<3;i++){
//                            unable_conv.set(i, 0);
//                        }
//                    }
//                }
            }
        }
    }

    private void addLayer(){
        if(numLayer>=5){
            Toast.makeText(getApplicationContext(),"현재 시스템 상 레이어 수는 5개까지만 허용됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
        else{
            numLayer++;
            if(numLayer==1){
                layertype.add(1);
                node_num.add(10);
                activation_func.add(1);
                kernel_size.add(1);
                pooling.add(2);
                unable_conv.add(0);
            }
            else if(numLayer==2){
                if(layertype.get(0)==2){
                    layertype.add(2);
                    node_num.add(10);
                    activation_func.add(1);
                    kernel_size.add(1);
                    pooling.add(2);
                    unable_conv.add(1);
                }else{
                    layertype.add(1);
                    node_num.add(10);
                    activation_func.add(1);
                    kernel_size.add(1);
                    pooling.add(2);
                    unable_conv.add(0);
                }
            }
            else if(numLayer>2){
                layertype.add(2);
                node_num.add(10);
                activation_func.add(1);
                kernel_size.add(1);
                pooling.add(2);
                unable_conv.add(1);
            }

            /*
            if(numLayer>3){
                layertype.add(2);
                unable_conv.add(1);
                node_num.add(128);
            }
            else{
                node_num.add(100);
                if(numLayer>1){
                    if(layertype.lastElement()==2){
                        layertype.add(2);
                        unable_conv.add(1);
                    }else{
                        layertype.add(1);
                        unable_conv.add(0);
                    }
                }else{
                    layertype.add(1);
                    unable_conv.add(0);
                }
            }*/

            final int button_num=numLayer;
            /*
            activation_func.add(1);
            kernel_size.add(1);
            pooling.add(2);*/

            Button layer=new Button(this);
            layer.setId(button_num);
            layer.setText("레이어 "+button_num);
            layer.setBackground(ContextCompat.getDrawable(this, R.drawable.style_customize));
            layer.setPadding(10,10,10,10);

            layout.addView(layer, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            layer.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getApplicationContext(), LayerOptionActivity.class);
                    intent.putExtra("layernum",button_num);
                    intent.putExtra("layertype", layertype.get(button_num-1));
                    intent.putExtra("nodenum", node_num.get(button_num-1));
                    intent.putExtra("activationfunction", activation_func.get(button_num-1));
                    intent.putExtra("kernel_size", kernel_size.get(button_num-1));
                    intent.putExtra("pooling", pooling.get(button_num-1));
                    intent.putExtra("unable_conv", unable_conv.get(button_num-1));
                    startActivityForResult(intent, 202);
                }
            });
        }

    }

    private void deleteLayer(){
        if(numLayer<=0){
            return;
        }
        Button removing_layer=(Button)findViewById(numLayer);
        layout.removeView(removing_layer);
        numLayer--;
        layertype.remove(numLayer);
        node_num.remove(numLayer);
        activation_func.remove(numLayer);
        kernel_size.remove(numLayer);
        pooling.remove(numLayer);
        unable_conv.remove(numLayer);
    }
}