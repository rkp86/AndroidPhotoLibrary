package com.example.photolibrary.controller;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.photolibrary.R;
import com.example.photolibrary.model.Album;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Albums extends AppCompatActivity {

    private ListView listView;
    static ArrayAdapter<String> adapter;
    public static ArrayList<Album> list = new ArrayList<Album>();
    public static int chosenAlbum = -1;
    static int run=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.albums);

        if (run ==-1){
            try {
               readApp();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        listView = findViewById(R.id.albums);

        ArrayList<String> albums = new ArrayList<String>();
        for (Album al : list) {
            albums.add(al.getName());
        }
        adapter = new ArrayAdapter<>(this, R.layout.custom_list, R.id.textView, albums);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((list,text,pos,id) -> indexAl(pos));

    }

    //load the action bar tools
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.albums_menu,menu);
        return true;
    }

    //if the action bar tools are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.search:
                startActivity(new Intent(this, Search.class));
                return true;

            case R.id.addAlbum:
                AddAlbum(this);
                return true;

            case R.id.deleteAlbum:
                delete();
                return true;

            case R.id.openAlbum:
                showPhoto();
                return true;

            case R.id.renameAlbum:
                rename(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void AddAlbum(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Add Album")
                .setMessage("Enter a new album name")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        if (task.length()>0) {
                            list.add(new Album(task.trim()));
                            adapter.add(task);
                            adapter.notifyDataSetChanged();
                        }
                        try {
                            writeObject();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }

    public void indexAl(int pos){
        chosenAlbum = pos;
        listView.setSelected(true);
        listView.setChoiceMode(pos);
    }
    //when an album in the list view is clicked on
    private void showPhoto() {
        if (chosenAlbum>=0) {
            Intent intent = new Intent(this, Photos.class);
            startActivity(intent);
        }
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("No album selected.");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
       /* Bundle bundle = new Bundle();
        bundle.putString(ROUTE_NAME, routeNames[pos]);
        bundle.putString(ROUTE_DETAIL, routeDetails[pos]);
        Intent intent = new Intent(this, Photos.class);
        intent.putExtras(bundle);
        startActivity(intent);
        */
    }

    public void delete(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if (chosenAlbum >= 0 && Albums.list.size()>0) {
            alertDialogBuilder.setMessage("Are you sure you want to delete this album?");
            alertDialogBuilder.setNegativeButton("NO",null);
            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Albums.list.remove(Albums.chosenAlbum);
                    setListAdapter();
                    try {
                        writeObject();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
        else {
            alertDialogBuilder.setMessage("No album selected.");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }


    public void rename(Context c) {
        if (chosenAlbum>=0) {
            final EditText taskEditText = new EditText(c);
            AlertDialog dialog = new AlertDialog.Builder(c)
                    .setTitle("Rename Album")
                    .setMessage("Enter an album name")
                    .setView(taskEditText)
                    .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(taskEditText.getText());
                            if (task.length() > 0) {
                                Albums.list.get(Albums.chosenAlbum).setName(task);
                            }

                            try {
                                writeObject();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            setListAdapter();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        }
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setMessage("No album selected.");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

    public void setListAdapter() {
        ArrayList<String> albums = new ArrayList<String>();
        for (Album al : list) {
            albums.add(al.getName());
        }
        adapter = new ArrayAdapter<>(this, R.layout.custom_list, R.id.textView, albums);
        listView.setAdapter(adapter);
    }

    private void writeObject() throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream("/data/data/com.example.photolibrary/files/data.dat");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(Albums.list);
        objectOutputStream.close();
        fileOutputStream.close();

    }

    private void readApp()
            throws IOException, EOFException, ClassNotFoundException {
        File myFile = new File("/data/data/com.example.photolibrary/files/data.dat");
        if (myFile.length()>0) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(myFile));
            ArrayList<Album> users = (ArrayList<Album>)ois.readObject();
            list = users;
            ois.close();
        }
    }


}
