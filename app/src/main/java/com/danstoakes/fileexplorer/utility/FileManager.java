package com.danstoakes.fileexplorer.utility;

import android.os.Parcel;
import android.os.Parcelable;

import com.danstoakes.fileexplorer.helper.FileXMLHelper;

import java.io.File;
import java.util.List;

public class FileManager implements Parcelable
{
    private String filePathForContext;

    public FileManager (String directory)
    {
        FileXMLHelper.createDocument(directory);
        FileXMLHelper.createRoot();
    }

    protected FileManager(Parcel in) {
    }

    public void setFilePathForContext (String path)
    {
        filePathForContext = path;
    }

    public String getFilePathForContext ()
    {
        return filePathForContext;
    }

    public void createLevel (int index, String path, List<File> files)
    {
        FileXMLHelper.createLevel(index, path, files);
    }

    public void removeLevel (int index)
    {
        FileXMLHelper.removeLevel(index);
    }

    public List<File> getLevels ()
    {
        return FileXMLHelper.getLevels();
    }

    public int getLevelCount ()
    {
        return FileXMLHelper.getLevelCount();
    }

    public String getDirectoryPathAtLevel (int index)
    {
        return FileXMLHelper.getPathAtLevel(index);
    }

    public File getDirectoryAtLevel (int index)
    {
        return new File(getDirectoryPathAtLevel(index));
    }

    public List<File> getFilesAtLevel (int index)
    {
        return FileXMLHelper.getLevelNodes(index);
    }

    public void rollbackToLevel (int index)
    {
        for (int i = getLevelCount() - 1; i > index; i--)
            removeLevel(i);
    }

    public void clearLevels ()
    {
        FileXMLHelper.clearLevels();
    }

    public static final Creator<FileManager> CREATOR = new Creator<FileManager>()
    {
        @Override
        public FileManager createFromParcel(Parcel in)
        {
            return new FileManager(in);
        }

        @Override
        public FileManager[] newArray(int size)
        {
            return new FileManager[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}