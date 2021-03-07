package co.ld.codechallenge;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;


import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

import com.android.dx.command.Main;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import co.ld.codechallenge.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import co.ld.codechallenge.network.NetworkManager;
import co.ld.codechallenge.util.EspressoIdlingResource;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToHolder;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;


public class FlowTest {

    MockWebServer mockWebServer;
    Retrofit retrofit;

    @Before
    public void setUp() {
        EspressoIdlingResource.setDefaultIdlingResource();
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());

        mockWebServer = new MockWebServer();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(getGsonCoverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();

        NetworkManager.getInstance().setExecutor(retrofit);
    }

    @After
    public void tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void testAppContext() {
        Context context = getInstrumentation().getTargetContext();
        assertEquals("co.ld.codechallenge", context.getPackageName());
    }


    public void setResponse(boolean shouldLoadItems) {
        if (shouldLoadItems) {
            mockWebServer.setDispatcher(new Dispatcher() {
                @Override
                public MockResponse dispatch(RecordedRequest request) {
                    return new MockResponse().setResponseCode(HttpURLConnection.HTTP_OK)
                            .setBody(getJsonFile());
                }
            });
        } else {
            mockWebServer.setDispatcher(new Dispatcher() {
                @Override
                public MockResponse dispatch(RecordedRequest request) {
                    return new MockResponse()
                            .throttleBody(1024, 10, TimeUnit.SECONDS);
                }
            });
        }
    }

    @Test
    public void testDetails() {
        ActivityScenario.launch(MainActivity.class);

        setResponse(true);

        onView(withId(R.id.repo_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.repo_list)).perform(click());
        onView(withId(R.id.name))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.desc))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.url))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.username))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.stars))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testSuccessfulResponse() {
        ActivityScenario.launch(MainActivity.class);
        setResponse(true);
        onView(withId(R.id.repo_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testFailedResponse() {
        ActivityScenario.launch(MainActivity.class);
        setResponse(false);
        onView(withId(R.id.repo_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void testDeviceRotation() {
        ActivityScenario.launch(MainActivity.class);
        setResponse(true);

        onView(withId(R.id.repo_list)).perform(scrollToPosition(20));
        EspressoIdlingResource.increment();

        UiDevice device = UiDevice.getInstance(getInstrumentation());

        try {
            device.setOrientationRight();
            device.waitForIdle(1000);

        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private GsonConverterFactory getGsonCoverter() {
        return GsonConverterFactory.create(
                new GsonBuilder()
                        .enableComplexMapKeySerialization()
                        .setLenient()
                        .create()
        );
    }

    private String getJsonFile() {
        InputStream is = getInstrumentation()
                .getTargetContext().getResources().openRawResource(R.raw.mockedjson);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
            is.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writer.toString();
    }
}
