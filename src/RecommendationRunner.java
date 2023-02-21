
import java.util.*;

public class RecommendationRunner implements Recommender {

    private int howManyMoviesToVote = 15;

    //returns an arraylist of movieIDs to present user for voting, the list was formed by:
    //creating a list of all genres of movies mentioned in movieDatabase
    //then calculating each genre "weight" coefficient in total genre variety
    //then calculating how many movies of each genre should be presented in voting list
    //taking average rating of the most popular movies of each genre, get 15 top of them (or less, if there is less than 15 movies of this genre exist in the moviedatabase
    //mix this top-15(or less) movies for each genre - and get from it the first num-needed for this genre movies in the answer-list
    public ArrayList<String> getItemsToRate() {
        String moviefile = "ratedmoviesfull.csv";
        MovieDatabase.initialize(moviefile);
        String ratingsfile = "ratings.csv";
        ForthRatings fr = new ForthRatings(ratingsfile);
        Random myRandom = new Random();

        ArrayList<String> answer = new ArrayList<String>();
        ArrayList<Rating> numMoviesToGetByGenre = howManyMoviesOfGenreNeeded();

        int howManyAdded = 0;
        while (answer.size() < howManyMoviesToVote) {
            for (Rating currRating : numMoviesToGetByGenre) {
                String currGenre = currRating.getItem();
                double numMoviesToGet = currRating.getValue();

                GenreFilter genrFilt = new GenreFilter(currGenre);
                ArrayList<String> topMoviesForThisGenre = MovieDatabase.filterBy(genrFilt);
                ArrayList<String> cutTopMoviesForThisGenre = cutOnlyTop(topMoviesForThisGenre, howManyMoviesToVote); //now we have a list of top-15 movies for this genre (rates made by average only)
                Collections.shuffle(cutTopMoviesForThisGenre); ///write it down!

                for (int i = 0; i < numMoviesToGet; i++) { //we are going to randomly choose needed number of this genre movies - to the answer list
                    String movieIDToAdd = cutTopMoviesForThisGenre.get(i);

                    //just to check that this movie is not already in the answer list
                    if (!answer.contains(movieIDToAdd)) {
                        answer.add(movieIDToAdd);
                        if (answer.size() == howManyMoviesToVote) {
                            return answer;
                        }
                    }
                }
            }
        }
        return answer;
    }


    //helper method to get the list of all genres mentioned in movieDatabase
    private ArrayList<String> getAllGenresList() {
        ArrayList<String> allGenres = new ArrayList<String>();

        ArrayList<String> allMovieID = MovieDatabase.filterBy(new TrueFilter()); //means we get an array of all movies IDs
        for (String currentMovieID : allMovieID) { //for every movie in out base
            String[] thisMovieGenres = MovieDatabase.getGenres(currentMovieID).split(", ");

            for (int i = 0; i < thisMovieGenres.length; i++) {
                String oneOfGenres = thisMovieGenres[i];
                if (!allGenres.contains(oneOfGenres)) {
                    allGenres.add(oneOfGenres);
                }
            }
        }

        return allGenres;
    }


    //helper method to calculate frequency of every genre in the whole movieDatabase
    //returns a list of Rating-objects (ID is genre, value - the frequency of genre (how many times it was found))
    //genres in returned answer are sorted from most popular to less popular
    private ArrayList<Rating> getRatingOfGenres() {

        ArrayList<Rating> answer = new ArrayList<Rating>();
        ArrayList<String> allGenres = getAllGenresList();

        for (String genre : allGenres) {
            GenreFilter thisGenFilt = new GenreFilter(genre);
            //getting the list of all movieIDs of this genre
            ArrayList<String> allThisGenreMovies = MovieDatabase.filterBy(thisGenFilt);
            double thisGenreFreq = allThisGenreMovies.size();
            Rating thisGenreRating = new Rating(genre, thisGenreFreq);
            answer.add(thisGenreRating);
        }

        Collections.sort(answer, Collections.reverseOrder());
        return answer;
    }


