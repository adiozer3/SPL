package bgu.spl181.net.impl.myProtocol;

import bgu.spl181.net.api.MessagingProtocol;
import bgu.spl181.net.api.bidi.BidiMessagingProtocol;
import bgu.spl181.net.api.bidi.Connections;
import bgu.spl181.net.impl.SharedData.sharedData;

import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class USTBPprotocol implements BidiMessagingProtocol<String> {

    protected sharedData SharedData;

    protected boolean shouldTerminate = false;
    protected String userName;
    protected String password;
    protected String info;
    protected String requestName;
    protected String requestParam;
    protected Connections connections;
    protected int connectionId;
    protected boolean isLoggedin=false;
    // protected boolean isAdmin=false;             //check if we need this field?

    @Override
    public void start(int connectionId, Connections<String> connections) {
        this.connections=connections;
        this.connectionId=connectionId;
        //TODO
    }


    public void setData(sharedData sd)
    {
        this.SharedData=sd;
    }


    public  void register ( String [] splitMsg)
    {
        boolean isFound=false;
        boolean passed=true;
        ArrayList<User> usersList=Parser.ParseUsers();
        if(isLoggedin)// error case 1
            passed = false;
        //error cse 2
        else if (passed)
        {
            for (int i=0;i<usersList.size();i++)
            {
                if(splitMsg[1].equals(usersList.get(i).getUsername())){
                    isFound=true;
                    break;
                }
            }
            if (isFound)
                passed=false;
        }
        //error case 3+4
        else if(passed) {
            if (splitMsg.length <= 2)
                passed = false;
            else if (splitMsg.length == 4)
                if (!splitMsg[3].contains("country="))
                    passed = false;
                else if (splitMsg.length == 3)
                    if (splitMsg[1].contains("country=") || splitMsg[2].contains("country="))
                        passed = false;
        }

        if (passed) {
            connections.send(connectionId,"ACK registration succeeded");
            userName = splitMsg[1];
            password = splitMsg[2];
            info = splitMsg[3].substring(9, splitMsg[3].length() - 1);
            User newUser = new User(userName,password,info);
            usersList.add(newUser);
            Parser.writeUsers(usersList);
        } else
            connections.send(connectionId,"ERROR registration failed");

    }




    public  void signout ()
    {
        if (isLoggedin) {
            connections.send(connectionId, "ACK signout succeeded");
            isLoggedin=false;
        }
        else
        connections.send(connectionId,"ERROR signout failed");


    }



    public  void login (String [] splitMsg)
    {
        boolean isFound=false;
        boolean passed=true;
        ArrayList<User> usersList=Parser.ParseUsers();
        //TODO error case 1- performed Successfull login??
        if(isLoggedin)
            passed=false;
        if(passed) {
            //error case 3- search in json for username and password
            for (int i = 0; i < usersList.size(); i++) {           //checks if the user name and password matches as appears in json
                if (splitMsg[1].equals(usersList.get(i).getUsername())) {
                    if (splitMsg[2].equals(usersList.get(i).getPassword())) {
                        isFound = true;
                        break;
                    }
                }
            }
            if (isFound)
                passed = false;
            if (splitMsg.length < 3)// checking input is legal
                passed = false;

        }
        if(passed){
            connections.send(connectionId,"ACK login succeeded");
            userName = splitMsg[1];
            password = splitMsg[2];
            isLoggedin=true;
        }
        else
            connections.send(connectionId,"ERROR login failed");

    }

    @Override
    public void process(String msg) {


        String [] splitMsg = msg.split(" ");
        shouldTerminate="SIGNOUT".equals(msg);
        if(shouldTerminate)
            signout();
        else if (splitMsg[0].equals("REGISTER"))
            register(splitMsg);
        else if (splitMsg[0].equals("LOGIN"))
            login(splitMsg);
        else if (splitMsg[0].equals("REQUEST")){
            handleRequest(msg);
        }

    }

    public abstract void handleRequest(String msg);

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
