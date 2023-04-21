package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          20/04/2023
 */


import javafx.scene.image.Image;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;

public final class TileManager {
    private final Path path;
    private final String serverName;


    public TileManager(Path path, String serverName) {
        this.path = path;
        this.serverName = serverName;
    }


    public Image imageForTileAt(TileId tileId) {
        boolean found = false;
        try ( InputStream stream = new FileInputStream(
                "C:\\Users\\Eva Mangano\\OneDrive\\Documents\\EPFL\\4 - BA2\\PROJET\\Javions\\resources\\messages_20230318_0915.bin" ) ) {
            URL u = new URL( "https://tile.openstreetmap.org/17/67927/46357.png" );
            URLConnection c = u.openConnection();
            c.setRequestProperty( "User-Agent", "Javions" );
            InputStream i = c.getInputStream();
        }
        catch ( MalformedURLException e ) {
            throw new RuntimeException( e );
        }
        catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }


    public record TileId(int zoom, int x, int y) {
        public static boolean isValid(int zoom, int x, int y) {
            //TODO find what constitutes a valid tile id
            return false;
        }
    }
}
