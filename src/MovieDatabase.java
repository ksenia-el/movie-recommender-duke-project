
import java.util.*;

import org.apache.commons.csv.*;
import edu.duke.FileResource;


//this class is an efficient way to get info about movies
//it stores movie info in a HashMap for fast lookup by movie ID

//plus allows filtering movies based on several criteria 

//!!!all methods and fields are static, so no need to create new instance of this class to use its methods
//you just need to call methods like MovieDatabase.getMovie("0120915")
public class MovieDatabase {
    private static HashMap<String, Movie> ourMovies; //movie ID + movie object with all info about it

    public static void initialize(String moviefile) { //name of file used to initialize the movie database
        if (ourMovies == null) {
            ourMovies = new HashMap<String, Movie>();
            loadMovies("data/" + moviefile);
        }
    }

    private static void initialize() { //or this method instead - will load specific file if another file has not been loaded
        if (ourMovies == null) {
            ourMovies = new HashMap<String, Movie>();
            loadMovies("data/ratedmoviesfull.csv");
        }
    }

    // helper method to build a hashmap (used in constructor) 
    private static void loadMovies(String filename) {
        FirstRatings fr = new FirstRatings();
        ArrayList<Movie> list = fr.loadMovies(filename);
        for (Movie m : list) {
            ourMovies.put(m.getID(), m);
        }
    }

    public static boolean containsID(String id) {
        initialize();
        return ourMovies.containsKey(id);
    }


    //methods to return some info about specific movie 
    public static int getYear(String id) {
        initialize();
        return ourMovies.get(id).getYear();
    }

    public static String getGenres(String id) {
        initialize();
        return ourMovies.get(id).getGenres();
    }

    public static String getTitle(String id) {
        initialize();
        return ourMovies.get(id).getTitle();
    }


    public static Movie getMovie(String id) {
        initialize();
        return ourMovies.get(id);
    }


    public static String getPoster(String id) {
        initialize();
        return ourMovies.get(id).getPoster();
    }

    public static int getMinutes(String id) {
        initialize();
        return ourMovies.get(id).getMinutes();
    }

    public static String getCountry(String id) {
        initialize();
        return ourMovies.get(id).getCountry();
    }

    public static String getDirector(String id) {
        initialize();
        return ourMovies.get(id).getDirector();
    }

    //returns the number of movies in the database
    public static int size() {
        return ourMovies.size();
    }


    //returns an ArrayList of movie IDs that match this filter
    public static ArrayList<String> filterBy(Filter f) {
        initialize();
        ArrayList<String> list = new ArrayList<String>();
        for (String id : ourMovies.keySet()) {
            if (f.satisfies(id)) {
                list.add(id);
            }
        }

        return list;
    }

}
