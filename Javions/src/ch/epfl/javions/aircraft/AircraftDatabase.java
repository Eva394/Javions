package ch.epfl.javions.aircraft;

import java.io.*;
import java.net.URLDecoder;
import java.util.Objects;
import java.util.zip.ZipFile;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase {

    private String fileName;

    public AircraftDatabase(String fileName) {

        this.fileName = Objects.requireNonNull(fileName);
    }

    //TODO why get file is suggesting to replace even though it is already written above (line 15)?

    public AircraftData get(IcaoAddress address) throws IOException {
        fileName = getClass().getResource("/aircraft.zip").getFile();
        fileName = URLDecoder.decode(fileName, UTF_8);
        String addressString = address.string();
        try (ZipFile z = new ZipFile(fileName);
             InputStream s = z.getInputStream(z.getEntry(address.string().substring(4) + ".csv"));
             Reader r = new InputStreamReader(s, UTF_8);
             BufferedReader b = new BufferedReader(r)) {
            String l = "";

            while ((l = b.readLine()) != null)  {

                if (l.compareTo(addressString) > 0) {
                    return null;
                }

                if (l.startsWith(addressString)) {
                    String[] data = l.split(",",-1);

                    AircraftRegistration registration = new AircraftRegistration(data[0]);
                    AircraftTypeDesignator typeDesignator = new AircraftTypeDesignator(data[1]);
                    String model = data[2];
                    AircraftDescription description = new AircraftDescription(data[3]);
                    WakeTurbulenceCategory wakeTurbulenceCategory = WakeTurbulenceCategory.of(data[4]);

                    return new AircraftData(registration, typeDesignator, model, description, wakeTurbulenceCategory);
                }
            }
            return null;
        }
    }
}