    //helper method to calculate total sum of genre frequencies (to use in the method below)
    private double sumOfAllFrequences() {
        ArrayList<Rating> allGenresFreqs = getRatingOfGenres();
        double answer = 0;
        for (Rating currRating : allGenresFreqs) {
            double currGenreFreq = currRating.getValue();
            answer = answer + currGenreFreq;
        }
        return answer;
    }


    //calculates how many movies of each genre need to be be added to the voting list later
    //every rating: string genre + int of how many movies of this genre needed to be add to voting list
    private ArrayList<Rating> howManyMoviesOfGenreNeeded() {

        ArrayList<Rating> freqOfAllGenres = getRatingOfGenres();
        double totalFreq = sumOfAllFrequences();
        ArrayList<Rating> answer = new ArrayList<Rating>();

        for (Rating currGenreRating : freqOfAllGenres) {
            String genre = currGenreRating.getItem();
            double currGenreFreq = currGenreRating.getValue();
            double genreCoef = totalFreq / currGenreFreq;
            int howManyOfThisGenreNeeded = (int) Math.round(genreCoef * howManyMoviesToVote);
            Rating genreCoefRating = new Rating(genre, howManyOfThisGenreNeeded);
            answer.add(genreCoefRating);
        }

        return answer;
    }


    //helper method to cut some number top positions from a list (used in one of two main methods - getItemsToRate) 
    private ArrayList<String> cutOnlyTop(ArrayList<String> whatToCut, int howMany) {
        if (howMany > whatToCut.size()) {
            return whatToCut;
        }

        ArrayList<String> answer = new ArrayList<String>();
        for (int i = 0; i < howMany; i++) {
            String getIt = whatToCut.get(i);
            answer.add(getIt);
        }
        return answer;
    }


    //just to test everything above
    public void testGetItemsToRate() {
        ArrayList<String> answer = getItemsToRate();

        System.out.println(answer);

        for (String movieID : answer) {
            String currTitle = MovieDatabase.getTitle(movieID);
            int currYear = MovieDatabase.getYear(movieID);
            String currGenres = MovieDatabase.getGenres(movieID);
            int currLength = MovieDatabase.getMinutes(movieID);

            System.out.println(currTitle + " " + currYear + " " + currGenres + " " + currLength + " minutes long");
        }

    }


