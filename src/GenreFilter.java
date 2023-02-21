
public class GenreFilter implements Filter {

    private String myGenre;


    public GenreFilter(String genre) {
        myGenre = genre;
    }

    @Override
    public boolean satisfies(String id) {
        String genresOfThisMovie = MovieDatabase.getGenres(id);
        //creates an array of genres - in case there are more than one genre for this particular movie
        String[] genresSplitted = genresOfThisMovie.split(", "); //!!! ITS IMPORTANT TO SPLIT BY comma AND space - because this is how genres split in the string originally
        for (int i = 0; i < genresSplitted.length; i++) {
            String currGenre = genresSplitted[i];
            if (currGenre.equals(myGenre)) {
                return true;
            }
        }
        return false;
    }
}
