package com.statletics.bodyweightconnect.util;

/**
 * Created by Tonni on 06.08.2016.
 */
public class ExterciseTranslator {

    private static String PREFIX = "metronom_";
    private static String SUFFIX = "_time_";


    public static String translateToKey(String exercise, String page){

        String exerciseName = "";
        exerciseName = exercise.toUpperCase().replaceAll(" ","_");
        return PREFIX+exerciseName+SUFFIX;

    }


}
