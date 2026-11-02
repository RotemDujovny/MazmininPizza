package com.example.mazminiggapigga;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {

    private final String size;
    private final String crust;
    private final int quantity;
    private final int price;

    public Order(String size, String crust, int quantity, int price) {
        this.size = size;
        this.crust = crust;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSize() { return size; }
    public String getCrust() { return crust; }
    public int getQuantity() { return quantity; }
    public int getPrice() { return price; }

    protected Order(Parcel in) {
        size = in.readString();
        crust = in.readString();
        quantity = in.readInt();
        price = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(size);
        dest.writeString(crust);
        dest.writeInt(quantity);
        dest.writeInt(price);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) { return new Order(in); }

        @Override
        public Order[] newArray(int n) { return new Order[n]; }
    };
}
