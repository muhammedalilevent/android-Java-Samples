package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText input1;
    EditText input2;

    TextView resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         input1 = findViewById(R.id.input1);
         input2 = findViewById(R.id.input2);

         resultText = findViewById(R.id.resultText);



    }

    public void sum(View view) {

        if(input1.getText().toString().matches("") || input2.getText().toString().matches("")){

            resultText.setText("Enter Number!");

        }else{
            //Sonucu Integer Almak Yol1
            int number1= Integer.parseInt(input1.getText().toString());
            int number2 = Integer.parseInt(input2.getText().toString());

            int result = number1 + number2;

            resultText.setText("Result : "+result);

        }


    }

    public void deduct(View view){

        if(input1.getText().toString().matches("") || input2.getText().toString().matches("")){

            resultText.setText("Enter Number!");

        }else{
            //Sonucu Integer Almak Yol1
            int number1= Integer.parseInt(input1.getText().toString());
            int number2 = Integer.parseInt(input2.getText().toString());

            int result = number1 - number2;

            resultText.setText("Result : "+result);

        }

    }

    public  void  divide(View view){

        if(input1.getText().toString().matches("") || input2.getText().toString().matches("")){

            resultText.setText("Enter Number!");

        }else{
            //Sonucu Integer Almak Yol1
            int number1= Integer.parseInt(input1.getText().toString());
            int number2 = Integer.parseInt(input2.getText().toString());

            int result = number1 / number2;

            resultText.setText("Result : "+result);

        }

    }

    public void multiply(View view){

        if(input1.getText().toString().matches("") || input2.getText().toString().matches("")){

            resultText.setText("Enter Number!");

        }else{
            //Sonucu Integer Almak Yol1
            int number1= Integer.parseInt(input1.getText().toString());
            int number2 = Integer.parseInt(input2.getText().toString());

            int result = number1 * number2;

            resultText.setText("Result : "+result);

        }

    }
}