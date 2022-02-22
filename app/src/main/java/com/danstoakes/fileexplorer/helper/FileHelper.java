package com.danstoakes.fileexplorer.helper;

import android.webkit.MimeTypeMap;

import java.io.File;

public class FileHelper
{
    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String AUDIO = "audio";
    public static final String VIDEO = "video";
    public static final String APPLICATION = "application";

    public static String getFileExtension (File file)
    {
        return MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
    }

    public static String getMimeType (File file)
    {
        String fileExtension = getFileExtension(file);

        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
    }

    public static String getFileType (String mimeType)
    {
        if (mimeType != null && mimeType.contains("/"))
            return mimeType.substring(0, mimeType.indexOf("/"));
        return "";
    }

    public static String getDisplayFileName (File file)
    {
        return file.getAbsoluteFile().getName();
    }

    public static boolean isNotSystemFile(File file)
    {
        if (file.isFile())
        {
            String extension = getFileExtension(file);
            String mimeType = getMimeType(file);

            return (extension == null || extension.length() <= 0 || mimeType != null);
        } else
        {
            String fileName = getDisplayFileName(file);
            return (file.canWrite() && fileName.charAt(0) != '.');
        }
    }
}