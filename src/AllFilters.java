import java.util.ArrayList;

//this class combines several filters
public class AllFilters implements Filter {
    ArrayList<Filter> filters;

    public AllFilters() {
        filters = new ArrayList<Filter>();
    }

    public void addFilter(Filter f) {
        filters.add(f);
    }

    //returns true if the movie satisfies the criteria of ALL THE FILTERS in the arraylist
    @Override
    public boolean satisfies(String id) {
        for (Filter f : filters) {
            if (!f.satisfies(id)) {
                return false;
            }
        }

        return true;
    }

}
