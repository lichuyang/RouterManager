package com.powerock.hydra;

import com.powerock.demo2.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RestartActivity extends Activity {
	static Button okButton;
	static TextView progressView;
	private TimeCount time;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restart);
		getActionBar().hide();
		progressView = (TextView)findViewById(R.id.progressview);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; 
		android.view.ViewGroup.LayoutParams params = progressView.getLayoutParams();  
	    params.height= width*5/7;
	    params.width =width*5/7;
	    progressView.setLayoutParams(params);
	    progressView.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float)(width*70.0/540));
		okButton = (Button) findViewById(R.id.okButton);
		okButton.setTextColor(Color.parseColor("#4e537b"));
		okButton.setEnabled(false);
		time = new TimeCount(22000, 1000);
		time.start();
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(RestartActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
			}
		});

	}
}

class TimeCount extends CountDownTimer {
	public TimeCount(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);// ��������Ϊ��ʱ��,�ͼ�ʱ��ʱ����
	}

	@Override
	public void onFinish() {// ��ʱ���ʱ����
		RestartActivity.okButton.setTextColor(Color.parseColor("#ffffff"));
		RestartActivity.okButton.setEnabled(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {// ��ʱ������ʾ
		
		RestartActivity.progressView.setText(millisUntilFinished / 1000-1+"");
	}
}
