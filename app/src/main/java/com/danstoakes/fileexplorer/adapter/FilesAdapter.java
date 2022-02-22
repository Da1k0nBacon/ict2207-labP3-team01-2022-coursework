package com.danstoakes.fileexplorer.adapter;

import android.app.Activity;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.danstoakes.fileexplorer.helper.FileHelper;
import com.danstoakes.fileexplorer.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesHolder> {
    private OnFilesAdapterEventListener listener;

    private final File directory;
    private List<File> files;

    public FilesAdapter(File directory, List<File> files)
    {
        this.directory = directory;
        this.files = files;
    }

    @NonNull
    @Override
    public FilesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from (
                parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FilesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesHolder holder, int position) {
        File file = files.get(position);
        holder.setAttributes(file);
    }

    @Override
    public int getItemCount() {
        if (files != null)
            return files.size();
        return 0;
    }

    public void setAdapterEventListener (OnFilesAdapterEventListener listener)
    {
        this.listener = listener;
    }

    public List<File> removeFile (File file)
    {
        notifyItemRangeRemoved(0, files.size());
        files.remove(file);

        return files;
    }

    public void updateFiles (List<File> files)
    {
        this.files = files;
        notifyItemRangeChanged(0, this.files.size());
    }

    public interface OnFilesAdapterEventListener
    {
        void onDirectoryClicked (View view, File file);
        void onFileClicked (View view, File file);
        void onFileLongClicked (View view, File file);
    }

    class FilesHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final ImageView icon;
        private final TextView filePath;

        private final TextView fileSize;
        private final TextView fileDate;

        public FilesHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_file_cv);
            icon = itemView.findViewById(R.id.item_file_iv);
            filePath = itemView.findViewById(R.id.item_file_path_tv);

            fileSize = itemView.findViewById(R.id.item_file_size_tv);
            fileDate = itemView.findViewById(R.id.item_file_date_tv);
        }

        private void setGlideThumbnail (File file, int placeholderId)
        {
            int thumbnailSize = icon.getContext().getResources().getDimensionPixelSize(
                    R.dimen.thumbnail_height);

            Glide.with(icon.getContext())
                    .asBitmap()
                    .load(file)
                    .centerCrop()
                    .placeholder(placeholderId)
                    .override(thumbnailSize, thumbnailSize).into(icon);
        }

        private void setAttributes (File file)
        {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (file.isDirectory())
                        listener.onDirectoryClicked(v, file);
                    else
                        listener.onFileClicked(v, file);
                }
            });

            ((Activity) cardView.getContext()).registerForContextMenu(cardView);
            cardView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v) {
                    ((Activity) cardView.getContext()).openContextMenu(cardView);
                    listener.onFileLongClicked(v, file);
                    return true;
                }
            });

            if (file.isDirectory())
            {
                if (FileHelper.isNotSystemFile(file))
                {
                    if (FileHelper.getDisplayFileName(file).equalsIgnoreCase("pictures") ||
                            FileHelper.getDisplayFileName(file).equalsIgnoreCase("dcim"))
                    {
                        icon.setImageResource(R.drawable.ic_baseline_folder_images_48);
                    } else if (FileHelper.getDisplayFileName(file).equalsIgnoreCase("android"))
                    {
                        icon.setImageResource(R.drawable.ic_baseline_folder_system_48);
                    } else
                    {
                        icon.setImageResource(R.drawable.ic_baseline_folder_48);
                    }
                } else
                {
                    setGlideThumbnail(file, R.drawable.ic_baseline_folder_hidden_48);
                }

                File[] files = file.listFiles();
                if (files.length == 1)
                    fileSize.setText(files.length + " item");
                else
                    fileSize.setText(files.length + " items");
            } else if (file.isFile())
            {
                String fileExtension = FileHelper.getFileExtension(file);
                String mimeType = FileHelper.getMimeType(file);

                if (FileHelper.isNotSystemFile(file))
                {
                    switch (FileHelper.getFileType(mimeType))
                    {
                        case FileHelper.TEXT:
                            icon.setImageResource(R.drawable.ic_baseline_text_48);
                            break;
                        case FileHelper.IMAGE:
                            setGlideThumbnail(file, R.drawable.ic_baseline_image_48);
                            break;
                        case FileHelper.AUDIO:
                            icon.setImageResource(R.drawable.ic_baseline_music_note_48);
                            break;
                        case FileHelper.VIDEO:
                            setGlideThumbnail(file, R.drawable.ic_baseline_video_48);
                            break;
                        case FileHelper.APPLICATION:
                            if (fileExtension.equals("pdf")) {
                                icon.setImageResource(R.drawable.ic_baseline_text_48);
                            } else
                            {
                                setGlideThumbnail(file, R.drawable.ic_baseline_folder_48);
                            }
                            break;
                        default:
                            setGlideThumbnail(file, R.drawable.ic_baseline_folder_48);
                            break;
                    }
                } else
                {
                    setGlideThumbnail(file, R.drawable.ic_baseline_folder_hidden_48);
                }
                fileSize.setText(Formatter.formatShortFileSize(fileSize.getContext(), file.length()) + "");
            }
            filePath.setText(file.getName());

            String pattern = android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMMdyyyy");
            SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
            String dateToDisplay = format.format(new Date(file.lastModified()));

            SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = df.format(new Date(file.lastModified()));

            fileDate.setText(dateToDisplay + " " + time);
        }
    }
}