    //this method prints out an HTML table of 5 movie tops recommended by the program to the specific user (with specified ID)
    //these recommendations doesn't include movies that are already seen by the user
    public void printRecommendationsFor(String webRaterID) {
        String moviefile = "ratedmoviesfull.csv";
        MovieDatabase.initialize(moviefile);
        String ratingsfile = "ratings.csv";
        ForthRatings fr = new ForthRatings(ratingsfile);


        //in case the user didn't rate anything 
        if (!baseContainsRater(webRaterID) || allRatesAreZero(webRaterID)) {
            System.out.println("<html><head><style>body{font-family:Futura}h1{color:#ae6b97}p{color:#522d45;}</style></head><body><h1>Unable to create personal recommendations</h1><p>Looks like no movies were rated by you. Please, try to do it again</p></body></html>");
            return;
        }


        Rater thisUserRater = RaterDatabase.getRater(webRaterID); //then we can call Rater.hasRating(movieID) to check whether this movie was already seen by user
        ArrayList<String> moviesRatedByUser = thisUserRater.getItemsRated();


        int topSimRatersNeeded = 30;
        int minimalRaters = 2; //the min number of recommendations from similar raters needed for some movie to be selected for recommendations


        //choosing top 5 dramas ever
        String genre = "Drama";
        GenreFilter genrFilt = new GenreFilter(genre);
        ArrayList<Rating> result1 = fr.getSimilarRatingsByFilter(webRaterID, topSimRatersNeeded, minimalRaters, genrFilt);
        result1 = cutSeenAndGetFiveBest(result1, webRaterID);
        String titleOfTop1 = "TOP 5 DRAMAS EVER";


        //5 best adventure films of the last 20 years
        String genre2 = "Adventure";
        int year = 1972;
        GenreFilter genrFilt2 = new GenreFilter(genre2);
        YearAfterFilter yearFilt = new YearAfterFilter(year);
        AllFilters allFilt = new AllFilters();
        allFilt.addFilter(genrFilt2);
        allFilt.addFilter(yearFilt);
        ArrayList<Rating> result2 = fr.getSimilarRatingsByFilter(webRaterID, topSimRatersNeeded, minimalRaters, allFilt);
        result2 = cutSeenAndGetFiveBest(result2, webRaterID);
        String titleOfTop2 = "TOP 5 ADVENTURE MOVIES OF THE LAST 50 YEARS";


        //5 best short movies (80-95 min long) of all time
        int minMinutes = 1;
        int maxMinutes = 120;
        MinutesFilter minFilt = new MinutesFilter(minMinutes, maxMinutes);
        ArrayList<Rating> result3 = fr.getSimilarRatingsByFilter(webRaterID, topSimRatersNeeded, minimalRaters, minFilt);
        result3 = cutSeenAndGetFiveBest(result3, webRaterID);
        String titleOfTop3 = "TOP 5 FILMS UP TO 120 MINUTES LONG OF ALL TIME";


        //5 best European movies of the last 10 years
        EUCountryFilter EUFilt = new EUCountryFilter();
        int year2 = 1992;
        YearAfterFilter yearFilt2 = new YearAfterFilter(year2);
        AllFilters allFilt2 = new AllFilters();
        allFilt2.addFilter(EUFilt);
        allFilt2.addFilter(yearFilt2);
        ArrayList<Rating> result4 = fr.getSimilarRatingsByFilter(webRaterID, topSimRatersNeeded, minimalRaters, allFilt2);
        result4 = cutSeenAndGetFiveBest(result4, webRaterID);
        String titleOfTop4 = "TOP 5 EUROPEAN MOVIES OF THE LAST 30 YEARS";


        //top 5 world best crime movies of the all times
        String genre3 = "Crime";
        GenreFilter genrFilt3 = new GenreFilter(genre3);
        ArrayList<Rating> result5 = fr.getSimilarRatingsByFilter(webRaterID, topSimRatersNeeded, minimalRaters, genrFilt3);
        result5 = cutSeenAndGetFiveBest(result5, webRaterID);
        String titleOfTop5 = "TOP 5 BEST CRIME MOVIES ALL OVER THE WORLD EVER";


        //finally, presenting everything as an HTML code

        //creating the beginning of the page (including the header of the table)
        StringBuilder sb = new StringBuilder("<html><head><style>body{font-family:Futura}h1{color:#ae6b97}p{color:#522d45;}table,th,td{border: 1px solid;border-color:#5DBF2F;border-collapse:collapse;padding-top:1px;padding-bottom:1px;padding-left:4px;padding-right:2px;}th{background-color:#f6eff3}</style></head><body><h1>List of movie recommendations special for you</h1><p>Based on rates from users who are close to you in preferences</p><table>");

        //adding a code for each top in our table
        sb.append(thisTopInHTML(result1, titleOfTop1));
        sb.append(thisTopInHTML(result2, titleOfTop2));
        sb.append(thisTopInHTML(result3, titleOfTop3));
        sb.append(thisTopInHTML(result4, titleOfTop4));
        sb.append(thisTopInHTML(result5, titleOfTop5));

        //ending the code of the page
        sb.append("<tr><td colspan=\"7\"><i>***If there are too many incomplete tops, please, start again from the first page and try to rate more movies</i></td></tr></table><p><i>Created by Ksenia (Aksana) Yelyashevich, 2022</i></p></body></html>");
        System.out.println(sb.toString());
    }

    //this helper method gets rid of movies that are already seen by the user
    //returns top-5 movies (or less if we do not have enough data)
    //returns ArrayList<Rating> that consists of 1) movieID 2) its rating
    private ArrayList<Rating> cutSeenAndGetFiveBest(ArrayList<Rating> whatToClean, String webRaterID) {
        Rater thisUserRater = RaterDatabase.getRater(webRaterID); //then we can call Rater.hasRating(movieID) to check whether this movie was already seen by user
        ArrayList<String> moviesRatedByUser = thisUserRater.getItemsRated();

        ArrayList<Rating> answer = new ArrayList<Rating>();
        ArrayList<Rating> finalAnswer = new ArrayList<Rating>();

        for (int i = 0; i < whatToClean.size(); i++) {
            Rating currentRating = whatToClean.get(i);
            String currMovieRatingID = currentRating.getItem();
            if (!moviesRatedByUser.contains(currMovieRatingID)) {//if this movie was not rated by user, and so was not seen
                answer.add(currentRating); //we add it to the answer list
            }
        }

        for (int k = 0; k < Math.min(5, answer.size()); k++) {
            Rating currRating = answer.get(k);
            finalAnswer.add(currRating);
        }
        return finalAnswer;
    }

