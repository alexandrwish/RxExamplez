package com.magenta.maxunits.mobile.activity.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.magenta.maxunits.mobile.MxApplication;
import com.magenta.maxunits.mobile.common.Constants;
import com.magenta.maxunits.mobile.http.HttpClient;
import com.magenta.maxunits.mobile.http.record.LoginResultRecord;
import com.magenta.maxunits.mobile.mc.MxAndroidUtil;
import com.magenta.maxunits.mobile.mc.MxMobile;
import com.magenta.maxunits.mobile.mc.MxSettings;
import com.magenta.maxunits.mobile.rpc.RPCOut;
import com.magenta.maxunits.mobile.service.ServicesRegistry;
import com.magenta.maxunits.mobile.task.GetRemoteSettings;
import com.magenta.maxunits.mobile.task.RemoteSettingsCallback;
import com.magenta.maxunits.mobile.ui.dialogs.InputDialog;
import com.magenta.maxunits.mobile.utils.Checksum;
import com.magenta.maxunits.mobile.utils.StringUtils;
import com.magenta.maxunits.mobile.utils.UserUtils;
import com.magenta.maxunits.mx.R;
import com.magenta.mc.client.android.smoke.activity.SmokeLoginActivity;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.client.Login;
import com.magenta.mc.client.client.XMPPClient;
import com.magenta.mc.client.components.dialogs.SynchronousCallback;
import com.magenta.mc.client.log.MCLogger;
import com.magenta.mc.client.log.MCLoggerFactory;
import com.magenta.mc.client.settings.Settings;
import com.magenta.mc.client.setup.Setup;
import com.magenta.mc.client.util.McDiagnosticAgent;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SmokeLoginActivity implements RemoteSettingsCallback {

    private String oldLocale;
    private SharedPreferences preferences;
    private Activity previous;
    private EditText mEditTextAccount;
    private EditText mEditTextLogin;
    private EditText mEditTextpassword;
    private ProgressDialog mUpdateSettingsProgress;
    private GetRemoteSettings mGetRemoteSettings;

    public void initActivity(Bundle savedInstanceState) {
        setContentView(R.layout.login_activity);
        mEditTextAccount = (EditText) findViewById(R.id.mxAccount);
        mEditTextLogin = (EditText) findViewById(R.id.mxUsername);
        mEditTextpassword = (EditText) findViewById(R.id.mxPassword);
        Button mLoginButton = ((Button) findViewById(com.magenta.mc.client.android.smoke.R.id.login_button));
        mLoginButton.setText(getString(R.string.mx_activity_login_login));
        ((TextView) findViewById(R.id.mcApplicationName)).setText(Settings.get().getAppName());
        List<String> permission = new LinkedList<String>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.INTERNET);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.CALL_PHONE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.VIBRATE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.WRITE_SETTINGS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.WAKE_LOCK);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.READ_LOGS);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.WRITE_CALL_LOG);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            permission.add(Manifest.permission.READ_CALL_LOG);
        }
        ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 27102016);
        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCurrentActivity();
                processLogin();
                HttpClient.getInstance().login(mEditTextAccount.getText().toString(), mEditTextLogin.getText().toString(), mEditTextpassword.getText().toString())
                        .enqueue(new Callback<LoginResultRecord>() {
                            public void onResponse(Call<LoginResultRecord> call, Response<LoginResultRecord> response) {
                                if (response != null && response.body() != null && !response.body().getError()) {
                                    LoginResultRecord record = response.body();
                                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    preferences.edit().putString(Constants.AUTH_TOKEN, record.getToken()).apply();
                                }
                            }

                            public void onFailure(Call<LoginResultRecord> call, Throwable t) {
                                MCLoggerFactory.getLogger(LoginActivity.class).error(t.getMessage(), t);
                            }
                        });
            }
        });
        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setCurrentActivity();
                processSettingsButtonClick();
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mEditTextLogin.setText(preferences.getString("user.login", ""));
        mEditTextAccount.setText(preferences.getString("user.account", ""));
        mEditTextAccount.setVisibility(MxSettings.getInstance().hasFeature(MxSettings.Features.ACCOUNT) ? View.VISIBLE : View.GONE);
        Activity currentActivity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        if (currentActivity != null && !(currentActivity instanceof LoginActivity) && XMPPClient.getInstance().isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, currentActivity.getClass());
            startActivity(intent);
        }
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createGpsDisabledAlert();
        }
        mGetRemoteSettings = new GetRemoteSettings(this);
    }

    protected void setCurrentActivity() {
        // ?
    }

    protected void processLogin() {
        if (Settings.get().isOfflineVersion()) {
            login();
            return;
        }
        String login = mEditTextLogin.getText().toString().trim();
        String account = mEditTextAccount.getText().toString().trim();
        preferences.edit().putString("user.login", login).putString("user.account", account).apply();
        MxSettings.getInstance().setUserAccount(account);
        if (account.isEmpty() || login.isEmpty() || mEditTextpassword.getText().toString().trim().isEmpty()) {
            return;
        }
        try {
            final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus() != null ? getCurrentFocus().getWindowToken() : null, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignore) {
        }
        mUpdateSettingsProgress = new ProgressDialog(this);
        mUpdateSettingsProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mUpdateSettingsProgress.setMessage(getString(R.string.sync_in_progress));
        mUpdateSettingsProgress.setCancelable(false);
        mUpdateSettingsProgress.show();
        mGetRemoteSettings.update();
    }

    protected void checkHistory(Runnable runnable) {
        MxApplication.getInstance().checkHistory(runnable);
    }

    protected void logSettings() {
        MCLogger logger = MCLoggerFactory.getLogger(LoginActivity.class);
        MxSettings settings = (MxSettings) Setup.get().getSettings();
        logger.debug("Settings start:");
        for (Map.Entry<Object, Object> entry : settings.entrySet()) {
            logger.debug(entry.getKey() + " : " + entry.getValue() + " ;");
        }
        logger.debug("Settings end.");
    }

    protected void processSettingsButtonClick() {
        final String password = StringUtils.toBlank(MxSettings.getInstance().getSettingsPassword());
        if (password != null) {
            InputDialog.showPasswordInput(getString(R.string.mx_settings_locked), getString(R.string.mx_enter_password), new InputDialog.Callback<String>() {
                public void ok(final String value) {
                    if (Checksum.md5(value).equals(password)) {
                        ServicesRegistry.getWorkflowService().showSettings(LoginActivity.this, SettingsActivity.class.hashCode());
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.mx_incorrect_password), Toast.LENGTH_SHORT).show();
                    }
                }

                public void cancel() {
                    // ignore
                }
            }, this);
        } else {
            oldLocale = MxSettings.get().getProperty(Settings.LOCALE_KEY);
            ServicesRegistry.getWorkflowService().showSettings(LoginActivity.this, SettingsActivity.class.hashCode());
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (SettingsActivity.class.hashCode() == requestCode) {
            final String newLocale = MxSettings.get().getProperty(Settings.LOCALE_KEY);
            if (oldLocale == null ? newLocale != null : !oldLocale.equals(newLocale)) { // restart
                finish();
                startActivity(new Intent(this, getClass()));
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mx_login, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mx_menu_login_properties) {
            processSettingsButtonClick();
        } else if (item.getItemId() == R.id.mx_menu_login_exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            MxMobile.getInstance().exit();
        }
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createGpsDisabledAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.mx_activity_login_gpsDisabled)
                .setCancelable(false)
                .setPositiveButton(R.string.mx_activity_login_gpsEnable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                showGpsOptions();
                            }
                        });
        builder.setNegativeButton(R.string.mx_activity_login_cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showGpsOptions() {
        Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(gpsOptionsIntent);
    }

    protected Activity chooseNextActivity() {
        return previous;
    }

    protected void onResume() {
        previous = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        super.onResume();
        installApkUpdateIfPresent();
    }

    private void login() {
        String login = preferences.getString("user.login", "");
        String account = preferences.getString("user.account", "");
        final String password = UserUtils.encodeLoginPassword(mEditTextpassword.getText().toString());
        final String userName = UserUtils.createUserId(login, account);
        Settings.get().setUserId(userName.trim());
        Settings.get().setPassword(password);
        Settings.get().setUpdateApplicationName(account);
        Settings.get().saveSettings();
        checkHistory(new Runnable() {
            public void run() {
                MxApplication.getInstance().setLoginPress(true);
                Login.login(Settings.get().getUserId(), Settings.get().getPassword(),

                        new SynchronousCallback() {
                            public void done(boolean ok) {
                                if ((ok && XMPPClient.getInstance().isLoggedIn()) || Settings.get().isOfflineVersion()) {
                                    McDiagnosticAgent.getInstance().signalCredentials(userName + "|" + password);
                                    new Handler().post(new Runnable() {
                                        public void run() {
                                            if (MxSettings.getInstance().hasFeature(MxSettings.Features.ACCOUNT_CONFIGURATION)) {
                                                RPCOut.reloadJobs();
                                                RPCOut.accountConfiguration();
                                                RPCOut.sendImei(MxAndroidUtil.getImei());
                                                MxApplication.getInstance().setLoginPress(false);
                                                ServicesRegistry.getWorkflowService().showNextActivity(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                logSettings();
            }
        });
    }

    public void getRemoteSettingsSuccess() {
        if (!isFinishing() && mUpdateSettingsProgress.isShowing()) {
            mUpdateSettingsProgress.dismiss();
        }
        login();
    }

    public void getRemoteSettingsError() {
        if (!isFinishing()) {

            if (mUpdateSettingsProgress.isShowing()) {
                mUpdateSettingsProgress.dismiss();
            }
            new AlertDialog.Builder(this)
                    .setTitle(R.string.mx_api_server_connection_error_title)
                    .setMessage(R.string.mx_api_server_connection_error_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.mx_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mGetRemoteSettings.update();
                        }
                    })
                    .setNegativeButton(R.string.mx_cancel, null)
                    .create()
                    .show();
        }
    }

    protected void installApkUpdateIfPresent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.getBoolean("update") && bundle.containsKey("path")) {
            Uri path = Uri.fromFile(new File(bundle.getString("path")));
            Intent updateIntent = new Intent("android.intent.action.VIEW");
            updateIntent.setDataAndType(path, "application/vnd.android.package-archive");
            updateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            updateIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(updateIntent);
            getIntent().removeExtra("update");
        }
    }
}