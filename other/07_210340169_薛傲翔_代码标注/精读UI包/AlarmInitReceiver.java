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

package net.micode.notes.ui;

import android.app.AlarmManager; //对系统闹钟服务的接口
import android.app.PendingIntent;//获取闹钟通知信息
import android.content.BroadcastReceiver;//接受来自系统和应用的广播并对其进行响应的组件
import android.content.ContentUris;//用来处理“content”的约束uri对象
import android.content.Context;//翻译上下文环境
import android.content.Intent;//解决应用的各项组件之间的通讯
import android.database.Cursor;//数据库的游标操作

import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.NoteColumns;


public class AlarmInitReceiver extends BroadcastReceiver {

    //创建对象用例
    private static final String [] PROJECTION = new String [] {
        NoteColumns.ID,
        NoteColumns.ALERTED_DATE
    };

    //对数据库的操作，调用标签ID和闹钟时间
    private static final int COLUMN_ID                = 0;
    private static final int COLUMN_ALERTED_DATE      = 1;

    //重写onReceive函数
    //onReceive是广播的程序执行接收器
    @Override
    public void onReceive(Context context, Intent intent) {
        //System.currentTimeMillis()产生一个当前的毫秒
        //这个毫秒从1970年1月1日0时起的毫秒数
        long currentDate = System.currentTimeMillis();

        //Cursor在这里的作用是通过过查找数据库中的标签内容，找到和当前系统时间相等的标签
        Cursor c = context.getContentResolver().query(Notes.CONTENT_NOTE_URI,
                PROJECTION,
                NoteColumns.ALERTED_DATE + ">? AND " + NoteColumns.TYPE + "=" + Notes.TYPE_NOTE,
                new String[] { String.valueOf(currentDate) },
                null);

        //这里是根据数据库里的闹钟时间创建了一个闹钟机制
        // 下面的代码主要表现了其中一种创建闹钟机制，如新建Intent、PendingIntent以及AlarmManager等
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    long alertDate = c.getLong(COLUMN_ALERTED_DATE);
                    Intent sender = new Intent(context, AlarmReceiver.class);
                    sender.setData(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, c.getLong(COLUMN_ID)));
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, sender, 0);
                    AlarmManager alermManager = (AlarmManager) context
                            .getSystemService(Context.ALARM_SERVICE);
                    alermManager.set(AlarmManager.RTC_WAKEUP, alertDate, pendingIntent);
                } while (c.moveToNext());
            }
            c.close();
        }
    }
}
