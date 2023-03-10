package com.example.sae32;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sae32.databinding.FragmentServerBinding;
import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Messaging.Messaging;
import com.example.sae32.logic.Messaging.TextViewMessagingHandler;
import com.example.sae32.logic.utils.ConnectionType;

/* Fragment Serveur */

public class FirstFragment extends Fragment {
    private FragmentServerBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentServerBinding.inflate(inflater, container, false);
        TextViewMessagingHandler handler;
        handler = TextViewMessagingHandler.get("Server",AppObject.serverMessaging);
        handler.setOutput(binding.ServerMessagingTextView);
        AppObject.serverMessaging.publishAll();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_MainFragment);
            }
        });
        binding.buttonLaunchServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String servname = binding.editTextServerName.getText().toString();
                    if(!servname.equals("")) {
                        AppObject.serverMessaging.createServer(
                                Integer.parseInt(binding.ListeningPort.getText().toString()),
                                ConnectionType.valueOf(binding.buttonServerType.getText().toString()),
                                servname
                                );
                    }else{
                        AppObject.logger.warning("Please enter a Server name");
                    }
                }
                catch(NumberFormatException e){
                    AppObject.logger.warning((String)getText(R.string.network_error1));
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}