package net.micode.notes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// 通过SQLiteOpenHelper的子类生成，所以我们需要建一个类来继承SQLiteOpenHelper。
public class DBhelper extends SQLiteOpenHelper
{
    // 数据库名称
    public static final String db_name = "Users.db";
    private Context mcontext;
    // SQL创建条目
    private static final String SQL_CREATE_ENTRIES = "create table db_of_user(" +
            "_user_name TEXT," +
            "_password TEXT," +
            "_firstName TEXT," +
            "_LastName TEXT," +
            "_email TEXT" + ")";
    // 由于这个类是一个抽象类，我们需要实现他的构造方法和抽象方法
    public DBhelper(Context context) {
        super(context, db_name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db, oldVersion, newVersion);
    }
}