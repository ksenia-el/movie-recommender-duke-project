
import java.util.*;


public class ForthRatings {

    public ForthRatings() {
        // default constructor
        this("ratings.csv");
    }

    public ForthRatings(String ratingsfile) {
        RaterDatabase.initialize(ratingsfile);
    }


    //method to calculate average rating for some movie from all Rater-objects 
    //it returns average ONLY if this movie has at least some number of ratings - which is parameter minimalRaters
    public double getAverageByID(String movieID, int minimalRaters) {
        int howManyRatings = 0;
        double generalRating = 0.0;

        ArrayList<Rater> allRaters = RaterDatabase.getRaters();

        for (int i = 0; i < allRaters.size(); i++) { //for every rater-object
            Rater current = allRaters.get(i);

            if (current.hasRating(movieID)) {
                howManyRatings++;
                generalRating = generalRating + current.getRating(movieID);
            }
        }

        double answer = 0.0;
        if (howManyRatings >= minimalRaters) {
            answer = generalRating / howManyRatings;
        }
        return answer;
    }


    //finds average rating for every movie (that have been rated by at least some min number of raters)
    public ArrayList<Rating> getAverageRatings(int minimalRaters) {
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter()); //means we get an array of all of movies IDs
        ArrayList<Rating> answer = new ArrayList<Rating>();

        for (int i = 0; i < movies.size(); i++) {
            String movieID = movies.get(i);
            double averageRating = getAverageByID(movieID, minimalRaters);
            if (averageRating != 0.0) { //
                Rating newRating = new Rating(movieID, averageRating);
                answer.add(newRating);
            }
        }

