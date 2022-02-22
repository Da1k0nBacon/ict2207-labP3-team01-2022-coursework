package com.danstoakes.fileexplorer.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danstoakes.fileexplorer.utility.FileManager;
import com.danstoakes.fileexplorer.utility.FileRunner;
import com.danstoakes.fileexplorer.utility.FileSorter;
import com.danstoakes.fileexplorer.utility.FragmentManager;
import com.danstoakes.fileexplorer.R;
import com.danstoakes.fileexplorer.adapter.FilesAdapter;
import com.danstoakes.fileexplorer.helper.SharedPreferenceHelper;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FilesFragment extends Fragment implements FilesAdapter.OnFilesAdapterEventListener
{
    private FileManager fileManager;
    private FragmentManager fragmentManager;

    private FilesAdapter filesAdapter;

    private OnFragmentHierarchyComponentCreatedListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        Bundle arguments = getArguments();

        if (arguments != null)
        {
            fileManager = requireArguments().getParcelable("FILE_MANAGER");
            fragmentManager = requireArguments().getParcelable("FRAGMENT_MANAGER");

            int start = fileManager.getLevelCount() - 1;
            filesAdapter = new FilesAdapter(
                    fileManager.getDirectoryAtLevel(start), fileManager.getFilesAtLevel(start));
            filesAdapter.setAdapterEventListener(this);

            RecyclerView recyclerView = view.findViewById(R.id.fragment_files_rv);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            recyclerView.setAdapter(filesAdapter);
        }

        return view;
    }

    private void loadFiles (File file, List<File> files, int sortType, int orderType)
    {
        FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity != null)
        {
            FileRunner fileRunner = new FileRunner();
            fileRunner.setCallback(new FileRunner.Callback() {
                @Override
                public void onBegin()
                {
                    fragmentActivity.runOnUiThread(() -> changeLoadingDisplay());
                }

                @Override
                public void run()
                {
                    FileSorter.sortFiles(files, sortType, orderType);
                }

                @Override
                public void onComplete() {
                    fragmentActivity.runOnUiThread(() -> {
                        fileManager.createLevel(fileManager.getLevelCount(), file.getAbsolutePath(), files);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("FILE_MANAGER", fileManager);
                        bundle.putParcelable("FRAGMENT_MANAGER", fragmentManager);

                        FilesFragment filesFragment = new FilesFragment();
                        filesFragment.setArguments(bundle);
                        filesFragment.setOnFragmentHierarchyComponentCreatedListener(listener);

                        int count = fragmentActivity.getSupportFragmentManager().getBackStackEntryCount();
                        fragmentManager.add("filesFragment" + count);

                        fragmentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.activity_main_files_fl, filesFragment, "filesFragment" + count)
                                .addToBackStack(null)
                                .commit();

                        listener.onFragmentHierarchyComponentCreated(file.getPath());
                    });
                }
            });
            fileRunner.initialise();
        }
    }

    @Override
    public void onDirectoryClicked(View view, File file)
    {
        int sortType = SharedPreferenceHelper.getInteger(view.getContext(), "sortType");
        int orderType = SharedPreferenceHelper.getInteger(view.getContext(), "orderType");

        loadFiles(file, Arrays.asList(file.listFiles()), sortType, orderType);
    }

    @Override
    public void onFileClicked(View view, File file)
    {
        Uri uri =  Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mime = "*/*";
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        if (mimeTypeMap.hasExtension(
                MimeTypeMap.getFileExtensionFromUrl(uri.toString())))
            mime = mimeTypeMap.getMimeTypeFromExtension(
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        intent.setDataAndType(FileProvider.getUriForFile(
                view.getContext(), view.getContext().getPackageName() + ".provider", file) ,mime);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    @Override
    public void onFileLongClicked(View view, File file)
    {
        fileManager.setFilePathForContext(file.getAbsolutePath());
    }

    public void updateFiles ()
    {
        int index = fileManager.getLevelCount() - 1;
        filesAdapter.updateFiles(fileManager.getFilesAtLevel(index));
    }

    public void removeFile (File file)
    {
        int index = fileManager.getLevelCount() - 1;

        List<File> files = filesAdapter.removeFile(file);
        fileManager.createLevel(index, fileManager.getDirectoryPathAtLevel(index), files);
    }

    private void changeLoadingDisplay ()
    {
        View view = getView();
        if (view != null)
        {
            View recyclerView = ((ViewGroup) view.getParent()).findViewById(R.id.fragment_files_rv);
            View progressBar = ((ViewGroup) view.getParent()).findViewById(R.id.fragment_files_pb);

            int progressBarVisibility = progressBar.getVisibility();
            int recyclerViewVisibility = recyclerView.getVisibility();

            recyclerView.setVisibility(progressBarVisibility);
            progressBar.setVisibility(recyclerViewVisibility);
        }
    }

    public void setOnFragmentHierarchyComponentCreatedListener (OnFragmentHierarchyComponentCreatedListener listener)
    {
        this.listener = listener;
    }

    public interface OnFragmentHierarchyComponentCreatedListener
    {
        void onFragmentHierarchyComponentCreated (String filePath);
    }
}