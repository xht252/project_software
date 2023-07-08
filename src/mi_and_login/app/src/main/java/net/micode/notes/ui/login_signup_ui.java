package net.micode.notes.ui;

// 第一步 存储个人信息
// 第二步 每一次登录便签首先需要进行密码的输入
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

// 数据库
import net.micode.notes.data.DBhelper;
import net.micode.notes.data.DBmanager;
// R库
import net.micode.notes.R;
// 主界面库
import net.micode.notes.ui.NotesListActivity;

import net.micode.notes.ui._user;

public class login_signup_ui extends AppCompatActivity {

    static EditText username;
    EditText password;
    EditText reg_username;
    EditText reg_password;
    EditText reg_firstName;
    EditText reg_lastName;
    EditText reg_email;
    Button login, signUp, reg_register;
    TextInputLayout txtInLayoutUsername, txtInLayoutPassword, txtInLayoutRegPassword;
    CheckBox rememberMe;
    _user us = new _user();

    DBmanager db;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建数据库
        DBhelper helper = new DBhelper(login_signup_ui.this);
        db = new DBmanager(login_signup_ui.this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        txtInLayoutUsername = findViewById(R.id.txtInLayoutUsername);
        txtInLayoutPassword = findViewById(R.id.txtInLayoutPassword);
        rememberMe = findViewById(R.id.rememberMe);


        ClickLogin();

        // 用于显示注册页面的注册按钮
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(MainActivity.this, getBaseContext().toString(), Toast.LENGTH_SHORT).show();
                ClickSignUp();
            }
        });
    }

    // 这是执行检查注册操作的方法登录
    private void ClickLogin() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag_of_username = false, flag_of_password = false;
                if (username.getText().toString().trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                            Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                    txtInLayoutUsername.setError("Username should not be empty");
                }

                if (password.getText().toString().trim().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Please fill out these fields",
                            Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
                    snackbar.show();
                    txtInLayoutPassword.setError("Password should not be empty");
                }

                if(db.selectPass(login_signup_ui.this , password) && db.selectName(login_signup_ui.this , username))
                {
                    Toast.makeText(login_signup_ui.this , "成功进入" , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login_signup_ui.this , NotesListActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public static String get_user_name()
    {
        return username.getText().toString().trim();
    }
    // 用于打开注册页面的方法以及用于注册的其他处理或检查
    private void ClickSignUp() {
        // 先定义 AlertDialog.Builder 父类 builder
        // 使用父类构造 dialog 对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(login_signup_ui.this);
        AlertDialog dialog = builder.create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.register, null);
        dialog.setView(dialogView);

        reg_username = dialogView.findViewById(R.id.reg_username);
        reg_password = dialogView.findViewById(R.id.reg_password);
        reg_firstName = dialogView.findViewById(R.id.reg_firstName);
        reg_lastName = dialogView.findViewById(R.id.reg_lastName);
        reg_email = dialogView.findViewById(R.id.reg_email);
        reg_register = dialogView.findViewById(R.id.reg_register);
        txtInLayoutRegPassword = dialogView.findViewById(R.id.txtInLayoutRegPassword);


        reg_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                // 获取注册名字
                if (reg_username.getText().toString().trim().isEmpty()) {
                    reg_username.setError("Please fill out this field");
                    flag = false;
                } else {
                    //Here you can write the codes for checking username
                    us.setUsername(reg_username);
                }

                // 获得注册密码
                if (reg_password.getText().toString().trim().isEmpty()) {
                    // 设置密码可见性启用开关
                    txtInLayoutRegPassword.setPasswordVisibilityToggleEnabled(false);
                    reg_password.setError("Please fill out this field");
                    flag = false;
                } else {
                    txtInLayoutRegPassword.setPasswordVisibilityToggleEnabled(true);
                    us.setPassword(reg_password);
                }

                // 获得名字的姓
                if (reg_firstName.getText().toString().trim().isEmpty()) {
                    reg_firstName.setError("Please fill out this field");
                    flag = false;
                } else {
                    //Here you can write the codes for checking firstname
                    us.setFirstName(reg_firstName);
                }

                // 获得名字的名
                if (reg_lastName.getText().toString().trim().isEmpty()) {
                    reg_lastName.setError("Please fill out this field");
                    flag = false;
                } else {
                    us.setLastName(reg_lastName);
                }

                // 获得邮箱
                if (reg_email.getText().toString().trim().isEmpty()) {
                    reg_email.setError("Please fill out this field");
                    flag = false;
                } else {
                    us.setEmail(reg_email);
                }
                // 退出接口
                if (flag) {
                    db.add(reg_username , reg_password , reg_firstName , reg_lastName , reg_email);
                    dialog.cancel();
                }
                //Toast.makeText(MainActivity.this , "被点击" , Toast.LENGTH_SHORT).show();
                // moveTaskToBack(true);
                // finish();
            }
        });
        dialog.show();
    }
}