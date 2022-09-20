package com.example.photolibrary.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.photolibrary.R;
import com.example.photolibrary.model.Album;
import com.example.photolibrary.model.Photo;
import com.example.photolibrary.model.Tag;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class DisplayPhoto extends AppCompatActivity {

    ImageView img;
    Toolbar myToolbar;
    Button prev;
    Button next;
    Button addTag;
    Button deleteTag;
    private ListView tags ;
    static ArrayAdapter<String> adapter;

    private int tagIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_photo);


        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setDisplayHomeAsUpEnabled(true);

        prev = findViewById(R.id.previous);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                previous();
            }
        });

        next=findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                next();
            }
        });

        addTag = findViewById(R.id.addTagButton);
        addTag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addtag();
            }
        });
        deleteTag = findViewById(R.id.deleteTagButton);
        deleteTag.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deletetag();
            }
        });

      //  viewpager = (ViewPager) findViewById(R.id.viewpager_id);
        String uri = Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getImage();
        img = findViewById(R.id.displaypic);
        img.setImageURI(Uri.parse(uri));

        tags = findViewById(R.id.tags);

        ArrayList<String> phototags = new ArrayList<String>();
        for (Tag t : Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getTags()) {
            phototags.add(t.toString());
        }
        adapter = new ArrayAdapter<>(this, R.layout.custom_list, R.id.textView, phototags);
        tags.setAdapter(adapter);

        try {
            writeObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        tags.setOnItemClickListener((list,text,pos,id) -> indexTag(pos));
    }

    public void indexTag(int pos){
        tagIndex = pos;
        tags.setSelected(true);
        tags.setChoiceMode(pos);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.display_menu,menu);
        return true;
    }
    //if the action bar tools are clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, Photos.class);
                startActivity(intent);
                return true;

            case R.id.deletePhoto:
                deletePhoto();
                return true;

            case R.id.move:
                move();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deletePhoto(){
        if (Photos.chosenPic>=0) {
            Albums.list.get(Albums.chosenAlbum).getPhotos().remove(Photos.chosenPic);
            try {
                writeObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Albums.list.get(Albums.chosenAlbum).getPhotos().size()==0){
            Intent intent = new Intent(this, Photos.class);
            startActivity(intent);
        }
        else
            next();
    }


    public void ch (){
        if (Albums.list.get(Albums.chosenAlbum).getPhotos().size()==0){
            Intent intent = new Intent(this, Photos.class);
            startActivity(intent);
        }
        else
            next();

    }
    public void move() {
        AlertDialog.Builder tagd = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.move_dialog,null);
        tagd.setTitle("Move to album...");

        Spinner spin = (Spinner) view.findViewById(R.id.spinner3);
        ArrayList<String> albums = new ArrayList<String>();
        for (Album a : Albums.list) {
            albums.add(a.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, albums);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        tagd.setPositiveButton("Move", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                int a = spin.getSelectedItemPosition();
                Albums.list.get(a).getPhotos().add(Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic));
                Albums.list.get(Albums.chosenAlbum).getPhotos().remove(Photos.chosenPic);
                try {
                    writeObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ch();
            }
        });

        tagd.setNegativeButton("Cancel",null);
        tagd.setView(view);
        AlertDialog dialog = tagd.create();
        dialog.show();
    }

    public void displayInfo(){
        String uri = Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getImage();
        img.setImageURI(Uri.parse(uri));

        ArrayList<String> phototags = new ArrayList<String>();
        for (Tag t : Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getTags()) {
            phototags.add(t.toString());
        }
        adapter = new ArrayAdapter<>(this, R.layout.custom_list, R.id.textView, phototags);
        tags.setAdapter(adapter);


    }

    public void previous(){
        Photos.chosenPic--;
        if (Photos.chosenPic < 0) {
            if (Albums.list.get(Albums.chosenAlbum).getPhotos().size() == 0) {
                Intent intent = new Intent(this, Photos.class);
                startActivity(intent);
            }
            else {
                Photos.chosenPic = Albums.list.get(Albums.chosenAlbum).getPhotos().size() - 1;
            }
        }
        displayInfo();
    }


    public void next(){
        if (Albums.list.get(Albums.chosenAlbum).getPhotos().size() == 0) {
            Photos.chosenPic=0;
            Intent intent = new Intent(this, Photos.class);
            startActivity(intent);
            Log.d("ALbummm #", Albums.chosenAlbum+"");

        }
        Photos.chosenPic++;
        if (Photos.chosenPic >= Albums.list.get(Albums.chosenAlbum).getPhotos().size()) {
           Photos.chosenPic = 0;
        }
        displayInfo();
    }

    public void addtag(){
        AlertDialog.Builder tagd = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.tag_dialog,null);
        tagd.setTitle("Add a tag");

        Spinner spin = (Spinner) view.findViewById(R.id.spinner3);
        EditText text = view.findViewById(R.id.textd);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.tagNames));
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        tagd.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean empty = false; //if no value input
                boolean location = false; //if location is repeated
                boolean check = false; //if tag is repeated
                String tagValue = text.getText().toString();

                if (tagValue.length()<=0) empty = true;

                for (Tag t: Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getTags()) {
                    if (t.getName().equals(spin.getSelectedItem().toString()) && t.getValue().equals(tagValue.toLowerCase()))
                        check = true;
                    if (t.getName().equals("location") && spin.getSelectedItem().toString().equals("location")) {
                        location = true;
                    }
                }

                if (!check && !empty && spin.getSelectedItemPosition()==0) {//person
                    Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getTags().add(new Tag("person",tagValue));
                }
                else if (!location && !check && !empty && spin.getSelectedItemPosition()==1) { //location
                    Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getTags().add(new Tag("location",tagValue));
                }

                errorTags(empty, location, check, spin.getSelectedItem().toString());
            }
        });

        tagd.setNegativeButton("Cancel",null);
        tagd.setView(view);
        AlertDialog dialog = tagd.create();
        dialog.show();

    }

    public void errorTags(boolean empty, boolean one, boolean check, String n) {
        if (empty) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Must enter a tag value.");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if(check) { //there is no duplicate tag in the list so you can add the new one
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("This tag already exists.");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else if (one) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Cannot have more than one of this tagName: " + n);
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else {
            displayInfo();
            try {
                writeObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public void deletetag(){
        if (tagIndex>=0) {
            Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getTags().remove(tagIndex);
        }
            ArrayList<String> phototags = new ArrayList<String>();
            for (Tag t : Albums.list.get(Albums.chosenAlbum).getPhotos().get(Photos.chosenPic).getTags()) {
                phototags.add(t.toString());
            }
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(this, R.layout.activity_main, phototags);
            tags.setAdapter(adapter);
        try {
            writeObject();
        } catch (Exception e) {
            e.printStackTrace();
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