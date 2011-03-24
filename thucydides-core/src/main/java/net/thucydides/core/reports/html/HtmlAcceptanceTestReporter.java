package net.thucydides.core.reports.html;

import static net.thucydides.core.model.ReportNamer.ReportType.HTML;

import java.io.File;
import java.io.IOException;

import net.thucydides.core.model.AcceptanceTestRun;
import net.thucydides.core.reports.AcceptanceTestReporter;

import org.apache.velocity.VelocityContext;

import com.google.common.base.Preconditions;

/**
 * Generates acceptance test results in XML form.
 * 
 */
public class HtmlAcceptanceTestReporter extends HtmlReporter implements AcceptanceTestReporter {

    private static final String DEFAULT_ACCEPTANCE_TEST_REPORT = "velocity/default.vm";
    
    public HtmlAcceptanceTestReporter() {
        setTemplatePath(DEFAULT_ACCEPTANCE_TEST_REPORT);
    }
    /**
     * Generate an XML report for a given test run.
     */
    public File generateReportFor(final AcceptanceTestRun testRun) throws IOException {

        Preconditions.checkNotNull(getOutputDirectory());

        VelocityContext context = new VelocityContext();
        context.put("testrun", testRun);
        String htmlContents = mergeVelocityTemplate(context);

        copyResourcesToOutputDirectory();

        String reportFilename = testRun.getReportName(HTML);
        return writeReportToOutputDirectory(reportFilename, htmlContents);
    }
}