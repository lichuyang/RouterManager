package com.powerock.hydra;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.powerock.demo2.R;
import com.powerock.hydra.WritingFirmActivity.UploadFirmTask;
import com.powerock.hydra.common.BaseUtils;
import com.powerock.hydra.common.ProgressImageView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BackupActivity extends Activity{
	private Button nextButton;
	private Button startButton;
	
	private TextView progressView;
	private ProgressImageView progressImageView;
	
	private String gateway;
	private String userpass;
	
	private int progressNumber = 0;
	
	private long fileSize;
	private int downloadSize;
	
	private String routerType;
	
	static int finalHeight;
	static int finalWidth;
	
	private long startTime;
	private long stopTime;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup);
		getActionBar().hide();
		progressView = (TextView)findViewById(R.id.progressview);
		progressImageView = (ProgressImageView)findViewById(R.id.progressimageview);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; 
		android.view.ViewGroup.LayoutParams params = progressView.getLayoutParams();  
	    params.height= width*5/7;
	    params.width =width*5/7;
	    progressView.setLayoutParams(params);
	    progressImageView.setLayoutParams(params);
	    progressView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float)(width*40.0/540));
		Intent intent = getIntent();
		routerType = intent.getStringExtra("type");
		gateway = intent.getStringExtra("ip");
		userpass = intent.getStringExtra("userpass");
		nextButton = (Button)findViewById(R.id.nextButton);
		startButton = (Button)findViewById(R.id.startButton);
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(BackupActivity.this, WritingFirmActivity.class);
				intent.putExtra("ip", gateway);
				intent.putExtra("userpass", userpass);
				intent.putExtra("type", routerType);
				startActivity(intent);
			}
		});
		startButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startButton.setTextColor(Color.parseColor("#4e537b"));
				startButton.setEnabled(false);
				String url = "http://" + gateway + "/userRpm/config.bin";
				File dir = new File(Environment.getExternalStorageDirectory()+ "/Hydra/FirmBackup/");
				if (!dir.exists()) {
					System.out.println(2);
					dir.mkdirs();
				}
				Calendar c1 = Calendar.getInstance();
				startTime = c1.getTimeInMillis();
				for(int i=5; i < 100; i+=5){
					BaseUtils.processHandler(progressView, progressImageView, i, i*100, 0);
				}
				BackUpTask task = new BackUpTask();
				task.execute(url,Environment.getExternalStorageDirectory()+ "/Hydra/FirmBackup/" ,routerType+ "-" + gateway+"-config.bin");
			}
		});
		
	}
	
	
	class BackUpTask extends AsyncTask<String, Integer, String> {
		
		

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HttpGet httpRequest = new HttpGet(arg0[0]);
			httpRequest.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			httpRequest.addHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpRequest.addHeader("Authorization", "Basic " + userpass);
			httpRequest.addHeader("Accept-Encoding", "gzip, deflate");
			httpRequest.addHeader("Connection", "keep-alive");
			httpRequest.addHeader("Referer", "http://" + gateway
					+ "/incoming/Firmware.htm");
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpRequest);
			} catch (ClientProtocolException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			HttpEntity entity = httpResponse.getEntity();
			fileSize = entity.getContentLength();
			try {
				System.out.println("fileSize:" + fileSize + entity.isStreaming());
			} catch (IllegalStateException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
		   // if (fileSize <= 0) throw new RuntimeException("无法获知文件大小 ");
			InputStream inputStream = null;
			try {
				inputStream = entity.getContent();
			} catch (IllegalStateException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			byte[] b = new byte[1024];
			int readedLength = -1;
		    OutputStream fos = null;
			try {
				fos = new FileOutputStream(new File(arg0[1]+arg0[2]));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(routerType.equals("TL-WR941N"))
				fileSize = 24888;
			else if(routerType.equals("TL-WR720N"))
				fileSize = 23416;
			else if(routerType.equals("TL-WR703N"))
				fileSize = 22904;
			else if(routerType.equals("DIR505"))
				fileSize = 345;
			try {
				while( (readedLength = inputStream.read(b)) != -1){
					fos.write(b, 0, readedLength);
					downloadSize += readedLength;
					System.out.println("downloadSize:" + downloadSize + "   " + fileSize );
					publishProgress(Math.round(downloadSize*100/fileSize));
					}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				inputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		    try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Calendar c2 = Calendar.getInstance();
			stopTime = c2.getTimeInMillis();
			if(stopTime - startTime > 9500){
				progressView.setText("100");
				progressImageView.setProgress(100);
				nextButton.setTextColor(Color.parseColor("#ffffff"));
				nextButton.setEnabled(true);
				View toastView = getLayoutInflater().inflate(R.layout.custom_toast, null);
				TextView tv = (TextView)toastView.findViewById(R.id.toasttext);
				tv.setText("备份成功！");
				Toast	toastEnd = new Toast(BackupActivity.this);
				  toastEnd.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
				  toastEnd.setDuration(Toast.LENGTH_SHORT);
				  toastEnd.setView(toastView);
				  toastEnd.show();
			}else{
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						progressView.setText("100");
						progressImageView.setProgress(100);
						nextButton.setTextColor(Color.parseColor("#ffffff"));
						nextButton.setEnabled(true);
						View toastView = getLayoutInflater().inflate(R.layout.custom_toast, null);
						TextView tv = (TextView)toastView.findViewById(R.id.toasttext);
						tv.setText("备份成功！");
						Toast	toastEnd = new Toast(BackupActivity.this);
					  toastEnd.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
					  toastEnd.setDuration(Toast.LENGTH_SHORT);
					  toastEnd.setView(toastView);
					  toastEnd.show();
					}
				}, 10000 - stopTime + startTime);
				
			}
		}	
		protected void onProgressUpdate(Integer... values){			
		}
		
	}
}
