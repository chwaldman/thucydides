package net.thucydides.core.steps;


import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.model.TestResult;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Represents a class interested in knowing about test execution flow and results.
 */
public interface StepListener {

    /**
     * Start a test case made up of several test steps.
     */
    void testStarted(final ExecutedStepDescription description);

    /**
     * Start a test run using a non-java test, e.g. an easyb story.
     *
     * @param description
     */
    void testRunStarted(final String description);

    /**
     * Called when all tests have finished.
     *
     * @param result the summary of the test run, including all the tests that failed
     */
    void testFinished(TestStepResult result);

    /**
     * Called when a test step is about to be started.
     *
     * @param description the description of the test that is about to be run
     *                    (generally a class and method name)
     */
    void stepStarted(ExecutedStepDescription description);

    /**
     * Called when an test step has finished, whether the test succeeds or fails.
     *
     * @param description the description of the test that just ran
     */
    void stepFinished(ExecutedStepDescription description);

    /**
     * Called when a test step group without a backing Java method (e.g. an easyb story) is about to be started.
     *
     * @param description the description of the test group that is about to be run
     */
    void stepGroupStarted(String description);

    /**
     * Called when a test step group is about to be started.
     *
     * @param description the description of the test group that is about to be run
     */
    void stepGroupStarted(ExecutedStepDescription description);

    /**
     * Called when an test step group has finished.
     */
    void stepGroupFinished();

    /**
     * Finish a test step group and define an overall default result.
     */
    void stepGroupFinished(TestResult result);

    /**
     * Mark a test step as having succeeded.
     */
    void stepSucceeded();

    /**
     * Called when a test step fails.
     *
     * @param failure describes the test that failed and the exception that was thrown
     */
    void stepFailed(StepFailure failure);

    /**
     * Called when a step will not be run, generally because a test method is annotated
     * with {@link org.junit.Ignore}.
     *
     * @param description describes the test that will not be run
     */
    void stepIgnored(ExecutedStepDescription description);

    /**
     * A step listener should be able to return a set of test results at the end of the test run.
     */
    List<AcceptanceTestRun> getTestRunResults();

    /**
     * Update the status of the current step (e.g to IGNORED or SKIPPED) without changing anything else.
     */
    void updateCurrentStepStatus(TestResult result);

    /**
     * Used to update the webdriver driver for screenshots if a listener is reused between scenarios.
     */
    public void setDriver(final WebDriver driver);

    /**
     * The currently-used WebDriver instance for these tests.
     */
    WebDriver getDriver();

    /**
     *  Should return true if a step failure has been logged.
     *  We need to share this information if multiple step libraries are used.
     */
    boolean aStepHasFailed();

    /**
     * Reset the step failure flag for a new test.
     */
    void noStepsHaveFailed();

    /**
     * If a step failed, what was the error.
     */
    Throwable getStepError();
}
