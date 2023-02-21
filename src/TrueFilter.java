
public class TrueFilter implements Filter {
    @Override

    //this class can be used to select every movie from movieDataBase
    public boolean satisfies(String id) {
        return true;
    }

}
