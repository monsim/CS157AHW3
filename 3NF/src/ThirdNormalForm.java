import java.io.*;  
import java.util.*;  

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
            if(!string.substring(i, i + 1).equals(","))
                result.add(string.substring(i, i+1));
        }
        return result;   
    } 
    
    /**
     * Computes the closure of attributes based on their respective FDs.
     * @param target    the attributes to find the closure of
     * @param fds       their respective FDs
     * @return          the closure of the attributes
     */
    private static HashSet<String> closure(String target, List<String> fds ) {
        HashSet<String> closure = new HashSet<String>();
        HashSet<String> xOld = new HashSet<String>();
        String[] LHSAttributes = target.split(",");
        // Note: I changed this to compute the closure of more than 1 attribute, e.g. {AB}+
        // whereas before it could only compute singleton attributes, like {A}+
        for (String attribute : LHSAttributes) {
            closure.add(attribute);
        }
   
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

    /**
     * Step 1: Finds the minimal basis for the relation.
     * @param file  the file containing the original FDs
     * @return      the minimal basis (as a list of strings)
     */
    private static List<String> minimalBasis(File file) throws FileNotFoundException {
        return reduce(minimizeLHS(singletonRHS(getFDList(file))));
    }

    /**
     * Step 2: Schematizes the relations based off their minimal basis.
     * Example: AB -> C, C -> B, A -> D "schematized" becomes R1(A, B, C), R2(B, C), R3(A, D)
     * @param fds   the FDs of the relation to schematize
     * @return      the new relations based on the minimal basis
     */
    private static List<String> schematize(List<String> fds) {
        List<String> result = new ArrayList<String>();
        for (String fd: fds) {
            result.add(fd.replaceAll(";", ","));
        }
        // sort the resulting schemas by lexicographic order
        Collections.sort(result);
        return clean(result);
    }
    
    /**
     * Deletes the proper subsets of a relation given its FDs.
     * Example: R1(A, B, C), R2(B, C). We can delete R2 because it is a proper subset of R1.
     */
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
                if (c != ',') {                 // if c is a letter 
                    if (seen.add("" + c)) {     // "" added to make it a string
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

    /**
     * Computes the powerset (set of all subsets) of the attributes of a relation.
     * @param attributes    the attributes of the relation
     * @return              the powerset of the attributes
     */
    private static ArrayList<HashSet<String>> powerset(HashSet<String> attributes) {
        ArrayList<HashSet<String>> sets = new ArrayList<HashSet<String>>();
        if (attributes.isEmpty()) {
            sets.add(new HashSet<String>());
            return sets;
        }
        ArrayList<String> list = new ArrayList<String>(attributes);
        String head = list.get(0);
        HashSet<String> rest = new HashSet<String>(list.subList(1, list.size())); 
        for (HashSet<String> set : powerset(rest)) {
            HashSet<String> newSet = new HashSet<String>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }       
        return sets;
    }
    
    /**
     * Finds the superkeys of a relation given the list of attributes and its FDs.
     * @param attributeList     the list of attributes of the relation
     * @param fds               the functional dependencies in respect to the attributes of the relation
     * @return                  a set of superkeys for the given relation
     */
    private static HashSet<HashSet<String>> findSuperKeys(HashSet<String> attributeList, List<String> fds) {
        // a hash map where: Key = each set in the powerset, Value = each of the set's respective closures
        HashMap<HashSet<String>, HashSet<String>> setToClosure = new HashMap<>();
        ArrayList<HashSet<String>> attributePowerSet = powerset(attributeList);
        HashSet<HashSet<String>> superkeys = new HashSet<>();
        
        for (HashSet<String> set : attributePowerSet) {
            // first convert the set to an array list
            ArrayList<String> list = new ArrayList<String>(set);
            // now convert that array list to a string since closure method takes in the LHS as a string
            String attributes = "";
            for (String s : list) {
                attributes += s + ",";
            }
            // compute the closure of each set in the powerset and add that to closures
            setToClosure.put(set, closure(attributes, fds));
        }
        
        // go through each subset & closure and find the superkeys
        for (Map.Entry<HashSet<String>, HashSet<String>> entry : setToClosure.entrySet()) {
            // check each of the sets to see if they are a superkey (contains all the attributes)
            if (entry.getValue().containsAll(attributeList)) {
                // add that set in the superkey list
                superkeys.add(entry.getKey());
            }
        }
        
        return superkeys;
    }
    
    
    /**
     * Finds the keys of a relation.
     * @param superkeys     the computed superkeys
     * @return              the keys of the relation
     */
    public static List<HashSet<String>> findKeys(HashSet<HashSet<String>> superkeys) {
        List<HashSet<String>> keys = new ArrayList<HashSet<String>>();
        int minimalKeySize = Integer.MAX_VALUE;
        
        // find the number of attributes of the minimal superkeys
        for (HashSet<String> superkey : superkeys) {
            if (superkey.size() < minimalKeySize) {
                minimalKeySize = superkey.size();
            }
        }
        
        // loop through the superkeys again to get the actual keys
        for (HashSet<String> superkey : superkeys) {
            if (superkey.size() == minimalKeySize) {
                keys.add(superkey);
            }
        }
        
        return keys;
    }
    
    // step 3: add a new relation containing the keys if there isn't one already
    /**
     * checking if we need to add another relation that represents a key
     * @return new relations with possible added key 
     */
    private static List<String> checkRelationsForKey(List<String> relations, List<HashSet<String>> keys, List<String> fds, HashSet<String> attributes) {
    		List<String> keyList = new ArrayList<>();
    		List<String> newRelations = relations;
    		
    		//check if current relations contain superkeys already, if it does then return the original relations
    		for (String relation: newRelations) {
    			if(closure(relation, fds).containsAll(attributes)) {
    				return newRelations;
    			}
    		}
    		
    		//turning keys into a list of strings
    		for (HashSet<String> set: keys) {
    			String toChange = "";
    			for (String s: set) {
    				toChange += s + ",";
    				toChange = toChange.replaceAll(" ", "");
    			}
    			if (toChange.endsWith(",")) {
    				toChange = toChange.substring(0, toChange.length()-1);
    			}
    			
    			keyList.add(toChange);
    		}
    		
    		//sort keys by lexographic order 
    		Collections.sort(keyList);
    		
    		//if we got here, none of the original relations were superkeys, so we need to add the first key in keyList as a relation
    		newRelations.add(keyList.get(0));
    		return newRelations;
    }
    

    public static void main (String[] args) throws FileNotFoundException {
        File file = new File("input.txt");

        // Step 1:
        // Find a minimal basis for F, say G.
        // Step 2:
        // For each FD X → A in G, use XA as the schema of one of the relations in the decomposition.
        List<String> relations = schematize(minimalBasis(file));

        // Step 3:
        // If none of the relation schema from Step 2 is a superkey for R, add another relation whose schema is a key for R.
        List<String> fds3 = minimalBasis(file);
        HashSet<String> attributeList = getAttributes(file);
        
        List<String> thirdNF = checkRelationsForKey(relations, findKeys(findSuperKeys(attributeList, fds3)), fds3, attributeList);
        for (String relation: thirdNF) {
            System.out.println(relation);
        }
    }
}
