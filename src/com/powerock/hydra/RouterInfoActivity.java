package com.powerock.hydra;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.powerock.demo2.R;
import com.powerock.hydra.common.BaseUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RouterInfoActivity extends Activity {
	private Button nextButton;
	private String netmask;
	private String devIp;
	private String gateway;

	private TextView routerView;
	private TextView deviceIpView;
	private TextView maskView;
	private TextView routerTypeView;
	private String url;

	private String routerType = "";
	
	private String[] routerTypeAll= {"TL-WR703N", "TL-WR720N", "TL-WR941N", "DIR505", "netcore磊科", "TL-WR841N"};
	
	private int tag = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.router_info);
		Intent intent = getIntent();
		netmask = intent.getStringExtra("netmask");
		devIp = intent.getStringExtra("ip");
		gateway = intent.getStringExtra("gateway");
		getActionBar().hide();
		nextButton = (Button) findViewById(R.id.nextButton);
		routerView = (TextView) findViewById(R.id.routeriptext);
		deviceIpView = (TextView) findViewById(R.id.deviceiptext);
		maskView = (TextView) findViewById(R.id.maskcodetext);
		routerTypeView = (TextView) findViewById(R.id.routertypetext);

		routerView.setText(gateway);
		deviceIpView.setText(devIp);
		maskView.setText(netmask);

		url = "http://" + gateway + ":80";
		// routerTypeView.setText("unknown");

		SetRouterTypeTask task = new SetRouterTypeTask();
		task.execute();
		
		
		
		
		
		
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(RouterInfoActivity.this, GetPassActivity.class);
				intent.putExtra("ip", gateway);
				intent.putExtra("type", routerType);
				startActivity(intent);
			}
		});

	}

	class SetRouterTypeTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate");
			httpGet.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
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
			Header[] headers = null;
			try {
				headers = httpResponse.getAllHeaders();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i<headers.length;i++){
				System.out.println("headers" + i + ": "+ headers[i]);
				if(headers[i].getValue().toUpperCase().contains("703N")){
					routerType = routerTypeAll[0];
				}else if(headers[i].getValue().toUpperCase().contains("720N")){
					routerType = routerTypeAll[1];
				}else if(headers[i].getValue().toUpperCase().contains("941N")){
					routerType = routerTypeAll[2];
				}else if(headers[i].getValue().toUpperCase().contains("941N")){
					routerType = routerTypeAll[2];
				}else if(headers[i].getValue().toUpperCase().contains("841N")){
					routerType = routerTypeAll[5];
				}
			}
			System.out.println("routerType:" + routerType);
			return null;
		}

		
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!routerType.equals("")){
			routerTypeView.postInvalidate();
			routerTypeView.setText(routerType);
			nextButton.setTextColor(Color.parseColor("#ffffff"));
			nextButton.setEnabled(true);
			}else{
				SetDRouterTypeTask dTask = new SetDRouterTypeTask();
			dTask.execute();
			
			}
		}
	}
	
	class SetDRouterTypeTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HttpGet httpGet = new HttpGet(url + "/xml/lang.xml");
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate");
			httpGet.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
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
				result = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				if(result.contains("DIR-505"))
					routerType = routerTypeAll[3];
			System.out.println("result:" + result);
			return null;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!routerType.equals("")){
				routerTypeView.postInvalidate();
				routerTypeView.setText(routerType);
				nextButton.setTextColor(Color.parseColor("#ffffff"));
				nextButton.setEnabled(true);
			}else{
				SetLKRouterTypeTask lkTask = new SetLKRouterTypeTask();
				lkTask.execute();
			}
		}
	}
	
	
	class SetLKRouterTypeTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
			httpGet.setHeader("Accept-Encoding", "gzip, deflate");
			httpGet.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
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
			Header[] headers = null;
			try {
				headers = httpResponse.getAllHeaders();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0;i<headers.length;i++){
				System.out.println("headers" + i + ": "+ headers[i]);
				if(headers[i].toString().contains("WWW-Authenticate: Basic realm=\"user\"")){
					routerType = routerTypeAll[4];
				}
			}
			System.out.println("routerType:" + routerType);
			return null;
		}

		
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(!routerType.equals("")){
			routerTypeView.postInvalidate();
			routerTypeView.setText(routerType);
			nextButton.setTextColor(Color.parseColor("#ffffff"));
			nextButton.setEnabled(true);
			}else{
				BaseUtils.customToast(RouterInfoActivity.this, "无法获取路由器型号！");				
			}
		}
	}
	
}
