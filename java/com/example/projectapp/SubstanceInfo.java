package com.example.projectapp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SubstanceInfo implements Serializable {

    private String name;
    private String percent;
    private int consumed;

    public SubstanceInfo( String name, String percent, int consumed){
        this.name = name;
        this.percent = percent;
        this. consumed = consumed;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPercent(String percent){
        this.percent = percent;
    }

    public String getPercent() {
        return percent;
    }

    public void setid(int consumed){ this.consumed = consumed;}

    public int getConsumed() { return consumed; }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("percent", percent);
        result.put("consumed", consumed);

        return result;
    }

}
