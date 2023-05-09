package ch.epfl.javions.gui;
/*
 *  Author :        Mangano Eva
 *  Date :          09/05/2023
 */


import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class test {

    public static void main(String[] args) {
        List<Integer> isU = List.of( 5, 7, 1, 12, 6, 3, 10, 5, 6, 7, 8, 9 );
        List<Integer> isS = sort( isU, (x, y) -> x < y );
        System.out.println( isU );
        System.out.println( isS );

        List<String> ssU = isU.stream()
                              .map( i -> "s" + i )
                              .toList();
        List<String> ssS = sort( ssU, (x, y) -> x.compareTo( y ) < 0 );
        System.out.println( ssU );
        System.out.println( ssS );
    }


    public interface Order<T> {
        boolean isSmaller(T x, T y);
    }


    //TODO  use interface function with method apply
    static <T> List<T> sort(List<T> list, BiPredicate<T, T> biPredicate) {
        List<T> result = new ArrayList<>( list.size() );
        for ( T e : list ) {
            int i = 0;
            while ( i < result.size() && biPredicate.test( result.get( i ), e ) ) {
                i++;
            }
            result.add( i, e );
        }
        return result;
    }
}
