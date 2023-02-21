
import edu.duke.*;

import java.util.*;

import org.apache.commons.csv.*; //to use CSVParser


//to process the movie and ratings data + answer questions about them
public class FirstRatings {


    //this method process every record from CSV file of movie info
    //returns an ArrayList of type Movie with all of the movie data from the file
    public ArrayList<Movie> loadMovies(String filename) {
        FileResource fr = new FileResource(filename);
        CSVParser parser = fr.getCSVParser();
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        for (CSVRecord record : parser) {
            //VERSION ONE OF HOW TO CREATE MOVIE OBJECT
            Movie currMovie = new Movie(record.get("id"), record.get("title"), record.get("year"), record.get("genre"), record.get("director"), record.get("country"), record.get("poster"), Integer.parseInt(record.get("minutes")));
            //VERSION TWO:
            //Movie currMovie = new Movie(record.get("id"), record.get("title"),record.get("year"), record.get("genre"));
            movieList.add(currMovie);
        }
        return movieList;
    }


    //GENERAL METHOD TO USE MOVIE DATA
    public void testLoadMovies() {
        //for testing on a small piece of data
        //String filename = "data/ratedmovies_short.csv";
        String filename = "data/ratedmoviesfull.csv";

        //String filename = "data/ratedmoviesfull.csv";

        //array of data for every movie
        ArrayList<Movie> answer = loadMovies(filename);
        System.out.println("\nThe process of analyzing file is completed.\nThe number of movies detected: " + answer.size());

        //for testing on a small piece of data
        //System.out.println("\nThe whole list of movies from the ArrayList:\n");
        //for (int i=0; i<answer.size(); i++){
        //Movie current = answer.get(i);
        //String info = current.toString();
        //System.out.println(info);


        //that's the code to find
        //1) number of films of specific genre
        //2) number of films with specific length
        //3) max number of films directed by one director + and who these directors are

        String genre = "Comedy";
        int howManyOfThisGenre = 0;

        int length = 150;
        int howManyOfThisLonger = 0;

        for (int i = 0; i < answer.size(); i++) {
            Movie current = answer.get(i);

            String currGenre = current.getGenres();
            if (currGenre.contains(genre)) {
                howManyOfThisGenre++;
            }

            int currLength = current.getMinutes();
            if (currLength > length) {
                howManyOfThisLonger++;
            }
        }

        System.out.println("The number of films that include " + genre + " genre is: " + howManyOfThisGenre);
        System.out.println("The number of films that are more then " + length + " minutes long: " + howManyOfThisLonger);


        HashMap<String, ArrayList<String>> directorAndFilms = directorsAndTheirFilms(answer);
        int maxFilmsForDir = dirWithMaxFilms(directorAndFilms);

        System.out.println("The max number of filmes directed by one person is: " + maxFilmsForDir +
                "\nThe whole list of such directors:\n");
        for (String director : directorAndFilms.keySet()) {
            ArrayList<String> thisDirFilms = directorAndFilms.get(director);
            if (thisDirFilms.size() == maxFilmsForDir) {
                System.out.println(director);
            }
        }
    }

    //helper-method to create a hashMap with data: director + arraylist of ids of his/her films
    private HashMap<String, ArrayList<String>> directorsAndTheirFilms(ArrayList<Movie> data) {
        HashMap<String, ArrayList<String>> allDirsAndTheirFilms = new HashMap<String, ArrayList<String>>();
        for (int i = 0; i < data.size(); i++) {
            Movie current = data.get(i); //loop over all movie objects

            //for director field in this movie
            String directorsForThisMovie = current.getDirector();
            String[] arrayOfDirectors = directorsForThisMovie.split(","); //get an array of director/directors of this movie

            //for every director/derectors of this movie
            for (int k = 0; k < arrayOfDirectors.length; k++) {
                String currDirector = arrayOfDirectors[k]; //gets the name of director
                ArrayList<String> thisDirFilms; //and this will be an arrayList of his/her films

                //if this director is new for our hashmap
                if (!allDirsAndTheirFilms.containsKey(currDirector)) {
                    thisDirFilms = new ArrayList<String>(); //we create a new list for him/her
                } else {
                    thisDirFilms = allDirsAndTheirFilms.get(currDirector); //or we get already existing list of her/his films
                }
                thisDirFilms.add(current.getID()); //we update the list of films of this director
                allDirsAndTheirFilms.put(currDirector, thisDirFilms); //we update the list of some dir films

            }

        }
        //now we have a hash map with every director - and ids of his/her films
        return allDirsAndTheirFilms;
    }