        return answer;
    }


    //helper method that returns an arraylist of Ratings = 1) movies that have min number of ratings 2) satisfies some filter criteria
    public ArrayList<Rating> getAverageRatingsByFilter(int minimalRaters, Filter filterCriteria) {
        ArrayList<String> filtered = MovieDatabase.filterBy(filterCriteria); //an ArrayList of movie IDs that match this filter

        ArrayList<Rating> answer = new ArrayList<Rating>();

        for (int i = 0; i < filtered.size(); i++) { //for every movie ID that firstly matched our filter
            String movieID = filtered.get(i);
            double averageRatingThisMovie = getAverageByID(movieID, minimalRaters); //count average rating
            if (averageRatingThisMovie != 0.0) { // //and check whether it fits by number of ratings - before adding to the answer
                Rating newRating = new Rating(movieID, averageRatingThisMovie);
                answer.add(newRating);
            }
        }
        return answer;
    }


    //NOT TESTED !!! (only checked by Yahor)

    //helper method to calculate dot-product - the number of closeness in movie tastes between TWO raters
    //CALCULATES SIMILARITY RATING 
    private double dotProduct(Rater me, Rater r) {
        ArrayList<String> itemsRatedByMe = me.getItemsRated(); //list of movieIDs rated by this rater
        ArrayList<String> itemsRatedByR = r.getItemsRated(); //?

        double result = 0;

        for (int i = 0; i < itemsRatedByMe.size(); i++) { //for every movie rated by Me
            String movieIDRatedByMe = itemsRatedByMe.get(i); //gets ID of this movie
            double movieRatingByMe = me.getRating(movieIDRatedByMe) - 5; //because we need to center it to fit -5 to 5 scale

            //if (itemsRatedByR.contains(movieIDRatedByMe)){  //OR:
            if (r.hasRating(movieIDRatedByMe)) {
                double movieRatingByR = r.getRating(movieIDRatedByMe) - 5;
                double movieRatingByBoth = movieRatingByMe * movieRatingByR;
                result = result + movieRatingByBoth;
            }
        }

        return result;
    }

    //returns the list of RATERS SORTED BY SIMILARITY TO ONE RATER SPECIFIED 
    //not all raters included, but only those who have positive similarity rating
    //sorted from highest to lowest
    //rating-object here = (item) ID of rater + (value) dot product/"weight"/similarity of this rater for the rater specified in parameters

    private ArrayList<Rating> getSimilarities(String id) {
        ArrayList<Rater> allRaterObjects = RaterDatabase.getRaters(); //to get all the rater-objects from database
        Rater RaterToCompareWith = RaterDatabase.getRater(id); //we will compare every rater from database with this one

        //for test only
        //System.out.println("We start the process of finding similar raters to the rater with ID " + RaterToCompareWith.getID());

        ArrayList<Rating> answer = new ArrayList<Rating>();

        for (int i = 0; i < allRaterObjects.size(); i++) {//to compare every rater-object from database - with ONE specific from parameters
            Rater currentRaterObject = allRaterObjects.get(i);
            String currentRaterID = currentRaterObject.getID();

            //for test only 
            //System.out.println("Calculating his/her similarity to the rater with ID " + currentRaterObject.getID()); 

            if (!currentRaterID.equals(id)) {//just to check that we do not compare the same rater with itself
                double dotProductOfTwo = dotProduct(RaterToCompareWith, currentRaterObject);

                //for test only
                //System.out.println("The dot product is: " + dotProductOfTwo); 

                if (dotProductOfTwo > 0) { //to include only raters with whom dot-product of similarity is a positive number, not negative
                    Rating dotRatingOfTwo = new Rating(currentRaterID, dotProductOfTwo); //in each rating here - ID of rater + dot-product of similarity between this rater and specified in parameter
                    answer.add(dotRatingOfTwo);
                }
            }
        }

        Collections.sort(answer, Collections.reverseOrder()); //to sort an arraylist from highest to lowest value

        //for test only
        //System.out.println("The whole list of similar raters: " + answer);

        return answer;
    }


    //helper method to cut the list of similar raters - to the specified-number top of raters
    private ArrayList<Rating> cutTopOfArray(ArrayList<Rating> listToCut, int howManyTopPositions) {
        ArrayList<Rating> answer = new ArrayList<Rating>();
        for (int i = 0; i < Math.min(howManyTopPositions, listToCut.size()); i++) {
            Rating current = listToCut.get(i);
            answer.add(current);
        }
        return answer;
    }


    public ArrayList<Rating> getSimilarRatingsByFilter(String id, int numSimilarRaters, int minimalRaters, Filter filterCriteria) {
        ArrayList<Rating> allSimilarRaters = getSimilarities(id);
        if (allSimilarRaters.size() < numSimilarRaters) { //in case we do not have enough numSimilarRaters to create a list of recommended movies
            //throw new RuntimeException("Not enough similar raters detected to create a recommendation");
            ArrayList<Rating> answer = new ArrayList<Rating>();
        }
        //to get only specified (in parameters) number of top-similar-raters
        ArrayList<Rating> topOfSimilarRaters = cutTopOfArray(allSimilarRaters, numSimilarRaters);


        //list of all movieIDs that fits some filter
        ArrayList<String> allMovieID = MovieDatabase.filterBy(filterCriteria); //means we get an array of all of movies IDs

        ArrayList<Rating> answer = new ArrayList<Rating>();

        //and for every movie - 1) get it's rating from every top-rater 
        //2) if we have at least minimalRaters-number of ratings for this movie from top-raters - then calculate weighted average for this movie and add the result to the answer
        for (int i = 0; i < allMovieID.size(); i++) {
            String movieID = allMovieID.get(i);
            int howManyOfTopsRated = 0; //to count how many top raters actually rated this movie
            double weightedMovieAverage = 0;
            double resultForMovie;

            for (int k = 0; k < topOfSimilarRaters.size(); k++) {

                Rating thisRaterInfo = topOfSimilarRaters.get(k); //get Rating of this rater (we can request ID of rater and his/her weight)
                String currentTopRaterID = thisRaterInfo.getItem(); //get ID of this Rater
                double weightOfThisRater = thisRaterInfo.getValue();

                Rater currentTopRater = RaterDatabase.getRater(currentTopRaterID); //from this Rater we can request raterID + info about his/her ratings

                if (currentTopRater.hasRating(movieID)) {
                    double movieRating = currentTopRater.getRating(movieID);
                    double weightedMovieRating = weightOfThisRater * movieRating;

                    weightedMovieAverage = weightedMovieAverage + weightedMovieRating;
                    howManyOfTopsRated++;
                }
            }

            if (howManyOfTopsRated >= minimalRaters) {
                resultForMovie = weightedMovieAverage / howManyOfTopsRated;
                Rating movieApproved = new Rating(movieID, resultForMovie);
                answer.add(movieApproved);
            }
        }

        Collections.sort(answer, Collections.reverseOrder()); //return the answer sorted from highest to smallest ratings
        return answer;
    }

} 

