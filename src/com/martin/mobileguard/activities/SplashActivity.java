package com.martin.mobileguard.activities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.martin.mobileguard.R;
import com.martin.mobileguard.utils.IOHelper;

import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class SplashActivity extends Activity {

	private TextView tv_splash_version;
	private TextView tv_update_info;
	private String versionName;
	private String updateMessage;
	private String updateUrl;
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				loadHomeActivity();
				break;
			case 2:				
				AlertDialog.Builder builder=new Builder(SplashActivity.this);
				builder.setTitle("发现更新的版本");
				builder.setMessage(updateMessage);
				//取消屏蔽，禁止按取消按键 builder.setCancelable(false);
				builder.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						loadHomeActivity();
					}
				});
				builder.setPositiveButton("马上更新", new OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(SplashActivity.this, "loading...", Toast.LENGTH_SHORT).show();
						updateApp(updateUrl);
					}										
				});
				
				builder.setNegativeButton("下次再说",new OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						loadHomeActivity();
					}
				});				
				builder.create().show();
				break;
			case 3:
				Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
				loadHomeActivity();
				break;
			case 4:
				Toast.makeText(SplashActivity.this, "JSON解析失败", Toast.LENGTH_SHORT).show();
				loadHomeActivity();
				break;
			}			
		}		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_update_info=(TextView) findViewById(R.id.tv_update_info);
		versionName=getAppVersion();
		tv_splash_version.setText("版本号:" + versionName);
		checkUpdateInfo();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		loadHomeActivity();
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void checkUpdateInfo() {
		new Thread() {
			public void run() {
				Message msg = handler.obtainMessage();
				long startTime=System.currentTimeMillis();
				String path = getResources().getString(R.string.updateurl);
				try {
					URL url = new URL(path);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestMethod("GET");
					conn.setReadTimeout(2000);
					conn.setConnectTimeout(2000);
					if (conn.getResponseCode() == 200) {
						InputStream is = conn.getInputStream();
						String result = IOHelper.formatToString(is);
						JSONObject json = new JSONObject(result);
						String lastVersionName = (String) json.getString("versionname");
						updateMessage = (String) json.getString("description");	
						updateUrl = (String) json.getString("apkurl");	
						if (versionName.equals(lastVersionName)) {
							// 不必更新
							msg.what = 1;
						} else {
							// 更新系统
							msg.what = 2;
						}						
					}
				} catch (MalformedURLException e) {
					msg.what = 3;
				} catch (IOException e) {
					msg.what = 3;
				} catch (JSONException e) {
					msg.what = 4;					
				}finally{
					long endTime=System.currentTimeMillis();
					long dTime=endTime-startTime;
					if(dTime<2000){
						try {
							Thread.sleep(2000-dTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					handler.sendMessage(msg);
				}
			}
		}.start();
	}
	private void loadHomeActivity() {
		Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
		startActivity(intent);
		finish();
	}
	private String getAppVersion() {
		PackageManager manager = getPackageManager();// 所有的程序管理器
		try {
			// 通过包名定位到本程序
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private void updateApp(String url) {
		// 确认更新
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			tv_update_info.setVisibility(View.VISIBLE);
			HttpUtils utils = new HttpUtils();
			utils.download(url,Environment.getExternalStorageDirectory()
							+ "/mobilesafe2.0.apk",
					new RequestCallBack<File>() {

						@Override
						public void onFailure(HttpException arg0,
								String arg1) {
							// TODO Auto-generated method stub
							Toast.makeText(SplashActivity.this, "更新失败",0).show();
							arg0.printStackTrace();
						}

						@Override
						public void onSuccess(ResponseInfo<File> arg0) {
							// TODO Auto-generated method stub
							File f = new File(Environment.getExternalStorageDirectory()
									+ "/mobilesafe2.0.apk");
							Intent intent = new Intent();
							intent.setAction("android.intent.action.VIEW");
							intent.setDataAndType(Uri.fromFile(f),"application/vnd.android.package-archive");
							startActivityForResult(intent, 0);
						}
						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							// TODO Auto-generated method stub
							super.onLoading(total, current, isUploading);
							int progress = (int) (current * 100 / total);
							
							tv_update_info.setText("下载进度:" + progress + "%");
						}
					});
		} else {
			Toast.makeText(SplashActivity.this, "SD卡不可用", 0).show();
		}
	}
	
}
