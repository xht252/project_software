package net.micode.notes.ui;

import android.widget.EditText;

public class _user
{
    EditText username, password,
            firstName, lastName, email;

    void _user(EditText reg_un , EditText reg_ps , EditText fn , EditText ln , EditText em)
    {
        username = reg_un;
        password = reg_ps;
        firstName = fn;
        lastName = ln;
        email = em;
    }

    public EditText getUsername() {
        return username;
    }

    public EditText getPassword() {
        return password;
    }

    public EditText getFirstName(){
        return firstName;
    }

    public EditText getLastName(){
        return lastName;
    }

    public EditText getEmail(){
        return email;
    }

    public void setUsername(EditText reg_un){
        username = reg_un;
    }

    public void setPassword(EditText reg_ps){
        password = reg_ps;
    }

    public void setFirstName(EditText fn){
        firstName = fn;
    }

    public void setLastName(EditText ln){
        lastName = ln;
    }

    public void setEmail(EditText em){
        email = em;
    }
}
