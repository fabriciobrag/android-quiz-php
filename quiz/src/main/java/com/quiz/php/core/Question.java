package com.quiz.php.core;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by fabricio on 1/29/14.
 */
public class Question {

    private int id;

    /**
     * Type of question
     * 1 - single-choice
     * 2 - text
     * 3 - multi-choices
     */
    private int type;

    private String question;
    private String answer;
    private String inputAnswer;

    private Category category;
    private ArrayList<Alternative> alternatives;
    private boolean isAnswered;

    @Override
    public String toString() {

        return String.format("\n Question: %s \n Is correct: %s \n Type: %s \n isAnswered: %s" ,
                getId(), isCorrect(), type, isAnswered);
    }

    public boolean isAnswered() {
        return isAnswered;
    }
    public void setAnswered(boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Alternative> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(ArrayList<Alternative> alternatives) {
        this.alternatives = alternatives;
    }
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    private String filterAnswer(String answer){

        String s = "";
        if (answer != null) {
            if (answer.startsWith("_")) {
                return answer;
            }
            s =  answer.substring(0, answer.indexOf("(") + 1).toLowerCase();
        }

        return s;
    }

    public boolean isCorrect() {
        switch (type) {

            case 1: //single choice
                for (Alternative a : alternatives) {
                    if (a.isSelected() && a.isCorrect() ) {
                        return  true;
                    }
                }
                break;

            case 2: //text
                String input = filterAnswer(getInputAnswer());

                Log.d("answer", String.format("Answer %s - %s", input, filterAnswer(getAnswer())));
                if (!input.isEmpty()  && filterAnswer(getAnswer()).equals(input)){
                    return true;
                }
                break;

            case 3: //multi
                int corrects = 0;
                for (Alternative a : alternatives) {
                    if (a.isSelected() && a.isCorrect()) {
                        corrects += 1;
                    }
                }
                return corrects == getNumOfCorrectOptions();
        }
        return false;
    }

    /**
     * Return number of correct options, for multi choice questions
     * @return int
     */
    public int getNumOfCorrectOptions() {
        int corrects = 0;
        for (Alternative a : alternatives) {
            if (a.isCorrect() ) {
                corrects += 1;
            }
        }
        return corrects;
    }

    /**
     * Return number of selected questions
     * @return int
     */
    public int getNumOfChecked() {
        int answered = 0;
        for (Alternative a : alternatives){
            if (a.isSelected()) {
                answered += 1;
            }
        }
        return  answered;
    }

    public int getRemaining(){
        int remaining = getNumOfCorrectOptions() - getNumOfChecked();

        return remaining > 0 ? remaining : 0;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getInputAnswer() {
        return inputAnswer;
    }

    public void setInputAnswer(String inputAnswer) {
        this.inputAnswer = inputAnswer;

        isAnswered = inputAnswer != null && !inputAnswer.isEmpty();
    }


}
