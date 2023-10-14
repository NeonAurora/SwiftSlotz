package com.example.swiftslotz.utilities;

public interface ClientNameCallback {
    void onClientNameReceived(String clientName);
    void onError(String error);
}
