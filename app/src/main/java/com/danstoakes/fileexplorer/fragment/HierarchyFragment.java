package com.danstoakes.fileexplorer.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danstoakes.fileexplorer.utility.FileManager;
import com.danstoakes.fileexplorer.utility.FragmentManager;
import com.danstoakes.fileexplorer.R;
import com.danstoakes.fileexplorer.adapter.HierarchyAdapter;

public class HierarchyFragment extends Fragment implements HierarchyAdapter.OnHierarchyAdapterEventListener
{
    private FileManager fileManager;
    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_hierarchy, container, false);
        Bundle arguments = getArguments();

        if (arguments != null)
        {
            fileManager = requireArguments().getParcelable("FILE_MANAGER");
            fragmentManager = requireArguments().getParcelable("FRAGMENT_MANAGER");

            HierarchyAdapter hierarchyAdapter = new HierarchyAdapter(fileManager.getLevels());
            hierarchyAdapter.setOnHierarchyAdapterEventListener(this);

            RecyclerView recyclerView = view.findViewById(R.id.fragment_hierarchy_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(
                    view.getContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setItemAnimator(null);
            recyclerView.setAdapter(hierarchyAdapter);
        }
        return view;
    }

    private HierarchyAdapter getAdapter ()
    {
        View view = getView();
        if (view != null)
        {
            RecyclerView recyclerView = view.findViewById(R.id.fragment_hierarchy_rv);
            return (HierarchyAdapter) recyclerView.getAdapter();
        }
        return null;
    }

    public void updateFragmentContents ()
    {
        HierarchyAdapter hierarchyAdapter = getAdapter();
        if (hierarchyAdapter != null)
            hierarchyAdapter.updateHierarchy(fileManager.getLevels());
    }

    @Override
    public void onHierarchyItemClicked(View view, HierarchyAdapter adapter, int index)
    {
        int originalSize = fileManager.getLevelCount();

        fileManager.rollbackToLevel(index);
        fragmentManager.rollbackTo(index);
        adapter.updateHierarchy(index);

        FragmentActivity fragmentActivity = getActivity();
        for (int i = index; i < originalSize - 1; i++)
            fragmentActivity.getSupportFragmentManager().popBackStackImmediate();
    }
}