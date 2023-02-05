package com.example.sae32;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sae32.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.LoggerView;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ArrayAdapter<String> interfacesadapter;
    private ArrayAdapter<String> ipAdapter;
    public LoggerView loggerView= new LoggerView(Level.ALL);
    static Logger logger;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        AppObject.initClass(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        logger=AppObject.getLogger();
        loggerView.setView(binding.textView);
        loggerView.setContext(getApplicationContext());
        setContentView(binding.getRoot());
        loggerView.setVisibility(View.INVISIBLE);
        interfacesadapter =  new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        interfacesadapter.addAll(AppObject.getInterfaces());
        ipAdapter= new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        ipAdapter.setNotifyOnChange(true);
        binding.InterfacesSpinner.setAdapter(interfacesadapter);
        binding.IpSpinner.setAdapter(ipAdapter);
        binding.InterfacesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id){
                MainActivity.this.ipAdapter.clear();
                List<String> IpList = AppObject.getIpInterface(adapterView.getSelectedItem().toString());
                if(IpList.size()==0){
                    IpList.add("None");
                }
                MainActivity.this.ipAdapter.addAll(IpList);
            }
            public void onNothingSelected(AdapterView<?> adapterView){
            }
        });
        binding.loggerswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.this.loggerView.setVisibility(View.VISIBLE);
                }
                else{
                    MainActivity.this.loggerView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        logger.info("Welcome to netApp");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}