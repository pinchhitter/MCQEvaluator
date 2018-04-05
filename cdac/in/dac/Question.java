package cdac.in.dac;

import java.util.List;
import java.util.ArrayList;

class Response{

	public List<String> responses;

	public Response(String response){

		this.responses = new ArrayList<String>();
		response = response.trim();
		for(int  i= 0; i < response.trim().length(); i++){	
			this.responses.add( response.charAt(i)+"");		
		}	
	}

}

class Answer{

	public List<String> answers;

	public Answer(String answer){
		this.answers = new ArrayList<String>();
		answer = answer.trim();
		for(String ans: answer.split("OR") ){
			this.answers.add( ans.trim() );	
		}
	}	
}

abstract class Question{
		
	public Answer answer;
	public String unattemptCharacter = "-";
	public String DL;

	public boolean isMTA;
	public boolean isMTN;

	public Double marks;
	public Double negative;

	public Integer attempt;
	public Integer correct;
	public Integer wrong;
	public Integer unattempted;

	abstract Double eval(Response response);
	abstract void print(Integer quid);

	static void printHeader(){
		System.out.println("\nID, isMTA, isMTN, Answer, attempt, unattempt, correct, wrong, DifficultyLevel");
		System.out.println("------------------------------------------------------------------------------");
	}
}

class MultipalChoice extends Question{

	private String questionId;
	private String validString1 = "ABCD";
	private String validString2 = "abcd";

	private boolean isValid(Response response){

		if( response.responses.size() == 1){

			if( response.responses.get(0).length() == 1 ){
				if( validString1.indexOf( response.responses.get(0) ) >= 0 || validString2.indexOf( response.responses.get(0) ) >= 0 ){
					return true;
				}
				return false;		
			}else{
				return false;
			}	

		}
	return false;
	}


	public MultipalChoice(Answer answer, Double marks, Double negative){
		this.answer = answer;
		this.marks = marks;
		this.negative = negative;
		this.isMTA = false;
		this.isMTN = false;
		this.correct = 0;
		this.wrong = 0;
		this.unattempted = 0;
		this.attempt = 0;

		for( String an: answer.answers ){
			if( an.equals("MTN") || an.equals("mtn") )
				isMTN = true;
			else if( an.equals("MTA") || an.equals("mta") )
				isMTA = true;
		}
	}
	
	public Double eval(Response response){

		if( this.isMTA ){
			return this.marks;
		}
		else if( this.isMTN ){
			return new Double(0.0);
		}
		if( isValid( response ) ){

			this.attempt++;

			for(String ans: this.answer.answers ){

				if( response.responses.get(0).equalsIgnoreCase( ans ) ){
					this.correct++;
					return this.marks;
				}
			}
			this.wrong++;
			return negative;
		}else if ( response.responses.get(0).equals( unattemptCharacter ) ){
			this.unattempted++;
			return new Double(0.0);
		}	

		return new Double(0.0);
	}


	public void print(Integer quid){

		System.out.print(quid+", "+this.isMTA+", "+this.isMTN+", ");
		int count = 0;
		for(String ans: this.answer.answers){
			count++;
			if(count == 1)	
				System.out.print(ans);
			else
				System.out.print(" OR "+ans);
		}
		System.out.println(", "+this.attempt+", "+this.unattempted+", "+this.correct+", "+this.wrong+", "+this.DL);
	}
}
