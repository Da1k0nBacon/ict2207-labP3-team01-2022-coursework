package com.danstoakes.fileexplorer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.danstoakes.fileexplorer.R;

public class CreateFolderDialog extends Dialog implements View.OnClickListener,
        DialogInterface.OnDismissListener
{
    public static int VALID_FILENAME = 1;
    public static int INVALID_FILENAME = 2;
    public static int EMPTY_FILENAME = 3;

    private OnDialogOptionSelectedListener listener;

    public CreateFolderDialog(@NonNull Context context)
    {
        super(context);
        setOwnerActivity ((Activity) context);

        Window window = super.getWindow ();
        if (window != null)
            window.setBackgroundDrawable (new ColorDrawable(Color.TRANSPARENT));

        super.setContentView (R.layout.dialog_create);
        super.setCancelable (true);

        EditText folderNameEditText = findViewById(R.id.dialog_create_et);
        folderNameEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        Button cancelButton = findViewById(R.id.dialog_create_cancel_btn);
        cancelButton.setOnClickListener(this);
        Button createButton = findViewById(R.id.dialog_create_create_btn);
        createButton.setOnClickListener(this);
    }

    private Activity getActivity ()
    {
        return getOwnerActivity();
    }

    private void hideKeyboard ()
    {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (v instanceof Button)
        {
            if (id == R.id.dialog_create_create_btn)
            {
                EditText folderNameEditText = findViewById(R.id.dialog_create_et);

                String folderName = folderNameEditText.getText().toString();
                if (folderName.length() > 0 && !folderName.contains("\\") && !folderName.contains("/"))
                    listener.onFolderNameEntered(VALID_FILENAME, folderName);
                else if (folderName.contains("\\") || folderName.contains("/"))
                    listener.onFolderNameEntered(INVALID_FILENAME, folderName);
                else if (folderName.length() == 0)
                    listener.onFolderNameEntered(EMPTY_FILENAME, null);
            }
            hideKeyboard();
            dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        hideKeyboard();
    }

    public void setOnDialogOptionSelectedListener (OnDialogOptionSelectedListener listener)
    {
        this.listener = listener;
    }

    public interface OnDialogOptionSelectedListener
    {
        void onFolderNameEntered (int returnCode, String folderName);
    }
}