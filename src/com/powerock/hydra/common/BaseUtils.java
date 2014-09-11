package com.powerock.hydra.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import com.powerock.demo2.R;
import com.powerock.hydra.RouterInfoActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class BaseUtils {
	private String SDPATH;
	
	private int FILESIZE = 4 * 1024; 
	
	public String getSDPATH(){
		return SDPATH;
	}
	
	public BaseUtils(){
		//得到当前外部存储设备的目录( /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}
	
	/**
	 * 在SD卡上创建文件
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException{
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}
	
	/**
	 * 在SD卡上创建目录
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName){
		File dir = new File(SDPATH + dirName);
		dir.mkdirs();
		return dir;
	}
	
	/**
	 * 判断SD卡上的文件夹是否存在
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName){
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	
	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path,String fileName,InputStream input){
		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
                            byte[] buffer = new byte[FILESIZE];

			
                           int length;
                           while((length=(input.read(buffer))) >0){
                                 output.write(buffer,0,length);
                           }

			output.flush();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static void customToast(Context context, String toast){
		View toastView = ((Activity) context).getLayoutInflater().inflate(R.layout.custom_toast, null);
		TextView tv = (TextView)toastView.findViewById(R.id.toasttext);
		tv.setText(toast);
		Toast	toastEnd = new Toast(context);
		  toastEnd.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 0);
		  toastEnd.setDuration(Toast.LENGTH_SHORT);
		  toastEnd.setView(toastView);
		  toastEnd.show();
	}
	
	
	public static void processHandler(final TextView progressView, final ProgressImageView progressImageView, final int progress, int time, final int tag){
		 Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					if(tag == 1 && Integer.parseInt(progressView.getText().toString()) < 100){
						progressView.setText(progress + "");
						progressImageView.setProgress(progress);
					}else{
						progressView.setText(progress + "");
						progressImageView.setProgress(progress);
					}
				}
			}, time);
	}
	
	public static int getRandomNumber(int[] randomNumber){
		int length = randomNumber.length;
		Random ran = new Random();
		return randomNumber[ran.nextInt(length)];
	}
}
