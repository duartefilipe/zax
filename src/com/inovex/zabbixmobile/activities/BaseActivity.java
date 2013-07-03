package com.inovex.zabbixmobile.activities;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.inovex.zabbixmobile.R;
import com.inovex.zabbixmobile.data.ZabbixDataService;
import com.inovex.zabbixmobile.data.ZabbixDataService.OnLoginProgressListener;
import com.inovex.zabbixmobile.data.ZabbixDataService.ZabbixDataBinder;

public abstract class BaseActivity extends SherlockFragmentActivity implements
		ServiceConnection, OnLoginProgressListener {

	protected ZabbixDataService mZabbixService;

	private ProgressDialog mLoginProgress;

	protected ActionBar mActionBar;

	private static final String TAG = BaseActivity.class.getSimpleName();

	/** Defines callbacks for service binding, passed to bindService() */
	@Override
	public void onServiceConnected(ComponentName className, IBinder service) {
		ZabbixDataBinder binder = (ZabbixDataBinder) service;
		mZabbixService = binder.getService();
		mZabbixService.setActivityContext(BaseActivity.this);
		mZabbixService.performZabbixLogin(this);

	}

	@Override
	public void onServiceDisconnected(ComponentName arg0) {
	}

	@Override
	protected void onStart() {
		super.onStart();
		bindService();
	}

	/**
	 * Binds the Zabbix service.
	 */
	protected void bindService() {
		Intent intent = new Intent(this, ZabbixDataService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		mActionBar = getSupportActionBar();

		mActionBar.setHomeButtonEnabled(true);
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setDisplayShowTitleEnabled(true);
		super.onCreate(arg0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// unbindService(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG , "onDestroy");
		if (isFinishing()) {
			Log.d(TAG, "unbindService");
			unbindService(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onLoginStarted() {
		disableUI();
		mLoginProgress = new ProgressDialog(BaseActivity.this);
		mLoginProgress.setTitle(R.string.zabbix_login);
		mLoginProgress.setMessage(getResources().getString(
				R.string.zabbix_login_in_progress));
		// mLoginProgress.setCancelable(false);
		mLoginProgress.setIndeterminate(true);
		mLoginProgress.show();
	}

	@Override
	public void onLoginFinished(boolean success) {
		if (mLoginProgress != null)
			mLoginProgress.dismiss();
		if (success) {
			Toast.makeText(this, R.string.zabbix_login_successful,
					Toast.LENGTH_LONG).show();
			enableUI();
		}
	}

	protected abstract void disableUI();

	protected abstract void enableUI();
}
