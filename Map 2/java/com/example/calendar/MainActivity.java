package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ADD HERE
        lvItems = (ListView) findViewById(R.id.lvItems);
        items = new ArrayList<String>();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        /*items.add("First Item");
        items.add("Second Item");*/
        setupListViewListener();

        System.out.println(runScript(this));
    }

    public static String runScript(Context androidContextObject) {
        // Get the JavaScript in previous section
        try {

            Resources resources = androidContextObject.getResources();
            InputStream rawResource = resources.openRawResource(R.raw.config);


            Properties properties = new Properties();
            properties.load(rawResource);

            String source = properties.getProperty("jsExecute");
            String functionName = "getRhinoHello";
            //Object[] functionParams = new Object[]{};
            Object[] functionParams = new Object[]{"cool"};
            // Every Rhino VM begins with the enter()
            // This Context is not Android's Context
            org.mozilla.javascript.Context rhino = org.mozilla.javascript.Context.enter();

            // Turn off optimization to make Rhino Android compatible
            rhino.setOptimizationLevel(-1);

            Scriptable scope = rhino.initStandardObjects();

            // This line set the javaContext variable in JavaScript
            //ScriptableObject.putProperty(scope, "javaContext", org.mozilla.javascript.Context.javaToJS(androidContextObject, scope));

            // Note the forth argument is 1, which means the JavaScript source has
            // been compressed to only one line using something like YUI
            rhino.evaluateString(scope, source, "JavaScript", 1, null);

            // We get the hello function defined in JavaScript
            Object obj = scope.get(functionName, scope);

            if (obj instanceof Function) {
                Function function = (Function) obj;
                // Call the hello function with params
                Object result = function.call(rhino, scope, scope, functionParams);
                // After the hello function is invoked, you will see logcat output

                // Finally we want to print the result of hello function
                String response = org.mozilla.javascript.Context.toString(result);
                return response;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // We must exit the Rhino VM
            org.mozilla.javascript.Context.exit();
        }

        return null;
    }

    // Attaches a long click listener to the listview
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {
                        // Remove the item within array at position
                        items.remove(pos);
                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        // Return true consumes the long click event (marks it handled)
                        return true;
                    }

                });
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
    }

    private void readItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, items);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openMap(View v){
        Intent intent = new Intent(this, OpenMapActivity.class);
        startActivity(intent);
    }
}
