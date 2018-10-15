import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class ThirdNormalForm {
	
	//hashmap arraylist ints --> arraylist ints
	//return list of lists
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		File file = new File("input.txt");
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		HashMap<ArrayList<Integer>, ArrayList<Integer>> FDs = new HashMap<>();
		ArrayList<Integer> attributes = new ArrayList<>();
		storeFDs(scanner, FDs, attributes);
		
		System.out.println("stored");
		
		for (Map.Entry<ArrayList<Integer>, ArrayList<Integer>> entry : FDs.entrySet()) {
            List<Integer> key = entry.getKey();
            List<Integer> value = entry.getValue();
            System.out.println(key + " " + value);
        }
		
		//trying to test closure()
		ArrayList<Integer> first = new ArrayList<>();
		first.add(1);
		first.add(2);
		
		ArrayList<Integer> firstRHS = FDs.get(first);
		System.out.println("firstRHS: " + firstRHS);
		ArrayList<Integer> c = closure (FDs, first);
		
		System.out.println("here goes nothing: ");
		for (int i = 0; i < c.size(); i++) {
			System.out.println(c.get(i));
		}
		
//		for (ArrayList<Integer> i : FDs.keySet()) {
//			for (int j = 0; j < i.size(); j++) {
//				System.out.print(i.get(j));
//			}
//			System.out.println();
//		}
		System.out.println("here wweriwer");
		for (Map.Entry<ArrayList<Integer>, ArrayList<Integer>> entry : FDs.entrySet()) {
            List<Integer> key = entry.getKey();
            List<Integer> value = entry.getValue();
            System.out.println(key + " " + value);
        }
	}
	
	public static void storeFDs(Scanner scanner, HashMap<ArrayList<Integer>, ArrayList<Integer>> fds, ArrayList<Integer> attributes) {
		int largestAttribute = 0;
		while (scanner.hasNextLine()) {
			String[] data = scanner.nextLine().split(";");
			String[] lhs = data[0].split(",");
			String[] rhs = data[1].split(",");
			ArrayList<Integer> lhsList = new ArrayList<>();
			ArrayList<Integer> rhsList = new ArrayList<>();
			for (int i = 0; i < lhs.length; i++) {
				if (Integer.parseInt(lhs[i]) > largestAttribute) {
					largestAttribute = Integer.parseInt(lhs[i]);
				}
				lhsList.add(Integer.parseInt(lhs[i]));
			}
			for (int i = 0; i < rhs.length; i++) {
				if (Integer.parseInt(rhs[i]) > largestAttribute) {
					largestAttribute = Integer.parseInt(rhs[i]);
				}
				rhsList.add(Integer.parseInt(rhs[i]));
			}
			fds.put(lhsList, rhsList);
		}
		// A1, ..., An
		for (int i = 1; i < largestAttribute + 1; i++) {
			attributes.add(i);
		}
	}
	
	//pass fds and lhs of what you want to find the closure of
	//need to account for A --> B and B --> C then C is in the closure of A 
	
	public static ArrayList<Integer> closure(HashMap<ArrayList<Integer>, ArrayList<Integer>> fds, ArrayList<Integer> toFindClosureOf) {
		ArrayList<Integer> x = toFindClosureOf;
		ArrayList<Integer> xOld = new ArrayList<>();
		
		//need to apply splitting rule!
		//how to account for fds that are like A --> BC (splitting rule)
		
		while (!isTwoArrayListsWithSameValues(x, xOld)) {
			System.out.println("here we are: " + x);
			xOld = x;
			
			for (Map.Entry<ArrayList<Integer>, ArrayList<Integer>> entry : fds.entrySet()) {
				System.out.println("ok");
				ArrayList<Integer> key = entry.getKey();
		        ArrayList<Integer> value = entry.getValue();
		        if (doesItContain(x, key)) {		//if the lhs of this fd is in x, add the rhs to it (account removing duplicates ex if )
		        		System.out.println("yip it contains " + key);
		        		for (int i = 0; i < value.size(); i++) {
		        			System.out.println("adding");
		        			ArrayList<Integer> valueToCheck = new ArrayList<>();
		        			valueToCheck.add(value.get(i));
		        			System.out.println("is this in x " + value.get(i));
		        			System.out.println(x);
		        			if (doesItContain(x, valueToCheck) == false) {	//trying to account for duplicates 
		        				System.out.println("here");
		        				x.add(value.get(i));
		        			}
		        	  	}
		        }
			} 
			
		}
		return x;		
	}
	
	//checks if two (which should be only one element after splitting rule) is in one
	public static boolean doesItContain(ArrayList<Integer> one, ArrayList<Integer> two){
		for (int i = 0; i < one.size(); i++) {
			if (one.get(i) == two.get(0)) return true;
		}
		return false;
	}
	
	//checks equality of two ALs
	public static boolean isTwoArrayListsWithSameValues(ArrayList<Integer> x, ArrayList<Integer> xOld)
    {
        //null checking
        if(x==null && xOld==null)
            return true;
        if((x == null && xOld != null) || (x != null && xOld == null))
            return false;

        if(x.size()!=xOld.size())
            return false;
        for(Object itemList1: x)
        {
            if(!xOld.contains(itemList1))
                return false;
        }

        return true;
    }

	// returns a set of all subsets of the attributes of R
	// you need this method to compute the keys of R (later part of the algorithm)
	public static ArrayList<Set<Integer>> subsets(Set<Integer> attributes) {
	    ArrayList<Set<Integer>> sets = new ArrayList<Set<Integer>>();
	    if (attributes.isEmpty()) {
	        sets.add(new HashSet<Integer>());
	        return sets;
	    }
	    ArrayList<Integer> list = new ArrayList<Integer>(attributes);
	    int head = list.get(0);
	    Set<Integer> rest = new HashSet<Integer>(list.subList(1, list.size())); 
	    for (Set<Integer> set : subsets(rest)) {
	        Set<Integer> newSet = new HashSet<Integer>();
	        newSet.add(head);
	        newSet.addAll(set);
	        sets.add(newSet);
	        sets.add(set);
	    }       
	    return sets;
	}

	// find the superkeys by computing the closure of all the subsets of the attributes
	// the subsets whose closures are equal to all the attributes are superkeys
	public static HashMap<ArrayList<Integer>, ArrayList<Integer>> findSuperkeys(
			HashMap<ArrayList<Integer>, ArrayList<Integer>> fds, ArrayList<Integer> attributes) {
		return null;
	}
}
