package tcp;

import java.io.Serializable;


public class Survey implements Serializable {
	protected static final long serialVersionUID = 1589985257L;

	
	private final String question1 = "Was the teacher welcoming?";
	private String answer1;
	private final String question2 = "Would you recommend?";
	private String answer2;
	private String commentBox;
	private int surveyID;
	
	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	
	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
	
	public void commentBox(String commentBox) {
		this.commentBox = commentBox;
	}
	
	public String createSurvey() {
		StringBuilder sb = new StringBuilder();
		sb.append(question1 + "\n");
		sb.append(answer1 + "\n\n");
		sb.append(question2 + "\n");
		sb.append(answer2 + "\n\n");
		sb.append(commentBox);
		return sb.toString();
	}
}
