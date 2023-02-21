
import edu.duke.*;

import java.util.*;

import org.apache.commons.csv.*;

//this class is an efficient way to get info about raters  
public class RaterDatabase {
    private static HashMap<String, Rater> ourRaters; //maps Rater ID + Rater object (which includes ID of rater + all his/her movie ratings)

    //this method creates hashmap outRaters if it does not exist
    private static void initialize() {
        // this method is only called from addRatings
        if (ourRaters == null) {
            ourRaters = new HashMap<String, Rater>();
        }
    }

    //this method initialize database from a file
    public static void initialize(String filename) {
        if (ourRaters == null) {
            ourRaters = new HashMap<String, Rater>();
            addRatings("data/" + filename);
        }
    }


    //(HELPER METHODS USED IN INITIALIZE METHOD ABOVE)
    //helper method to the method above to add rater ratings to the database from a file
    public static void addRatings(String filename) {
        initialize();
        FileResource fr = new FileResource(filename);
        CSVParser csvp = fr.getCSVParser();
        for (CSVRecord rec : csvp) {
            String id = rec.get("rater_id");
            String item = rec.get("movie_id");
            String rating = rec.get("rating");
            addRaterRating(id, item, Double.parseDouble(rating));
        }
    }

    //helper method to addRatings method above
    //to add 1 rater and his/her rating of specific movie to the database
    public static void addRaterRating(String raterID, String movieID, double rating) {
        initialize();
        Rater rater = null;
        if (ourRaters.containsKey(raterID)) {
            rater = ourRaters.get(raterID);
        } else {
            rater = new EfficientRater(raterID);
            ourRaters.put(raterID, rater);
        }
        rater.addRating(movieID, rating);
    }

    public static boolean containsID(String id) {
        initialize();
        return ourRaters.containsKey(id);
    }

    //returns a Rater-object by ID of this rater
    public static Rater getRater(String id) {
        initialize();

        return ourRaters.get(id);
    }

    //returns arrayList of all Rater-objects from database 
    public static ArrayList<Rater> getRaters() {
        initialize();
        ArrayList<Rater> list = new ArrayList<Rater>(ourRaters.values());

        return list;
    }

    //returns the number of raters in database
    public static int size() {
        return ourRaters.size();
    }


}
