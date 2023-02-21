
//selects movies that were created in this year or later 

public class YearAfterFilter implements Filter {
    private int myYear;

    public YearAfterFilter(int year) {
        myYear = year;
    }

    @Override
    public boolean satisfies(String id) {
        return MovieDatabase.getYear(id) >= myYear;
    }

}
