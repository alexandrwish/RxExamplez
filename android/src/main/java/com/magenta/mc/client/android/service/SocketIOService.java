package com.magenta.mc.client.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.mc.MxSettings;
import com.magenta.mc.client.android.mc.client.Login;
import com.magenta.mc.client.android.mc.log.MCLoggerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
import java.util.TimerTask;
import java.util.TreeMap;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;

public class SocketIOService extends Service {

    protected final Map<Long, String> mReferences = new TreeMap<>(new Comparator<Long>() {

        public int compare(Long k1, Long k2) {
            return k1.compareTo(k2);
        }
    });
    protected final Gson mGson = new Gson();
    private final String JOB_NEW = "newJob";
    private final String JOB_CHANGED = "jobChanged";
    private final String JOB_REMOVE = "jobCancel";
    private final String LOG_NEED = "logNeed";
    private final String LOG_NEED_CANCEL = "logNotNeed";
    private final String NEW_USER_LOGIN = "newUserLogIn";
    private final String REPOST = "repost";
    protected Socket mSocket;
    protected long mTimeStamp;
    protected TimerTask mUpdateTask = getUpdateTask();


    public void onCreate() {
        super.onCreate();
        try {
            IO.Options options = new IO.Options();
            options.path = "/pda.io";
            mSocket = IO.socket(getSocketUrl(), options);
        } catch (URISyntaxException e) {
            MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
            return;
        }
        //connection messages
        mSocket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            public void call(Object... args) {
                if (args.length == 0) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error("EVENT_CONNECT_ERROR");
                    return;
                } else {
                    MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_CONNECT_ERROR: " + args[0].toString());
                }
                if (isAuthFailed(args[0])) {
                    onLoginError();
                }
            }
        }).on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            public void call(Object... args) {
                onConnected();
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            public void call(Object... args) {
                onDisconnected();
            }
        }).on(Socket.EVENT_CONNECTING, new Emitter.Listener() {
            public void call(Object... args) {
                onConnecting();
            }
        }).on(JOB_NEW, new Emitter.Listener() {
            public void call(Object... args) {
                MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("Socket [%s] get message for user %s", mSocket.id(), Settings.get().getLogin()));
                if (args.length < 2) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error("Incorrect data packet from server. No args present for " + JOB_NEW, new RuntimeException());
                    return;
                }
                try {
                    String json = (String) args[args.length - 2];
                    Ack ack = (Ack) args[args.length - 1];
                    MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("JOB NEW has arrived %s", json));
                    onJobChanged(json, ack);
                } catch (JSONException | ClassCastException | SQLException e) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
                }
            }
        }).on(JOB_CHANGED, new Emitter.Listener() {
            public void call(Object... args) {
                MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("Socket [%s] get message for user %s", mSocket.id(), Settings.get().getLogin()));
                if (args.length < 2) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error("Incorrect data packet from server. No args present for " + JOB_CHANGED, new RuntimeException());
                    return;
                }
                try {
                    String json = (String) args[args.length - 2];
                    Ack ack = (Ack) args[args.length - 1];
                    MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("JOB CHANGE has arrived %s", json));
                    onJobChanged(json, ack);
                } catch (SQLException | JSONException | ClassCastException e) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
                }
            }
        }).on(JOB_REMOVE, new Emitter.Listener() {
            public void call(Object... args) {
                MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("Socket [%s] get message for user %s", mSocket.id(), Settings.get().getLogin()));
                if (args.length < 2) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error("Incorrect data packet from server. No args present for " + JOB_REMOVE, new RuntimeException());
                    return;
                }
                try {
                    String json = (String) args[args.length - 2];
                    Ack ack = (Ack) args[args.length - 1];
                    MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("JOB REMOVE has arrived %s", json));
                    onJobChanged(json, ack);
                } catch (JSONException | ClassCastException | SQLException e) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
                }
            }
        }).on(LOG_NEED, new Emitter.Listener() {
            public void call(Object... args) {
                MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("Socket [%s] get message for user %s", mSocket.id(), Settings.get().getLogin()));
                if (args.length < 2) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error("Incorrect data packet from server. No args present for " + LOG_NEED, new RuntimeException());
                    return;
                }
                try {
                    String json = (String) args[args.length - 2];
                    Ack ack = (Ack) args[args.length - 1];
                    MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("LOG NEED has arrived %s", json));
                    onLogRequested(json, ack);
                } catch (JSONException | ClassCastException e) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
                }

            }
        }).on(LOG_NEED_CANCEL, new Emitter.Listener() {
            public void call(Object... args) {
                if (args.length < 2) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error("Incorrect data packet from server. No args present for " + LOG_NEED_CANCEL, new RuntimeException());
                    return;
                }
                try {
                    String json = (String) args[0];
                    Ack ack = (Ack) args[args.length - 1];
                    MCLoggerFactory.getLogger(SocketIOService.class).debug("LOG NEED CANCEL has arrived");
                    onLogAborted(json, ack);
                } catch (JSONException | ClassCastException e) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
                }
            }
        }).on(NEW_USER_LOGIN, new Emitter.Listener() {
            public void call(Object... args) {
                MCLoggerFactory.getLogger(SocketIOService.class).debug("NEW USER LOGIN has arrived");
                if (args.length < 2) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error("Incorrect data packet from server. No args present for " + NEW_USER_LOGIN, new RuntimeException());
                    return;
                }
                try {
                    String json = (String) args[args.length - 2];
                    Ack ack = (Ack) args[args.length - 1];
                    onConflictNewUser(json, ack);
                } catch (JSONException | ClassCastException e) {
                    MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
                }
            }
        });
        mSocket.connect();
        McAndroidApplication.getInstance().getTimer().schedule(mUpdateTask, 5000, 5000);
    }


    protected void onConnecting() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_CONNECTING");
    }

    protected void onConnected() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_CONNECT");
