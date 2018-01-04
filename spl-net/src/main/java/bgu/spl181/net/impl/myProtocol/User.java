package bgu.spl181.net.impl.myProtocol;

import java.util.ArrayList;

public class User {

    private Integer balance;

    private String username;

    private ArrayList<UserMovie> movies;

    private String type;

    private String password;

    private String country;


    public User( String user, String password, String country){
        this.username=user;
        this.password=password;
        this.country=country;
        this.balance=0;
        this.type="normal";
        this.movies= new ArrayList<>();
    }

    public boolean isAdmin(){
        return getType().equals("admin");
    }

    public int getBalance ()
    {
        return balance;
    }

    public void setBalance (int balance)
    {
        this.balance = balance;
    }

    public String getUsername ()
    {
        return username;
    }

    public void setUsername (String username)
    {
        this.username = username;
    }

    public ArrayList<UserMovie> getMovies ()
    {
        return movies;
    }

    public void setMovies (ArrayList<UserMovie> movies)
    {
        this.movies = movies;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getPassword ()
    {
        return password;
    }

    public void setPassword (String password)
    {
        this.password = password;
    }

    public String getCountry ()
    {
        return country;
    }

    public void setCountry (String country)
    {
        this.country = country;
    }


    @Override
    public String toString()
    {
        return "ClassPojo [balance = "+balance+", username = "+username+", movies = "+movies+", type = "+type+", password = "+password+", country = "+country+"]";
    }
}

