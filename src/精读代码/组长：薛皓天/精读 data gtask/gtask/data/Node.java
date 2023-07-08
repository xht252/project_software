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

package net.micode.notes.gtask.data;

import android.database.Cursor;

import org.json.JSONObject;

// 定义节点类
public abstract class Node {
    // 不进行同步
    public static final int SYNC_ACTION_NONE = 0;

    // 增添信息同步至云端
    public static final int SYNC_ACTION_ADD_REMOTE = 1;

    // 增添信息同步至本地
    public static final int SYNC_ACTION_ADD_LOCAL = 2;

    // 删除信息同步至云端
    public static final int SYNC_ACTION_DEL_REMOTE = 3;

    // 删除信息同步至本地
    public static final int SYNC_ACTION_DEL_LOCAL = 4;

    // 更新信息同步至云端
    public static final int SYNC_ACTION_UPDATE_REMOTE = 5;

    // 更新信息同步至本地
    public static final int SYNC_ACTION_UPDATE_LOCAL = 6;

    // 同步冲突
    public static final int SYNC_ACTION_UPDATE_CONFLICT = 7;

    // 同步错误
    public static final int SYNC_ACTION_ERROR = 8;

    private String mGid;

    private String mName;

    private long mLastModified;

    private boolean mDeleted;

    public Node() {
        mGid = null;
        mName = "";
        mLastModified = 0;
        mDeleted = false;
    }

    public abstract JSONObject getCreateAction(int actionId);

    public abstract JSONObject getUpdateAction(int actionId);

    public abstract void setContentByRemoteJSON(JSONObject js);

    public abstract void setContentByLocalJSON(JSONObject js);

    public abstract JSONObject getLocalJSONFromContent();

    public abstract int getSyncAction(Cursor c);

    public void setGid(String gid) {
        this.mGid = gid;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setLastModified(long lastModified) {
        this.mLastModified = lastModified;
    }

    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }

    public String getGid() {
        return this.mGid;
    }

    public String getName() {
        return this.mName;
    }

    public long getLastModified() {
        return this.mLastModified;
    }

    public boolean getDeleted() {
        return this.mDeleted;
    }

}
