package doit.study.dodroid;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;


class QuestionsPagerAdapter extends FragmentStatePagerAdapter {
    private final String LOG_TAG = "NSA " + getClass().getName();
    private QuizData mQuizData;


    public QuestionsPagerAdapter(FragmentManager fm, QuizData quizData) {
        super(fm);
        this.mQuizData = quizData;
    }


    @Override
    public Fragment getItem(int position) {
        Log.i(LOG_TAG, "getItem, pos=" + position);
        return QuestionFragment.newInstance(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        Log.i(LOG_TAG, "instantiateItem, pos="+position);
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return mQuizData.size();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        for (String tag: mQuizData.getById(position).getTags())
            title += tag+" ";
        return title;
    }

    // FIXME: insufficient stub, to reload all cached views
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
