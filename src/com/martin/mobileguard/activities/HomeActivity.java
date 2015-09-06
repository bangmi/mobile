package com.martin.mobileguard.activities;

import com.martin.mobileguard.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {
	private GridView griviView;
	private SharedPreferences sp;
	private String[] homeName = { "手机安全", "通信卫士", "应用管理", "进程管理", "流量管理",
			"手机杀毒", "缓存清理", "高级工具", "个人设置" };
	private int[] homeIcon = new int[] { R.drawable.mp1, R.drawable.mp2,
			R.drawable.app_selector, R.drawable.mp4, R.drawable.mp5,
			R.drawable.mp6, R.drawable.mp7, R.drawable.mp8, R.drawable.mp9 };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		griviView = (GridView) findViewById(R.id.gridviewhome);
		griviView.setAdapter(new gridviewAdapter());
		griviView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					if (sp.getString("password", "").length() > 0) {
						verifyPassWord();
					} else {
						setPassWord();
					}
					break;

				default:
					break;
				}
			}
		});
	}

	AlertDialog verifypwdDialog;

	protected void verifyPassWord() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(this, R.layout.dialog_verifypwd, null);
		et_pwd = (EditText) view.findViewById(R.id.et_pwd);
		btn_submit = (Button) view.findViewById(R.id.btn_submit);
		btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				verifypwdDialog.dismiss();
			}
		});
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = et_pwd.getText().toString();
				if (TextUtils.isEmpty(pwd) ) {
					Toast.makeText(getApplicationContext(), "请输入密码", 0).show();
					return;
				}	
				//验证密码是否正确
				if(sp.getString("password", null).equals(pwd)){
					Intent intent=new Intent(HomeActivity.this,Setup1Activity.class);
					startActivity(intent);
				}else {
					Toast.makeText(getApplicationContext(), "密码错误", 0).show();
					return;
				}					
				verifypwdDialog.dismiss();
			}
		});
		builder.setView(view);
		verifypwdDialog=builder.show();
	}

	private EditText et_pwd;
	private EditText et_pwd_confirm;
	private Button btn_submit;
	private Button btn_cancle;

	private AlertDialog setpwdDialog;

	private void setPassWord() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		View view = View.inflate(this, R.layout.dialog_setpwd, null);
		et_pwd = (EditText) view.findViewById(R.id.et_pwd);
		et_pwd_confirm = (EditText) view.findViewById(R.id.et_pwd_confirm);
		btn_submit = (Button) view.findViewById(R.id.btn_submit);
		btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
		builder.setView(view);
		setpwdDialog = builder.show();
		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setpwdDialog.dismiss();
			}
		});
		btn_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = et_pwd.getText().toString();
				String pwd_confirm = et_pwd_confirm.getText().toString();
				if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
					Toast.makeText(getApplicationContext(), "请输入密码", 0).show();
					return;
				}
				if (!pwd.equals(pwd_confirm)) {
					Toast.makeText(getApplicationContext(), "密码不一致，请核实后再输入", 0)
							.show();
					et_pwd_confirm.setText("");
					et_pwd.setText("");
					return;
				}
				sp.edit().putString("password", pwd).commit();
				Intent intent=new Intent(HomeActivity.this,Setup1Activity.class);
				startActivity(intent);
				setpwdDialog.dismiss();
			}
		});
	}

	class gridviewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return homeName.length;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view;
			if (convertView == null) {
				view = View.inflate(HomeActivity.this, R.layout.item_homegride,
						null);
			} else {
				view = convertView;
			}
			TextView name = (TextView) view.findViewById(R.id.tv_homeName);
			ImageView icon = (ImageView) view.findViewById(R.id.iv_homeIcon);
			name.setText(homeName[position]);
			icon.setBackgroundResource(homeIcon[position]);
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

	}
}
