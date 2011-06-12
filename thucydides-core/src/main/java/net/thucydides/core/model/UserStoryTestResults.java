package net.thucydides.core.model;

import ch.lambdaj.function.convert.Converter;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.convert;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static net.thucydides.core.model.ReportNamer.ReportType.ROOT;

/**
 * A collection of test results, corresponding to a the acceptance tests for a single user story.
 * User stories can in turn belong to Features.
 * 
 * @author johnsmart
 * 
 */
public class UserStoryTestResults {

    private List<TestOutcome> testOutcomes;
    
    private final String title;
    
    private final Story userStory;
    
    /**
     * Create a new acceptance test run instance.
     */
    public UserStoryTestResults(final Story userStory) {
        testOutcomes = new ArrayList<TestOutcome>();
        this.title = userStory.getName();
        this.userStory = userStory;
    }

    public Story getUserStory() {
        return userStory;
    }

    public String getReportName(final ReportNamer.ReportType type) {
        ReportNamer reportNamer = new ReportNamer(type);
        return reportNamer.getNormalizedTestNameFor(getUserStory());
    }

    public String getReportName() {
        return getReportName(ROOT);
    }
    
    /**
     * Add a test run result to the aggregate set of results.
     */
    public void recordTestRun(final TestOutcome testOutcome) {
        testOutcomes.add(testOutcome);
    }

    /**
     * How many test runs in total have been recorded.
     *
     */
    public int getTotal() {
       return testOutcomes.size();
    }

    /**
     * How many test cases contain at least one failing test.
     */
    public int getFailureCount() {
        return select(testOutcomes, having(on(TestOutcome.class).isFailure())).size();
    }

    /**
     * How many test cases contain only successful or ignored tests.
     */
    public int getSuccessCount() {
        return select(testOutcomes, having(on(TestOutcome.class).isSuccess())).size();
    }

    public Integer getPendingCount() {
        return select(testOutcomes, having(on(TestOutcome.class).isPending())).size();
    }

    public List<TestOutcome> getTestOutcomes() {
        return ImmutableList.copyOf(testOutcomes);
    }

    public String getTitle() {
        return title;
    }

    private static class ExtractTestResultsConverter implements Converter<TestOutcome, TestResult> {
        public TestResult convert(final TestOutcome step) {
            return step.getResult();
        }
    }

    private List<TestResult> getCurrentTestResults() {
        return convert(getTestOutcomes(), new ExtractTestResultsConverter());
    }

    
    public TestResult getResult() {
        TestResultList testResults = new TestResultList(getCurrentTestResults());
        return testResults.getOverallResult();
    }

    /**
     * Does this set of test results correspond to a specified user story?
     */
    public boolean containsResultsFor(final Story aUserStory) {
        return getUserStory().equals(aUserStory);
    }
}
