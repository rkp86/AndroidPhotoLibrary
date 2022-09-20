package com.example.photolibrary.controller;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.photolibrary.R;
import com.example.photolibrary.adapter.CustomAdapter;
import com.example.photolibrary.model.Album;
import com.example.photolibrary.model.Photo;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Photos extends AppCompatActivity {

    private GridView photosList;
    static int chosenPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        chosenPic=-1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        //myToolbar.setTitle("" + Albums.list.get(Albums.chosenAlbum).getName());
        if (Albums.chosenAlbum>=0)
        ab.setTitle("" + Albums.list.get(Albums.chosenAlbum).getName());
        //enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        photosList = findViewById(R.id.photos);

        setImages();
        photosList.setOnItemClickListener((list,text,pos,id) -> indexP(pos));
    }


    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.photos_menu,menu);
        return true;
    }


    //if the action bar tools are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle presses on the action bar items
        switch (item.getItemId()) {

            case android.R.id.home:
                Albums.chosenAlbum=-1;
                Intent intent = new Intent(this, Albums.class);
                startActivity(intent);
                return true;

            case R.id.addPhoto:
                onClick(new View(this));
                return true;

            case R.id.deletePhoto:
                deletePhoto();
                return true;

            case R.id.displayPicture:
                display();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       // startActivityForResult(intent, 3);
    }

    @SuppressLint("WrongConstant")
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            Albums.list.get(Albums.chosenAlbum).addPhoto(new Photo(uri.toString()));
            try {
                writeObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setImages();
    }

    public void indexP(int pos){
        chosenPic = pos;
        photosList.setSelected(true);
        photosList.setChoiceMode(pos);
    }

    public void display() {
        if (chosenPic>=0) {
            Intent intent = new Intent(this, DisplayPhoto.class);
            startActivity(intent);
        }
        else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("No photo selected.");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void setImages() {
        ArrayList<String> images = new ArrayList<String>();
        Log.d("ALbum #", Albums.chosenAlbum+"");
        Log.d("album", Albums.list.size()+"");
        if (Albums.list.get(Albums.chosenAlbum).getPhotos().size()>0) {
            for (Photo p : Albums.list.get(Albums.chosenAlbum).getPhotos()) {
                images.add(p.getImage());
            }
            CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), images);
            photosList.setAdapter(customAdapter);
        }
        if (images.size()==0) chosenPic=-1;
    }


    public void deletePhoto(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        if (chosenPic >= 0) {
            alertDialogBuilder.setMessage("Are you sure you want to delete this photo?");
            alertDialogBuilder.setNegativeButton("NO",null);
            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Albums.list.get(Albums.chosenAlbum).getPhotos().remove(Photos.chosenPic);
                    setImages();
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
            alertDialogBuilder.setMessage("No photo selected.");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

    private void writeObject() throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream("/data/data/com.example.photolibrary/files/data.dat");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(Albums.list);
        objectOutputStream.close();
        fileOutputStream.close();

    }

}

