package com.danstoakes.fileexplorer.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.danstoakes.fileexplorer.utility.FileSorter;
import com.danstoakes.fileexplorer.R;
import com.danstoakes.fileexplorer.helper.SharedPreferenceHelper;

public class SortDialog extends Dialog implements View.OnClickListener
{
    private int sortType;
    private int orderType;
    private int orientation;

    private OnDialogOptionSelectedListener listener;

    public SortDialog(@NonNull Context context)
    {
        super(context);
        setOwnerActivity ((Activity) context);

        Window window = super.getWindow ();
        if (window != null)
            window.setBackgroundDrawable (new ColorDrawable(Color.TRANSPARENT));

        super.setContentView (R.layout.dialog_sort);
        super.setCancelable (true);

        sortType = SharedPreferenceHelper.getInteger(context, SharedPreferenceHelper.sortKey);
        orderType = SharedPreferenceHelper.getInteger(context, SharedPreferenceHelper.orderKey);

        RadioButton nameButton = findViewById(R.id.dialog_sort_name_btn);
        nameButton.setOnClickListener(this);
        nameButton.setChecked(sortType == FileSorter.SORT_NAME ||
                sortType == SharedPreferenceHelper.DEFAULT_VALUE);
        RadioButton dateButton = findViewById(R.id.dialog_sort_date_btn);
        dateButton.setOnClickListener(this);
        dateButton.setChecked(sortType == FileSorter.SORT_DATE);
        RadioButton typeButton = findViewById(R.id.dialog_sort_type_btn);
        typeButton.setOnClickListener(this);
        typeButton.setChecked(sortType == FileSorter.SORT_TYPE);
        RadioButton sizeButton = findViewById(R.id.dialog_sort_size_btn);
        sizeButton.setOnClickListener(this);
        sizeButton.setChecked(sortType == FileSorter.SORT_SIZE);

        RadioButton ascendingButton = findViewById(R.id.dialog_sort_asc_btn);
        ascendingButton.setOnClickListener(this);
        ascendingButton.setChecked(orderType == FileSorter.ORDER_ASCENDING);
        RadioButton descendingButton = findViewById(R.id.dialog_sort_desc_btn);
        descendingButton.setOnClickListener(this);
        descendingButton.setChecked(orderType == FileSorter.ORDER_DESCENDING ||
                orderType == SharedPreferenceHelper.DEFAULT_VALUE);

        Button cancelButton = findViewById(R.id.dialog_sort_cancel_btn);
        cancelButton.setOnClickListener(this);
        Button doneButton = findViewById(R.id.dialog_sort_done_btn);
        doneButton.setOnClickListener(this);

        if (getWidth() > getHeight())
            requestRedraw(context.getResources().getConfiguration().orientation);

        orientation = context.getResources().getConfiguration().orientation;
    }

    public void requestRedraw (int orientation)
    {
        if (this.orientation != orientation)
        {
            this.orientation = orientation;

            ScrollView scrollView = findViewById(R.id.dialog_sort_sv);
            ViewGroup.LayoutParams params = scrollView.getLayoutParams();
            if (orientation == 1)
            {
                // portrait
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                        params.width, LinearLayout.LayoutParams.WRAP_CONTENT));
            } else if (orientation == 2)
            {
                // landscape
                scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                        params.width, (int) (getHeight() / 1.8)));
            }
        }
    }

    private Activity getActivity ()
    {
        return getOwnerActivity();
    }

    private DisplayMetrics getMetrics ()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics;
    }

    private int getWidth ()
    {
        return getMetrics().widthPixels;
    }

    private int getHeight ()
    {
        return getMetrics().heightPixels;
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (v instanceof RadioButton && ((RadioButton) v).isChecked())
        {
            if (id == R.id.dialog_sort_name_btn)
            {
                sortType = FileSorter.SORT_NAME;
            } else if (id == R.id.dialog_sort_date_btn)
            {
                sortType = FileSorter.SORT_DATE;
            } else if (id == R.id.dialog_sort_type_btn)
            {
                sortType = FileSorter.SORT_TYPE;
            } else if (id == R.id.dialog_sort_size_btn)
            {
                sortType = FileSorter.SORT_SIZE;
            } else if (id == R.id.dialog_sort_asc_btn)
            {
                orderType = FileSorter.ORDER_ASCENDING;
            } else if (id == R.id.dialog_sort_desc_btn)
            {
                orderType = FileSorter.ORDER_DESCENDING;
            }
        } else if (v instanceof Button)
        {
            if (v.getId() == R.id.dialog_sort_done_btn)
            {
                SharedPreferenceHelper.setInteger(getOwnerActivity(),
                        SharedPreferenceHelper.sortKey, sortType);
                SharedPreferenceHelper.setInteger(getOwnerActivity(),
                        SharedPreferenceHelper.orderKey, orderType);
                listener.onSortOptionSelected(sortType, orderType);
            }
            dismiss();
        }
    }

    public void setOnDialogOptionSelectedListener (OnDialogOptionSelectedListener listener)
    {
        this.listener = listener;
    }

    public interface OnDialogOptionSelectedListener
    {
        void onSortOptionSelected (int sortType, int orderType);
    }
}