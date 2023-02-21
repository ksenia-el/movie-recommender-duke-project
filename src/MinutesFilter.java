
/**
 * Returns true if the movie is at least some Min minutes length and
 * no more than some Max minutes
 */
public class MinutesFilter implements Filter {
    private int myMinMinutes;
    private int myMaxMinutes;

    public MinutesFilter(int minMinutes, int maxMinutes) {
        myMinMinutes = minMinutes;
        myMaxMinutes = maxMinutes;
    }

    @Override
    public boolean satisfies(String id) {
        int length = MovieDatabase.getMinutes(id);
        return length >= myMinMinutes && length <= myMaxMinutes;
    }

}
