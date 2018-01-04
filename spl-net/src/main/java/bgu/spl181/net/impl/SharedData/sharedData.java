package bgu.spl181.net.impl.SharedData;

import bgu.spl181.net.impl.myProtocol.Movie;
import bgu.spl181.net.impl.myProtocol.Parser;
import bgu.spl181.net.impl.myProtocol.User;

import java.util.ArrayList;

public class sharedData {

    private ArrayList<User> usersList;
    private ArrayList<Movie> moviesList;
    private ArrayList<Integer> loggedIn;
    private final Parser myParser=new Parser();


    public  sharedData()
    {
        usersList=myParser.ParseUsers();
        moviesList=myParser.ParseMovies();
        loggedIn=new ArrayList<>();

    }

    public ArrayList<Movie> getMoviesList() {
        return moviesList;
    }

    public void setMoviesList(ArrayList<Movie> moviesList) {
        this.moviesList = moviesList;
        myParser.writeMovies(this.moviesList);
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public ArrayList<Integer> getLoggedIn() {
        return loggedIn;
    }

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList = usersList;
        myParser.writeUsers(this.usersList);
    }

    public  void addLoggedIn (int connectionId)
    {
        loggedIn.add(connectionId);
    }

    public void removeLoggedIn(int connectionId)
    {
        loggedIn.remove(connectionId);
    }


    public  int getCurentUser(String userName) {
        for (int i=0; i<usersList.size(); i++) {
            if (usersList.get(i).getUsername().equals(userName)) {
                return i;
            }
        }
        return -1;
    }

    public  int getCurentMovie(String movieName) {
        for (int i=0; i<moviesList.size(); i++) {
            if (moviesList.get(i).getName().equals(movieName)) {
                return i;
            }
        }
        return -1;
    }
}
