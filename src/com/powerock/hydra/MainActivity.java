package com.powerock.hydra;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.powerock.demo2.R;
import com.powerock.hydra.receiver.RouterConnectListener;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	public static Button startButton;
	public static TextView waitText;
	public static TextView alreadyText;
	public static String deviceIp = null;
	private RouterConnectListener mListener;
	
	private ImageView logoView;
	
    public static String  gateway;  
    public static String  netmask;  
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		logoView = (ImageView)findViewById(R.id.logo);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; 
		android.view.ViewGroup.LayoutParams params = logoView.getLayoutParams();  
	    params.height= width*6/7;
	    params.width =width*6/7;
	    logoView.setLayoutParams(params);
		waitText = (TextView)findViewById(R.id.waitText);
		alreadyText = (TextView)findViewById(R.id.alreadyText);
		
		mListener = new RouterConnectListener();
		IntentFilter filter = new IntentFilter();  
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
		registerReceiver(mListener, filter); 
		
		
		startButton = (Button)findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, RouterInfoActivity.class);
				intent.putExtra("ip", deviceIp);
				intent.putExtra("gateway", gateway);
				intent.putExtra("netmask", netmask);
				//Toast.makeText(MainActivity.this, deviceIp + "  "+gateway + " " +netmask, Toast.LENGTH_SHORT).show();
				startActivity(intent);
				finish();
			}
		});

		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mListener); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
}
