package com.example.photolibrary.controller;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.photolibrary.R;
import com.example.photolibrary.adapter.CustomAdapter;
import com.example.photolibrary.model.Photo;

import java.util.ArrayList;
import java.util.Locale;

public class Search extends AppCompatActivity {

    GridView gridView;
    private ArrayList<String> images = new ArrayList<String>();
    private ArrayList<Photo> results = new ArrayList<Photo>();
    Button search;
    Spinner tagnames;
    Spinner tagnames2;
    RadioButton and;
    RadioButton or;
    AutoCompleteTextView searchInput;
    AutoCompleteTextView searchInput2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        images.clear();

        tagnames = findViewById(R.id.spinner);
        tagnames2 = findViewById(R.id.spinner2);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_activated_1, getResources().getStringArray(R.array.tagNames));
        tagnames.setAdapter(nameAdapter);
        tagnames2.setAdapter(nameAdapter);

        and = findViewById(R.id.and);
        or = findViewById(R.id.or);

        searchInput = findViewById(R.id.firstTagValue);
        searchInput2 = findViewById(R.id.secondTagValue);

        ArrayList<String> tagss = new ArrayList<String>();
        for (int i = 0; i < Albums.list.size(); i++ ) {
            for (int j = 0; j < Albums.list.get(i).getPhotos().size(); j++ ) {
                for (int k = 0; k < Albums.list.get(i).getPhotos().get(j).getTags().size(); k++ ) {
                    tagss.add(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue());
                }
            }
        }
        ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1 ,tagss);
        searchInput.setAdapter(adapt);
        searchInput2.setAdapter(adapt);

        search = findViewById(R.id.searchButton);
        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchPhotos();
            }
        });

        gridView = findViewById(R.id.imageGrid);

    }

    public void setPics() {
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), images);
        gridView.setAdapter(customAdapter);
    }

    public void searchPhotos(){
        images.clear();
        results.clear();
        int b=0;

        if (searchInput.getText().length() <= 0) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Enter a search term");
            alertDialogBuilder.setNegativeButton("OK",null);

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else {
            for (int i = 0; i < Albums.list.size(); i++ ) { //for loop goes through all albums of user
                if (b==1) break;
                for (int j = 0; j < Albums.list.get(i).getPhotos().size(); j++) { //for loop goes through all photos in each album


                    if (searchInput2.getText().length() <= 0) searchInput2.setText("");
                    for (int k = 0; k < Albums.list.get(i).getPhotos().get(j).getTags().size(); k++) { //for loop goes through list of tags for a specific photo

                        if (or.isChecked() || (!and.isChecked() && !or.isChecked()) || searchInput2.getText().length() <= 0) { //if the search is OR
                            if ((tagnames.getSelectedItem().toString().equals(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getName()) && searchInput.getText().toString().toLowerCase().equalsIgnoreCase(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue())) ||
                                    (tagnames2.getSelectedItem().toString().equals(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getName()) && searchInput2.getText().toString().equalsIgnoreCase(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue()))) {
                                results.add(Albums.list.get(i).getPhotos().get(j));
                                break; //found right tag so photo is part of results
                            }
                            else if (tagnames.getSelectedItem().toString().equals(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getName()) && Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue().toLowerCase().indexOf(searchInput.getText().toString().toLowerCase())==0) {
                                results.add(Albums.list.get(i).getPhotos().get(j));
                                break;
                            }
                            /**
                            else if (tagnames2.getSelectedItem().toString().equals(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getName()) && Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue().toLowerCase().indexOf(searchInput2.getText().toString().toLowerCase())==0){
                                results.add(Albums.list.get(i).getPhotos().get(j));
                                break;
                            }
                            */

                            }


                        else if (and.isChecked()) { //if the search is AND
                            if (tagnames.getSelectedItem().toString().equals(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getName()) && searchInput.getText().toString().equalsIgnoreCase(Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue()))
                            {
                                for (int l = 0; l < Albums.list.get(i).getPhotos().get(j).getTags().size(); l++ ) {
                                    if (tagnames2.getSelectedItem().toString().equals(Albums.list.get(i).getPhotos().get(j).getTags().get(l).getName()) && searchInput2.getText().toString().equalsIgnoreCase(Albums.list.get(i).getPhotos().get(j).getTags().get(l).getValue())) {
                                        results.add(Albums.list.get(i).getPhotos().get(j));
                                        break; //found right tag so photo is part of results
                                    }
                                }
                            }

                            /**
                            else if (Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue().toLowerCase().startsWith(searchInput.getText().toString().toLowerCase())) {
                                for (int l = 0; l < Albums.list.get(i).getPhotos().get(j).getTags().size(); l++ ) {
                                    if (Albums.list.get(i).getPhotos().get(j).getTags().get(k).getValue().toLowerCase().startsWith(searchInput2.getText().toString().toLowerCase())) {
                                        results.add(Albums.list.get(i).getPhotos().get(j));
                                        break; //found right tag so photo is part of results
                                    }
                                }
                            }
                             */
                        }
                    }
                }
            }
            images.clear();
            for (Photo p : results){
                images.add(p.getImage());
            }
            setPics();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, Albums.class);
                startActivity(intent);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

}