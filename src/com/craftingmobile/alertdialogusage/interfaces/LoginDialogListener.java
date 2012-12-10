package com.craftingmobile.alertdialogusage.interfaces;

/**
 * Interface for Activities to implement to receive the result (the
 * username and password in this case)
 * 
 */
public interface LoginDialogListener {
    void onFinishLoginDialog(String username, String password);
}
