package cdac.in.dac;

import java.util.Map;
import java.util.Comparator;
import java.util.TreeMap;


class Candidate{

	String studentId;
	Integer rank;
	Double totalMarks;
	Double scaleMarks;

	Map<Integer,Response> responses;
	Map<Integer,Double> marks;

	public Candidate(String studentId, String response){

		this.studentId = studentId;
		this.rank = -1;
		this.totalMarks = new Double("0.0");
		this.scaleMarks = new Double("0.0");
		response = response.trim();
		this.responses = new TreeMap<Integer, Response>();
		this.marks = new TreeMap<Integer, Double>();

		for(int i = 0; i < response.trim().length(); i++){
			this.responses.put( new Integer(i+1), new Response( response.charAt(i)+"" ) );
			this.marks.put( new Integer(i+1), new Double(0.0));
		}
	}

	static void printHeader(){
		System.out.println("StudentId, TotalMartks, ScaleMarks, Rank");
		System.out.println("----------------------------------------");
	}

	void print(){
		System.out.println(studentId+", "+totalMarks+", "+scaleMarks+", "+rank);
	}
}

class SortbyMarks implements Comparator<Candidate>{
	public int compare(Candidate c1, Candidate c2) {
		if( ( c1.totalMarks - c2.totalMarks )  == 0 )
			return 0;
		else if( ( c1.totalMarks - c2.totalMarks ) > 0 )
			return -1;
		else 
			return 1;
	}
}
