package ch.epfl.javions.aircraft;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipFile;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class AircraftDatabase {

    private String fileName;

    public AircraftDatabase(String fileName) {
        this.fileName = Objects.requireNonNull(fileName);
    }

    public AircraftData get(IcaoAddress address) throws IOException {
        fileName = getClass().getResource("/aircraft.zip").getFile();
        String adressString = address.string();
        try (ZipFile z = new ZipFile(fileName);
             InputStream s = z.getInputStream(z.getEntry(address.string().substring(4) + ".csv"));
             Reader r = new InputStreamReader(s, UTF_8);
             BufferedReader b = new BufferedReader(r)) {
            String l = "";

            while ((l = b.readLine()) != null)  {

                if (l.compareTo(adressString) > 0) {
                    return null;
                }

                if (l.startsWith(adressString)) {
                    String[] data = l.split(",");

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