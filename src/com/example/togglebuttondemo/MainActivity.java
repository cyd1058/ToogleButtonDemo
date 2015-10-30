package com.example.togglebuttondemo;

import com.example.togglebuttondemo.MyToggleButton.OnStateChangedListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MyToggleButton toggle_button = (MyToggleButton) findViewById(R.id.toggle_button);
		toggle_button.setOnStateChangedListener(new OnStateChangedListener() {

			@Override
			public void onStateChanged(boolean isOpen) {
				Toast.makeText(getApplicationContext(), isOpen ? "打开" : "关闭", Toast.LENGTH_SHORT).show();
			}
			
		});
	}

}
