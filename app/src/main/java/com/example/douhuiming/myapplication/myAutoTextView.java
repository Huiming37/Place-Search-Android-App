package com.example.douhuiming.myapplication;


import android.content.Context;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

public class myAutoTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView{

    public myAutoTextView(Context context) {
        super(context);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode==KeyEvent.KEYCODE_ENTER)
        {
            // Just ignore the [Enter] key
            return true;
        }
        // Handle all other keys in the default way
        return super.onKeyDown(keyCode, event);
    }
}
