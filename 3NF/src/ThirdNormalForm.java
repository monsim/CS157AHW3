import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class ThirdNormalForm {

    // this removes the trivial dependencies as well
    private static List<String> getFDList(File file) throws FileNotFoundException {
        Scanner scanner= new Scanner(file);
        List<String> list = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            String[] split = scanner.nextLine().split(";");
            String result = split[1];
            for (char c: split[0].toCharArray()) {
                if (c != ',') {
                    String replace = "" + c;

                    result = result.replaceAll(replace, "");
                }
            }
            if (!result.equals("")) {
                list.add(split[0] + ";" + result);
            }

        }
        return list;
    }

    private static List<String>  singletonRHS(List<String> fds) {
        List<String> result = new ArrayList<String>();
        for (String fd: fds) {
            String[] split = fd.split(";");
            if (split[1].length() > 1 ) {
                for (int i = 0; i < split[1].length(); i ++) {
                    if (!split[1].substring(i, i+1).equals(",")) {
                        String temp = split[0] + ";" + split[1].substring(i, i + 1);
                        result.add(temp);
                    }
                }
            } else {
                result.add(fd);
            }
        }

        return result;
    }

    private static List<String> reduce(List<String> fds)  {
        List<String> result = new ArrayList<String>();
        for (String fd: fds) {
            List<String> subset = new ArrayList<String>();
            subset.addAll(fds);
            subset.remove(fd);

            String[] split = fd.split(";");
            HashSet<String> closure = closure(split[0], subset);
            if (!closure.contains(split[1])) {
                result.add(fd);
            }

        }
        return result;
    }
    private static List<String> minimizeLHS(List<String> fds ) {
        List<String> result = new ArrayList<String>();
        for( String fd: fds) {
            String[] split = fd.split(";");
            if (split[0].length() > 1) {
                result.add(removeLHS(split[0], fds) + ";" + split[1]);
            } else {
                result.add(fd);
            }
        }
        return result;
    }

    private static String removeLHS(String leftFD, List<String> fds) {
        String result = leftFD;
        for( int i = 0; i < leftFD.length(); i++) {
            String current = leftFD.substring(i, i+1);
            HashSet<String> closure = closure(current, fds);
            for (String key:  closure) {
                if ( leftFD.contains(key)  && !key.equals(current)) {
                    result = result.replaceAll(key, "");
                    result = result.replace(",", "");
                }
            }
        }
        return result;
    }

    private static List<String> listifyString(String string) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < string.length(); i++) {
            if(!string.substring(i, i+1).equals(","))
                result.add(string.substring(i, i+1));
        }
        return result;
    }
    
    private static HashSet<String> closure(String target, List<String> fds ) {
        HashSet<String> closure = new HashSet<String>();
        HashSet<String> xOld = new HashSet<String>();
        closure.add(target);

        while (!xOld.containsAll(closure)) {
            xOld = new HashSet<String>(closure);
            for (String fd: fds) {
                String[] split = fd.split(";");
                if (split[0].length() > 1 ) {
                     if (closure.containsAll(listifyString(split[0]))) {
                         closure.add(split[1]);
                     }
                } else {
                    if (closure.contains(split[0])) {
                        closure.add(split[1]);
                    }
                }
            }
        }


        return closure;
    }

    private static List<String> minimalBasis(File file) throws FileNotFoundException {
        return reduce(minimizeLHS(singletonRHS(getFDList(file))));
    }

    // step 2
    private static List<String> schematize(List<String> fds) {
        List<String> result = new ArrayList<String>();
        for (String fd: fds) {
            result.add(fd.replaceAll(";", ","));
        }
        // sort the resulting schemas by lexicographic order
        Collections.sort(result);
        return clean(result);
    }
    private static List<String> clean(List<String> fds) {
        List<String> result = new ArrayList<String>();
        HashSet<String> seen = new HashSet<String>();
        for (String fd : fds) {
//            System.out.println ("fd: " + fd);
//            for (String s: seen) {
//                System.out.println(s);
//            }
            boolean flag = false;
            for (char c: fd.toCharArray()) {
                if (c != ',') {					// if c is a letter 
                    if (seen.add("" + c)) {		// "" added to make it a string
                        flag = true;
                    }
                }
            }
            if (flag) {
                result.add(fd);
            }
        }
        return result;
    }

    private static HashSet<String> getAttributes(File file) throws FileNotFoundException {
        HashSet<String> attributes = new HashSet<String>();
        Scanner scanner= new Scanner(file);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().replaceAll(",", "").replaceAll(";","");
            for (char c: line.toCharArray()) {
                attributes.add(""+ c);
            }
        }
        return attributes;
    }
    
    /*
    private static HashSet<String> closureOfSubsets (HashSet<String> attributeList) {
    	
    }
    */
    
    public static void main (String[] args) throws FileNotFoundException {
        File file = new File("input.txt");

//        for (String s: attributes) {
//            System.out.println(s);
//        }

        // Find a minimal basis for F, say G.
        // For each FD X→A in G, use XA as the schema of one of the relations in the decomposition.
        List<String> fds = schematize(minimalBasis(file));
        for (String s: fds) {
            System.out.println(s);
        }

        // If none of the relation schema from Step 2 is a superkey for R, add another relation whose schema is a key for R.
        HashSet<String> attributes = getAttributes(file);

//        List<String> fds = getFDList(file);
//        for (String s: fds) {
//            System.out.println(s);
//        }
        
        /*
        List<String> fds2 = getFDList(file);
        System.out.println(closure("1", fds2));
        */
        
    }
}
