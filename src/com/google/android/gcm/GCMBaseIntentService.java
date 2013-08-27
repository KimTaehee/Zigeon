/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gcm;

import static com.google.android.gcm.GCMConstants.ERROR_SERVICE_NOT_AVAILABLE;
import static com.google.android.gcm.GCMConstants.EXTRA_ERROR;
import static com.google.android.gcm.GCMConstants.EXTRA_REGISTRATION_ID;
import static com.google.android.gcm.GCMConstants.EXTRA_SPECIAL_MESSAGE;
import static com.google.android.gcm.GCMConstants.EXTRA_TOTAL_DELETED;
import static com.google.android.gcm.GCMConstants.EXTRA_UNREGISTERED;
import static com.google.android.gcm.GCMConstants.INTENT_FROM_GCM_LIBRARY_RETRY;
import static com.google.android.gcm.GCMConstants.INTENT_FROM_GCM_MESSAGE;
import static com.google.android.gcm.GCMConstants.INTENT_FROM_GCM_REGISTRATION_CALLBACK;
import static com.google.android.gcm.GCMConstants.VALUE_DELETED_MESSAGES;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import kr.re.ec.zigeon.util.*;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.SystemClock;

// TODO: Auto-generated Javadoc
/**
 * Skeleton for application-specific {@link IntentService}s responsible for
 * handling communication from Google Cloud Messaging service.
 * <p>
 * The abstract methods in this class are called from its worker thread, and
 * hence should run in a limited amount of time. If they execute long
 * operations, they should spawn new threads, otherwise the worker thread will
 * be blocked.
 */
public abstract class GCMBaseIntentService extends IntentService {

    /** The Constant TAG. */
    public static final String TAG = "GCMBaseIntentService";

    // wakelock
    /** The Constant WAKELOCK_KEY. */
    private static final String WAKELOCK_KEY = "GCM_LIB";
    
    /** The s wake lock. */
    private static PowerManager.WakeLock sWakeLock;

    // Java lock used to synchronize access to sWakelock
    /** The Constant LOCK. */
    private static final Object LOCK = GCMBaseIntentService.class;

    /** The m sender id. */
    private final String mSenderId;

    // instance counter
    /** The s counter. */
    private static int sCounter = 0;

    /** The Constant sRandom. */
    private static final Random sRandom = new Random();

    /** The Constant MAX_BACKOFF_MS. */
    private static final int MAX_BACKOFF_MS =
        (int) TimeUnit.SECONDS.toMillis(3600); // 1 hour

    // token used to check intent origin
    /** The Constant TOKEN. */
    private static final String TOKEN =
            Long.toBinaryString(sRandom.nextLong());
    
    /** The Constant EXTRA_TOKEN. */
    private static final String EXTRA_TOKEN = "token";

    /**
     * Subclasses must create a public no-arg constructor and pass the
     * sender id to be used for registration.
     *
     * @param senderId the sender id
     */
    protected GCMBaseIntentService(String senderId) {
        // name is used as base name for threads, etc.
        super("GCMIntentService-" + senderId + "-" + (++sCounter));
        mSenderId = senderId;
    }

    /**
     * Called when a cloud message has been received.
     *
     * @param context application's context.
     * @param intent intent containing the message payload as extras.
     */
    protected abstract void onMessage(Context context, Intent intent);

    /**
     * Called when the GCM server tells pending messages have been deleted
     * because the device was idle.
     *
     * @param context application's context.
     * @param total total number of collapsed messages
     */
    protected void onDeletedMessages(Context context, int total) {
    }

    /**
     * Called on a registration error that could be retried.
     *
     * <p>By default, it does nothing and returns {@literal true}, but could be
     * overridden to change that behavior and/or display the error.
     *
     * @param context application's context.
     * @param errorId error id returned by the GCM service.
     *
     * @return if {@literal true}, failed operation will be retried (using
     *         exponential backoff).
     */
    protected boolean onRecoverableError(Context context, String errorId) {
        return true;
    }

    /**
     * Called on registration or unregistration error.
     *
     * @param context application's context.
     * @param errorId error id returned by the GCM service.
     */
    protected abstract void onError(Context context, String errorId);

    /**
     * Called after a device has been registered.
     *
     * @param context application's context.
     * @param registrationId the registration id returned by the GCM service.
     */
    protected abstract void onRegistered(Context context,
            String registrationId);

    /**
     * Called after a device has been unregistered.
     *
     * @param context application's context.
     * @param registrationId the registration id that was previously registered.
     */
    protected abstract void onUnregistered(Context context,
            String registrationId);

