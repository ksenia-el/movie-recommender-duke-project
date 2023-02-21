
//class returns true if a movie has at least ONE of the directors that initialized as a parameter to this class

public class DirectorsFilter implements Filter {

    private String[] myDirectors;

    //parameter - a list of directors separated by commas
    public DirectorsFilter(String directors) {
        myDirectors = directors.split(",");
    }

    //SPLIT BY COMMAS WITHOUT SPACES
    public boolean satisfies(String id) {
        String[] currDirectors = MovieDatabase.getDirector(id).split(", ");
        for (int i = 0; i < myDirectors.length; i++) {
            String myDirector = myDirectors[i];
            for (int k = 0; k < currDirectors.length; k++) {
                String currDirector = currDirectors[k];

                //for test only
                //System.out.println(" for movie " + id + " currDirector " + currDirector + " compared to (my director) " + myDirector);

                if (currDirector.equals(myDirector)) {
                    return true;
                }
            }
        }
        return false;
    }

}
