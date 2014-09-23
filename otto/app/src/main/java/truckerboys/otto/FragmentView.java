package truckerboys.otto;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Simon Petersson on 2014-09-23.
 */
public abstract class FragmentView extends Fragment {
    private String pageTitle = "Page Title";
    private int fragmentLayout;

    public FragmentView(String pageTitle, int fragmentLayout){
        setPageTitle(pageTitle);
        this.fragmentLayout = fragmentLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(fragmentLayout, container, false);
    }

    /**
     * @return The title for this page.
     */
    public String getPageTitle(){
        return pageTitle;
    }

    /**
     * Set the title for this page
     * @param pageTitle New title to bet set.
     */
    public void setPageTitle(String pageTitle){
        this.pageTitle = pageTitle;
    }
}
