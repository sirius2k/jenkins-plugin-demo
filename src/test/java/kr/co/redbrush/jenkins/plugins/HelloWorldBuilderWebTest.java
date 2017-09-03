package kr.co.redbrush.jenkins.plugins;

import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class HelloWorldBuilderWebTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    private String builderName = "Builder Name";

    @Test
    public void testHelloWorldBuilderExistsOnConfigurePage() throws Exception {
        HtmlPage page = jenkinsRule.createWebClient().goTo("configure");

        LOGGER.info("Configure Page\n{}", page.getWebResponse().getContentAsString());

        WebAssert.assertTextPresent(page, "Hello World Builder");
        WebAssert.assertTextPresent(page, "French");
        WebAssert.assertTextPresent(page, "Check if we should say hello in French");
        WebAssert.assertInputPresent(page, "_.useFrench");
        WebAssert.assertInputDoesNotContainValue(page, "_.useFrench", "");
    }

    @Test
    public void testSetupHelloWorldBuilderOnConfigurePage() throws Exception {
        boolean useFrench = true;
        setupUseFrenchOnConfigurePage(useFrench);
        assertUseFrenchChecked(useFrench);

        useFrench = false;
        setupUseFrenchOnConfigurePage(useFrench);
        assertUseFrenchChecked(useFrench);
    }

    private void setupUseFrenchOnConfigurePage(boolean useFrench) throws Exception {
        HtmlPage page = jenkinsRule.createWebClient().goTo("configure");
        HtmlForm form = page.getFormByName("config");
        HtmlInput useFrenchInput = form.getInputByName("_.useFrench");

        useFrenchInput.setChecked(useFrench);

        jenkinsRule.submit(form);
    }

    private void assertUseFrenchChecked(boolean useFrench) throws IOException, SAXException {
        HtmlPage page = jenkinsRule.createWebClient().goTo("configure");
        HtmlForm form = page.getFormByName("config");
        HtmlInput useFrenchInput = form.getInputByName("_.useFrench");

        LOGGER.info("useFrenchInput checked : {}, useFrench : {}", useFrenchInput.isChecked(), useFrench);

        assertThat("useFrench was not configured correctly.", useFrenchInput.isChecked(), is(useFrench));
    }

    @Test
    public void testSetupHelloWorldBuilderOnProjectConfigurePage() throws Exception {
        String newName = "New Name";
        FreeStyleProject project = createFreeStyleProjectWithHelloWorldBuilder();
        HelloWorldBuilder builderBefore = project.getBuildersList().get(HelloWorldBuilder.class);

        // go to "/your_project/configure"
        HtmlPage page = jenkinsRule.createWebClient().getPage(project, "configure");

        LOGGER.info("Project Configure Page\n{}", page.getWebResponse().getContentAsString());

        WebAssert.assertTextPresent(page, "Say hello world");

        HtmlForm configForm = page.getFormByName("config");
        HtmlInput nameInput = configForm.getInputByName("_.name");
        nameInput.setValueAttribute(newName);

        jenkinsRule.submit(configForm);

        HelloWorldBuilder builderAfter = project.getBuildersList().get(HelloWorldBuilder.class);

        LOGGER.info("builderName before : {}, after : {}, newName : {}", builderBefore.getName(), builderAfter.getName(), newName);

        assertThat("Name is not matched.", builderBefore.getName(), is(builderName));
        assertThat("Name has not changed.", builderAfter.getName(), is(newName));
    }

    private FreeStyleProject createFreeStyleProjectWithHelloWorldBuilder() throws IOException {
        HelloWorldBuilder helloWorldBuilder = new HelloWorldBuilder(builderName);

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.getBuildersList().add(helloWorldBuilder);

        return project;
    }

    @Test
    public void testBuildWithUseFrench() throws Exception {
        boolean useFrench = true;
        setupUseFrenchOnConfigurePage(useFrench);
        buildFreeStyleProjectWithUseFrench(useFrench);

        useFrench = false;
        setupUseFrenchOnConfigurePage(useFrench);
        buildFreeStyleProjectWithUseFrench(useFrench);
    }

    private void buildFreeStyleProjectWithUseFrench(boolean useFrench) throws Exception {
        FreeStyleProject project = createFreeStyleProjectWithHelloWorldBuilder();
        FreeStyleBuild build = project.scheduleBuild2(0).get();
        HelloWorldBuilder builder = project.getBuildersList().get(HelloWorldBuilder.class);
        String log = FileUtils.readFileToString(build.getLogFile());

        LOGGER.info("Build {} completed. name : {}", build.getDisplayName(), builder.getName());
        LOGGER.info("Build Log : \n{}", log);

        if (useFrench) {
            assertThat("Hello Builder was not executed successfully.", log, containsString("Bonjour, " + builder.getName() + "!"));
        } else {
            assertThat("Hello Builder was not executed successfully.", log, containsString("Hello, " + builder.getName() + "!"));
        }
    }
}
