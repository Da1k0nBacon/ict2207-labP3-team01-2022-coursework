package com.danstoakes.fileexplorer.utility;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileSorter
{
    public static final int SORT_NAME = 1;
    public static final int SORT_DATE = 2;
    public static final int SORT_TYPE = 3;
    public static final int SORT_SIZE = 4;

    public static final int ORDER_ASCENDING = 1;
    public static final int ORDER_DESCENDING = 2;

    public static void sortFiles (List<File> files, int sortType, int orderType)
    {
        if (files != null)
        {
            if (sortType == SORT_NAME)
            {
                Collections.sort(files, new SortFilesByName());
            } else if (sortType == SORT_DATE)
            {
                Collections.sort(files, new SortFilesByDate());
            } else if (sortType == SORT_TYPE)
            {
                Collections.sort(files, new SortFilesByType());
            } else if (sortType == SORT_SIZE)
            {
                Collections.sort(files, new SortFilesBySize());
                Collections.sort(files, new SortFoldersBySize());
            }

            if (orderType == ORDER_DESCENDING)
                reverse(files);
        }
    }

    private static void reverse (List<File> files)
    {
        Collections.reverse(files);
    }

    public static class SortFilesByName implements Comparator<File>
    {
        @Override
        public int compare(File o1, File o2)
        {
            return o1.getAbsolutePath().toLowerCase().compareTo(o2.getAbsolutePath().toLowerCase());
        }
    }

    public static class SortFilesByDate implements Comparator<File>
    {
        @Override
        public int compare(File o1, File o2)
        {
            return Long.compare(o1.lastModified(), o2.lastModified());
        }
    }

    public static class SortFilesByType implements Comparator<File>
    {
        @Override
        public int compare(File o1, File o2)
        {
            final int o1LastDot = o1.getAbsolutePath().lastIndexOf('.');
            final int o2LastDot = o2.getAbsolutePath().lastIndexOf('.');

            if ((o1LastDot == -1) == (o2LastDot == -1))
                return o2.getAbsolutePath().substring(o2LastDot + 1).compareTo(
                        o1.getAbsolutePath().substring(o1LastDot+ 1));
            else if (o1LastDot == -1)
                return -1;
            else
                return 1;
        }
    }

    public static class SortFilesBySize implements Comparator<File>
    {
        @Override
        public int compare(File o1, File o2)
        {
            return Long.compare(o1.length(), o2.length());
        }
    }

    public static class SortFoldersBySize implements Comparator<File>
    {
        @Override
        public int compare(File o1, File o2)
        {
            if (o1.isDirectory() && o2.isDirectory())
            {
                File[] o1Files = o1.listFiles();
                File[] o2Files = o2.listFiles();

                if (o1Files != null && o2Files != null)
                {
                    int o1Size = o1.listFiles().length;
                    int o2Size = o2.listFiles().length;

                    if (o1Size == o2Size)
                        return 0;
                    else
                        return Integer.compare(o1Size, o2Size);
                }
            } else if (o1.isDirectory() && !o2.isDirectory())
            {
                return 1;
            } else if (!o1.isDirectory() && o2.isDirectory())
            {
                return -1;
            }
            return 0;
        }
    }
}