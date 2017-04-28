package com.magenta.mc.client.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Pair;

import com.google.gson.Gson;
import com.magenta.mc.client.android.McAndroidApplication;
import com.magenta.mc.client.android.binder.SocketBinder;
import com.magenta.mc.client.android.common.Constants;
import com.magenta.mc.client.android.common.IntentAttributes;
import com.magenta.mc.client.android.common.Settings;
import com.magenta.mc.client.android.common.UserStatus;
import com.magenta.mc.client.android.log.MCLoggerFactory;
import com.magenta.mc.client.android.record.UpdateRunEvent;
import com.magenta.mc.client.android.resender.Resender;
import com.magenta.mc.client.android.service.holder.ServiceHolder;
import com.magenta.mc.client.android.ui.activity.common.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class SocketIOService extends Service {

    protected final Gson mGson = new Gson();
    private final PublishSubject<Pair<Long, String>> publisher = PublishSubject.create();
    private final String JOB_NEW = "newJob";
    private final String JOB_CHANGED = "jobChanged";
    private final String JOB_REMOVE = "jobCancel";
    private final String LOG_NEED = "logNeed";
    private final String LOG_NEED_CANCEL = "logNotNeed";
    private final String NEW_USER_LOGIN = "newUserLogIn";
    private final String REPOST = "repost";
    protected Socket mSocket;
    protected long mTimeStamp;
    private Subscription subscribtion;

    public PublishSubject<Pair<Long, String>> getPublisher() {
        return publisher;
    }

    public void onCreate() {
        super.onCreate();
        try {
            IO.Options options = new IO.Options();
            options.path = "/pda.io";
            options.reconnection = true;
            options.reconnectionDelay = 5000;
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
        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            public void call(Object... args) {
                onReconnected();
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
        subscribe();
    }

    protected void subscribe() {
        subscribtion = publisher.subscribeOn(Schedulers.io())
                .delay(5, TimeUnit.SECONDS)
                .filter(new Func1<Pair<Long, String>, Boolean>() {
                    public Boolean call(Pair<Long, String> pair) {
                        return pair.first + 5000 <= mTimeStamp;
                    }
                })
                .subscribe(new Subscriber<Pair<Long, String>>() {
                    public void onCompleted() {
                        //место для вашей рекламы
                    }

                    public void onError(Throwable e) {
                        MCLoggerFactory.getLogger(SocketIOService.class).error(e.getMessage(), e);
                    }

                    public void onNext(Pair<Long, String> longStringPair) {
                        ServiceHolder.getInstance().startService(HttpService.class, Pair.create(IntentAttributes.HTTP_TYPE, Constants.JOBS_TYPE));
                    }
                });
    }

    protected void onConnecting() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_CONNECTING");
    }

    protected void onConnected() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_CONNECT");
        McAndroidApplication.getInstance().setStatus(UserStatus.ONLINE);
        repost();
    }

    protected void onReconnected() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_RECONNECT");
        Resender.getInstance().start();
    }

    protected void onDisconnected() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("EVENT_DISCONNECT");
        McAndroidApplication.getInstance().setStatus(UserStatus.OFFLINE);
    }

    protected void onLoginError() {
        MCLoggerFactory.getLogger(SocketIOService.class).debug("FAILED socket.io login");
        startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    protected void onJobChanged(String json, Ack ack) throws JSONException, SQLException {
//        JSONObject rootJson = new JSONObject(json);
//        JSONObject messageJson = rootJson.getJSONObject("message");
//        String key = rootJson.optString("key");
//        MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("Callback %s ack key", key));
//        ack.call(key);
        UpdateRunEvent event = /*mGson.fromJson(messageJson.getJSONObject("properties").optString("msg"), UpdateRunEvent.class)*/new UpdateRunEvent(System.currentTimeMillis(), "");
        mTimeStamp = System.currentTimeMillis();
        publisher.onNext(Pair.create(System.currentTimeMillis(), event.getReference()));
        // TODO: 2/27/17 has states?
    }

    protected void onLogRequested(String json, Ack ack) throws JSONException {
        JSONObject rootJson = new JSONObject(json);
        String key = rootJson.getString("key");
//        JSONObject messageJson = rootJson.getJSONObject("message");
//        Long requestId = messageJson.getLong("id");
//        Long startDate = messageJson.getLong("start");
//        Long endDate = messageJson.getLong("end");
//        startService(LogService.getIntentNewRequest(this, requestId, startDate, endDate));
        MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("LOG NEED callback, %s ack key", key));
        ack.call(key);
    }

    protected void onLogAborted(String json, Ack ack) throws JSONException {
        JSONObject rootJson = new JSONObject(json);
        String key = rootJson.getString("key");
//        Long requestId = rootJson.getLong("message");
//        EventBus.getDefault().post(new CancelSendChunksEvent(requestId));
        MCLoggerFactory.getLogger(SocketIOService.class).debug(String.format("LOG ABORTED callback, %s ack key", key));
        ack.call(key);
    }

    protected String getSocketUrl() {
        String port = Settings.get().getPort();
        return ("443".equals(port) ? "https://" : "http://") + Settings.get().getHost() + ":" + port + Constants.SOCKET_IO_POSTFIX + "?" + Constants.AUTH_TOKEN + "=" + Settings.get().getAuthToken();
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
//        Login.getInstance().logout();
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

    public IBinder onBind(Intent intent) {
        return new SocketBinder(this);
    }
}