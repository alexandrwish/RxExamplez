package com.magenta.mc.client.android.ui.activity.common;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.MobileApp;
import com.magenta.mc.client.android.R;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.common.UserStatus;
import com.magenta.mc.client.android.http.HttpClient;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.setup.Setup;
import com.magenta.mc.client.android.service.HttpService;
import com.magenta.mc.client.android.ui.AndroidUI;
import com.magenta.mc.client.android.ui.activity.SettingsActivity;
import com.magenta.mc.client.android.ui.activity.SmokeActivity;
import com.magenta.mc.client.android.ui.delegate.LoginDelegate;
import com.magenta.mc.client.android.ui.dialog.InputDialog;
import com.magenta.mc.client.android.util.Checksum;
import com.magenta.mc.client.android.util.StringUtils;
import com.magenta.mc.client.android.util.UserUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class LoginActivity extends SmokeActivity<LoginDelegate> {

    private String oldLocale;
    private Activity previous;
    private EditText mEditTextAccount;
    private EditText mEditTextLogin;
    private EditText mEditTextPassword;

    public void initActivity(Bundle savedInstanceState) {
        setContentView(R.layout.login_activity);
        Activity currentActivity = ((AndroidUI) Setup.get().getUI()).getCurrentActivity();
        if (currentActivity != null
                && !(currentActivity instanceof LoginActivity)
                && !UserStatus.LOGOUT.equals(McAndroidApplication.getInstance().getStatus())) {
            Intent intent = new Intent(LoginActivity.this, currentActivity.getClass());
            startActivity(intent);
            finish();
            return;
        }
        mEditTextAccount = (EditText) findViewById(R.id.mxAccount);
        mEditTextLogin = (EditText) findViewById(R.id.mxUsername);
        mEditTextPassword = (EditText) findViewById(R.id.mxPassword);
        ((TextView) findViewById(R.id.mcApplicationName)).setText(getString(R.string.mx_app_name));
        Button loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setText(getString(R.string.mx_activity_login_login));
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processLogin();
            }
        });
        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                processSettingsButtonClick();
            }
        });
        mEditTextLogin.setText(Settings.get().getLogin());
        mEditTextAccount.setText(Settings.get().getAccount());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            createGpsDisabledAlert();
        }
        checkPermissions();
    }

    private void checkPermissions() {
        List<String> permission = new LinkedList<>();
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
    }

    protected void processLogin() {
        // TODO: 3/8/17 implement offline
        String login = mEditTextLogin.getText().toString().trim();
        String account = mEditTextAccount.getText().toString().trim();
        String password = UserUtils.encodeLoginPassword(mEditTextPassword.getText().toString());
        if (account.isEmpty() || login.isEmpty() || password.isEmpty()) {
            return;
        } else {
            Settings.SettingsBuilder.get().start().setLogin(login).setAccount(account).apply();
        }
        try {
            final InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus() != null ? getCurrentFocus().getWindowToken() : null, InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignore) {
        }
        HttpClient.getInstance().init();
        startService(new Intent(LoginActivity.this, HttpService.class)
                .putExtra(IntentAttributes.HTTP_TYPE, Constants.LOGIN_TYPE)
                .putExtra(IntentAttributes.HTTP_ACCOUNT, account)
                .putExtra(IntentAttributes.HTTP_LOGIN, login)
                .putExtra(IntentAttributes.HTTP_PASS, password)
        );
    }

    protected void processSettingsButtonClick() {
        final String password = StringUtils.toBlank("");
        if (password != null) {
            InputDialog.showPasswordInput(getString(R.string.mx_settings_locked), getString(R.string.mx_enter_password), new InputDialog.Callback<String>() {
                public void ok(final String value) {
                    if (Checksum.md5(value).equals(password)) {
                        startActivityForResult(new Intent(LoginActivity.this, SettingsActivity.class), SettingsActivity.class.hashCode());
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.mx_incorrect_password), Toast.LENGTH_SHORT).show();
                    }
                }

                public void cancel() {
                    // ignore
                }
            }, this);
        } else {
            oldLocale = Settings.get().getLocale();
            startActivityForResult(new Intent(LoginActivity.this, SettingsActivity.class), SettingsActivity.class.hashCode());
        }
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (SettingsActivity.class.hashCode() == requestCode) {
            final String newLocale = Settings.get().getLocale();
            if (oldLocale == null ? newLocale != null : !oldLocale.equals(newLocale)) {
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
            MobileApp.getInstance().exit();
        }
        return true;
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
        switchToCurrentActivityIfNecessary();
        installApkUpdateIfPresent();
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


    public boolean isHasTitleBar() {
        return false;
    }

    private void switchToCurrentActivityIfNecessary() {
        MCLoggerFactory.getLogger(getClass()).debug("Switch to current activity if necessary");
        if (Setup.isInitialized()) {
            Activity currentActivity = chooseNextActivity();
            String activityClass = currentActivity != null ? currentActivity.getClass().getSimpleName() : "null";
            MCLoggerFactory.getLogger(getClass()).debug("LoginActivity " + activityClass + " " + McAndroidApplication.getInstance().getStatus());
            if (currentActivity != null
                    && !(currentActivity instanceof LoginActivity)
                    && !UserStatus.LOGOUT.equals(McAndroidApplication.getInstance().getStatus())) {
                Intent intent = new Intent(LoginActivity.this, currentActivity.getClass());
                intent.putExtra("FROM_LOGIN_ACTIVITY", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(intent);
            }
        } else {
            MCLoggerFactory.getLogger(getClass()).debug("Setup is not initialized, can't get current activity.");
        }
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void showLoginError() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.try_again)
                .setCancelable(false)
                .setNeutralButton(R.string.mx_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
    }
}