package programfirebase.comdasd.example.javad.progland;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by javad on 05.06.2018.
 */

public class FullInf extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ful);
        getIncomingIntent();

    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra("Head")&& getIntent().hasExtra("Desc")&& getIntent().hasExtra("Inf")){
            String Head = getIntent().getStringExtra("Head");
            String Desc = getIntent().getStringExtra("Desc");
            String Inf = getIntent().getStringExtra("Inf");
            setINF(Head, Desc, Inf);
        }
    }


   private void setINF (String Headr, String Desc, String Infl){
       TextView head = findViewById(R.id.hed);
       head.setText(Headr);

       TextView desc = findViewById(R.id.desc);
       desc.setText(Desc);

       TextView inf = findViewById(R.id.inf);
       inf.setText(Infl);

   }
}
