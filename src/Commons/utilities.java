package Commons;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Random;
import java.util.regex.Pattern;

public class utilities
{
    private static Random rnd = new Random();

    //Copy selected string to user clipboard
    public static void copyToClip(String msg)
    {
        Clipboard clipboard = Clipboard.getSystemClipboard();

        final ClipboardContent content = new ClipboardContent();

        content.putString(msg);

        clipboard.setContent(content);
    }

    //returns a positive int or a -1
    public static int getPositiveInt(String num)
    {
        int qty;

        try
        {
            qty = Integer.parseInt(num);
        }
        catch (NumberFormatException e)
        {
            return -1;
        }

        if(qty < 0)
        {
            return -1;
        }

        return qty;
    }

    public static boolean isAlphaNumeric(String str)
    {
        return str.matches("^[a-zA-Z0-9]*$");
    }

    public static SimpleStringProperty formatLocalDate(LocalDate date)
    {
        return new SimpleStringProperty(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)));
    }

    public static boolean validateAssemblyLotPartNumber(String num)
    {
        return num.matches("([A-Z]|[a-z])([1-9]|[A-C])([1-9]|[A-V])([0-9]{4})(([A-Z]|[a-z]){2})([A-Z]|[a-z]|[0-9])");
    }


    //Generate a unique Component Lot Number
    //8 digit integer that does not exist in
    //the Component_Lots table
    public static String generateUniqueCompLotNumber()
    {
        int n;

        do { n = 10000000 + rnd.nextInt(90000000);
        }while(!DAL.componentDAO.checkComponentLotNum(String.valueOf(n)));

        return String.valueOf(n);
    }

    //Generate a unique ShipID
    //10 digit integer that does not exist in
    //the Fulfillment table
    public static String generateUniqueShipID()
    {
        StringBuilder n = new StringBuilder();

        do {
            n.append(rnd.nextInt(9)+1);

            for(int i=0; i<9; i++)
            {
                n.append(rnd.nextInt(10));
            }
        }while (!DAL.ShipItemsDAO.checkShipIDNum(n.toString()));


        return String.valueOf(n);
    }





    //Generates a random alphanumeric string of size 'size'
    public static String generateRandomAlphaNumericString(int size)
    {
        String key = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder result = new StringBuilder();

        for(int i=0; i<size; i++)
        {
            result.append(key.charAt(rnd.nextInt(key.length())));
        }

        return result.toString();
    }



    //Generate random numeric string of size 'size'
    public static String generateRandomNumericString(int size)
    {
        return String.valueOf(size < 1 ? 0 : new Random()
                .nextInt((9 * (int) Math.pow(10, size - 1)) - 1)
                + (int) Math.pow(10, size - 1));
    }




    //This date convention will be invalid after 2068-12-31 unless we encode the year
    //Using a different convention

    //Here we will take a Date object and convert it
    //to a 3 character encoded string.
    //
    //first character is a base 52 year code A = 2017 B = 2018 and so on until Z then a - z
    //second character is base 12 month code 0 - 9 and then A - C
    //third character is base 31 day code 0 - 9 and then A - V
    public static String dateToCodeConversion(LocalDate date)
    {
        String encodedDate;
        char encodedYear;
        char encodedMonth;
        char encodedDay;
        int yearBaseline = 2017;
        int monthBaseline = 10;
        int dayBaseline = 10;



        int year  = date.getYear();
        int month = date.getMonthValue();
        int day   = date.getDayOfMonth();


        //Convert the year to our code
        //
        //We represent A-Z capital as the first 26 years and then after that
        //we will use a-z to represent the last 26 years.
        //since ascii does not line up for lower and uppercase codes
        //we will need to separate the assignment depending if we are supposed
        //to use a lower case or upper case letter.

        //yearBaseline+25 will be 'Z' so if we are greater than that
        //we need to switch to lowercase
        if(year <= yearBaseline+25)
        {
            encodedYear = (char)((year - yearBaseline)+65);
        }
        else
        {
            encodedYear = (char)((year - yearBaseline-26)+97);
        }


        //Convert the month to our code
        //
        //months 1 - 9 should be converted to characters representing digits 1 - 9
        //months 10, 11 and 12 will be represented by A, B and C respectively
        //
        //If month is greater or equal to the baseline we convert to uppercase letters
        //else we convert the digit to its character representation
        if(month >= monthBaseline)
        {
            encodedMonth = (char)((month - monthBaseline)+65);
        }
        else
        {
            encodedMonth = (char)((month)+48);
        }

        //Convert day to out code //virtually the same as month convert
        //
        //day 1 - 9 should be converted to char representing its digit
        //day 10 - 31 should be converted to uppercase letters A - V
        //
        //If day is greater or equal to the baseline we convert to uppercase letters
        //else we convert the digit to its character representation
        if(day >= dayBaseline)
        {
            encodedDay = (char)((day - dayBaseline)+65);
        }
        else
        {
            encodedDay = (char)((day)+48);
        }


        encodedDate = String.valueOf(encodedYear) + String.valueOf(encodedMonth) + String.valueOf(encodedDay);


        return encodedDate;
    }

    //To validate a date code we check against a
    //regular expression to enforce length constraints
    //and then individual character constraints
    public static boolean validateDateCode(String code)
    {
        return Pattern.matches("([A-Z]|[a-z])([1-9]|[A-C])([1-9]|[A-V])", code);
    }


    //Convert date code to a LocalDate object
    //We return null if the argument is invalid otherwise we
    //we return a LocalDate object
    public static LocalDate dateCodeToDate(String code)
    {
        int year, month, day;

        int yearBaseline = 2017;
        int monthBaseline = 10;
        int dayBaseline = 10;

        if(!validateDateCode(code))
        {
            return null;
        }

        //Everything below here is sanitized

        char yearChar = code.charAt(0);
        char monthChar = code.charAt(1);
        char dayChar = code.charAt(2);


        //If the yearChar is less than 'a' we know we have an uppercase year code
        if((int)yearChar < 97)
        {
            //Here convert yearChar to an offset from 'A' then add the base year
            year = (((int)yearChar)-65)+yearBaseline;
        }
        else//Here we convert yearChar to an offset from 'a' then add base year and then add 26 for the prev 26 years
        {
            year = ((((int)yearChar)-97)+yearBaseline)+26;
        }


        //if monthChar is less than 'A' then we have a digit not a Letter
        if((int)monthChar < 65)
        {
            month = ((int)monthChar)-48;
        }
        else //Here we convert monthChar to an offset from 'A' and then add the baseline for previous months
        {
            month = (((int)monthChar)-65)+monthBaseline;
        }

        //Virtually the same as month
        //if dayChar is less than 'A' then we have a digit not a Letter
        if((int)dayChar < 65)
        {
            day = ((int)dayChar)-48;
        }
        else //Here we convert dayChar to an offset from 'A' and then add the baseline for previous days
        {
            day = (((int)dayChar)-65)+dayBaseline;
        }


        return LocalDate.of(year,month,day);
    }



}
