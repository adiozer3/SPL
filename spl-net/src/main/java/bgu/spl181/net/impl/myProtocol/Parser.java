
package bgu.spl181.net.impl.myProtocol;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

import javax.jws.soap.SOAPBinding;


public class Parser {

    public static  makeMoviesList MoviesList;
    public static makeUsersList UsersList;

    public static ArrayList<User> ParseUsers(){
        try {
            FileReader fr = new FileReader("example_Users.json");
            Gson gsonFile = new Gson();
            UsersList = gsonFile.fromJson(fr,makeUsersList.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return UsersList.Users;
    }


    public static ArrayList<Movie> ParseMovies(){
        try {
            FileReader fr = new FileReader("example_Movies.json");
            Gson gsonFile = new Gson();
            MoviesList = gsonFile.fromJson(fr,makeMoviesList.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return MoviesList.Movies;
    }




    public static class makeMoviesList{
        private ArrayList<Movie> Movies = new ArrayList<>();
    }

    public static class makeUsersList{
        private ArrayList<User> Users = new ArrayList<>();
    }


    public static void writeUsers(ArrayList<User> userList){
        try {
            FileWriter fw = new FileWriter("example_Users.json");
            Gson gsonFile = new Gson();
            gsonFile.toJson(userList,fw);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeMovies(ArrayList<Movie> movieList){
        try {
            FileWriter fw = new FileWriter("example_Movie.json");
            Gson gsonFile = new Gson();
            gsonFile.toJson(movieList,fw);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int getCurentUser(String userName, ArrayList<User> userList) {
        for (int i=0; i<userList.size(); i++) {
            if (userList.get(i).getUsername().equals(userName)) {
                return i;
            }
        }
        return -1;
    }

    public static int getCurentMovie(String movieName, ArrayList<Movie> movieList) {
        for (int i=0; i<movieList.size(); i++) {
            if (movieList.get(i).getName().equals(movieName)) {
                return i;
            }
        }
        return -1;
    }







}



