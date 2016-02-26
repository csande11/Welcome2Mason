package gmu.cs477.finalproject;

/**
 * Created by Corbin on 12/6/2015.
 */

//Building class for the holding of information from the incoming JSON object
//This class is used for building, dining, and housing info
public class Building {
    String name;
    String description;
    String latitude;
    String longitude;

    public Building(String name, String description, String latitude, String longitude){
        this.name=name;
        this.description=description;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
}
