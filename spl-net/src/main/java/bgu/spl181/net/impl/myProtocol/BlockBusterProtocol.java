package bgu.spl181.net.impl.myProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockBusterProtocol extends USTBPprotocol {


    private String id;
    private String name;
    private int price;
    private List<String> bannedCountries;
    private String availableAmount;
    private String totalAmount;


    public void handleRequest(String msg){
        String [] splitMsg = msg.split(" ");
            if (isLoggedin) {
                requestName = splitMsg[1];
                if (requestName.equals("balance info"))
                    balanceInfo();
                else if(requestName.equals("balance add")) {
                    requestParam = splitMsg[2];
                    addBalance(requestParam);

                }
                else if(requestName.equals("info")) {
                    if (splitMsg.length<3)
                        requestParam = "";
                    else
                        requestParam = splitMsg[2];
                    movieInfo(requestParam);
                }
                else if (requestName.equals("rent")){
                    requestParam = splitMsg[2];
                    rentAMovie(requestParam);
                }
                else if (requestName.equals("return")) {
                    requestParam = splitMsg[2];
                    returnMovie(requestParam);
                } else if (requestName.equals("addmovie")) {
                    if (splitMsg.length == 5)
                        addMovie(splitMsg[2], Integer.parseInt(splitMsg[3]), Integer.parseInt(splitMsg[4]), new String[0]);
                    else
                        addMovie(splitMsg[2], Integer.parseInt(splitMsg[3]), Integer.parseInt(splitMsg[4]), Arrays.copyOfRange(splitMsg, 5, splitMsg.length));
                }
                else if(requestName.equals("remmovie")){
                    requestParam = splitMsg[2];
                    remmovie(requestParam);
                }
                else if(requestName.equals("changeprice")){
                    changeprice(splitMsg[2], Integer.parseInt(splitMsg[3]));
                }

            }
        }



    public void balanceInfo ()
    {
        ArrayList<User> usersList= SharedData.getUsersList();
        int i=0;
        boolean isFound=false;
        while (!isFound && i<usersList.size()) {
            if(usersList.get(i).getUsername().equals(userName)) {
                isFound = true;
            }
            i++;
        }
        if(isFound)
            connections.send(connectionId,"ACK balance " +usersList.get(i).getBalance());
        else
            connections.send(connectionId,"ERROR request balanceinfo failed");


    }


    public  void movieInfo (String movie)
    {
        String msg="";
        ArrayList<Movie> moviesList=SharedData.getMoviesList();

        if (movie.equals("")) {
            for (int j=0;j<moviesList.size();j++)
                msg=msg+" "+moviesList.get(j).getName();
            connections.send(connectionId,"ACK info " +msg);
        }
        else
        {
            int i=0;
            boolean isFound=false;
            while (!isFound && i<moviesList.size()) {
                if(moviesList.get(i).getName().equals(movie)) {
                    isFound = true;
                }
                i++;
            }
            if (isFound)
                connections.send(connectionId,"ACK info " +moviesList.get(i-1).getName()+" "+moviesList.get(i).getAvailableAmount()+" "+moviesList.get(i).getPrice()+" "+moviesList.get(i).getBannedCountries());
            else
                connections.send(connectionId,"ERROR request info failed");
        }
    }

    public void rentAMovie (String movie){
        ArrayList<Movie> moviesList=SharedData.getMoviesList();
        ArrayList<User> usersList=SharedData.getUsersList();

        boolean failed= false;
        boolean movieExist=false;

        for (int i=0; i<moviesList.size(); i++)                 //checks uf the movie exists in the system
        {
            if (moviesList.get(i).getName()==movie) {
                movieExist = true;
                break;
            }
        }
        if(!movieExist)                                         //if the movie doesnt exist the request failed
            failed=true;



        if(!failed) {                                   //checks if user has enough money in his balance
            int moviePrice=0;
            int userBalance=0;

            for (int i = 0; i < moviesList.size(); i++) {
                if (moviesList.get(i).getName() == movie) {
                    moviePrice = moviesList.get(i).getPrice();
                    break;
                }
            }
            for (int i = 0; i < usersList.size(); i++) {
                if (usersList.get(i).getUsername() == userName) {
                    userBalance = usersList.get(i).getBalance();
                    break;
                }
            }
            if (userBalance < moviePrice)           //if he doesn't have enough balance the request failed
                failed = true;
        }


        if (!failed)                                //checks if there are available copies of the movie for rent
        {
            for (int i = 0; i < moviesList.size(); i++) {
                if (moviesList.get(i).getName() == movie) {
                    if (moviesList.get(i).getAvailableAmount()==0);
                        failed=true;
                }
            }
        }


        if (!failed)                                        //reminder: users info means his country!
        {
            boolean movieIsBanned=false;
            for(int i=0;i<moviesList.size(); i++)           //runs on the banned list of the movie and checks if ths users country is in it
            {
                if (moviesList.get(i).getName()==movie){
                    for(int j=0; j<moviesList.get(i).getBannedCountries().size(); j++)
                    {
                        if(moviesList.get(i).getBannedCountries().get(j)==info) {
                            movieIsBanned = true;
                            break;
                        }
                    }
                }
            }
            if(movieIsBanned)                   //if movie is banned then request failed
                failed=true;
        }


        if (!failed)                                    //checks if the user already rented the movie
        {
            boolean alreadyRented=false;
            for (int i=0;i<usersList.size();i++)
            {
                if(usersList.get(i).getUsername()==userName){
                    for(int j=0; j<usersList.get(i).getMovies().size();j++)
                    {
                        if(usersList.get(i).getMovies().get(j).getName()==movie)
                        {
                            alreadyRented=true;
                            break;
                        }
                    }
                }
            }
            if (alreadyRented)
                failed=false;
        }


        if(failed)                                  //if the request failed we send an ERROR message to the client
            connections.send(connectionId,"ERROR rent failed");
        else
        {
            int userindex= SharedData.getCurentUser(userName);
            int movieindex=SharedData.getCurentMovie(movie);

            UserMovie newMovieToUser= new UserMovie();
            newMovieToUser.setId(moviesList.get(movieindex).getId());
            newMovieToUser.setName(moviesList.get(movieindex).getName());
            usersList.get(userindex).getMovies().add(newMovieToUser);       //we create new USERMOVIE object to insert into the users movie list

            int newBlance=usersList.get(userindex).getBalance()-moviesList.get(movieindex).getPrice();
            usersList.get(userindex).setBalance(newBlance);                 //we reduce the price of the movie from the users balance

            int newAmount= moviesList.get(movieindex).getAvailableAmount()-1;       //we reduce the movie available amount field by 1
            moviesList.get(movieindex).setAvailableAmount(newAmount);

            connections.send(connectionId,"ACK rent "+movie+" success");

            SharedData.setMoviesList(moviesList); //at the end we update the json file with the new lists
            SharedData.setUsersList(usersList);
        }
    }






    public void remmovie (String movie)
    {
        ArrayList<Movie> moviesList=SharedData.getMoviesList();
        boolean failed=false;

        boolean isAdmin=false;
        //TODO check if user is admin and change the boolean
        if (!isAdmin)
            failed=true;


        if(!failed)                     //checks if the movie is not exist
        {
            int movieIndex=Parser.getCurentMovie(movie,moviesList);
            if (movieIndex==-1)
                failed=true;
        }

        if (!failed)                //checks if theres a user who is renting this movie
        {
            int movieIndex=Parser.getCurentMovie(movie,moviesList);
            if (moviesList.get(movieIndex).getAvailableAmount()!=moviesList.get(movieIndex).getTotalAmount())
                failed=true;
        }


        if (failed)
            connections.send(connectionId,"ERROR remmovie failed");
        else
        {
            int movieIndex=Parser.getCurentMovie(movie,moviesList);
            moviesList.remove(movieIndex);                              //we remove the movie from the movies list

            SharedData.setMoviesList(moviesList);                //we update the json;
            connections.send(connectionId,"ACK remmovie "+movie+" success");

        }

    }

    public void changeprice(String movie, int price){
        ArrayList<Movie> moviesList=SharedData.getMoviesList();
        boolean failed=false;

        boolean isAdmin=false;
        //TODO check if user is admin and change the boolean
        if (!isAdmin)
            failed=true;

        if(!failed)                     //checks if the movie is not exist
        {
            int movieIndex=Parser.getCurentMovie(movie,moviesList);
            if (movieIndex==-1)
                failed=true;
        }

        if(price<=0)
            failed=true;

        if (failed)
            connections.send(connectionId,"ERROR changeprice failed");
        else
        {
            int movieIndex=Parser.getCurentMovie(movie,moviesList);
            moviesList.get(movieIndex).setPrice(price);             //changes the price of the movie
            SharedData.setMoviesList(moviesList);          //update json
            connections.send(connectionId,"ACK changeprice "+movie+" success");
        }
    }



    public void addBalance(String amount) {
        int am = Integer.parseInt(amount);
        ArrayList<User> usersList = SharedData.getUsersList();
        int i = 0;
        boolean isFound = false;
        while (!isFound && i < usersList.size()) {
            if (usersList.get(i).getUsername().equals(userName)) {
                isFound = true;
            }
            i++;
        }
        if (isFound) {
            usersList.get(i - 1).setBalance(usersList.get(i - 1).getBalance() + am);
            SharedData.setUsersList(usersList);
            connections.send(connectionId, "ACK balance " + usersList.get(i - 1).getBalance());
        } else
            connections.send(connectionId, "ERROR request balance info failed " + usersList.get(i - 1).getBalance());
    }

    public void returnMovie(String movie) {
        ArrayList<Movie> moviesList = SharedData.getMoviesList();
        ArrayList<User> usersList = SharedData.getUsersList();
        int currentUserIndex = 0;
        int usersMovieIndex = 0;
        boolean isFoundMovie = false;
        boolean isRented = false;
        int i = 0;
        //Checks if movie is exists
        while (!isFoundMovie && i < moviesList.size()) {
            if (moviesList.get(i).getName().equals(movie)) {
                isFoundMovie = true;
                break;
            }
            i++;
        }
        //checks if movie is rented
        if (isFoundMovie) {
            currentUserIndex = Parser.getCurentUser(userName, usersList);
            if (currentUserIndex != -1) {
                while (isRented && usersMovieIndex < usersList.get(currentUserIndex).getMovies().size()) {
                    if (usersList.get(i).getMovies().get(usersMovieIndex).equals(movie)) {
                        isRented = true;
                        break;
                    }
                    usersMovieIndex++;
                }
            }
        }
        if (!isFoundMovie | !isRented)
            connections.send(connectionId, "ERROR request return failure");
        else {
            //Changes the number of rented movies
            moviesList.get(i).setAvailableAmount(moviesList.get(i).getAvailableAmount() - 1);
            //removes movie from user's movie list
            usersList.get(currentUserIndex).getMovies().remove(usersMovieIndex);
            SharedData.setUsersList(usersList);
            SharedData.setMoviesList(moviesList);
            connections.send(connectionId, "ACK return " + movie+" success");
            connections.broadcast("BROADCAST movie " + movie + " " + moviesList.get(i).getAvailableAmount() + " " + moviesList.get(i).getPrice());
        }
    }


    public void addMovie(String movie, int amount, int price, String[] countries) {
        ArrayList<Movie> moviesList = SharedData.getMoviesList();
        boolean isFailed = false;
        boolean movieExists = false;
        //TODO check if administrator
        isFailed = true;
        //Check if movie is already exists in the system
        if (!isFailed) {
            for (int i = 0; i < moviesList.size(); i++) {
                if (moviesList.get(i).getName().equals(movie)) {
                    movieExists = true;
                    break;
                }
            }
            if (movieExists)
                isFailed = true;
        }
        //Check if amount or price are lower than 0
        if (!isFailed)
            if (amount <= 0 | price <= 0)
                isFailed = true;
        if (isFailed)
            connections.send(connectionId, "ERROR request addmovie failure");
        else {
            int maxID = 0;
            for (int i = 0; i < moviesList.size(); i++)
                if (moviesList.get(i).getId() > maxID)
                    maxID = moviesList.get(i).getId();
            Movie newMovie = new Movie(maxID,name,price,amount,countries);
            moviesList.add(newMovie);
            SharedData.setMoviesList(moviesList);


            connections.send(connectionId, "ACK addmovie " + movie+ " success");
            connections.broadcast("BROADCAST movie " + movie + " " + amount + " " +price);
        }


    }

}


