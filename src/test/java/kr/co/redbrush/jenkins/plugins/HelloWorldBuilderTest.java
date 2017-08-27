package kr.co.redbrush.jenkins.plugins;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.*;
import hudson.model.Descriptor;
import hudson.model.FreeStyleProject;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
public class HelloWorldBuilderTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private StaplerRequest staplerRequest;

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
    public void testUseFrench() throws Exception {
        testUseFrench(true);
        testUseFrench(false);
    }

    private void testUseFrench(boolean useFrench) throws Descriptor.FormException {
        JSONObject formData = new JSONObject();
        formData.put("useFrench", useFrench);

        HelloWorldBuilder builder = new HelloWorldBuilder(builderName);
        builder.getDescriptor().configure(staplerRequest, formData);

        LOGGER.info("builder.getDescriptor.getUseFrench() : {}, useFrench : {}", builder.getDescriptor().getUseFrench(), useFrench);

        assertThat("useFrench value is not valid.", builder.getDescriptor().getUseFrench(), is(useFrench));
    }

    @Test
    public void testConfigurePage() throws Exception {
        HelloWorldBuilder builder = new HelloWorldBuilder(builderName);

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.getBuildersList().add(builder);

        HtmlPage page = jenkinsRule.createWebClient().goTo("configure");

        LOGGER.info("Configure Page\n{}", page.getWebResponse().getContentAsString());

        WebAssert.assertTextPresent(page, "Hello World Builder");
        WebAssert.assertTextPresent(page, "French");
        WebAssert.assertTextPresent(page, "Check if we should say hello in French");
        WebAssert.assertInputPresent(page, "_.useFrench");
        WebAssert.assertInputDoesNotContainValue(page, "_.useFrench", "");
    }

    @Test
    public void testHelloWorldBuilderConfig() throws Exception {
        HelloWorldBuilder builderBefore = new HelloWorldBuilder(builderName);
        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.getBuildersList().add(builderBefore);

        String useFrench = "true";
        HtmlPage page = jenkinsRule.createWebClient().getPage(project, "configure");
        HtmlForm form = page.getFormByName("config");
        for (Object object : page.getByXPath("//input")) {
            LOGGER.info("HtmlElement : {}", object);
        }
        HtmlInput useFrenchInput = (HtmlInput)page.getByXPath("//input");
        useFrenchInput.setValueAttribute(useFrench);
        jenkinsRule.submit(form);

        HelloWorldBuilder builderAfter = project.getBuildersList().get(HelloWorldBuilder.class);
        jenkinsRule.assertEqualBeans(builderBefore, builderAfter, "name");

        assertThat("useFrench value should be " + useFrench, builderAfter.getDescriptor().getUseFrench(), is(Boolean.valueOf(useFrench)));
    }

    /*
    @Test
    public void testUseFrench() throws Exception {
        HelloWorldBuilder builder = new HelloWorldBuilder(builderName);

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.getBuildersList().add(builder);

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        String log = FileUtils.readFileToString(build.getLogFile());

        LOGGER.info("{} completed.", build.getDisplayName());
        LOGGER.info("Log\n{}", log);

        assertThat("Log is not containing echo String", log, containsString("+ echo hello"));
    }
    */
}
