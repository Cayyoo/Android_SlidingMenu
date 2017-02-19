package com.example.slidingmenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.example.slidingmenu.view.CustomHorizontalScrollView;


public class MainActivity extends Activity {
    private CustomHorizontalScrollView mLeftMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mLeftMenu= (CustomHorizontalScrollView) findViewById(R.id.id_menu);
    }

    public void toggleMenu(View view){
        mLeftMenu.toggle();
    }

}
