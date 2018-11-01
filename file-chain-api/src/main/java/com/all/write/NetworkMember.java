package com.all.write;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class NetworkMember implements Serializable {
    private static final long serialVersionUID = 1L;

    private String publicKey;
    private String address;

    public NetworkMember() {
    }

    public NetworkMember(String publicKey, String address) {
        this.publicKey = publicKey;
        this.address = address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getAddress() {
        return address;
    }

    public void setString(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "NetworkMember{" +
                "publicKey='" + publicKey + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
