package com.danstoakes.fileexplorer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.danstoakes.fileexplorer.R;

public class StorageRationaleDialog extends Dialog implements View.OnClickListener
{
    private OnStorageRationaleOptionSelectedListener listener;

    public StorageRationaleDialog(@NonNull Context context)
    {
        super(context);
        setOwnerActivity ((Activity) context);

        Window window = super.getWindow ();
        if (window != null)
            window.setBackgroundDrawable (new ColorDrawable(Color.TRANSPARENT));

        super.setContentView (R.layout.dialog_storage_rationale);
        super.setCancelable (true);

        Button cancelButton = findViewById(R.id.storage_rationale_cancel_btn);
        cancelButton.setOnClickListener(this);
        Button continueButton = findViewById(R.id.storage_rationale_continue_btn);
        continueButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == R.id.storage_rationale_continue_btn)
            listener.onStorageRationaleOptionSelected(true);
        else if (id == R.id.storage_rationale_cancel_btn)
            listener.onStorageRationaleOptionSelected(false);

        dismiss();
    }

    public void setOnStorageRationalOptionSelectedListener (OnStorageRationaleOptionSelectedListener listener)
    {
        this.listener = listener;
    }

    public interface OnStorageRationaleOptionSelectedListener
    {
        void onStorageRationaleOptionSelected (boolean allow);
    }
}
