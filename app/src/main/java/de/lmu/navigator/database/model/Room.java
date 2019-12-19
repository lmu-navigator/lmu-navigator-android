package de.lmu.navigator.database.model;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Room extends RealmObject implements Synonymable<Room>{

    @Required
    @PrimaryKey
    private String code;

    private Floor floor;

    @Ignore
    private String floorCode;

    @Required
    private String name;

    private int posX;

    private int posY;

    private RealmList<RoomSynonym> synonyms = new RealmList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public String getFloorCode() {
        return floorCode;
    }

    public void setFloorCode(String floorCode) {
        this.floorCode = floorCode;
    }

    @Override
    public List<RoomSynonym> getSynonyms() {
        return synonyms;
    }

    @Override
    public boolean hasSynonyms() {
        return synonyms.size() > 0;
    }

    public void setSynonyms(RealmList<RoomSynonym> synonyms) {
        this.synonyms = synonyms;
    }
}
