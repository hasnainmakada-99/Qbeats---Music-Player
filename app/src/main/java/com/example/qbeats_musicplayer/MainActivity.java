package com.example.qbeats_musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
//import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView =findViewById(R.id.listview);
        Dexter.withContext(MainActivity.this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        ArrayList<File> mysongs=fetchfile(Environment.getExternalStorageDirectory());
                        String[]items=new String[mysongs.size()];
                       for (int i=0;i<mysongs.size();i++){
                           items[i]=mysongs.get(i).getName().replace(".mp3", "");
                       }
                       for(int i=0;i<mysongs.size();i++){
                           items[i]=mysongs.get(i).getName().replace(".mpeg", "");
                       }
                        ArrayAdapter<String> ad=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, items);
                       listView.setAdapter(ad);
                       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                           @Override
                           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                               Intent intent=new Intent(MainActivity.this, playsong.class);
                               String currentsong =listView.getItemAtPosition(i).toString();
                               intent.putExtra("getsongs", mysongs);
                               intent.putExtra("currentsongs", currentsong);
                               intent.putExtra("position", i);
                               startActivity(intent);
                           }
                       });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(), "Sorry Your Songs Could Not Be Fetched", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                })
                .check();

    }

    // This Method Will Fetch All the songs from the External Directory Of the Phone
    public ArrayList<File> fetchfile(File file){
      ArrayList arrayList=new ArrayList(); // the arraylist is dynamic so there is no limit on the number of files
      File[]songs=file.listFiles(); // array of files
                // This method will list all the files from the directory
        if(songs!=null){ // if the songs are there
            for(File myfile: songs){ // traversing through songs
                if(!myfile.isHidden() && myfile.isDirectory()){
                    arrayList.addAll(fetchfile(myfile));
                    // Arraylist wil add all the files to the listview
                }
                else{
                    if(myfile.getName().endsWith(".mp3") && !myfile.getName().startsWith(".")){
                        arrayList.add(myfile);
                    }
                    else if(myfile.getName().endsWith(".mpeg")){
                        arrayList.add(myfile);
                    }
                }
            }
        }
        return arrayList;
    }
}