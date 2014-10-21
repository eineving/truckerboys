package truckerboys.otto.utils;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class GoogleRequesterHandlerTest extends TestCase {

    @Test
    public void testDoInBackground() throws Exception {
        String response = "";
        try {
            String input = "stoc";
            String request = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + input + "&key=AIzaSyDEzAa31Uxan5k_06udZBkMRkZb1Ju0aSk";
            response = new GoogleRequesterHandler().execute(request).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assert (response).contains("Stockholm");
    }
}