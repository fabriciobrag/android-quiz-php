package com.quiz.php.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.quiz.php.R;
import com.quiz.php.core.Alternative;
import com.quiz.php.core.Category;
import com.quiz.php.core.Question;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by fabricio on 2/17/14.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 26;

    private static final String NAME = "quiz.db";
    private static final String TAG = "dbhelper";

    private final SQLiteDatabase mReadDB;

    Context mContext;

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
        mContext = context;
        mReadDB = getReadableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE categories (id INTEGER PRIMARY KEY, category TEXT)");
        db.execSQL("CREATE TABLE questions (id INTEGER PRIMARY KEY, type_question INTEGER, question TEXT, answer TEXT, category_id INTEGER REFERENCES category(id) )");
        db.execSQL("CREATE TABLE alternatives (id INTEGER PRIMARY KEY, question_id INTEGER REFERENCES question(id), alternative TEXT, is_correct INTEGER)");
        loadCategoriesFromJSON(db);
        loadQuestionsFromJSON(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS categories");
        db.execSQL("DROP TABLE IF EXISTS alternatives");
        db.execSQL("DROP TABLE IF EXISTS questions;");
        onCreate(db);
    }

    private void loadCategoriesFromJSON(SQLiteDatabase db) {
        InputStream inputStream = mContext.getResources().openRawResource(R.raw.categories);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int crt;

        try {
            crt = inputStream.read();
            while (crt != -1) {
                output.write(crt);
                crt = inputStream.read();
            }
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            JSONArray categories = new JSONArray(output.toString());
            for (int i = 0; i < categories.length(); i++) {
                Category c = new Category();
                c.setId(categories.getJSONObject(i).getInt("id"));
                c.setCategory(categories.getJSONObject(i).getString("category"));
                insertCategory(db, c);
                Log.d(TAG, c.toString());
            }

        }catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parser error json categories" + e.getMessage());
        }
    }

    private void loadQuestionsFromJSON(SQLiteDatabase db) {

        InputStream inputStream = mContext.getResources().openRawResource(R.raw.questions);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int crt;

        try {
            crt = inputStream.read();
            while (crt != -1) {
                output.write(crt);
                crt = inputStream.read();
            }
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            JSONArray jsonQuestions = new JSONArray(output.toString());
            for (int i = 0; i < jsonQuestions.length(); i++) {
                Question q = new Question();
                int type = jsonQuestions.getJSONObject(i).getInt("type_question");

                q.setId(jsonQuestions.getJSONObject(i).getInt("id"));
                q.setType(type);
                q.setQuestion(jsonQuestions.getJSONObject(i).getString("question"));
                q.setCategory(getCategoryById(db, jsonQuestions.getJSONObject(i).getInt("id_category")));

                if (type == 2) {
                    q.setAnswer(jsonQuestions.getJSONObject(i).getString("answer"));

                } else {

                    //alternatives
                    JSONArray altArray = jsonQuestions.getJSONObject(i).getJSONArray("alternatives");
                    ArrayList<Alternative> alternatives = new ArrayList<Alternative>();
                    for (int j = 0; j < altArray.length(); j++) {
                        Alternative a = new Alternative();
                        a.setAlternative(altArray.getJSONObject(j).getString("alternative"));
                        a.setId(altArray.getJSONObject(j).getInt("id"));
                        a.setCorrect(altArray.getJSONObject(j).getBoolean("is_correct"));
                        alternatives.add(a);
                    }
                    q.setAlternatives(alternatives);
                }

                insertQuestion(db, q);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error parser json questions" + e.getMessage());
        }
    }

    public void insertQuestion (SQLiteDatabase db, Question question) {
        ContentValues cv = new ContentValues();
        cv.put("id", question.getId());
        cv.put("question", question.getQuestion());
        cv.put("type_question", question.getType());

        Log.d("Sqlite", question.toString());

        if (question.getCategory() != null) {
            cv.put("category_id", question.getCategory().getId());
        }

        if (question.getType() == 2) {
            cv.put("answer", question.getAnswer());
        } else {

            ArrayList<Alternative> alternatives = question.getAlternatives();
            for (Alternative alt : alternatives) {
                insertAlternative(db, alt, question.getId());
            }
        }

        db.insert("questions", null, cv);
    }


    public void insertAlternative (SQLiteDatabase db, Alternative alternative, int idQuestion) {
        ContentValues cv = new ContentValues();
        cv.put("id", alternative.getId());
        cv.put("alternative", alternative.getAlternative());
        cv.put("question_id", idQuestion);
        cv.put("is_correct", ( alternative.isCorrect() ? 1 : 0) );

        db.insert("alternatives", null, cv);

    }

    public void insertCategory (SQLiteDatabase db, Category category) {
        ContentValues cv = new ContentValues();
        cv.put("id", category.getId());
        cv.put("category", category.getCategory());

        db.insert("categories", null, cv);
    }


    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<Category>();

        Cursor c = mReadDB.rawQuery("SELECT id, category FROM categories", null);

        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt(0));
                category.setCategory(c.getString(1));
                categories.add(category);

            } while (c.moveToNext());
        }

        return categories;
    }



    public ArrayList<Question> getRandomQuestions (int limit, String type) {
        ArrayList<Question> questions = new ArrayList<Question>();


        String query = "SELECT id, question, category_id, type_question, answer FROM questions ";

        if (!type.equals("0"))
            query += String.format(" WHERE type_question = '%s' AND id = 103", type);



        query += " ORDER BY RANDOM() LIMIT " + limit;

        Cursor c = mReadDB.rawQuery(query, null);

//        Cursor c = mReadDB.query(
//                "questions ORDER BY RANDOM()" /* table */,
//                new String [] {"id", "question", "category_id", "type_question", "answer"} /* columns */,
//                type.equals("0") ? null : "type_question = '" + type = /* where or selection */,
//                null /* selectionArgs i.e. value to replace ? */,
//                null /* groupBy */,
//                null /* having */,
//                null /* orderBy */,
//                String.valueOf(limit) /* limit */
//        );

        if (c.moveToFirst()) {
            do {


                Question q = new Question();
                int id = c.getInt(0);
                q.setId(id);
                q.setQuestion(c.getString(1));
                q.setCategory(getCategoryById(mReadDB, c.getInt(2)));

                int questionType = c.getInt(3);
                q.setType(questionType);

                if (questionType == 2) {
                    q.setAnswer(c.getString(4));
                } else {
                    q.setAlternatives(getAlternativesByQuestionID(id));
                }

                questions.add(q);

            } while (c.moveToNext());
        }
        return questions;
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questions = new ArrayList<Question>();

        Cursor c = mReadDB.rawQuery(
                "SELECT id, question, category_id, type_question, answer FROM questions", null);

        if (c.moveToFirst()) {
            do {
                Question q = new Question();
                int id = c.getInt(0);
                q.setId(id);
                q.setQuestion(c.getString(1));
                q.setCategory(getCategoryById(mReadDB, c.getInt(2)));

                int type = c.getInt(3);
                q.setType(type);

                if (type == 2) {
                    q.setAnswer(c.getString(4));
                } else {
                    q.setAlternatives(getAlternativesByQuestionID(id));
                }

                questions.add(q);

            } while (c.moveToNext());
        }
        return questions;
    }


    public Category getCategoryById(SQLiteDatabase db, int categoryId) {
        Cursor c = db.rawQuery("SELECT id, category FROM categories WHERE id = ?",
                new String[] {String.valueOf(categoryId)});

        if (c.moveToFirst()) {

            Category category = new Category();
            category.setId(c.getInt(0));
            category.setCategory(c.getString(1));

            return category;
        }
        return null;
    }

    public ArrayList<Alternative> getAlternativesByQuestionID (int questionID) {
        ArrayList<Alternative> alternatives = new ArrayList<Alternative>();

        Cursor c = mReadDB.query(
                "alternatives" /* table */,
                new String [] {"id", "alternative", "is_correct"} /* columns */,
                "question_id = ?" /* where or selection */,
                new String[] { String.valueOf(questionID) } /* selectionArgs i.e. value to replace ? */,
                null /* groupBy */,
                null /* having */,
                null /* orderBy */
        );

        if (c.moveToFirst()) {
            do {
                Alternative a = new Alternative();
                a.setId(c.getInt(0));
                a.setAlternative(c.getString(1));
                a.setCorrect( c.getInt(2) != 0 );

                alternatives.add(a);

            } while (c.moveToNext());
        }
        return alternatives;
    }
}
