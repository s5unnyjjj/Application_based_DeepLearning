package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Arrays;

public class LayerOptionActivity extends AppCompatActivity {

    String[] activation_func_list;
    int layer_num_get;
    int layertype_send;
    int node_num_send;
    int activationfunction_send;
    int kernel_size_send;
    int pooling_send;
    int unable_conv_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layeroption);

        Intent intent=new Intent(this.getIntent());

        layer_num_get=intent.getIntExtra("layernum", 0);
        layertype_send=intent.getIntExtra("layertype", 0);
        final int node_num_get=intent.getIntExtra("nodenum", 0);
        node_num_send=node_num_get;
        final int activationfunction_get=intent.getIntExtra("activationfunction", 0);
        activationfunction_send=activationfunction_get;
        kernel_size_send=intent.getIntExtra("kernel_size",0);
        pooling_send=intent.getIntExtra("pooling",0);
        unable_conv_send=intent.getIntExtra("unable_conv",0);

        TextView layer_num=(TextView)findViewById(R.id.layer_num);
        layer_num.setText("레이어 번호 : "+layer_num_get);

        final EditText node_num=(EditText)findViewById(R.id.node_num);
        node_num.setText(""+node_num_get);

        // Fully Connected layer를 선택했을 경우 일부 선택지를 숨겨야 함
        final TextView text_kernel_size=(TextView)findViewById(R.id.text_kernel_size);
        final RadioGroup radiogroup_kernelsize=(RadioGroup)findViewById(R.id.radiogroup_kernelsize);
        final TextView text_pooling=(TextView)findViewById(R.id.text_pooling);
        final RadioGroup radiogroup_pooling=(RadioGroup)findViewById(R.id.radiogroup_pooling);
        final TextView only_max_supported=(TextView)findViewById(R.id.only_max_supported);

        // 레이어 종류에 따라 노드수 필터수를 오가게 함
        final TextView text_node_or_filter=(TextView)findViewById(R.id.text_node_or_filter);

        // 커널 사이즈와 풀링 방식을 미리 체크하게 함
        RadioButton radio_3=(RadioButton)findViewById(R.id.radio_3);
        RadioButton radio_5=(RadioButton)findViewById(R.id.radio_5);
        if(kernel_size_send==1){
            radio_3.setChecked(true);
        }else{
            radio_5.setChecked(true);
        }


        RadioButton radio_max=(RadioButton)findViewById(R.id.radio_max);

        // 레이어 종류를 미리 체크하게 함
        RadioButton radio_conv=(RadioButton)findViewById(R.id.radio_conv);
        RadioButton radio_fc=(RadioButton)findViewById(R.id.radio_fc);
        if(layertype_send==1){
            radio_conv.setChecked(true);
        }
        else{
            text_node_or_filter.setText("노드 수 ");
            radio_fc.setChecked(true);
            text_kernel_size.setVisibility(View.INVISIBLE);
            radiogroup_kernelsize.setVisibility(View.INVISIBLE);
            text_pooling.setVisibility(View.INVISIBLE);
            radiogroup_pooling.setVisibility(View.INVISIBLE);
            only_max_supported.setVisibility(View.INVISIBLE);
        }
        if(unable_conv_send==1){
            radio_conv.setEnabled(false);
            radio_fc.setChecked(true);
            text_kernel_size.setVisibility(View.INVISIBLE);
            radiogroup_kernelsize.setVisibility(View.INVISIBLE);
            text_pooling.setVisibility(View.INVISIBLE);
            radiogroup_pooling.setVisibility(View.INVISIBLE);
            only_max_supported.setVisibility(View.INVISIBLE);
        }

        // 활성함수 리스트
        activation_func_list=getResources().getStringArray(R.array.activation_func_list);
        ArrayAdapter<String> act_func_adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, activation_func_list);
        final Spinner activation_func=(Spinner)findViewById(R.id.activation_func);
        activation_func.setAdapter(act_func_adapter);
        activation_func.setSelection(activationfunction_get);
        if(layertype_send==1){
            activation_func.setEnabled(false);
        }

        // 레이어 종류
        RadioGroup radiogroup_layertype=(RadioGroup)findViewById(R.id.radiogroup_layertype);

        radiogroup_layertype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton select=(RadioButton)findViewById(id);
                String temp= (String) select.getText();
                if(temp.contains("Fully")){
                    text_node_or_filter.setText("노드 수 ");
                    text_kernel_size.setVisibility(View.INVISIBLE);
                    radiogroup_kernelsize.setVisibility(View.INVISIBLE);
                    text_pooling.setVisibility(View.INVISIBLE);
                    radiogroup_pooling.setVisibility(View.INVISIBLE);
                    only_max_supported.setVisibility(View.INVISIBLE);
                    layertype_send=2;
                    activation_func.setEnabled(true);
                }
                else{
                    text_node_or_filter.setText("Filter 수");
                    text_kernel_size.setVisibility(View.VISIBLE);
                    radiogroup_kernelsize.setVisibility(View.VISIBLE);
                    text_pooling.setVisibility(View.VISIBLE);
                    radiogroup_pooling.setVisibility(View.VISIBLE);
                    only_max_supported.setVisibility(View.VISIBLE);
                    layertype_send=1;
                    activation_func.setSelection(1);
                    activation_func.setEnabled(false);
                }
                // Toast.makeText(getApplicationContext(), select.getText(), Toast.LENGTH_LONG).show();
            }
        });


        // 설정완료 버튼을 눌렀을 때
        Button setting_complete=(Button)findViewById(R.id.setting_complete);
        setting_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                node_num_send=Integer.parseInt(node_num.getText().toString());
                if(node_num_send>10){
                    Toast.makeText(getApplicationContext(),"현재 시스템 상 노드 수 혹은 필터 수는 10개까지만 허용됩니다.", Toast.LENGTH_LONG).show();
                }else{
                    String activationfunction_send_string=activation_func.getSelectedItem().toString();
                    activationfunction_send=Arrays.asList(activation_func_list).indexOf(activationfunction_send_string);
                    if(layertype_send==2){
                        kernel_size_send=1;
                        pooling_send=2;
                    }else{
                        RadioButton select_kernel_size=(RadioButton)findViewById(radiogroup_kernelsize.getCheckedRadioButtonId());
                        String temp_kernel_size=(String)select_kernel_size.getText();
                        if(temp_kernel_size.contains("3")){
                            kernel_size_send=1;
                        }else{kernel_size_send=2;}

                        RadioButton select_pooling=(RadioButton)findViewById(radiogroup_pooling.getCheckedRadioButtonId());
                        String temp_pooling=(String)select_pooling.getText();
                        if(temp_pooling.contains("Max")){
                            pooling_send=2;
                        }else{pooling_send=1;}
                    }
                    onBackPressed();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent setting_complete_intent=new Intent();
        setting_complete_intent.putExtra("layernum",layer_num_get);
        setting_complete_intent.putExtra("layertype",layertype_send);
        setting_complete_intent.putExtra("nodenum",node_num_send);
        setting_complete_intent.putExtra("activationfunction",activationfunction_send);
        setting_complete_intent.putExtra("kernel_size",kernel_size_send);
        setting_complete_intent.putExtra("pooling",pooling_send);
        setResult(RESULT_OK, setting_complete_intent);
        finish();
        super.onBackPressed();
    }
}