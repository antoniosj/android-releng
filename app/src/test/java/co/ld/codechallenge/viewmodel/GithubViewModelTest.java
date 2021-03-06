package co.ld.codechallenge.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveDataReactiveStreams;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GithubViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    GithubViewModel vm;

    @Before
    public void setUp() {
        vm = new GithubViewModel();
    }

    @Test
    public void getRepos() {
        vm.getRepos("javascript")
                .observe(mockLifecycleOwner(), listResponse -> assertNotNull(listResponse));
    }

    @Test
    public void getRepoRx() {
        LiveDataReactiveStreams.fromPublisher(vm.with(mockLifecycleOwner())
                .getRepoRx("javascript")).observeForever(
                listResponse -> assertNotNull(listResponse)
        );
    }

    @Test
    public void with() {
        assertNotNull(vm.with(mockLifecycleOwner()));
    }

    private static LifecycleOwner mockLifecycleOwner() {
        LifecycleOwner lcOwner = mock(LifecycleOwner.class);
        LifecycleRegistry lcRegistry = new LifecycleRegistry(lcOwner);
        lcRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        when(lcOwner.getLifecycle()).thenReturn(lcRegistry);
        return lcOwner;
    }
}