/*
 * This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://sam.zoy.org/wtfpl/COPYING for more details. 
 */
package com.craftingmobile.alertdialogusage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.craftingmobile.alertdialogusage.interfaces.LogoutDialogListener;

public class LogoutDialogFragment extends DialogFragment implements
        OnClickListener {

    private static final String TAG = LogoutDialogFragment.class
                                            .getSimpleName();

    private static final String USERNAME = "Username";
    
    // Used to communicate the result back to the Activity
    private LogoutDialogListener listener;

    /**
     * No-args constructor required Fragments
     */
    public LogoutDialogFragment() {};

    /**
     * Static factory method for creating dialog instances
     * 
     * @return
     */
    public static LogoutDialogFragment newInstance(String username) {
        LogoutDialogFragment fragment = new LogoutDialogFragment();

        // Add the username to the bundle of arguments for the fragment
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        fragment.setArguments(args);
        
        return fragment;
    }
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof LogoutDialogListener) {
            listener = (LogoutDialogListener) activity;
        } else {
            throw new RuntimeException("The Activity must " +
                    "implement the LogoutDialogListener interface!");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get the username from the arguments, if one isn't passed
        // we use a blank string so it still looks OK
        String username = "";
        if (getArguments().containsKey(USERNAME))
            username = ", " + getArguments().getString(USERNAME);
        
        // "Are you sure you want to log out, <username>?"
        String message 
            = getString(R.string.are_you_sure_you_want_to_log_out) +
              username +
              "?";
        
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(R.string.log_out);
        b.setMessage(message) ;
        b.setPositiveButton(R.string.yes, this);
        b.setNegativeButton(R.string.no, this);

        return b.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                listener.performLogout();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                listener.cancelLogout();
                break;
        }
    }
}