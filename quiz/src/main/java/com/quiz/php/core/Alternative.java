package com.quiz.php.core;

/**
 * Created by fabricio on 2/14/14.
 */
public class Alternative {

    private int id;
    private String alternative;
    private boolean isCorrect;
    private boolean isSelected;

    @Override
    public String toString() {
        return String.format("Value: %s, Correct: %s", getAlternative(), String.valueOf(isCorrect()));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlternative() {
        return alternative;
    }

    public void setAlternative(String value) {
        this.alternative = value;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
