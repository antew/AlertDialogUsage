/* 
 * 
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
import android.content.DialogInterface.OnShowListener;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.craftingmobile.alertdialogusage.interfaces.LoginDialogListener;

/**
 * A simple dialog that prompts for a Username and Password Activities
 * should implement {@link LoginDialogListener} to receive the entered
 * values in a callback.
 * 
 */
//@formatter:off
public class LoginDialogFragment extends DialogFragment implements
        OnEditorActionListener, TextWatcher, OnShowListener {

    private TextView            password;
    private TextView            username;
    LoginDialogListener         listener;
    private static final String TAG = LoginDialogFragment.class
                                            .getSimpleName();

    /**
     * No-args constructor required Fragments
     */
    public LoginDialogFragment() {};

    /**
     * Static factory used for instantiating new instances of this
     * dialog
     * 
     * @return A new {@link LoginDialogFragment}
     */
    public static LoginDialogFragment newInstance() {
        return new LoginDialogFragment();
    }
    
    /**
     * Make sure the containing Activity has implemented the
     * {@link LoginDialogListener} interface
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "onAttach");
        
        if (activity instanceof LoginDialogListener) {
            listener = (LoginDialogListener) activity;
        } else {
            throw new RuntimeException("The activity must implement"
                    + " the LoginDialogListener interface!");
        }
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Log.i(TAG, "onCreateDialog");
        LayoutInflater lf = LayoutInflater.from(getActivity());
        
        /** 
         *  Inflate our custom view for this dialog, it contains
         *  two TextViews and two EditTexts, one set for the username
         *  and a second set for the password
         */ 
        View v = lf.inflate(R.layout.login_dialog, null);

        username = (TextView) v.findViewById(R.id.login_username);
        password = (TextView) v.findViewById(R.id.login_password);

        /**
         * Register our listener for the 'Done' key on the soft keyboard
         */
        password.setOnEditorActionListener(this);
        username.requestFocus();

        final AlertDialog dialog 
            = new AlertDialog.Builder(getActivity())
                             .setView(v)
                             .setTitle(R.string.log_in)
                             .setPositiveButton(R.string.log_in, null)
                             .setNegativeButton(R.string.cancel, null)
                             .create();

        /**
         * We have to override setOnShowListener here (min API level 8)
         * in order to validate the inputs before closing the dialog.
         * Just overriding setPositiveButton closes the dialog
         * automatically when the button is pressed
         */
        dialog.setOnShowListener(this);

        /**
         * Show the soft keyboard automatically
         */
        dialog.getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        /**
         * These TextWatchers are used to clear the error icons
         * automatically once the user has remedied an error
         */
        username.addTextChangedListener(this);
        password.addTextChangedListener(this);
        return dialog;
    }

    /**
     * We have to override setOnShowListener here (min API level 8) in
     * order to validate the inputs before closing the dialog. Just
     * overriding setPositiveButton closes the automatically when the
     * button is pressed
     * 
     * @return The onShowListener for the AlertDialog
     */
    @Override
    public void onShow(DialogInterface dialog) {
        Button login = ((AlertDialog) dialog)
                .getButton(AlertDialog.BUTTON_POSITIVE);

        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
    }

    /**
     * Perform the login if the user presses the Done key from the
     * password field
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId,
            KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            doLogin();
            return true;
        }
        return false;
    }

    /**
     * Returns true if the username and password pass validation.
     * 
     * @return False if the username or password is blank
     */
    private boolean hasErrors() {
        boolean hasErrors = false;
        Drawable errorIcon = getErrorDrawable();
        String errorText = null;
        if (username.getText().length() == 0) {
            errorText = getString(R.string.enter_a_username);
            username.setError(errorText, errorIcon);
            hasErrors = true;
        }

        if (password.getText().length() == 0) {
            errorText = getString(R.string.enter_a_password);
            password.setError(errorText, errorIcon);
            hasErrors = true;
        }

        return hasErrors;
    }
    
    

    /**
     * Perform the login, provided {@link #hasErrors()} returns false
     */
    private void doLogin() {
        if (!hasErrors()) {
            listener.onFinishLoginDialog(username.getText().toString(),
                                         password.getText().toString());
            
            this.dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
            int count) {}

    /**
     * If the user receives an error message due to a blank field, this
     * automatically clears the error on the field once they remedy it.
     */
    @Override
    public void afterTextChanged(Editable s) {
        if (username.getText().length() > 0
                && username.getError() != null)
            username.setError(null);

        if (password.getText().length() > 0
                && password.getError() != null)
            password.setError(null);
    }

    /**
     * Returns an error icon for use with
     * {@link EditText#setError(CharSequence)}
     * 
     * @return A {@link Drawable} of the error icon
     */
    public Drawable getErrorDrawable() {
        Resources r = getActivity().getResources();
        Drawable drawable 
            = r.getDrawable(R.drawable.custom_indicator_input_error);

        /**
         * We have to set the bounds here because they will default
         * to zero for all values, meaning our icon will not display
         */
        drawable.setBounds(0,                              // Left 
                           0,                              // Top
                           drawable.getIntrinsicWidth(),   // Right
                           drawable.getIntrinsicHeight()); // Bottom

        return drawable;

    }

    // ****************************************************************
    // ********** All of these methods below are merely used **********
    // ********** to show the DialogFragment lifecycle **********
    // ****************************************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }
    
    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        Log.i(TAG, "onActivityCreated");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView");
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Log.i(TAG, "onSaveInstanceState");
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
        Log.i(TAG, "show(FragmentManager manager, String tag)");
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        Log.i(TAG, "show(FragmentTransaction transaction, String tag)");
        return super.show(transaction, tag);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.i(TAG, "onDismiss");
    }

}