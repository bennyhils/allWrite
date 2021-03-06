package com.all.write;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkMember that = (NetworkMember) o;
        return Objects.equals(publicKey, that.publicKey) &&
                Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publicKey, address);
    }
}
