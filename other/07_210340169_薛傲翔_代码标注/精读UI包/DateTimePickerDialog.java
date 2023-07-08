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


//前面几个UI包注释了这几个头文件的内容
import java.util.Calendar;

import net.micode.notes.R;
import net.micode.notes.ui.DateTimePicker;
import net.micode.notes.ui.DateTimePicker.OnDateTimeChangedListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;   //取消对话框的构造器运行
import android.content.DialogInterface.OnClickListener;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

public class DateTimePickerDialog extends AlertDialog implements OnClickListener {

    //创建一个Calendar类型的变量 mDate，方便时间的操作
    private Calendar mDate = Calendar.getInstance();
    private boolean mIs24HourView;

    //声明一个时间日期滚动选择控件 mOnDateTimeSetListener
    private OnDateTimeSetListener mOnDateTimeSetListener;

    //DateTimePicker控件，控件一般用于让用户可以从日期列表中选择单个值。
    //运行时，单击控件边上的下拉箭头，会显示为两个部分：一个下拉列表，一个用于选择日期的
    private DateTimePicker mDateTimePicker;

    public interface OnDateTimeSetListener {
        void OnDateTimeSet(AlertDialog dialog, long date);
    }

    //对该界面对话框的实例化
    public DateTimePickerDialog(Context context, long date) {

        //对数据库的操作
        super(context);
        mDateTimePicker = new DateTimePicker(context);

        //添加一个子视图
        setView(mDateTimePicker);
        mDateTimePicker.setOnDateTimeChangedListener(new OnDateTimeChangedListener() {
            public void onDateTimeChanged(DateTimePicker view, int year, int month,
                    int dayOfMonth, int hourOfDay, int minute) {

                //将视图中的各选项设置为系统当前时间
                mDate.set(Calendar.YEAR, year);
                mDate.set(Calendar.MONTH, month);
                mDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                mDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mDate.set(Calendar.MINUTE, minute);
                updateTitle(mDate.getTimeInMillis());
            }
        });

        //得到系统时间
        mDate.setTimeInMillis(date);

        //将秒数设置为0
        mDate.set(Calendar.SECOND, 0);
        mDateTimePicker.setCurrentDate(mDate.getTimeInMillis());

        //设置两个按钮
        setButton(context.getString(R.string.datetime_dialog_ok), this);
        setButton2(context.getString(R.string.datetime_dialog_cancel), (OnClickListener)null);

        set24HourView(DateFormat.is24HourFormat(this.getContext()));

        //时间标准化打印
        updateTitle(mDate.getTimeInMillis());
    }

    public void set24HourView(boolean is24HourView) {
        mIs24HourView = is24HourView;
    }

    //将时间日期滚动选择控件实例化
    public void setOnDateTimeSetListener(OnDateTimeSetListener callBack) {
        mOnDateTimeSetListener = callBack;
    }


    //android开发中常见日期管理工具类（API）——DateUtils：按照上下午显示时间
    private void updateTitle(long date) {
        int flag =
            DateUtils.FORMAT_SHOW_YEAR |
            DateUtils.FORMAT_SHOW_DATE |
            DateUtils.FORMAT_SHOW_TIME;
        flag |= mIs24HourView ? DateUtils.FORMAT_24HOUR : DateUtils.FORMAT_24HOUR;
        setTitle(DateUtils.formatDateTime(this.getContext(), date, flag));
    }


    //第一个参数arg0是接收到点击事件的对话框
    //第二个参数arg1是该对话框上的按钮
    public void onClick(DialogInterface arg0, int arg1) {
        if (mOnDateTimeSetListener != null) {
            mOnDateTimeSetListener.OnDateTimeSet(this, mDate.getTimeInMillis());
        }
    }

}