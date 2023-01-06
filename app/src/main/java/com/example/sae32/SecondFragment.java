package com.example.sae32;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sae32.databinding.FragmentSecondBinding;
import com.example.sae32.logic.Client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SecondFragment extends Fragment {

    private Client client;
    private FragmentSecondBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
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
                try{
                    InetAddress ip = InetAddress.getByName(SecondFragment.this.binding.editTextIp.getText().toString());
                    int port = Integer.parseInt(SecondFragment.this.binding.editTextPort.getText().toString());
                    SecondFragment.this.client = new Client(ip, port);
                }catch(UnknownHostException | NumberFormatException e){
                    MainActivity.logger.warning("Client not created: "+ e.getMessage());
                }
            }
        });
        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.addMessage(SecondFragment.this.binding.editTextMsg.getText().toString());
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