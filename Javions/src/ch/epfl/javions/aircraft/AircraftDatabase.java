package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * mictronics database of aircraft
 * @author Nagyung KIM (339628)
 */

public final class AircraftDatabase {

    private static final int WAKE_TURBULENCE_CATEGORY = 5;
    private static final int DESCRIPTION = 4;
    private static final int MODEL = 3;
    private static final int TYPE_DESIGNATOR = 2;
    private static final int REGISTRATION = 1;
    private final String fileName;


    /**
     * construct an object representing the mictronics database stored in the given file name and check whether is null
     * or not
     * @param fileName file name
     * @throws NullPointerException if fileName is null
     */
    public AircraftDatabase(String fileName) {

        this.fileName = Objects.requireNonNull( fileName );
    }


    /**
     * returns the data of the aircraft whose ICAO address is the given one or null if no entry exists in the database
     * of the address
     * @param address ICAO Address
     * @return the data of the aircraft whose ICAO address is the given one or null if no entry exists in the database
     * of the address
     * @throws IOException in case of input/output error
     */

    public AircraftData get(IcaoAddress address) throws IOException {
        String addressString = address.string();
        try ( ZipFile z = new ZipFile( fileName ) ;
              InputStream s = z.getInputStream( z.getEntry( addressString.substring( 4 ) + ".csv" ) ) ;
              Reader r = new InputStreamReader( s, UTF_8 ) ;
              BufferedReader b = new BufferedReader( r ) ) {
            String l = "";

            while ( ( l = b.readLine() ) != null ) {

                if ( l.startsWith( addressString ) ) {
                    String[] data = l.split( ",", -1 );

                    AircraftRegistration registration = new AircraftRegistration( data[REGISTRATION] );
                    AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator( data[TYPE_DESIGNATOR] );
                    String model = data[MODEL];
                    AircraftDescription description = new AircraftDescription( data[DESCRIPTION] );
                    WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of(
                            data[WAKE_TURBULENCE_CATEGORY] );

                    return new AircraftData( registration, typeDesignator, model, description, wakeTurbulenceCategory );
                }

                if ( l.compareTo( addressString ) > 0 ) {
                    return null;
                }
            }
            return null;
        }
    }
}

