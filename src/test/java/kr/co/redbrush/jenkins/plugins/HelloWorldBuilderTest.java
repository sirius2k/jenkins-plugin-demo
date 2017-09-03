package kr.co.redbrush.jenkins.plugins;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.PrintStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
public class HelloWorldBuilderTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private StaplerRequest staplerRequest;

    @Mock
    private TaskListener taskListener;

    @Mock
    private PrintStream printStream;

    private String builderName = "Builder Name";
    private String displayName = "Say hello world";

    @Test
    public void testNameAndDisplayName() {
        String builderName = "Builder Name";
        HelloWorldBuilder builder = new HelloWorldBuilder(builderName);

        assertThat("Invalid builder name", builder.getName(), is(builderName));
        assertThat("Invalid builder display name", builder.getDescriptor().getDisplayName(), is(displayName));
    }

    @Test
    public void testUseFrenchConfiguration() throws Exception {
        testUseFrenchConfiguration(true);
        testUseFrenchConfiguration(false);
    }

    private void testUseFrenchConfiguration(boolean useFrench) throws Descriptor.FormException {
        Run build  = null;
        FilePath workspace = null;
        Launcher launcher = null;

        when(taskListener.getLogger()).thenReturn(printStream);

        JSONObject formData = new JSONObject();
        formData.put("useFrench", useFrench);

        HelloWorldBuilder builder = new HelloWorldBuilder(builderName);
        builder.getDescriptor().configure(staplerRequest, formData);
        builder.perform(build, workspace, launcher, taskListener);

        LOGGER.info("builder.getDescriptor.getUseFrench() : {}, useFrench : {}", builder.getDescriptor().getUseFrench(), useFrench);

        assertThat("useFrench value is not valid.", builder.getDescriptor().getUseFrench(), is(useFrench));

        if (useFrench) {
            verify(printStream).println("Bonjour, " + builder.getName() + "!");
        } else {
            verify(printStream).println("Hello, " + builder.getName() + "!");
        }
    }
}
