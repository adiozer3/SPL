package bgu.spl181.net.impl.myProtocol;


import java.util.ArrayList;
import java.util.List;

public class Movie {
    private Integer id;
    private String name;
    private Integer price;
    private List<String> bannedCountries;
    private Integer availableAmount;
    private Integer totalAmount; // should it be string?

    public Movie(int id,String name, int price, int totalAmount, String[] bannedCountries)
    {
        this.id=id;
        this.name=name;
        this.price=price;
        this.totalAmount=totalAmount;
        this.availableAmount=totalAmount;
        this.bannedCountries=new ArrayList<>();
        for(int i=0;i<bannedCountries.length;i++)
            this.bannedCountries.add(bannedCountries[i]);



    }


    public void setAvailableAmount(int availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setBannedCountries(List<String> bannedCountries) {
        this.bannedCountries = bannedCountries;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getAvailableAmount() {
        return availableAmount;
    }

    public int getTotalAmount() {
        return totalAmount;
    }


    public int getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<String> getBannedCountries() {
        return bannedCountries;
    }
    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", name = " + name + "]";
    }


}