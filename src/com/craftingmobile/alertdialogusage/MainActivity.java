package com.craftingmobile.alertdialogusage;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.craftingmobile.alertdialogusage.interfaces.LoginDialogListener;
import com.craftingmobile.alertdialogusage.interfaces.LogoutDialogListener;

public class MainActivity extends FragmentActivity implements
        LoginDialogListener, LogoutDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.login);
        Button logout = (Button) findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LogoutDialogFragment logoutFragment 
                    = LogoutDialogFragment.newInstance("Bob");

                logoutFragment.show(getSupportFragmentManager(),
                                    "logout");
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LoginDialogFragment loginFragment 
                    = LoginDialogFragment.newInstance();

                loginFragment.show(getSupportFragmentManager(),
                                   "login");

            }
        });
    }

    @Override
    public void onFinishLoginDialog(String username, String password) {
        Toast.makeText(this,
                "Username: " + username + " Password: " + password,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void performLogout() {
        Toast.makeText(this, 
                       "Received log out request!",
                       Toast.LENGTH_SHORT
                      ).show();

    }

    @Override
    public void cancelLogout() {
        Toast.makeText(this,
                       "Log out cancelled!",
                       Toast.LENGTH_SHORT
                      ).show();

    }

}
