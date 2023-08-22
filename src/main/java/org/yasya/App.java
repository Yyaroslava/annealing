package org.yasya;

public class App {

	public static void main ( String[] args ) {
		//UI.run();
		//System.out.println(Constants.SYLLABUS_CONTENT_JSON);
		var s = new Syllabus();
		var sol = s.new Solution();
		System.out.println(sol.toString());
	}	
}