    //helper method to create ann HTML code for every top of movies 
    private String thisTopInHTML(ArrayList<Rating> thisTopOfMovies, String titleOfTop) {
        StringBuilder answer = new StringBuilder();
        answer.append("<tr> <th colspan=\"7\">" + titleOfTop + "</th> </tr><tr style=bold;><td><b>Rating</b></td><td><b>Title of the movie</b></td><td><b>Year</b></td><td><b>Genre(s)</b></td><td><b>Where it was filmed</b></td><td><b>By director(s)</b></td><td><b>Length (in minutes)</b></td></tr>"); //adding 1)one row with the title of the top 2) one empty row 3) row with headlines for columns

        //in case there is not enough similar raters to generate this top at all
        if (thisTopOfMovies.size() == 0) {
            answer.append("<tr><td colspan=\"7\">Not enough similar raters to generate this top</td></tr><tr><td colspan=\"7\"> <br> </td></tr>");
            return answer.toString();
        }

        for (int i = 0; i < thisTopOfMovies.size(); i++) {
            Rating currObject = thisTopOfMovies.get(i);  // (every Rating object includes 1) ID of the movie + 2) its weighted average rating)
            String currMovID = currObject.getItem();

            double currRating = currObject.getValue(); //getting weighted average rating of this movie
            String currTitle = MovieDatabase.getTitle(currMovID);
            int currYear = MovieDatabase.getYear(currMovID);
            String currCountr = MovieDatabase.getCountry(currMovID);
            String currGenres = MovieDatabase.getGenres(currMovID);
            String currDirs = MovieDatabase.getDirector(currMovID);
            int currLength = MovieDatabase.getMinutes(currMovID);

            answer.append("<tr><td>" + (int) currRating + "</td><td>" + currTitle + "</td><td>" + currYear + "</td><td>" + currGenres + "</td><td>" + currCountr + "</td><td>" + currDirs + "</td><td>" + currLength + "</td><tr>");
        }

        if (thisTopOfMovies.size() < 5) {
            int howManyEmpty = 5 - thisTopOfMovies.size();
            for (int i = 0; i < howManyEmpty; i++) {
                answer.append("<tr><td colspan=\"7\">Not enough recommendations from similar raters to fill out this position</td></tr>");
            }
        }

        answer.append("<tr><td colspan=\"7\"> <br> </td></tr>"); //adding an empty row in the end of this top
        return answer.toString();
    }

    //helper method to check whether RaterDatabase contains some Rater (by his/her ID)
    private boolean baseContainsRater(String raterID) {
        ArrayList<Rater> allRaterObjects = RaterDatabase.getRaters();
        for (Rater currRater : allRaterObjects) {
            String currRaterID = currRater.getID();
            if (currRaterID.equals(raterID)) {
                return true;
            }
        }
        return false;
    }

    //helper-method to check whether this user has rated anything
    //if there is no ratings from this user OR all his/her rates are equal to zero - returns true
    private boolean allRatesAreZero(String raterID) {
        Rater thisRater = RaterDatabase.getRater(raterID);

        //if this rater have no ratings
        if (thisRater.numRatings() == 0) {
            return true;
        }

        //or if all ratings by this user a equal to zero
        ArrayList<String> whatRated = thisRater.getItemsRated();
        for (int i = 0; i < whatRated.size(); i++) {
            String currMovieID = whatRated.get(i);
            double currMovieRating = thisRater.getRating(currMovieID);
            if (currMovieRating > 0) {
                return false; //= false that all ratings by this rater are equal to zero
            }
        }
        return true;
    }

    void testPrintRecommendationsFor() {
        String webRaterID = "5";
        printRecommendationsFor(webRaterID);
    }
}
