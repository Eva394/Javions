package ch.epfl.javions.adsb;
import ch.epfl.javions.Preconditions;
import java.util.regex.Pattern;


public record CallSign(String string) {
    private static Pattern CallSign;
    public CallSign{
        CallSign = Pattern.compile("[A-Z0-9 ]{0,8}");
        Preconditions.checkArgument( CallSign.matcher( string ).matches() || string.equals( "" ) );
    }
}
