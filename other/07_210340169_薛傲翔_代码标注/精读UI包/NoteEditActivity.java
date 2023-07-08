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

import android.app.Activity;    //负责与用户交互的组件
import android.app.AlarmManager;    //提供一种系统级的提示服务
import android.app.AlertDialog;     //用于提示和警告用户某些重要的信息
import android.app.PendingIntent;   //用于处理即将发生的事情
import android.app.SearchManager;   //提供对系统搜索服务的访问
import android.appwidget.AppWidgetManager;  //对AppWidget进行管理
import android.content.ContentUris;     //用于获取Uri路径后面的ID部分
import android.content.Context; //用来访问全局信息的接口
import android.content.DialogInterface; //提供一种常用的对话框接口
import android.content.Intent;  //可以在程序运行中连接两个不同的组件
import android.content.SharedPreferences;   //用来获取和修改持久化存储的数据
import android.graphics.Paint;  //创建和现在画笔属性相同的画笔
import android.os.Bundle;   //传递数据
import android.preference.PreferenceManager;    //用来记录应用，以及用户的喜好等
import android.text.Spannable;  //用于处理范围字符后面插入新字符时应用的新样式
import android.text.SpannableString;    //字符串类型，表示一段文本效果
import android.text.TextUtils;  //提供对字符串的一系列操作
import android.text.format.DateUtils;   //用于对时间数据格式化
import android.text.style.BackgroundColorSpan;  //背景色样式，显示可以用来设定文本的背景色
import android.util.Log;    //日志工具类
import android.view.LayoutInflater; //利用XML文件中定义的Viem的id属性来获取相应的View对象
import android.view.Menu;   //用来管理各种菜单项
import android.view.MenuItem;   //菜单项的子菜单
import android.view.MotionEvent;    //触摸屏幕产生的交互
import android.view.View;   //UI组建的实体
import android.view.View.OnClickListener;//点击事件的监听器
import android.view.WindowManager;  //管理Window的添加、更新和删除操作
import android.widget.CheckBox; //多选择按钮
import android.widget.CompoundButton;   //对按钮的交互
import android.widget.CompoundButton.OnCheckedChangeListener;   //按钮的监听
import android.widget.EditText; //与用户进行交互
import android.widget.ImageView;    //显示任意图像
import android.widget.LinearLayout; //是所有子视图在单个方向保持对齐
import android.widget.TextView; //一般用于显示一行或多行文本
import android.widget.Toast;    //包含用户的点击信息

//下面的包都是小米便签所自带的
import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.data.Notes.TextNote;
import net.micode.notes.model.WorkingNote;
import net.micode.notes.model.WorkingNote.NoteSettingChangedListener;
import net.micode.notes.tool.DataUtils;
import net.micode.notes.tool.ResourceParser;
import net.micode.notes.tool.ResourceParser.TextAppearanceResources;
import net.micode.notes.ui.DateTimePickerDialog.OnDateTimeSetListener;
import net.micode.notes.ui.NoteEditText.OnTextViewChangeListener;
import net.micode.notes.widget.NoteWidgetProvider_2x;
import net.micode.notes.widget.NoteWidgetProvider_4x;

//系统自带包
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


    //该类主要是针对标签的编辑
    //继承了系统内部许多和监听有关的类
