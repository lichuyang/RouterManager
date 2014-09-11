package com.powerock.hydra.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.trinea.android.common.util.ShellUtils;
import cn.trinea.android.common.util.ShellUtils.CommandResult;

import com.powerock.hydra.MainActivity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.widget.Toast;

public class RouterConnectListener extends BroadcastReceiver {

	private WifiManager wifii;

	private DhcpInfo d;

	@Override
	public void onReceive(final Context context, Intent intent) {

		boolean success = false;

		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (State.CONNECTED == state) {
			success = true;
		}
		if (null != connManager
				.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)) {
			state = connManager.getNetworkInfo(
					ConnectivityManager.TYPE_ETHERNET).getState();
			if (State.CONNECTED == state) {
				success = true;
			}
		}
		System.out.println("success:" + success);
		if (success) {
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface
						.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf
							.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							if (inetAddress.getHostAddress().startsWith("192")
									|| inetAddress.getHostAddress().startsWith(
											"10."))
								MainActivity.deviceIp = inetAddress
										.getHostAddress();
							System.out.println("ip:"
									+ inetAddress.getHostAddress().toString());
						}
					}
				}
			} catch (SocketException ex) {
				System.out.println("cuowu:" + ex.toString());
			}
			if (MainActivity.deviceIp != null) {
				ActivityManager am = (ActivityManager) context
						.getSystemService(Context.ACTIVITY_SERVICE);
				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
				System.out.println("activity:" + cn.getClassName());
				if (cn.getClassName().equals("com.powerock.hydra.MainActivity")) {
					String[] command = { "ip route show" };
					CommandResult result = ShellUtils.execCommand(command,
							false);
					
					
					Matcher wlanM = Pattern
							.compile("default via(.*?)dev wlan0").matcher(
									result.successMsg);
					// Toast.makeText(context, "wlanM:" +wlanM.find(),
					// Toast.LENGTH_LONG).show();
					if (wlanM.find()) {
						wifii = (WifiManager) context
								.getSystemService(Context.WIFI_SERVICE);
						d = wifii.getDhcpInfo();
						MainActivity.gateway = intToIp(d.gateway);
						MainActivity.netmask = intToIp(d.netmask);
					}

					Matcher ethM = Pattern.compile("default via(.*?)dev eth0")
							.matcher(result.successMsg);
					if (ethM.find()) {
						MainActivity.gateway = ethM.group(1).replace(" ", "");
						MainActivity.netmask = "255.255.255.0";
					}
					// MainActivity.gateway = "192.168.8.1";

					Handler handler1 = new Handler();
					handler1.postDelayed(new Runnable() {
						public void run() {
							MainActivity.waitText.setTextColor(Color
									.parseColor("#4e537b"));
						}
					}, 3000);

					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						public void run() {
							MainActivity.alreadyText.setTextColor(Color
									.parseColor("#ffffff"));
							MainActivity.startButton.setEnabled(true);
							MainActivity.startButton.setTextColor(Color
									.parseColor("#ffffff"));
						}
					}, 3200);
				}
			} else {
				Toast.makeText(context, "Á¬½Ó´íÎó£¡", Toast.LENGTH_SHORT).show();
			}
		} else {
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			System.out.println("activity:" + cn.getClassName());
			if (cn.getClassName().equals("com.powerock.hydra.MainActivity")) {
				MainActivity.gateway = null;
				MainActivity.netmask = null;
				Handler handler1 = new Handler();
				handler1.postDelayed(new Runnable() {
					public void run() {
						MainActivity.alreadyText.setTextColor(Color
								.parseColor("#4e537b"));
					}
				}, 3000);

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					public void run() {
						MainActivity.waitText.setTextColor(Color
								.parseColor("#ffffff"));
						MainActivity.startButton.setEnabled(false);
						MainActivity.startButton.setTextColor(Color
								.parseColor("#696e96"));
					}
				}, 3200);
			}
		}
	}

	private String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

}