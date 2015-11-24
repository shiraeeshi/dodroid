package doit.study.dodroid;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;


public class QuestionFragment extends LifecycleLoggingFragment implements View.OnClickListener {
    private final String LOG_TAG = "NSA " + getClass().getName();
    private static final boolean DEBUG = true;
    private OnStatisticChangeListener mCallback;
    // keys for bundle, to save state
    private static final String ID_KEY = "doit.study.dodroid.id_kye";
    // model stuff
    private Question mCurrentQuestion;
    private QuizData mQuizData;
    private int mPosition;
    // view stuff
    private View mView;
    private ArrayList<CheckBox> mvCheckBoxes;
    private Button mvCommitButton;
    private TextView mvQuestionText;
    private LinearLayout mvAnswersLayout;
    private TextView mvCurrentQuestionNum;
    private TextView mvTotalQuestionNum;
    private TextView mvRight;
    private TextView mvWrong;


    // Factory method
    public static QuestionFragment newInstance(int position) {
        if (DEBUG) Log.i("NSA", "newInstance "+position);
        // add Bundle args if needed here before returning new instance of this class
        QuestionFragment fragment = new QuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ID_KEY, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    // Container Activity must implement this interface
    public interface OnStatisticChangeListener {
        void onStatisticChanged();
    }

    @Override
    public void onAttach(Context activity){
        // for logging purpose
        ID = ((Integer) getArguments().getInt(ID_KEY)).toString();
        super.onAttach(activity);
        try {
            mCallback = (OnStatisticChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnStatisticChangeListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mPosition = getArguments().getInt(ID_KEY);
        mQuizData = ((GlobalData)getActivity().getApplication()).getQuizData();
        mCurrentQuestion = mQuizData.getById(mPosition);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mkViewsLinks(inflater, container);
        updateModel();
        updateView();
        populate();
        if (savedInstanceState != null)
            mvCommitButton.setEnabled((savedInstanceState.getInt("COMMIT_BUTTON_ENABLED") != -1));
        return mView;
    }

    private void mkViewsLinks(LayoutInflater inflater, ViewGroup container){
        if (DEBUG) Log.i(LOG_TAG, "mkViewsLinks "+ID);
        // You can not use the findViewById method the way you can in an Activity in a Fragment
        // So we get a reference to the view/layout_file that we used for this Fragment
        // That allows use to then reference the views by id in that file
        mView = inflater.inflate(R.layout.fragment_questions, container, false);
        mvQuestionText = (TextView) mView.findViewById(R.id.question);
        mvAnswersLayout = (LinearLayout) mView.findViewById(R.id.answers);
        mvCommitButton = (Button) mView.findViewById(R.id.commit_button);
        // You can not add onclick listener to a button in a fragment's xml
        // So we implement OnClickListener interface, check onClick() method
        mvCommitButton.setOnClickListener(this);
        mvRight = (TextView) mView.findViewById(R.id.right_counter);
        mvWrong = (TextView) mView.findViewById(R.id.wrong_counter);
        mvCurrentQuestionNum = (TextView) mView.findViewById(R.id.current_question_num);
        mvTotalQuestionNum = (TextView) mView.findViewById(R.id.total_question_num);
    }

    private void updateView(){

    }

    private void updateModel(){

    }

    // Map data from the current Question to the View elements
    public void populate() {
        if (DEBUG) Log.i(LOG_TAG, "populate "+ID);
        mvQuestionText.setText(mCurrentQuestion.getText());
        mvCurrentQuestionNum.setText("" + (mPosition+1));
        mvTotalQuestionNum.setText("/" + (mQuizData.size()-1));
        mvRight.setText("" + mQuizData.getTotalRightCounter());
        mvRight.setTextColor(Color.GREEN);
        mvWrong.setText(" " + mQuizData.getTotalWrongCounter());
        mvWrong.setTextColor(Color.RED);

        mvAnswersLayout.removeAllViewsInLayout();
        ArrayList<String> allAnswers = new ArrayList<>();
        allAnswers.addAll(mCurrentQuestion.getRightItems());
        allAnswers.addAll(mCurrentQuestion.getWrongItems());
        Collections.shuffle(allAnswers);

        mvCheckBoxes = new ArrayList<>();
        // create checkboxes dynamically
        for (int i = 0; i < allAnswers.size(); i++) {
            // Can not use "this" keyword for constructor here. Requires a Context and Fragment class does not inherit from Context
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(allAnswers.get(i));
            mvAnswersLayout.addView(checkBox);
            mvCheckBoxes.add(checkBox);
        }
    }


    public Boolean checkAnswers() {
        if (DEBUG) Log.i(LOG_TAG, "checkAnswers "+ID);
        Boolean goodJob = true;
        for (CheckBox cb : mvCheckBoxes) {
            // you can have multiple right answers
            String cbText = cb.getText().toString();
            if (cb.isChecked()) {
                if (!mCurrentQuestion.getRightItems().contains(cbText)) {
                    goodJob = false;
                    break;
                }
            } else if (mCurrentQuestion.getRightItems().contains(cbText)) {
                goodJob = false;
                break;
            }
        }
        Toast toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (goodJob) {
            toast.setText("Right");
            v.setTextColor(Color.GREEN);
            mvCommitButton.setBackgroundColor(0xFF00FF00); // => green color
            mQuizData.incrementRightCounter(mPosition);
            mvRight.setText("" + mQuizData.getTotalRightCounter());
            // FIXME: doesn't work with POSITION_NONE
            mvCommitButton.setEnabled(false);
            getArguments().putInt("COMMIT_BUTTON_ENABLED", -1);
        }
        else {
            toast.setText("Wrong");
            mvCommitButton.setBackgroundColor(Color.RED);
            v.setTextColor(Color.RED);
            mQuizData.incrementWrongCounter(mPosition);
            mvWrong.setText("/"+mQuizData.getTotalWrongCounter());

        }
        toast.show();

        mCallback.onStatisticChanged();
        return goodJob;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.commit_button):
                checkAnswers();
                break;
        }
    }
}
