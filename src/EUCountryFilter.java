
//this class filters movies that was filmed ONLY in EU country/countries

public class EUCountryFilter implements Filter {
    private String[] myCountry = {"Albania", "Andorra", "Armenia", "Austria", "Azerbaijan", "Belarus", "Belgium", "Bosnia and Herzegovina", "Bulgaria", "Croatia", "Cyprus", "Czech Republic", "Denmark", "Estonia", "Finland", "France", "Georgia", "Germany", "Greece", "Hungary", "Iceland", "Ireland", "Italy", "Kosovo", "Latvia", "Liechtenstein", "Lithuania", "Luxembourg", "Macedonia", "Malta", "Moldova", "Monaco", "Montenegro", "The Netherlands", "Norway", "Poland", "Portugal", "Romania", "Russia", "San Marino", "Serbia", "Slovakia", "Slovenia", "Spain", "Sweden", "Switzerland", "Turkey", "Ukraine", "UK", "Vatican City"};


    public EUCountryFilter() {
    }

    @Override
    public boolean satisfies(String id) {
        //creates an array of countries - in case there are more then one country for this particular movie
        String[] countriesOfThisMovie = MovieDatabase.getCountry(id).split(", ");
        ;//!!! ITS IMPORTANT TO SPLIT BY comma AND space - because this is how countries split in the string originally
        int numCountriesOfMovie = countriesOfThisMovie.length;
        int howManyOfThemEU = 0;

        for (int i = 0; i < countriesOfThisMovie.length; i++) {
            String currCountry = countriesOfThisMovie[i];

            for (int k = 0; k < myCountry.length; k++) {
                String countryToCompare = myCountry[k];
                if (currCountry.equals(countryToCompare)) {
                    howManyOfThemEU++;
                }
            }

        }

        return howManyOfThemEU == numCountriesOfMovie;
    }
}
