package com.powerock.hydra;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.powerock.demo2.R;
import com.powerock.hydra.RouterInfoActivity.SetRouterTypeTask;
import com.powerock.hydra.common.BaseUtils;
import com.powerock.hydra.common.ProgressImageView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WritingFirmActivity extends Activity {
	private Button nextButton;
	private Button startButton;
	private String gateway;
	private String userpass;
	private String routerType;
	private TextView progressView;
	private ProgressImageView progressImageView;
	
	private int width;
	private Timer timer;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writing_firm);
		getActionBar().hide();
		progressView = (TextView)findViewById(R.id.progressview);
		progressImageView = (ProgressImageView)findViewById(R.id.progressimageview);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; 
		android.view.ViewGroup.LayoutParams params = progressView.getLayoutParams();  
	    params.height= width*5/7;
	    params.width =width*5/7;
	    progressView.setLayoutParams(params);
	    progressImageView.setLayoutParams(params);
	    progressView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float)(width*40.0/540));
		Intent intent = getIntent();
		gateway = intent.getStringExtra("ip");
		userpass = intent.getStringExtra("userpass");
		routerType = intent.getStringExtra("type");
		
		
		nextButton = (Button) findViewById(R.id.nextButton);
		startButton = (Button) findViewById(R.id.startButton);
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(WritingFirmActivity.this, RestartActivity.class);
				startActivity(intent);
			}
		});

		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startButton.setTextColor(Color.parseColor("#4e537b"));
				startButton.setClickable(false);
				int[] randomNumber = {5, 6, 8, 10, 12 , 14}; 
				int number = BaseUtils.getRandomNumber(randomNumber);
				progressView.setText(number + "");
				progressImageView.setProgress(number);
				BaseUtils.customToast(WritingFirmActivity.this, "开始上传固件!");
				  if(routerType.equals("DIR505")){
					  DUploadFirmTask task = new DUploadFirmTask();
						task.execute();
				  }else{
				UploadFirmTask task = new UploadFirmTask();
				task.execute();
				  }
			}
		});

	}

	class UploadFirmTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/Hydra/WritingFirm");
			System.out.println(1);
			if (!dir.exists()) {
				System.out.println(2);
				dir.mkdirs();
			}
			String firmFile = null;
			if(routerType.equals("TL-WR703N")){
			firmFile = Environment.getExternalStorageDirectory()
					+ "/Hydra/WritingFirm/tlwr703n.bin";
			}else if(routerType.equals("TL-WR720N")){
				firmFile = Environment.getExternalStorageDirectory()
						+ "/Hydra/WritingFirm/tlwr720n.bin";
			}else if(routerType.equals("TL-WR941N")){
				firmFile = Environment.getExternalStorageDirectory()
						+ "/Hydra/WritingFirm/tlwr941n.bin";
			}
			if (!(new File(firmFile).exists())) {
				System.out.println(3);
				InputStream is = null;
				if(routerType.equals("TL-WR703N")){
					is = getResources().openRawResource(R.raw.tlwr703n);
				}else if(routerType.equals("TL-WR720N")){
					is = getResources().openRawResource(R.raw.tlwr720n);
				}else if(routerType.equals("TL-WR941N")){
					is = getResources().openRawResource(R.raw.tlwr941n);
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(firmFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] buffer = new byte[8192];
				int count = 0;
				try {
					while ((count = is.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String url = "http://" + gateway + "/incoming/Firmware.htm";
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			FileBody bin = new FileBody(new File(firmFile));
			StringBody upgrade = null;
			try {
				upgrade = new StringBody("\\xC9\\xFD\\x20\\xBC\\xB6");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("filename", bin);
			reqEntity.addPart("Upgrade", upgrade);
			httppost.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			httppost.addHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httppost.addHeader("Authorization", "Basic " + userpass);
			httppost.addHeader("Accept-Encoding", "gzip, deflate");
			httppost.addHeader("Connection", "keep-alive");
			httppost.addHeader("Referer", "http://" + gateway
					+ "/userRpm/SoftwareUpgradeRpm.htm");
			httppost.setEntity(reqEntity);
			HttpResponse response = null;
			System.out.println(5);
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpEntity resEntity = response.getEntity();
			try {
				System.out.println("result:"
						+ EntityUtils.toString(resEntity, "GB2312"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			View toastView = getLayoutInflater().inflate(R.layout.custom_toast, null);
			TextView tv = (TextView)toastView.findViewById(R.id.toasttext);
			int[] randomNumber = {30, 32, 35, 37, 40 , 42, 45, 48, 50}; 
			int number = BaseUtils.getRandomNumber(randomNumber);
			progressView.setText(number + "");
			progressImageView.setProgress(number);
			tv.setText("上传固件成功，开始写入固件！");
			Toast	toastEnd = new Toast(WritingFirmActivity.this);
			  toastEnd.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
			  toastEnd.setDuration(Toast.LENGTH_SHORT);
			  toastEnd.setView(toastView);
			  toastEnd.show();
			  
			  int[] randomNumber1 = {55, 58, 60, 62, 65 , 68, 70, 72, 75}; 
			  int number1 = BaseUtils.getRandomNumber(randomNumber1);
			  BaseUtils.processHandler(progressView, progressImageView, number1, 10000, 0);	
			  
			  int[] randomNumber2 = {78, 80, 82, 85, 88, 90, 92}; 
			  int number2 = BaseUtils.getRandomNumber(randomNumber2);
			  BaseUtils.processHandler(progressView, progressImageView, number2, 24000, 0);					
				
			  
			  int[] randomNumber3 = {95, 96, 97, 98}; 
			  int number3 = BaseUtils.getRandomNumber(randomNumber3);
			  BaseUtils.processHandler(progressView, progressImageView, number3, 42000, 1);	
			WritingFirmTask task = new WritingFirmTask();
			task.execute();
			
		}

	}

	class WritingFirmTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			
			
			String url = "http://" + gateway
					+ "/userRpm/FirmwareUpdateTemp.htm";
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			httpGet.addHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httpGet.addHeader("Authorization", "Basic " + userpass);
			httpGet.addHeader("Accept-Encoding", "gzip, deflate");
			httpGet.addHeader("Connection", "keep-alive");
			httpGet.addHeader("Referer", "http://" + gateway
					+ "/incoming/Firmware.htm");
			HttpResponse httpResponse = null;
			try {
				httpResponse = new DefaultHttpClient().execute(httpGet);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = null;
			try {
				result = EntityUtils.toString(httpResponse.getEntity(),
						"Gb2312");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("result:" + result);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		
			progressView.setText("100");
			progressImageView.setProgress(100);
			
			  
			  BaseUtils.customToast(WritingFirmActivity.this, "写入固件成功，正在重启！");
			nextButton.postInvalidate();
			nextButton.setEnabled(true);
			nextButton.setTextColor(Color.parseColor("#ffffff"));

			
		}
	}
	
	
	
	
	
	
	
	
	class DUploadFirmTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			File dir = new File(Environment.getExternalStorageDirectory()
					+ "/Hydra/WritingFirm");
			System.out.println(1);
			if (!dir.exists()) {
				System.out.println(2);
				dir.mkdirs();
			}
			
			String firmFile  = Environment.getExternalStorageDirectory()
						+ "/Hydra/WritingFirm/dir505.bin";
			if (!(new File(firmFile).exists())) {
				InputStream is = getResources().openRawResource(R.raw.dir505);
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(firmFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] buffer = new byte[8192];
				int count = 0;
				try {
					while ((count = is.read(buffer)) > 0) {
						fos.write(buffer, 0, count);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			String url = "http://" + gateway + "/my_cgi.cgi";
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			FileBody bin = new FileBody(new File(firmFile));
			StringBody upgradeAction = null;
			try {
				upgradeAction = new StringBody("load_fw");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("file", bin);
			reqEntity.addPart("which_action", upgradeAction);
			httppost.addHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			httppost.addHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			httppost.addHeader("Accept-Encoding", "gzip, deflate");
			httppost.addHeader("Connection", "keep-alive");
			httppost.addHeader("Referer", "http://" + gateway
					+ "/Firmware.htm");
			httppost.setEntity(reqEntity);
			HttpResponse response = null;
			System.out.println(5);
			try {
				response = httpclient.execute(httppost);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpEntity resEntity = response.getEntity();
			try {
				System.out.println("uploadresult:"
						+ EntityUtils.toString(resEntity, "GB2312"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			
			progressView.setText("40");
			progressImageView.setProgress(40);
			BaseUtils.customToast(WritingFirmActivity.this, "上传固件成功，开始写入固件！");
			  
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					progressView.setText("60");
					progressImageView.setProgress(60);
				}
			}, 3000);
			
			
			Handler handler1 = new Handler();
			handler1.postDelayed(new Runnable() {
				public void run() {
					progressView.setText("90");
					progressImageView.setProgress(90);
				}
			}, 9000);
			
			
			Handler handler2 = new Handler();
			handler2.postDelayed(new Runnable() {
				public void run() {
					if(progressView.getText().toString().equals("90")){
						progressView.setText("95");
						progressImageView.setProgress(95);
					}
				}
			}, 20000);
			DWritingFirmTask task = new DWritingFirmTask();
			task.execute();
			
			
			
			
		}

	}
	
	
	class DWritingFirmTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String url = "http://" + gateway
					+ "/firmware_upgrade.htm";
			
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = null;
			try {
				httpResponse = new DefaultHttpClient().execute(httpGet);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String result = null;
			try {
				result = EntityUtils.toString(httpResponse.getEntity(),
						"Gb2312");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("writresult:" + result);
			
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressView.setText("100");
			progressImageView.setProgress(100);
			
			  BaseUtils.customToast(WritingFirmActivity.this, "写入固件成功，正在重启！");
			  
			nextButton.postInvalidate();
			nextButton.setEnabled(true);
			nextButton.setTextColor(Color.parseColor("#ffffff"));
			

			
		}
	}
}