//        MateApplication.getInstance().setStatus(UserStatus.ONLINE); // TODO: 2/27/17 раскоментировать и имплементировать после выпиливания xmpp
        repost();
    }

    protected void onDisconnected() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_DISCONNECT");
//        MateApplication.getInstance().setStatus(UserStatus.OFFLINE); // TODO: 2/27/17 раскоментировать и имплементировать после выпиливания xmpp
    }

    protected void onLoginError() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("FAILED socket.io login");
        Login.getInstance().logout();
//        startActivity(new Intent(this, MateApplication.getInstance().getWorkflowService().getLoginActivity()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK)); // TODO: 2/27/17 раскоментировать и имплементировать после выпиливания xmpp
    }

    protected void onJobChanged(String json, Ack ack) throws JSONException, SQLException {
        synchronized (mReferences) {
            JSONObject rootJson = new JSONObject(json);
            JSONObject messageJson = rootJson.getJSONObject("message");
            String key = rootJson.optString("key");
            MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("Callback %s ack key", key));
            ack.call(key);
            // TODO: 2/27/17 implement
//            if (MateApplication.getInstance().getMainSession().getStatusEntityDao().count() > 0) {
//                MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("Status synchronisation in progress, reject update with key %s", key));
//            } else {
//                UpdateRunEvent event = mGson.fromJson(messageJson.getJSONObject("properties").optString("msg"), UpdateRunEvent.class);
//                Long k = -1L;
//                for (Map.Entry<Long, String> entry : mReferences.entrySet()) {
//                    if (entry.getValue().equalsIgnoreCase(event.getReference())) {
//                        k = entry.getKey();
//                        break;
//                    }
//                }
//                if (k > 0) {
//                    mReferences.remove(k);
//                }
//                mReferences.put(event.getTimestamp(), event.getReference());
//            }
        }
    }

    protected void onLogRequested(String json, Ack ack) throws JSONException {
        JSONObject rootJson = new JSONObject(json);
        String key = rootJson.getString("key");
        JSONObject messageJson = rootJson.getJSONObject("message");
        Long requestId = messageJson.getLong("id");
        Long startDate = messageJson.getLong("start");
        Long endDate = messageJson.getLong("end");
//        startService(LogService.getIntentNewRequest(this, requestId, startDate, endDate));
        MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("LOG NEED callback, %s ack key", key));
        ack.call(key);
    }

    protected void onLogAborted(String json, Ack ack) throws JSONException {
        JSONObject rootJson = new JSONObject(json);
        String key = rootJson.getString("key");
        Long requestId = rootJson.getLong("message");
//        EventBus.getDefault().post(new CancelSendChunksEvent(requestId));
        MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("LOG ABORTED callback, %s ack key", key));
        ack.call(key);
    }

    protected String getSocketUrl() {
        String port = "80"; // TODO: 2/6/17 impl
        return ("443".equals(port) ? "https://" : "http://") + MxSettings.get().getProperty(com.magenta.mc.client.android.mc.settings.Settings.HOST) + ":" + port + Constants.SOCKET_IO_POSTFIX + "?" + Constants.AUTH_TOKEN + "=" + Settings.get().getAuthToken();
    }

    protected boolean isAuthFailed(Object arg) {
        if (!(arg instanceof EngineIOException)) {
            return false;
        }
        EngineIOException engineException = (EngineIOException) arg;
        Throwable cause = engineException.getCause();
        if (cause == null) {
            return false;
        }
        String causeMessage = cause.getMessage();
        return "401".equals(causeMessage) || "403".equals(causeMessage);
    }

    protected void onConflictNewUser(String json, Ack ack) throws JSONException {
        JSONObject rootJson = new JSONObject(json);
        String key = rootJson.getString("key");
        MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("NEW USER LOGIN callback, %s ack key", key));
        ack.call(key);
        dropUser();
    }

    protected void dropUser() {
        Login.getInstance().logout();
        // TODO: 2/27/17 раскоментировать и имплементировать после выпиливания xmpp
//        EventBus.getDefault().postSticky(new DropUserEvent());
//        startActivity(new Intent(this, MateApplication.getInstance().getWorkflowService().getLoginActivity()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));

    }

    private void repost() {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit(REPOST);
        } else {
            MCLoggerFactory.getLogger(SocketIOService.class).debug("Reload jobs FAILED - Socket in the incorrect state");
        }
    }

    protected TimerTask getUpdateTask() {
        return new TimerTask() {
            public void run() {
//                if (System.currentTimeMillis() - mTimeStamp > 10000) {
//                    synchronized (mReferences) {
//                        if (!mReferences.isEmpty()) {
//                            try {
//                                MCLoggerFactory.getLogger(SocketIOService.class).debug("Start loading updates from server after update status");
//                                EventBus.getDefault().postSticky(new WaitEvent(true));
//                                for (Map.Entry<Long, String> e : mReferences.entrySet()) {
//                                    JobsResultRecord jobsResultRecord = MateApplication.getInstance().getApiClient().getJob(e.getValue());
//                                    Cache cache = MateApplication.getInstance().getCache();
//                                    boolean find = false;
//                                    if (jobsResultRecord.getJob() != null && !jobsResultRecord.getJob().isEmpty()) {
//                                        for (JobRecord record : (List<JobRecord>) jobsResultRecord.getJob()) {
//                                            cache.addUpdate(record.getReference());
//                                            cache.update(record);
//                                            if (e.getValue().equalsIgnoreCase(record.getReference())) {
//                                                find = true;
//                                            }
//                                        }
//                                        if (!find) {
//                                            cache.removeJob(e.getValue());
//                                        }
//                                    } else {
//                                        cache.removeJob(e.getValue());
//                                    }
//                                }
//                                if (MateApplication.getInstance().getCache().hasUpdate()) {
//                                    EventBus.getDefault().postSticky(new JobUpdateEvent(true));
//                                    MateSoundPool.getInstance().playSound(SoundType.SOUND_UPDATE.getNum(), true);
//                                } else {
//                                    EventBus.getDefault().postSticky(new JobUpdateEvent(false));
//                                }
//                                mReferences.clear();
//                            } finally {
//                                EventBus.getDefault().postSticky(new WaitEvent(false));
//                                MCLoggerFactory.getLogger(SocketIOService.class).debug("Stop loading updates from server after update status");
//                            }
//                        }
//                    }
//                }
            }
        };
    }

    public IBinder onBind(Intent intent) {
        return new Binder();
    }
}
