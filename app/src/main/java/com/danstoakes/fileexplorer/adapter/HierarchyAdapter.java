package com.danstoakes.fileexplorer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.danstoakes.fileexplorer.R;

import java.io.File;
import java.util.List;

public class HierarchyAdapter extends RecyclerView.Adapter<HierarchyAdapter.HierarchyHolder>
{
    private OnHierarchyAdapterEventListener listener;
    private List<File> files;

    public HierarchyAdapter(List<File> files)
    {
        this.files = files;
    }

    @NonNull
    @Override
    public HierarchyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from (
                parent.getContext()).inflate(R.layout.item_hierarchy, parent, false);
        return new HierarchyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HierarchyHolder holder, int position)
    {
        String path = files.get(position).getAbsolutePath();
        holder.setAttributes(new File(path), position);
    }

    @Override
    public int getItemCount()
    {
        return files.size();
    }

    public void updateHierarchy (List<File> files)
    {
        this.files = files;
        notifyItemRangeChanged(0, files.size());
    }

    public void updateHierarchy (int index)
    {
        notifyItemRangeRemoved(index, this.files.size());
        this.files = this.files.subList(0, index + 1);
    }

    public void setOnHierarchyAdapterEventListener (OnHierarchyAdapterEventListener listener)
    {
        this.listener = listener;
    }

    public interface OnHierarchyAdapterEventListener
    {
        void onHierarchyItemClicked (View view, HierarchyAdapter adapter, int index);
    }

    class HierarchyHolder extends RecyclerView.ViewHolder
    {
        private final CardView cardView;
        private final TextView path;

        public HierarchyHolder(@NonNull View itemView)
        {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_hierarchy_cv);
            path = itemView.findViewById(R.id.item_hierarchy_path_tv);
        }

        public void setAttributes (File file, int position)
        {
            cardView.setOnClickListener(v -> listener.onHierarchyItemClicked(
                    v, HierarchyAdapter.this, position));

            if (position == 0)
                path.setText(cardView.getContext().getString(R.string.item_hierarchy_placeholder));
            else
                path.setText(file.getAbsoluteFile().getName());
        }
    }
}