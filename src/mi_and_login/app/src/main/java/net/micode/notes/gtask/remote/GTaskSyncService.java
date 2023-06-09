/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.gtask.remote;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class GTaskSyncService extends Service {
    public final static String ACTION_STRING_NAME = "sync_action_type";

    public final static int ACTION_START_SYNC = 0;

    public final static int ACTION_CANCEL_SYNC = 1;

    public final static int ACTION_INVALID = 2;

    public final static String GTASK_SERVICE_BROADCAST_NAME = "net.micode.notes.gtask.remote.gtask_sync_service";

    public final static String GTASK_SERVICE_BROADCAST_IS_SYNCING = "isSyncing";

    public final static String GTASK_SERVICE_BROADCAST_PROGRESS_MSG = "progressMsg";

    private static GTaskASyncTask mSyncTask = null;

    private static String mSyncProgress = "";


    // 使用 GTaskASyncTask 实例启动同步任务，它是一个单独的类，用于在后台执行同步
    private void startSync() {
        if (mSyncTask == null) {
            mSyncTask = new GTaskASyncTask(this, new GTaskASyncTask.OnCompleteListener() {
                public void onComplete() {
                    mSyncTask = null;
                    sendBroadcast("");
                    stopSelf();
                }
            });
            sendBroadcast("");
            mSyncTask.execute();
        }
    }


    // 取消同步任务，如果它当前正在运行
    private void cancelSync() {
        if (mSyncTask != null) {
            mSyncTask.cancelSync();
        }
    }


    // mSyncTask 字段初始化为 null
    @Override
    public void onCreate() {
        mSyncTask = null;
    }

    // 通过检查 ACTION_STRING_NAME 额外字段并执行适当的操作来处理传入的广播意图
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_STRING_NAME)) {
            switch (bundle.getInt(ACTION_STRING_NAME, ACTION_INVALID)) {
                case ACTION_START_SYNC:
                    startSync();
                    break;
                case ACTION_CANCEL_SYNC:
                    cancelSync();
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }


    // 在系统内存不足时取消同步任务
    @Override
    public void onLowMemory() {
        if (mSyncTask != null) {
            mSyncTask.cancelSync();
        }
    }


    public IBinder onBind(Intent intent) {
        return null;
    }


    // 发送具有当前同步进度和状态的广播意图
    public void sendBroadcast(String msg) {
        mSyncProgress = msg;
        Intent intent = new Intent(GTASK_SERVICE_BROADCAST_NAME);
        intent.putExtra(GTASK_SERVICE_BROADCAST_IS_SYNCING, mSyncTask != null);
        intent.putExtra(GTASK_SERVICE_BROADCAST_PROGRESS_MSG, msg);
        sendBroadcast(intent);
    }

    // 开始同步
    public static void startSync(Activity activity) {
        GTaskManager.getInstance().setActivityContext(activity);
        Intent intent = new Intent(activity, GTaskSyncService.class);
        intent.putExtra(GTaskSyncService.ACTION_STRING_NAME, GTaskSyncService.ACTION_START_SYNC);
        activity.startService(intent);
    }

    // 取消同步
    public static void cancelSync(Context context) {
        Intent intent = new Intent(context, GTaskSyncService.class);
        intent.putExtra(GTaskSyncService.ACTION_STRING_NAME, GTaskSyncService.ACTION_CANCEL_SYNC);
        context.startService(intent);
    }

    // 正在同步
    public static boolean isSyncing() {
        return mSyncTask != null;
    }


    // 获取进度
    public static String getProgressString() {
        return mSyncProgress;
    }
}
