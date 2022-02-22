package com.danstoakes.fileexplorer.utility;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FragmentManager implements Parcelable
{
    private List<String> fragmentTags;

    private String hierarchyFragmentTag;

    public FragmentManager()
    {
        fragmentTags = new ArrayList<>();
    }

    public String get (int index)
    {
        return fragmentTags.get(index);
    }

    public String last ()
    {
        return get(count() - 1);
    }

    public void setHierarchyFragmentTag (String tag)
    {
        this.hierarchyFragmentTag = tag;
    }

    public String getHierarchyFragmentTag ()
    {
        return hierarchyFragmentTag;
    }

    public List<String> getFragmentTags ()
    {
        return fragmentTags;
    }

    public int count ()
    {
        return fragmentTags.size();
    }

    public void add (String tag)
    {
        fragmentTags.add(tag);
    }

    public void remove (int index)
    {
        fragmentTags.remove(index);
    }

    public void remove (String tag)
    {
        fragmentTags.remove(tag);
    }

    public void rollbackTo (int index)
    {
        fragmentTags = fragmentTags.subList(0, index + 1);
    }

    protected FragmentManager(Parcel in) {
        if (in.readByte() == 0x01) {
            fragmentTags = new ArrayList<String>();
            in.readList(fragmentTags, String.class.getClassLoader());
        } else {
            fragmentTags = null;
        }
        hierarchyFragmentTag = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (fragmentTags == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(fragmentTags);
        }
        dest.writeString(hierarchyFragmentTag);
    }

    @SuppressWarnings("unused")
    public static final Creator<FragmentManager> CREATOR = new Creator<FragmentManager>() {
        @Override
        public FragmentManager createFromParcel(Parcel in) {
            return new FragmentManager(in);
        }

        @Override
        public FragmentManager[] newArray(int size) {
            return new FragmentManager[size];
        }
    };
}