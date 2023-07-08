package net.micode.notes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;
public class DBmanager
{
    // 操作表的对象，进行增删改查
    private final SQLiteDatabase db;

    public DBmanager(Context con) {
        DBhelper dbHelper = new DBhelper(con);
        db = dbHelper.getWritableDatabase();
    }

    //    "create table user (" +
//    "user_name EditText," +
//    "password EditText," +
//    "firstName EditText," +
//    "LastName EditText," +
//    "email EditText" + ")"
    // 增加元素
    public void add(EditText reg_username , EditText reg_password , EditText reg_firstName ,
                    EditText reg_lastName , EditText Email)
    {
        ContentValues cv = new ContentValues();
        cv.put("_user_name" , reg_username.getText().toString());
        cv.put("_password" , reg_password.getText().toString());
        cv.put("_firstName" , reg_firstName.getText().toString());
        cv.put("_LastName" , reg_lastName.getText().toString());
        cv.put("_email" , Email.getText().toString());
        db.insert("db_of_user" , null , cv);
    }


    public boolean selectPass(Context context , EditText reg_password)
    {
        // Cursor cursor = db.query(
        //    FeedEntry.TABLE_NAME,   // 表名
        //    projection,             // 要返回的列数组(传递null获取全部)
        //    selection,              // WHERE的列
        //    selectionArgs,          // WHERE子句的值
        //    null,                   // 不要分组a
        //    null,                   // 不要按行组筛选
        //    sortOrder               // 排序顺序
        //    );
        Cursor cur = db.query("db_of_user" , null , null , null , null , null ,null);
        int pos = cur.getPosition();

        boolean flag = false;
        while(cur.moveToNext())
        {
            String pass = cur.getString(1);

            if(pass.equals(reg_password.getText().toString().trim()))
            {
                flag = true;
                break;
            }
        }

        cur.close();
        return flag;
    }

    public boolean selectName(Context context , EditText reg_username)
    {
        Cursor cur = db.query("db_of_user" , null , null , null , null , null ,null);
        int pos = cur.getPosition();

        boolean flag = false;
        while (cur.moveToNext())
        {
            String name = cur.getString(0);
            System.out.println(name);
            if (name.equals(reg_username.getText().toString().trim()))
            {
                flag = true;
                break;
            }
        }
        cur.close();

        return flag;
    }
}