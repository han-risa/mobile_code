package com.example.tanvir.diary.Activity;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tanvir.diary.Database.SqliteDatabase;
import com.example.tanvir.diary.NeedyClass.Information;
import com.example.tanvir.diary.NeedyClass.InformationAdapter;
import com.example.tanvir.diary.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    SqliteDatabase db;
    ArrayList<Information> arrayList;
    ArrayList<String> selectList = new ArrayList<String>();
    ArrayList<Integer> unDeleteSelect = new ArrayList<Integer>();

    ArrayAdapter arrayAdapter;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new SqliteDatabase(this);
        SQLiteDatabase sqliteDatabase = db.getWritableDatabase();

        listView = findViewById(R.id.ListviewId);

        arrayList=new ArrayList<Information>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

       // ClickListener untuk floating action bar
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(MainActivity.this,AddDataActivity.class);
               startActivity(intent);
            }
        });

        view();//memanggil view method

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this,UpdateActivity.class);
                intent.putExtra("subject",arrayList.get(i).getSubject());
                intent.putExtra("description",arrayList.get(i).getDescription());
                intent.putExtra("listId",arrayList.get(i).getId());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutIdMainActivity:
                about();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void view() {
        Cursor cursor = db.display();
        while (cursor.moveToNext()) {
            Information information = new Information(cursor.getString(0),cursor.getString(1),
                    cursor.getString(2),cursor.getString(3));
            arrayList.add(information);
        }
        Collections.reverse(arrayList);//membalikkan isi arrayList untuk menunjukan data dengan benar

        arrayAdapter = new InformationAdapter(this, arrayList);//memindahkan context dan arrayList ke arrayAdapter
        listView.setAdapter(arrayAdapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);//setting choice mode
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {//method untuk multiChoice option

            //untuk memastikan apakah item dalam kondisi Clickmode atau tidak
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                String id = arrayList.get(i).getId();//untuk mendapatkan id database
                //jika item ditekan kembali maka akan menjadi putih
                if(selectList.contains(id) && count>0){
                    listView.getChildAt(i).setBackgroundColor(Color.WHITE);
                    selectList.remove(id);
                    count--;
                }
                //sisanya akan menjadi warna abu
                else{
                    selectList.add(arrayList.get(i).getId());
                    listView.getChildAt(i).setBackgroundColor(Color.GRAY);
                    unDeleteSelect.add(i);//item position storing on new arrayList
                    count++;
                }
                actionMode.setTitle(count+" item selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();//untuk menghubungkan menu dan main menu
                inflater.inflate(R.menu.selector_layout,menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            //method ini untuk melakukan action seperti hapus dan share
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.deleteContextMenuId){
                    for(String i : selectList){
                        db.delete(i);
                        arrayAdapter.remove(i);
                        Toast.makeText(getApplicationContext(),count+" item Deleted",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }
                    arrayAdapter.notifyDataSetChanged();
                    actionMode.finish();
                    count = 0;
                }
                return true;
            }

            //method untuk destroy actionMode
            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                for(int i: unDeleteSelect){
                    listView.getChildAt(i).setBackgroundColor(Color.WHITE);//untuk mengembalikan warna item menjadi putih
                }
                count = 0;//untuk reset
                unDeleteSelect.clear();
                selectList.clear();
            }
        });
    }
    public void about(){
        Intent intent = new Intent(this,InstructionActivity.class);
        startActivity(intent);
    }
}
