package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          20/04/2023
 */


import ch.epfl.javions.Preconditions;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents an OpenStreetMap tile manager. Obtains and stores the tiles in cache
 */
public final class TileManager {
    private final static int MEMORY_CACHE_SIZE = 100;
    private final Path diskCachePath;
    private final String serverName;

    //TODO replace this redifinition by just declaration +
    //  Bonjour,
    //  Dans notre cas, je ne pense pas que ce soit nécessaire. En effet, la méthode next() de l'itérateur de
    //  l'ensemble des clefs (keySet) de LinkedHashMap retourne l'élément le plus ancien, que vous pouvez
    //  alors enlever à l'aide de la méthode remove(). Cela vous permettra d'écrire un peu moins de code.
    //  aka do smth like memoryCache.keySet().next().remove() -- see if this works in another page
    private final LinkedHashMap<Path, Image> memoryCache = new LinkedHashMap<Path, Image>() {
        protected boolean removeEldestEntry(Map.Entry<Path, Image> eldest) {
            return size() > MEMORY_CACHE_SIZE;
        }
    };


    /**
     * Constructor. Builds an instance of <code>TileManager</code>
     * @param path       path to the disk cache directory
     * @param serverName name of the server where we can find the tiles
     */
    public TileManager(Path path, String serverName) {
        this.diskCachePath = path;
        this.serverName = serverName;
        //this.memoryCache = new LinkedHashMap<>( MEMORY_CACHE_SIZE );
        if ( !Files.exists( diskCachePath ) ) {
            diskCachePath.toFile()
                         .mkdir();
        }
    }


    /**
     * Searches for the image with the given tile identity in memory cache, disk cache and OpenStreetMap server and
     * returns it
     * @param tileId tile identification
     * @return the image with the give tile identity
     * @throws IOException if there is an input/output error
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        int tileZoom = tileId.zoom;
        int tileX = tileId.x;
        int tileY = tileId.y;
        Preconditions.checkArgument( TileId.isValid( tileZoom, tileX, tileY ) );

        Path directoryPath = diskCachePath.resolve( String.valueOf( tileZoom ) )
                                          .resolve( String.valueOf( tileX ) );
        Path imagePath = directoryPath.resolve( tileY + ".png" );
        File imageFile = imagePath.toFile();
        Image image;

        //Search in memory cache for the path to the image. If present, returned
        if ( memoryCache.containsKey( imagePath ) ) {
            image = memoryCache.get( imagePath );
        }
        //search in disk cache. If present, placed into memory cache and returned
        else if ( Files.exists( imagePath ) ) {
            InputStream inputStream = new FileInputStream( imageFile );
            image = new Image( inputStream );
            memoryCache.put( imagePath, image );
        }
        //get from OpenStreetMap. Placed in disk cache and memory cache, returned
        else {
            Files.createDirectories( directoryPath );

            String urlString = "https:\\" + serverName + "\\" + tileZoom + "\\" + tileX + "\\" + tileY + ".png";
            URL url = new URL( urlString );
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty( "User-Agent", "Javions" );
            byte[] bytes = new byte[0];
            try ( InputStream in = urlConnection.getInputStream() ;
                  OutputStream out = new FileOutputStream( imageFile ) ) {
                bytes = in.readAllBytes();
                out.write( bytes );
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( bytes );
            image = new Image( byteArrayInputStream );
            memoryCache.put( imagePath, image );
        }
        return image;
    }


    /**
     * Represents the identity of an OpenStreetMap tile
     * @param zoom zoom level
     * @param x    x coordinate
     * @param y    y coordinate
     */
    public record TileId(int zoom, int x, int y) {

        /**
         * Constructor. Builds an instance of <code>TileId</code>
         * @param zoom zoom level
         * @param x    x coordinate
         * @param y    y coordinate
         * @throws IllegalArgumentException if the parameters don't constitute a valid tile identity
         */
        public TileId {
            Preconditions.checkArgument( isValid( zoom, x, y ) );
        }


        /**
         * Returns true if the parameters <code>zoom</code>, <code>x</code> and <code>y</code> constitute a valid tile
         * identity
         * @param zoom level of zoom
         * @param x    x coordinate
         * @param y    y coordinate
         * @return true if the parameters are valid, false if not
         */
        public static boolean isValid(int zoom, int x, int y) {
            double n = Math.pow( 2, zoom );
            return ( 0 <= zoom && zoom < 19 ) && ( 0 <= x && x < n ) && ( 0 <= y && y < n );
        }
    }
}
