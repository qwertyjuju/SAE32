package com.example.sae32;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Switch;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.example.sae32.logic.AppObject;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private ArrayAdapter<String> interfacesadapter;
    private ArrayAdapter<String> ipAdapter;
    private LoggerView loggerView;

    static Logger logger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger= Logger.getLogger("netApp");
        AppObject.initClass(this, logger);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loggerView= new LoggerView(binding.textView, Level.ALL);
        logger.addHandler(loggerView);
        logger.info("Welcome to netApp");
        interfacesadapter =  new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        interfacesadapter.addAll(AppObject.getInterfaces());
        ipAdapter= new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
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
        binding.IpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id){
                AppObject.setUsedIp(adapterView.getSelectedItem().toString());
            }
            public void onNothingSelected(AdapterView<?> adapterView){
            }
        });
        binding.loggerswitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((Switch) view).isChecked();
                if (checked) {
                    MainActivity.this.loggerView.setVisibility(View.VISIBLE);
                }
                else{
                    MainActivity.this.loggerView.setVisibility(View.INVISIBLE);
                }
            }
        });
        setSupportActionBar(binding.toolbar);
        //NavHostFragment navfragment = FragmentManager.findFragmentById((this, R.id.nav_host_fragment_content_main);
        /*
        navHostFragment navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        */
    }

    @Override
    public void onResume(){
        super.onResume();
        logger.info("Welcome to netApp");
        logger.info("MainActivity setup finished.");
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