package kr.co.redbrush.jenkins.plugins;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by kwpark on 26/08/2017.
 */
@Slf4j
public class AppTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    @Test
    public void testShell() throws Exception {
        String echoHello = "echo hello";
        Shell shell = new Shell(echoHello);

        FreeStyleProject project = jenkinsRule.createFreeStyleProject();
        project.getBuildersList().add(shell);

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        String log = FileUtils.readFileToString(build.getLogFile());

        LOGGER.info("{} completed.", build.getDisplayName());
        LOGGER.info("Log\n{}", log);

        assertThat("Log is not containing echo String", log, containsString("+ echo hello"));
    }
}