public class NoteEditActivity extends Activity implements OnClickListener,
        NoteSettingChangedListener, OnTextViewChangeListener {
    private class HeadViewHolder {
        public TextView tvModified;

        public ImageView ivAlertIcon;

        public TextView tvAlertDate;

        public ImageView ibSetBgColor;
    }

        //使用Map实现数据存储
    private static final Map<Integer, Integer> sBgSelectorBtnsMap = new HashMap<Integer, Integer>();
    static {
        sBgSelectorBtnsMap.put(R.id.iv_bg_yellow, ResourceParser.YELLOW);
        sBgSelectorBtnsMap.put(R.id.iv_bg_red, ResourceParser.RED);
        sBgSelectorBtnsMap.put(R.id.iv_bg_blue, ResourceParser.BLUE);
        sBgSelectorBtnsMap.put(R.id.iv_bg_green, ResourceParser.GREEN);
        sBgSelectorBtnsMap.put(R.id.iv_bg_white, ResourceParser.WHITE);
    }

    private static final Map<Integer, Integer> sBgSelectorSelectionMap = new HashMap<Integer, Integer>();
    static {
        //put函数是将指定值和指定键相连
        sBgSelectorSelectionMap.put(ResourceParser.YELLOW, R.id.iv_bg_yellow_select);
        sBgSelectorSelectionMap.put(ResourceParser.RED, R.id.iv_bg_red_select);
        sBgSelectorSelectionMap.put(ResourceParser.BLUE, R.id.iv_bg_blue_select);
        sBgSelectorSelectionMap.put(ResourceParser.GREEN, R.id.iv_bg_green_select);
        sBgSelectorSelectionMap.put(ResourceParser.WHITE, R.id.iv_bg_white_select);
    }

    private static final Map<Integer, Integer> sFontSizeBtnsMap = new HashMap<Integer, Integer>();
    static {
        //put函数是将指定值和指定键相连
        sFontSizeBtnsMap.put(R.id.ll_font_large, ResourceParser.TEXT_LARGE);
        sFontSizeBtnsMap.put(R.id.ll_font_small, ResourceParser.TEXT_SMALL);
        sFontSizeBtnsMap.put(R.id.ll_font_normal, ResourceParser.TEXT_MEDIUM);
        sFontSizeBtnsMap.put(R.id.ll_font_super, ResourceParser.TEXT_SUPER);
    }

    private static final Map<Integer, Integer> sFontSelectorSelectionMap = new HashMap<Integer, Integer>();
    static {
        //put函数是将指定值和指定键相连
        sFontSelectorSelectionMap.put(ResourceParser.TEXT_LARGE, R.id.iv_large_select);
        sFontSelectorSelectionMap.put(ResourceParser.TEXT_SMALL, R.id.iv_small_select);
        sFontSelectorSelectionMap.put(ResourceParser.TEXT_MEDIUM, R.id.iv_medium_select);
        sFontSelectorSelectionMap.put(ResourceParser.TEXT_SUPER, R.id.iv_super_select);
    }

    private static final String TAG = "NoteEditActivity";

    private HeadViewHolder mNoteHeaderHolder;//私有化一个表头
    private View mHeadViewPanel; //私有化一个界面操作mHeadViewPanel，对表头的操作
    private View mNoteBgColorSelector; //私有化一个界面操作mNoteBgColorSelector，对背景颜色的操作
    private View mFontSizeSelector;//私有化一个界面操作mFontSizeSelector，对标签字体的操作
    private EditText mNoteEditor;//声明编辑控件，对文本操作
    private View mNoteEditorPanel;//私有化一个界面操作mNoteEditorPanel，文本编辑的控制板
    private WorkingNote mWorkingNote;//对模板WorkingNote的初始化
    private SharedPreferences mSharedPrefs;//私有化SharedPreferences的数据存储方式
    private int mFontSizeId;//用于操作字体的大小

    private static final String PREFERENCE_FONT_SIZE = "pref_font_size";

    private static final int SHORTCUT_ICON_TITLE_MAX_LEN = 10;

    public static final String TAG_CHECKED = String.valueOf('\u221A');
    public static final String TAG_UNCHECKED = String.valueOf('\u25A1');

    private LinearLayout mEditTextList;//线性布局

    private String mUserQuery;
    private Pattern mPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //对数据库的访问操作
        this.setContentView(R.layout.note_edit);
        if (savedInstanceState == null && !initActivityState(getIntent())) {
            finish();
            return;
        }
        initResources();
    }

    /**
     * Current activity may be killed when the memory is low. Once it is killed, for another time
     * user load this activity, we should restore the former state
     */
    //为防止内存不足时程序的终止，在这里有一个保存现场的函数
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey(Intent.EXTRA_UID)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.putExtra(Intent.EXTRA_UID, savedInstanceState.getLong(Intent.EXTRA_UID));
            if (!initActivityState(intent)) {
                finish();
                return;
            }
            Log.d(TAG, "Restoring from killed activity");
        }
    }

    private boolean initActivityState(Intent intent) {
        /**
         * If the user specified the {@link Intent#ACTION_VIEW} but not provided with id,
         * then jump to the NotesListActivity
         */
        mWorkingNote = null;

        //ID在数据库中找到
        if (TextUtils.equals(Intent.ACTION_VIEW, intent.getAction())) {
            //如果用户实例化标签时，系统并未给出标签ID
            long noteId = intent.getLongExtra(Intent.EXTRA_UID, 0);
            mUserQuery = "";

            /**
             * Starting from the searched result
             */

            //根据键值查找ID
            if (intent.hasExtra(SearchManager.EXTRA_DATA_KEY)) {
                noteId = Long.parseLong(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
                mUserQuery = intent.getStringExtra(SearchManager.USER_QUERY);
            }

            //如果ID在数据库中未找到
            if (!DataUtils.visibleInNoteDatabase(getContentResolver(), noteId, Notes.TYPE_NOTE)) {
                Intent jump = new Intent(this, NotesListActivity.class);

                //程序将跳转到上面声明的intent——jump
                startActivity(jump);
                showToast(R.string.error_note_not_exist);
                finish();
                return false;
            }

            //ID在数据库中找到
            else {
                mWorkingNote = WorkingNote.load(this, noteId);
                if (mWorkingNote == null) {

                    //打印出红色的错误信息
                    Log.e(TAG, "load note failed with note id" + noteId);
                    finish();
                    return false;
                }
            }

            //setSoftInputMode——软键盘输入模式
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                            | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        // intent.getAction()
        // 大多用于broadcast发送广播时给机制（intent）设置一个action，就是一个字符串
        // 用户可以通过receive（接受）intent，通过 getAction得到的字符串，来决定做什么
        else if(TextUtils.equals(Intent.ACTION_INSERT_OR_EDIT, intent.getAction())) {
            // New note

            // intent.getInt（Long、String）Extra是对各变量的语法分析
            long folderId = intent.getLongExtra(Notes.INTENT_EXTRA_FOLDER_ID, 0);
            int widgetId = intent.getIntExtra(Notes.INTENT_EXTRA_WIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int widgetType = intent.getIntExtra(Notes.INTENT_EXTRA_WIDGET_TYPE,
                    Notes.TYPE_WIDGET_INVALIDE);
            int bgResId = intent.getIntExtra(Notes.INTENT_EXTRA_BACKGROUND_ID,
                    ResourceParser.getDefaultBgId(this));

            // Parse call-record note
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            long callDate = intent.getLongExtra(Notes.INTENT_EXTRA_CALL_DATE, 0);

            //如果该手机有联系人
            if (callDate != 0 && phoneNumber != null) {
                //如果输入的电话号码为空号
                if (TextUtils.isEmpty(phoneNumber)) {
                    Log.w(TAG, "The call record number is null");
                }
                long noteId = 0;

                //将电话号码与手机的号码簿相关
                if ((noteId = DataUtils.getNoteIdByPhoneNumberAndCallDate(getContentResolver(),
                        phoneNumber, callDate)) > 0) {
                    mWorkingNote = WorkingNote.load(this, noteId);
                    if (mWorkingNote == null) {
                        Log.e(TAG, "load call note failed with note id" + noteId);
                        finish();
                        return false;
                    }
                }

                //创建一个新的WorkingNote
                else {
                    mWorkingNote = WorkingNote.createEmptyNote(this, folderId, widgetId,
                            widgetType, bgResId);
                    mWorkingNote.convertToCallNote(phoneNumber, callDate);
                }
            } else {
                mWorkingNote = WorkingNote.createEmptyNote(this, folderId, widgetId, widgetType,
                        bgResId);
            }

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                            | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            Log.e(TAG, "Intent not specified action, should not support");
            finish();
            return false;
        }
        mWorkingNote.setOnSettingStatusChangedListener(this);
        return true;
    }
    //初始化操作
    @Override
    protected void onResume() {
        super.onResume();
        initNoteScreen();
    }

    //对界面的初始化操作
    private void initNoteScreen() {
        //对界面的初始化操作
        mNoteEditor.setTextAppearance(this, TextAppearanceResources
                .getTexAppearanceResource(mFontSizeId));

        //设置外观
        if (mWorkingNote.getCheckListMode() == TextNote.MODE_CHECK_LIST) {
            switchToListMode(mWorkingNote.getContent());
        } else {
            mNoteEditor.setText(getHighlightQueryResult(mWorkingNote.getContent(), mUserQuery));
            mNoteEditor.setSelection(mNoteEditor.getText().length());
        }
        for (Integer id : sBgSelectorSelectionMap.keySet()) {
            findViewById(sBgSelectorSelectionMap.get(id)).setVisibility(View.GONE);
        }
        mHeadViewPanel.setBackgroundResource(mWorkingNote.getTitleBgResId());
        mNoteEditorPanel.setBackgroundResource(mWorkingNote.getBgColorResId());

        mNoteHeaderHolder.tvModified.setText(DateUtils.formatDateTime(this,
                mWorkingNote.getModifiedDate(), DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_YEAR));

        /**
         * TODO: Add the menu for setting alert. Currently disable it because the DateTimePicker
         * is not ready
         */
        showAlertHeader();
    }

    //设置闹钟的显示
    private void showAlertHeader() {
        if (mWorkingNote.hasClockAlert()) {
            long time = System.currentTimeMillis();

            //如果系统时间大于了闹钟设置的时间，那么闹钟失效
            if (time > mWorkingNote.getAlertDate()) {
                mNoteHeaderHolder.tvAlertDate.setText(R.string.note_alert_expired);
            }

            //显示闹钟开启的图标
            else {
                mNoteHeaderHolder.tvAlertDate.setText(DateUtils.getRelativeTimeSpanString(
                        mWorkingNote.getAlertDate(), time, DateUtils.MINUTE_IN_MILLIS));
            }
            mNoteHeaderHolder.tvAlertDate.setVisibility(View.VISIBLE);
            mNoteHeaderHolder.ivAlertIcon.setVisibility(View.VISIBLE);
        } else {
            mNoteHeaderHolder.tvAlertDate.setVisibility(View.GONE);
            mNoteHeaderHolder.ivAlertIcon.setVisibility(View.GONE);
        };
    }

    //初始化
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initActivityState(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        //在创建一个新的标签时，先在数据库中匹配
        super.onSaveInstanceState(outState);
        /**
         * For new note without note id, we should firstly save it to
         * generate a id. If the editing note is not worth saving, there
         * is no id which is equivalent to create new note
         */

        //如果不存在，那么先在数据库中存储
        if (!mWorkingNote.existInDatabase()) {
            saveNote();
        }
        outState.putLong(Intent.EXTRA_UID, mWorkingNote.getNoteId());
        Log.d(TAG, "Save working note id: " + mWorkingNote.getNoteId() + " onSaveInstanceState");
    }

    //MotionEvent是对屏幕触控的传递机制
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        //颜色选择器在屏幕上可见
        if (mNoteBgColorSelector.getVisibility() == View.VISIBLE
                && !inRangeOfView(mNoteBgColorSelector, ev)) {
            mNoteBgColorSelector.setVisibility(View.GONE);
            return true;
        }

        //字体大小选择器在屏幕上可见
        if (mFontSizeSelector.getVisibility() == View.VISIBLE
                && !inRangeOfView(mFontSizeSelector, ev)) {
            mFontSizeSelector.setVisibility(View.GONE);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    //对屏幕触控的坐标进行操作
    private boolean inRangeOfView(View view, MotionEvent ev) {
        int []location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];

        //如果触控的位置超出了给定的范围，返回false
        if (ev.getX() < x
                || ev.getX() > (x + view.getWidth())
                || ev.getY() < y
                || ev.getY() > (y + view.getHeight())) {
                    return false;
                }
        return true;
    }

    //初始化资源
    private void initResources() {
        mHeadViewPanel = findViewById(R.id.note_title);
        mNoteHeaderHolder = new HeadViewHolder();
        mNoteHeaderHolder.tvModified = (TextView) findViewById(R.id.tv_modified_date);
        mNoteHeaderHolder.ivAlertIcon = (ImageView) findViewById(R.id.iv_alert_icon);
        mNoteHeaderHolder.tvAlertDate = (TextView) findViewById(R.id.tv_alert_date);
        mNoteHeaderHolder.ibSetBgColor = (ImageView) findViewById(R.id.btn_set_bg_color);
        mNoteHeaderHolder.ibSetBgColor.setOnClickListener(this);
        mNoteEditor = (EditText) findViewById(R.id.note_edit_view);
        mNoteEditorPanel = findViewById(R.id.sv_note_edit);
        mNoteBgColorSelector = findViewById(R.id.note_bg_color_selector);

        //对标签各项属性内容的初始化
        for (int id : sBgSelectorBtnsMap.keySet()) {
            ImageView iv = (ImageView) findViewById(id);
            iv.setOnClickListener(this);
        }

        //对字体大小的选择
        mFontSizeSelector = findViewById(R.id.font_size_selector);
        for (int id : sFontSizeBtnsMap.keySet()) {
            View view = findViewById(id);
            view.setOnClickListener(this);
        };
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mFontSizeId = mSharedPrefs.getInt(PREFERENCE_FONT_SIZE, ResourceParser.BG_DEFAULT_FONT_SIZE);
        /**
         * HACKME: Fix bug of store the resource id in shared preference.
         * The id may larger than the length of resources, in this case,
         * return the {@link ResourceParser#BG_DEFAULT_FONT_SIZE}
         */
        if(mFontSizeId >= TextAppearanceResources.getResourcesSize()) {
            mFontSizeId = ResourceParser.BG_DEFAULT_FONT_SIZE;
        }
        mEditTextList = (LinearLayout) findViewById(R.id.note_edit_list);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(saveNote()) {
            Log.d(TAG, "Note data was saved with length:" + mWorkingNote.getContent().length());
        }
        clearSettingState();
    }


        //和桌面小工具的同步
        private void updateWidget() {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        if (mWorkingNote.getWidgetType() == Notes.TYPE_WIDGET_2X) {
            intent.setClass(this, NoteWidgetProvider_2x.class);
        } else if (mWorkingNote.getWidgetType() == Notes.TYPE_WIDGET_4X) {
            intent.setClass(this, NoteWidgetProvider_4x.class);
        } else {
            Log.e(TAG, "Unspported widget type");
            return;
        }

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {
            mWorkingNote.getWidgetId()
        });

        sendBroadcast(intent);
        setResult(RESULT_OK, intent);
    }

    //对点击事件进行封装（ID）
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_set_bg_color) {
            mNoteBgColorSelector.setVisibility(View.VISIBLE);
            findViewById(sBgSelectorSelectionMap.get(mWorkingNote.getBgColorId())).setVisibility(
                    View.VISIBLE);
        } else if (sBgSelectorBtnsMap.containsKey(id)) {
            findViewById(sBgSelectorSelectionMap.get(mWorkingNote.getBgColorId())).setVisibility(
                    View.GONE);
            mWorkingNote.setBgColorId(sBgSelectorBtnsMap.get(id));
            mNoteBgColorSelector.setVisibility(View.GONE);
        } else if (sFontSizeBtnsMap.containsKey(id)) {
            findViewById(sFontSelectorSelectionMap.get(mFontSizeId)).setVisibility(View.GONE);
            mFontSizeId = sFontSizeBtnsMap.get(id);
            mSharedPrefs.edit().putInt(PREFERENCE_FONT_SIZE, mFontSizeId).commit();
            findViewById(sFontSelectorSelectionMap.get(mFontSizeId)).setVisibility(View.VISIBLE);
            if (mWorkingNote.getCheckListMode() == TextNote.MODE_CHECK_LIST) {
                getWorkingText();
                switchToListMode(mWorkingNote.getContent());
            } else {
                mNoteEditor.setTextAppearance(this,
                        TextAppearanceResources.getTexAppearanceResource(mFontSizeId));
            }
            mFontSizeSelector.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(clearSettingState()) {
            return;
        }

        saveNote();
        super.onBackPressed();
    }

    //判断当前可视化标签的情况
    private boolean clearSettingState() {
        if (mNoteBgColorSelector.getVisibility() == View.VISIBLE) {
            mNoteBgColorSelector.setVisibility(View.GONE);
            return true;
        } else if (mFontSizeSelector.getVisibility() == View.VISIBLE) {
            mFontSizeSelector.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    //改变背景颜色
    public void onBackgroundColorChanged() {
        findViewById(sBgSelectorSelectionMap.get(mWorkingNote.getBgColorId())).setVisibility(
                View.VISIBLE);
        mNoteEditorPanel.setBackgroundResource(mWorkingNote.getBgColorResId());
        mHeadViewPanel.setBackgroundResource(mWorkingNote.getTitleBgResId());
    }

    //对选择菜单的准备
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isFinishing()) {
            return true;
        }
        clearSettingState();
        menu.clear();
        if (mWorkingNote.getFolderId() == Notes.ID_CALL_RECORD_FOLDER) {
            // MenuInflater是用来实例化Menu目录下的Menu布局文件的
            getMenuInflater().inflate(R.menu.call_note_edit, menu);
        } else {
            getMenuInflater().inflate(R.menu.note_edit, menu);
        }
        if (mWorkingNote.getCheckListMode() == TextNote.MODE_CHECK_LIST) {
            menu.findItem(R.id.menu_list_mode).setTitle(R.string.menu_normal_mode);
        } else {
            menu.findItem(R.id.menu_list_mode).setTitle(R.string.menu_list_mode);
        }
        if (mWorkingNote.hasClockAlert()) {
            menu.findItem(R.id.menu_alert).setVisible(false);
        } else {
            menu.findItem(R.id.menu_delete_remind).setVisible(false);
        }
        return true;
    }

    /*
     * 函数功能：动态改变菜单选项内容
     * 函数实现：如下注释
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //根据菜单的id来编剧相关项目
        switch (item.getItemId()) {

            //创建一个新的便签
            case R.id.menu_new_note:
                createNewNote();
                break;

            //删除便签
            case R.id.menu_delete:

                //创建关于删除操作的对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                // 设置标签的标题为alert_title_delete
                builder.setTitle(getString(R.string.alert_title_delete));

                //设置对话框图标
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                //设置对话框内容
                builder.setMessage(getString(R.string.alert_message_delete_note));

                //添加“YES”按钮
                //建立按键监听器
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {

                            //点击所触发事件
                            public void onClick(DialogInterface dialog, int which) {
                                //  删除单签便签
                                deleteCurrentNote();
                                finish();
                            }
                        });

                //添加“NO”的按钮
                builder.setNegativeButton(android.R.string.cancel, null);
                //显示对话框
                builder.show();
                break;

            //字体大小的编辑
            case R.id.menu_font_size:
                // 将字体选择器置为可见
                mFontSizeSelector.setVisibility(View.VISIBLE);
                // 通过id找到相应的大小
                findViewById(sFontSelectorSelectionMap.get(mFontSizeId)).setVisibility(View.VISIBLE);
                break;

            //选择列表模式
            case R.id.menu_list_mode:
                mWorkingNote.setCheckListMode(mWorkingNote.getCheckListMode() == 0 ?
                        TextNote.MODE_CHECK_LIST : 0);
                break;

            //菜单共享
            case R.id.menu_share:
                getWorkingText();
                // 用sendto函数将运行文本发送到遍历的本文内
                sendTo(this, mWorkingNote.getContent());
                break;

            case R.id.menu_send_to_desktop:
                //发送到桌面
                sendToDesktop();
                break;

            //创建提醒器
            case R.id.menu_alert:
                setReminder();
                break;
            case R.id.menu_delete_remind:
                //删除日期提醒
                mWorkingNote.setAlertDate(0, false);
                break;
            default:
                break;
        }
        return true;
    }
    
    /*
     * 函数功能：建立事件提醒器
     * 函数实现：如下注释
     */

        private void setReminder() {

            // 建立修改时间日期的对话框
        DateTimePickerDialog d = new DateTimePickerDialog(this, System.currentTimeMillis());
        d.setOnDateTimeSetListener(new OnDateTimeSetListener() {

            //建立时间日期的监听器
            public void OnDateTimeSet(AlertDialog dialog, long date) {
                //选择提醒的日期
                mWorkingNote.setAlertDate(date	, true);
            }
        });

        //显示对话框
        d.show();
    }

    /**
     * Share note to apps that support {@link Intent#ACTION_SEND} action
     * and {@text/plain} type
     */

    /*
     * 函数功能：共享便签
     * 函数实现：如下注释
     */

    private void sendTo(Context context, String info) {
        //建立intent链接选项
        Intent intent = new Intent(Intent.ACTION_SEND);
        //将需要传递的便签信息放入text文件中
        intent.putExtra(Intent.EXTRA_TEXT, info);
        //编辑连接器的类型
        intent.setType("text/plain");
        //在acti中进行链接
        context.startActivity(intent);
    }

    /*
     * 函数功能：创建一个新的便签
     * 函数实现：如下注释
     */
    private void createNewNote() {
        // Firstly, save current editing notes

        //保存当前便签
        saveNote();

        // For safety, start a new NoteEditActivity
        finish();
        //设置链接器
        Intent intent = new Intent(this, NoteEditActivity.class);
        //该活动定义为创建或编辑
        intent.setAction(Intent.ACTION_INSERT_OR_EDIT);
        //将运行便签的id添加到INTENT_EXTRA_FOLDER_ID标记中
        intent.putExtra(Notes.INTENT_EXTRA_FOLDER_ID, mWorkingNote.getFolderId());
        //开始activity并链接
        startActivity(intent);
    }

    /*
     * 函数功能：删除当前便签
     * 函数实现：如下注释
     */
    private void deleteCurrentNote() {

        //假如当前运行的便签内存有数据
        if (mWorkingNote.existInDatabase()) {
            HashSet<Long> ids = new HashSet<Long>();
            long id = mWorkingNote.getNoteId();
            //如果不是头文件夹建立一个hash表把便签id存起来
            if (id != Notes.ID_ROOT_FOLDER) {
                ids.add(id);
            }

            //否则报错
            else {
                Log.d(TAG, "Wrong note id, should not happen");
            }

            //在非同步模式情况下
            if (!isSyncMode()) {

                //删除操作
                if (!DataUtils.batchDeleteNotes(getContentResolver(), ids)) {
                    Log.e(TAG, "Delete Note error");
                }
            }

            //同步模式
            else {
                //移动至垃圾文件夹的操作
                if (!DataUtils.batchMoveToFolder(getContentResolver(), ids, Notes.ID_TRASH_FOLER)) {
                    Log.e(TAG, "Move notes to trash folder error, should not happens");
                }
            }
        }
        //将这些标签的删除标记置为true
        mWorkingNote.markDeleted(true);
    }

    /*
     * 函数功能：判断是否为同步模式
     * 函数实现：直接看NotesPreferenceActivity中同步名称是否为空
     */
    private boolean isSyncMode() {
        return NotesPreferenceActivity.getSyncAccountName(this).trim().length() > 0;
    }


    /*
     * 函数功能：设置提醒时间
     * 函数实现：如下注释
     */
    public void onClockAlertChanged(long date, boolean set) {
        /**
         * User could set clock to an unsaved note, so before setting the
         * alert clock, we should save the note first
         */

        //首先保存已有的便签
        if (!mWorkingNote.existInDatabase()) {
            saveNote();
        }
        if (mWorkingNote.getNoteId() > 0) {
            Intent intent = new Intent(this, AlarmReceiver.class);

            //若有有运行的便签就是建立一个链接器将标签id都存在uri中
            intent.setData(ContentUris.withAppendedId(Notes.CONTENT_NOTE_URI, mWorkingNote.getNoteId()));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

            //设置提醒管理器
            AlarmManager alarmManager = ((AlarmManager) getSystemService(ALARM_SERVICE));
            showAlertHeader();

            //如果用户设置了时间，就通过提醒管理器设置一个监听事项
            if(!set) {
                alarmManager.cancel(pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            }
        }

        //没有运行的便签就报错
        else {
            /**
             * There is the condition that user has input nothing (the note is
             * not worthy saving), we have no note id, remind the user that he
             * should input something
             */
            Log.e(TAG, "Clock alert setting error");
            showToast(R.string.error_note_empty_for_clock);
        }
    }

    /*
     * 函数功能：Widget发生改变的所触发的事件
     */
    public void onWidgetChanged() {
        updateWidget();
    } //更新Widget

    /*
     * 函数功能： 删除编辑文本框所触发的事件
     * 函数实现：如下注释
     */
    public void onEditTextDelete(int index, String text) {
        int childCount = mEditTextList.getChildCount();
        //没有编辑框的话直接返回
        if (childCount == 1) {
            return;
        }

        //通过id把编辑框存在便签编辑框中
        for (int i = index + 1; i < childCount; i++) {
            ((NoteEditText) mEditTextList.getChildAt(i).findViewById(R.id.et_edit_text))
                    .setIndex(i - 1);
        }

        //删除特定位置的视图
        mEditTextList.removeViewAt(index);
        NoteEditText edit = null;
        if(index == 0) {
            edit = (NoteEditText) mEditTextList.getChildAt(0).findViewById(
                    R.id.et_edit_text);
        } else {
            //通过id把编辑框存在空的NoteEditText中
            edit = (NoteEditText) mEditTextList.getChildAt(index - 1).findViewById(
                    R.id.et_edit_text);
        }
        int length = edit.length();
        edit.append(text);
        edit.requestFocus();//请求优先完成该此编辑
        edit.setSelection(length);//定位到length位置处的条目
    }

    /*
     * 函数功能：进入编辑文本框所触发的事件
     * 函数实现：如下注释
     */
    public void onEditTextEnter(int index, String text) {
        /**
         * Should not happen, check for debug
         */

        //越界报错
        if(index > mEditTextList.getChildCount()) {
            Log.e(TAG, "Index out of mEditTextList boundrary, should not happen");
        }

        View view = getListItem(text, index);
        //建立一个新的视图并添加到编辑文本框内
        mEditTextList.addView(view, index);
        NoteEditText edit = (NoteEditText) view.findViewById(R.id.et_edit_text);
        //请求优先操作
        edit.requestFocus();
        //定位到起始位置
        edit.setSelection(0);
        //遍历子文本框并设置对应对下标
        for (int i = index + 1; i < mEditTextList.getChildCount(); i++) {
            ((NoteEditText) mEditTextList.getChildAt(i).findViewById(R.id.et_edit_text))
                    .setIndex(i);
        }
    }

    /*
     * 函数功能：切换至列表模式
     * 函数实现：如下注释
     */
    private void switchToListMode(String text) {
        mEditTextList.removeAllViews();
        String[] items = text.split("\n");
        //清空所有视图，初始化下标
        int index = 0;
        //遍历所有文本单元并添加到文本框中
        for (String item : items) {
            if(!TextUtils.isEmpty(item)) {
                mEditTextList.addView(getListItem(item, index));
                index++;
            }
        }
        mEditTextList.addView(getListItem("", index));
        //优先请求此操作
        mEditTextList.getChildAt(index).findViewById(R.id.et_edit_text).requestFocus();
        //将便签编辑器不可见
        mNoteEditor.setVisibility(View.GONE);
        //将文本编辑框置为可见
        mEditTextList.setVisibility(View.VISIBLE);
    }

    /*
     * 函数功能：获取高亮效果的反馈情况
     * 函数实现：如下注释
     */
    private Spannable getHighlightQueryResult(String fullText, String userQuery) {
        //新建一个效果选项
        SpannableString spannable = new SpannableString(fullText == null ? "" : fullText);
        if (!TextUtils.isEmpty(userQuery)) {
            //将用户的询问进行解析
            mPattern = Pattern.compile(userQuery);
            //建立一个状态机检查Pattern并进行匹配
            Matcher m = mPattern.matcher(fullText);
            int start = 0;
            while (m.find(start)) {
                //设置背景颜色
                spannable.setSpan(
                        new BackgroundColorSpan(this.getResources().getColor(
                                R.color.user_query_highlight)), m.start(), m.end(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                //更新起始位置
                start = m.end();
            }
        }
        return spannable;
    }

    /*
     * 函数功能：获取列表项
     * 函数实现：如下注释
     */
    private View getListItem(String item, int index) {
        //创建一个视图
        View view = LayoutInflater.from(this).inflate(R.layout.note_edit_list_item, null);
        final NoteEditText edit = (NoteEditText) view.findViewById(R.id.et_edit_text);
        //创建一个文本编辑框并设置可见性
        edit.setTextAppearance(this, TextAppearanceResources.getTexAppearanceResource(mFontSizeId));
        CheckBox cb = ((CheckBox) view.findViewById(R.id.cb_edit_item));
        //建立一个打钩框并设置监听器
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit.setPaintFlags(edit.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    edit.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
                }
            }
        });

        if (item.startsWith(TAG_CHECKED)) {
            //选择勾选
            cb.setChecked(true);
            edit.setPaintFlags(edit.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            item = item.substring(TAG_CHECKED.length(), item.length()).trim();
        } else if (item.startsWith(TAG_UNCHECKED)) {
            //选择不勾选
            cb.setChecked(false);
            edit.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
            item = item.substring(TAG_UNCHECKED.length(), item.length()).trim();
        }

        edit.setOnTextViewChangeListener(this);
        edit.setIndex(index);
        //运行编辑框的监听器对该行为作出反应，并设置下标及文本内容
        edit.setText(getHighlightQueryResult(item, mUserQuery));
        return view;
    }

    /*
     * 函数功能：便签内容发生改变所 触发的事件
     * 函数实现：如下注释
     */
    public void onTextChange(int index, boolean hasText) {
        //越界报错
        if (index >= mEditTextList.getChildCount()) {
            Log.e(TAG, "Wrong index, should not happen");
            return;
        }

        //如果内容不为空则将其子编辑框可见性置为可见，否则不可见
        if(hasText) {
            mEditTextList.getChildAt(index).findViewById(R.id.cb_edit_item).setVisibility(View.VISIBLE);
        } else {
            mEditTextList.getChildAt(index).findViewById(R.id.cb_edit_item).setVisibility(View.GONE);
        }
    }

    /*
     * 函数功能：检查模式和列表模式的切换
     * 函数实现：如下注释
     */
    public void onCheckListModeChanged(int oldMode, int newMode) {
        //检查模式切换到列表模式
        if (newMode == TextNote.MODE_CHECK_LIST) {
            switchToListMode(mNoteEditor.getText().toString());
        } else {
            //若是获取到文本就改变其检查标记
            if (!getWorkingText()) {
                mWorkingNote.setWorkingText(mWorkingNote.getContent().replace(TAG_UNCHECKED + " ",
                        ""));
            }
            mNoteEditor.setText(getHighlightQueryResult(mWorkingNote.getContent(), mUserQuery));
            mEditTextList.setVisibility(View.GONE);
            //修改文本编辑器的内容和可见性
            mNoteEditor.setVisibility(View.VISIBLE);
        }
    }

    /*
     * 函数功能：设置勾选选项表并返回是否勾选的标记
     * 函数实现：如下注释
     */
    private boolean getWorkingText() {
        //初始化check标记
        boolean hasChecked = false;
        // 若模式为CHECK_LIST
        if (mWorkingNote.getCheckListMode() == TextNote.MODE_CHECK_LIST) {
            //创建可变字符串
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mEditTextList.getChildCount(); i++) {
                //遍历所有子编辑框的视图
                View view = mEditTextList.getChildAt(i);
                NoteEditText edit = (NoteEditText) view.findViewById(R.id.et_edit_text);

                //若文本不为空
                if (!TextUtils.isEmpty(edit.getText())) {

                    //该选项框已打钩
                    if (((CheckBox) view.findViewById(R.id.cb_edit_item)).isChecked()) {
                        sb.append(TAG_CHECKED).append(" ").append(edit.getText()).append("\n");
                        //扩展字符串为已打钩并把标记置true
                        hasChecked = true;
                    }

                    //扩展字符串添加未打钩
                    else {
                        sb.append(TAG_UNCHECKED).append(" ").append(edit.getText()).append("\n");
                    }
                }
            }
            //利用编辑好的字符串设置运行便签的内容
            mWorkingNote.setWorkingText(sb.toString());
        } else {
            // 若不是该模式直接用编辑器中的内容设置运行中标签的内容
            mWorkingNote.setWorkingText(mNoteEditor.getText().toString());
        }
        return hasChecked;
    }

    /*
     * 函数功能：保存便签
     * 函数实现：如下注释
     */
    private boolean saveNote() {

        //运行 getWorkingText()之后保存
        getWorkingText();
        boolean saved = mWorkingNote.saveNote();

        //如英文注释所说链接RESULT_OK是为了识别保存的2种情况，一是创建后保存，二是修改后保存
        if (saved) {
            /**
             * There are two modes from List view to edit view, open one note,
             * create/edit a node. Opening node requires to the original
             * position in the list when back from edit view, while creating a
             * new node requires to the top of the list. This code
             * {@link #RESULT_OK} is used to identify the create/edit state
             */
            setResult(RESULT_OK);
        }
        return saved;
    }

    /*
     * 函数功能：将便签发送至桌面
     * 函数实现：如下注释
     */
    private void sendToDesktop() {
        /**
         * Before send message to home, we should make sure that current
         * editing note is exists in databases. So, for new note, firstly
         * save it
         */
        //若不存在数据也就是新的标签就保存起来先
        if (!mWorkingNote.existInDatabase()) {
            saveNote();
        }

        //若是有内容
        if (mWorkingNote.getNoteId() > 0) {
            Intent sender = new Intent();
            //建立发送到桌面的连接器
            Intent shortcutIntent = new Intent(this, NoteEditActivity.class);
            //链接为一个视图
            shortcutIntent.setAction(Intent.ACTION_VIEW);
            shortcutIntent.putExtra(Intent.EXTRA_UID, mWorkingNote.getNoteId());

            //将便签的相关信息都添加到要发送的文件里
            //设置sneder的行为是发送
            sender.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            sender.putExtra(Intent.EXTRA_SHORTCUT_NAME,
                    makeShortcutIconTitle(mWorkingNote.getContent()));
            sender.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(this, R.drawable.icon_app));
            sender.putExtra("duplicate", true);
            sender.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            showToast(R.string.info_note_enter_desktop);

            //显示到桌面
            sendBroadcast(sender);
        }

        //空便签直接报错
        else {
            /**
             * There is the condition that user has input nothing (the note is
             * not worthy saving), we have no note id, remind the user that he
             * should input something
             */
            Log.e(TAG, "Send to desktop error");
            showToast(R.string.error_note_empty_for_send_to_desktop);
        }
    }

    /*
     * 函数功能：编辑小图标的标题
     * 函数实现：如下注释
     */
    private String makeShortcutIconTitle(String content) {
        content = content.replace(TAG_CHECKED, "");
        content = content.replace(TAG_UNCHECKED, "");
        //直接设置为content中的内容并返回，有勾选和未勾选2种
        return content.length() > SHORTCUT_ICON_TITLE_MAX_LEN ? content.substring(0,
                SHORTCUT_ICON_TITLE_MAX_LEN) : content;
    }

    /*
     * 函数功能：显示提示的视图
     * 函数实现：根据下标显示对应的提示
     */
    private void showToast(int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    /*
     * 函数功能：持续显示提示的视图
     * 函数实现：根据下标和持续的时间（duration）编辑提示视图并显示
     */
    private void showToast(int resId, int duration) {
        Toast.makeText(this, resId, duration).show();
    }
}