    /* (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    public final void onHandleIntent(Intent intent) {
        try {
            Context context = getApplicationContext();
            String action = intent.getAction();
            if (action.equals(INTENT_FROM_GCM_REGISTRATION_CALLBACK)) {
                handleRegistration(context, intent);
            } else if (action.equals(INTENT_FROM_GCM_MESSAGE)) {
                // checks for special messages
                String messageType =
                        intent.getStringExtra(EXTRA_SPECIAL_MESSAGE);
                if (messageType != null) {
                    if (messageType.equals(VALUE_DELETED_MESSAGES)) {
                        String sTotal =
                                intent.getStringExtra(EXTRA_TOTAL_DELETED);
                        if (sTotal != null) {
                            try {
                                int total = Integer.parseInt(sTotal);
                                LogUtil.v("Received deleted messages " +
                                        "notification: " + total);
                                onDeletedMessages(context, total);
                            } catch (NumberFormatException e) {
                                LogUtil.e("GCM returned invalid number of " +
                                        "deleted messages: " + sTotal);
                            }
                        }
                    } else {
                        // application is not using the latest GCM library
                        LogUtil.e("Received unknown special message: " +
                                messageType);
                    }
                } else {
                    onMessage(context, intent);
                }
            } else if (action.equals(INTENT_FROM_GCM_LIBRARY_RETRY)) {
                String token = intent.getStringExtra(EXTRA_TOKEN);
                if (!TOKEN.equals(token)) {
                    // make sure intent was generated by this class, not by a
                    // malicious app.
                    LogUtil.e("Received invalid token: " + token);
                    return;
                }
                // retry last call
                if (GCMRegistrar.isRegistered(context)) {
                    GCMRegistrar.internalUnregister(context);
                } else {
                    GCMRegistrar.internalRegister(context, mSenderId);
                }
            }
        } finally {
            // Release the power lock, so phone can get back to sleep.
            // The lock is reference-counted by default, so multiple
            // messages are ok.

            // If onMessage() needs to spawn a thread or do something else,
            // it should use its own lock.
            synchronized (LOCK) {
                // sanity check for null as this is a public method
                if (sWakeLock != null) {
                    LogUtil.v("Releasing wakelock Start");
                    sWakeLock.release();
                    LogUtil.v("Releasing wakelock end");
                } else {
                    // should never happen during normal workflow
                    LogUtil.e("Wakelock reference is null");
                }
            }
        }
    }

    /**
     * Called from the broadcast receiver.
     * <p>
     * Will process the received intent, call handleMessage(), registered(),
     * etc. in background threads, with a wake lock, while keeping the service
     * alive.
     *
     * @param context the context
     * @param intent the intent
     * @param className the class name
     */
    static void runIntentInService(Context context, Intent intent,
            String className) {
        synchronized (LOCK) {
            if (sWakeLock == null) {
                // This is called from BroadcastReceiver, there is no init.
                PowerManager pm = (PowerManager)
                        context.getSystemService(Context.POWER_SERVICE);
                sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        WAKELOCK_KEY);
            }
        }
        LogUtil.v("Acquiring wakelock");
        sWakeLock.acquire();
        LogUtil.v("className" + className);
        intent.setClassName(context, className);
        context.startService(intent);
    }

    /**
     * Handle registration.
     *
     * @param context the context
     * @param intent the intent
     */
    private void handleRegistration(final Context context, Intent intent) {
        String registrationId = intent.getStringExtra(EXTRA_REGISTRATION_ID);
        String error = intent.getStringExtra(EXTRA_ERROR);
        String unregistered = intent.getStringExtra(EXTRA_UNREGISTERED);
        LogUtil.d("handleRegistration: registrationId = " + registrationId +
                ", error = " + error + ", unregistered = " + unregistered);

        // registration succeeded
        if (registrationId != null) {
            GCMRegistrar.resetBackoff(context);
            GCMRegistrar.setRegistrationId(context, registrationId);
            onRegistered(context, registrationId);
            return;
        }

        // unregistration succeeded
        if (unregistered != null) {
            // Remember we are unregistered
            GCMRegistrar.resetBackoff(context);
            String oldRegistrationId =
                    GCMRegistrar.clearRegistrationId(context);
            onUnregistered(context, oldRegistrationId);
            return;
        }

        // last operation (registration or unregistration) returned an error;
        LogUtil.d("Registration error: " + error);
        // Registration failed
        if (ERROR_SERVICE_NOT_AVAILABLE.equals(error)) {
            boolean retry = onRecoverableError(context, error);
            if (retry) {
                int backoffTimeMs = GCMRegistrar.getBackoff(context);
                int nextAttempt = backoffTimeMs / 2 +
                        sRandom.nextInt(backoffTimeMs);
                LogUtil.d("Scheduling registration retry, backoff = " +
                        nextAttempt + " (" + backoffTimeMs + ")");
                Intent retryIntent =
                        new Intent(INTENT_FROM_GCM_LIBRARY_RETRY);
                retryIntent.putExtra(EXTRA_TOKEN, TOKEN);
                PendingIntent retryPendingIntent = PendingIntent
                        .getBroadcast(context, 0, retryIntent, 0);
                AlarmManager am = (AlarmManager)
                        context.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + nextAttempt,
                        retryPendingIntent);
                // Next retry should wait longer.
                if (backoffTimeMs < MAX_BACKOFF_MS) {
                  GCMRegistrar.setBackoff(context, backoffTimeMs * 2);
                }
            } else {
                LogUtil.d("Not retrying failed operation");
            }
        } else {
            // Unrecoverable error, notify app
            onError(context, error);
        }
    }

}