    //helper method to calculate the max number of filmes for one director
    private int dirWithMaxFilms(HashMap<String, ArrayList<String>> data) {
        //calculating the max number of films that one person directed
        int maxMoviesbyDirector = 0;
        for (String director : data.keySet()) {
            ArrayList<String> thisDirFilms = data.get(director);
            int thisDirHowMany = thisDirFilms.size();
            if (thisDirHowMany > maxMoviesbyDirector) {
                maxMoviesbyDirector = thisDirHowMany;
            }
        }
        return maxMoviesbyDirector;
    }


    //GENERAL METHOD THAT USES RATERS DATA
    //proccess every record from CSV file - file of raters & their ratings
    //returns an arrayList of type Rater - and their ratings
    public ArrayList<Rater> loadRaters(String filename) {
        FileResource fr = new FileResource(filename);
        CSVParser parser = fr.getCSVParser();

        ArrayList<Rater> answer = new ArrayList<Rater>();

        for (CSVRecord record : parser) {
            String currID = record.get("rater_id");
            Rater currRater = findOrCreateRater(answer, currID);
            currRater.addRating(record.get("movie_id"), Double.parseDouble(record.get("rating")));

        }
        return answer;
    }

    //helper method to the method above
    private Rater findOrCreateRater(ArrayList<Rater> allKnownRaters, String id) {
        for (Rater currRater : allKnownRaters) {
            String currID = currRater.getID();

            //if this rater already exists in the arrayList
            if (currID.equals(id)) {
                return currRater;
            }
        }
        Rater newRater = new EfficientRater(id);
        allKnownRaters.add(newRater);  //adding new rater to the whole list of them
        return newRater;
    }


    //reads CSV file with rater-ratings data - to form an arraylist of raters-its ratings
    //prints out the total number of raters
    //for each rater - 1) its ID and the number of ratings (prints this on one line)
    //2) every movie id rated by this rater + the rating for this film
    public void testLoadRaters() {
        String filename = "data/ratings.csv";
        ArrayList<Rater> result = loadRaters(filename);

        //prints out the total number of raters detected after parcing a file with all Raters data
        System.out.println("*** The total number of raters detected: " + result.size());

        //prints out every movie+rating created by each rater FOR TESTING
        //printMovieAndRatingFromEveryRater(result);

        //prints out how many ratings were created by specific rater
        //String idOfRater = "2";
        String idOfRater = "193";
        int howManyRatingsWereCreatedBY = numberOfRatingsBySomeRater(result, idOfRater);
        System.out.println("The number of ratings created by rater with ID " + idOfRater + " is: " + howManyRatingsWereCreatedBY);

        //calculates max number of ratings created by one rater
        //+ prints out max number + how many raters have so many ratings + IDs of such raters
        maxNumRatings(result);

        //calculates the number of ratings that a specific movie has
        String movieID = "1798709";
        int howMany = howManyRatingsMovieHas(result, movieID);
        System.out.println("The number of ratings that movie with ID " + movieID + " has: " + howMany);

        //how many different movies have been rated by all raters
        int howManyGen = howManyMoviesRatedInGeneral(result);
        System.out.println("How many different movies were rated by all the raters at all: " + howManyGen + "***");

    }


