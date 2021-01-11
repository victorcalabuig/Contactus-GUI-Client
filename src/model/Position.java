package model;

public class Position {

    public String userId;
    public int latitude;
    public int longitude;

    public Position(String userId, int latitude, int longitude){
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isOutOfGrid(int size){
        return latitude >= size || longitude >= size;
    }



}
