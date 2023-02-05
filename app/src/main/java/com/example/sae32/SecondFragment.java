package com.example.sae32;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sae32.databinding.FragmentClientBinding;
import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Messaging.Messaging;
import com.example.sae32.logic.Messaging.TextViewMessagingHandler;
import com.example.sae32.logic.utils.ConnectionType;

import java.net.InetAddress;
import java.net.UnknownHostException;

/* Fragment Client*/
public class SecondFragment extends Fragment {
    private FragmentClientBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentClientBinding.inflate(inflater, container, false);
        TextViewMessagingHandler handler = TextViewMessagingHandler.get("Client", AppObject.clientMessaging);
        handler.setOutput(binding.ClientMessagingTextView);
        AppObject.clientMessaging.publishAll();
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        binding.buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_MainFragment);
            }
        });
        binding.buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Clientname= binding.editTextClientName.getText().toString();
                if(!Clientname.equals("")) {
                    try {
                        InetAddress ip = InetAddress.getByName(SecondFragment.this.binding.editTextIp.getText().toString());
                        int port = Integer.parseInt(SecondFragment.this.binding.editTextPort.getText().toString());
                        AppObject.clientMessaging.createClient(
                                ip,
                                port,
                                SecondFragment.this.binding.editTextClientName.getText().toString(),
                                ConnectionType.valueOf(SecondFragment.this.binding.buttonClientType.getText().toString())
                        );
                    } catch (UnknownHostException | NumberFormatException e) {
                        AppObject.logger.warning("Client not created: " + e.getMessage());
                    }
                }else{
                    AppObject.logger.warning("Please enter a clientname");
                }
            }
        });
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppObject.clientMessaging.sendMessage(SecondFragment.this.binding.editTextMsg.getText().toString());
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}