    //helper-method uses arraylist of rater-objects to analize every rater
    //and prints out every movie+rating created by each rater
    private void printMovieAndRatingFromEveryRater(ArrayList<Rater> allRaterData) {
        for (int i = 0; i < allRaterData.size(); i++) { //for every rater-object
            Rater current = allRaterData.get(i);

            String IDofRater = current.getID(); //gets ID of this rater
            int numberOfRatings = current.numRatings(); //gets total number of ratings from this rater
            System.out.println("\nThe rater with ID " + IDofRater + " has " + numberOfRatings + " ratings. The whole list of them:\n");

            //*prints out every rating from this rater (id of movie+rating from 1 to 10)
            ArrayList<String> IDsOFRatedMovies = current.getItemsRated();
            for (int k = 0; k < IDsOFRatedMovies.size(); k++) {
                String IDofMovie = IDsOFRatedMovies.get(k);
                double ratingForThisMovie = current.getRating(IDofMovie);
                System.out.println("ID of movie: " + IDofMovie + " Rating: " + ratingForThisMovie);
            }

        }
    }

    //helper method to find the number of ratings by specific rater
    private int numberOfRatingsBySomeRater(ArrayList<Rater> allRaterData, String idOfRater) {
        for (int i = 0; i < allRaterData.size(); i++) {
            Rater current = allRaterData.get(i);
            String idOfCurrent = current.getID();

            if (idOfCurrent.equals(idOfRater)) {
                int numMoviesRated = current.numRatings();
                return numMoviesRated;
            }
        }
        return -1; //in case there is no rater with such ID
    }

    //helper method to find the max number of ratings by one rater - in the whole Arraylist of raters
    //plus find how many raters have so many ratings
    //and print out their IDs
    private void maxNumRatings(ArrayList<Rater> allRaterData) {
        int maxNumOfRatings = 0;
        for (int i = 0; i < allRaterData.size(); i++) {
            Rater current = allRaterData.get(i);
            int currNumRatings = current.numRatings();
            if (currNumRatings > maxNumOfRatings) {
                maxNumOfRatings = currNumRatings;
            }
        }
        //now we have the number of max ratings

        int numberOfSuchRaters = 0;
        ArrayList<String> idsOfTheseRaters = new ArrayList<String>();

        for (int k = 0; k < allRaterData.size(); k++) {
            Rater current = allRaterData.get(k);
            int currNumRatings = current.numRatings();
            if (currNumRatings == maxNumOfRatings) {
                numberOfSuchRaters++;
                idsOfTheseRaters.add(current.getID());
            }
        }
        //now we have the number and arraylist of raters with this score

        System.out.println("\nThe maximum number of ratings created by one rater is: " + maxNumOfRatings +
                "\nThe number of such raters (with this max number of ratings): " + numberOfSuchRaters + "\nThe whole list of these raters (their IDs):");

        //now we print out ID of every rater with this record
        for (int o = 0; o < idsOfTheseRaters.size(); o++) {
            System.out.println(idsOfTheseRaters.get(o));
        }
    }


    //calculates the number of ratings that a specific movie has
    private int howManyRatingsMovieHas(ArrayList<Rater> allRaterData, String movieID) {
        int numRatingsForThisMovie = 0;
        for (int k = 0; k < allRaterData.size(); k++) {
            Rater current = allRaterData.get(k);
            if (current.hasRating(movieID)) {
                numRatingsForThisMovie++;
            }
        }
        return numRatingsForThisMovie;
    }


    //how many different movies have been rated by all raters (based on rater data)
    private int howManyMoviesRatedInGeneral(ArrayList<Rater> allRaterData) {
        ArrayList<String> moviesRated = new ArrayList<String>();
        for (int k = 0; k < allRaterData.size(); k++) {
            Rater current = allRaterData.get(k);
            ArrayList<String> moviesRatedByThis = current.getItemsRated();
            for (int i = 0; i < moviesRatedByThis.size(); i++) {//looping over ID of films rated by ONE rater
                String idOfMovie = moviesRatedByThis.get(i);
                if (!moviesRated.contains(idOfMovie)) {
                    moviesRated.add(idOfMovie);
                }
            }
            //calculating of movies IDs from one rater completed
        }
        //calculating all movies completed
        return moviesRated.size();
    }

}

