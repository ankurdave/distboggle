import java.util.Comparator;

public class ByStringLength implements Comparator {
	/**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
	public int compare(Object o1, Object o2) {
		String s1 = (String) o1;
		String s2 = (String) o2;
		if (s1.length() > s2.length())
			return 1;
		else if (s1.length() < s2.length())
			return -1;
		else
			return s1.compareTo(s2);
	}
}
