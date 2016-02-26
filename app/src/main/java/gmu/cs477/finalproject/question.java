package gmu.cs477.finalproject;

/**
 * Created by Corbin on 12/6/2015.
 */

//Question class to store the information about the question
public class question {
    String questiontext;
    String nfcanswer;
    boolean answered;

    question(String questiontext,String nfcanswer){
        this.questiontext=questiontext;
        this.nfcanswer=nfcanswer;
        answered=false;
    }

    public String getQuestiontext() {
        return questiontext;
    }

    public String getNfcanswer() {
        return nfcanswer;
    }

    public void setAnswered(){
        answered=true;
    }
    public boolean isAnswered(){
        return answered;
    }
}
