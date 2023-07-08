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
// 声明小米便签窗口部件这个包
package net.micode.notes.widget;
// 导入各种类
import android.appwidget.AppWidgetManager;
import android.content.Context;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.tool.ResourceParser;

// 继承了NoteWidgetProvider类，定义了2*2大小的窗口，并对部分函数进行重载
public class NoteWidgetProvider_2x extends NoteWidgetProvider {
    @Override
    // 更新窗口
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.update(context, appWidgetManager, appWidgetIds);
    }

    @Override
    // 返回2*2窗口布局
    protected int getLayoutId() {
        return R.layout.widget_2x;
    }

    @Override
    // 获取背景id
    protected int getBgResourceId(int bgId) {
        return ResourceParser.WidgetBgResources.getWidget2xBgResource(bgId);
    }

    @Override
    // 获取widget类型
    protected int getWidgetType() {
        return Notes.TYPE_WIDGET_2X;
    }
}
