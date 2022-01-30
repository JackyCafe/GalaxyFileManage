package com.ian.galaxyfilemanage;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    String TAG = MainActivity.class.getName();
    File download,pictures,document,movies,dcim;
    List<File> fileList = new ArrayList<>();
    ConstraintLayout cl;
    TextView tv;
    Timer timer;
    TaskHandler handler;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    String[] envs = {Environment.DIRECTORY_DOWNLOADS
            ,Environment.DIRECTORY_DOCUMENTS
            ,Environment.DIRECTORY_MOVIES
            ,Environment.DIRECTORY_DCIM
            ,Environment.DIRECTORY_ALARMS
            ,Environment.DIRECTORY_AUDIOBOOKS
            ,Environment.DIRECTORY_NOTIFICATIONS
            ,Environment.DIRECTORY_PICTURES};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = new Timer();
        tv = findViewById(R.id.textView);
        cl = findViewById(R.id.cl);
        handler = new TaskHandler();

        for (String env: envs) {
            fileList.add(new File(Environment.getExternalStoragePublicDirectory(env).toString()));
        }


        if (checkPermission()){
            manageFiles();
        }else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                try{
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                activityResultLauncher.launch(intent);}catch (Exception e){

                }
            }
        }

    }




    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                     Intent it = result.getData();
                     manageFiles();

                }
            });


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT  >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        }else{
            int writePerm =   ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPerm = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            return writePerm== PackageManager.PERMISSION_GRANTED && readPerm==PackageManager.PERMISSION_GRANTED;
        }
    }


    public void manageFiles(){
        StringBuilder sb = new StringBuilder();
        for(File path:fileList){
            for(File f:path.listFiles()){
                if (f.isDirectory()){
                    try {
                        Log.v(TAG,path.getPath()+"-->"+f.getName());
                        sb.append(path.getPath()+"-->"+f.getName()+"\n");
                        deleteDirectoryStream(f.toPath());
                    } catch (IOException e) {
                        Log.v(TAG,e.toString());
                        e.printStackTrace();
                    }
                }else{
                    sb.append(path.getPath()+"-->"+f.getName()+"\n");
                    Log.v(TAG,path.getPath()+"-->"+f.getName());
                    f.delete();
                }
            }
        }
        tv.setText(sb.toString());
        Log.v(TAG,"Done!");

    }

    void deleteDirectoryStream(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    public void btndelete(View view) {
        manageFiles();
    }

    public void schedule(View view) {
        timer.schedule(new MyTask(fileList,"200"),0,5*1000*60);
    }

    public class MyTask extends TimerTask {
        //    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd--HH時mm分ss");
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        List<File> files;
        String job ;
        public MyTask(List<File> files,String job){
            this.files = files;
            this.job = job;
        }
        @Override
        public void run() {

            handler.sendEmptyMessage(0);

        }
    }

    class TaskHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            manageFiles();
        }
    }

}