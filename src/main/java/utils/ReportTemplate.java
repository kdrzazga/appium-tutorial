package utils;

import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

//Adjusted default report to change the look, but its very messy

/**
 * Reporter that generates a single-page HTML report of the test results.
 */
public class ReportTemplate implements IReporter {
    private static final Logger LOG = Logger.getLogger(ReportTemplate.class);

    protected File outputFile;

    protected String fileOutdir;

    protected PrintWriter writer;

    protected final List<SuiteResult> suiteResults = Lists.newArrayList();

    // Reusable buffer
    private final StringBuilder buffer = new StringBuilder();

    private String fileName = "Appium_Workshop_Test_Report.html";

    private static final String JVM_ARG = "emailable.report2.name";

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }


    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        try {
            writer = createWriter(outputDirectory);
        } catch (IOException e) {
            LOG.error("Unable to create output file", e);
            return;
        }
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }

        writeDocumentStart();
        writeHead();
        writeBody();

        writeDocumentEnd();

        writer.close();

        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy__HH_mm");

        String newName = fileName + "_" + dateFormat.format(Calendar.getInstance().getTime()) + ".html";
        File finalOutputFile = new File(fileOutdir, newName);
        outputFile.renameTo(finalOutputFile);

    }

    protected PrintWriter createWriter(String outdir) throws IOException {

        new File(outdir).mkdirs();
        String jvmArg = System.getProperty(JVM_ARG);
        if (jvmArg != null && !jvmArg.trim().isEmpty()) {
            fileName = jvmArg;
        }

        fileOutdir = outdir;
        outputFile = new File(outdir, fileName);
        return new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
    }

    protected void writeDocumentStart() {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.print("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    }

    protected void writeHead() {
        writer.print("<head>");
        writer.print("<title>TestNG Report</title>");
        writeStylesheet();
        writer.print("</head>");
    }

    protected void writeStylesheet() {
        writer.print("<style type=\"text/css\">");
        writer.print("body {font-family: Calibri}");
        writer.print("table {font-family: Calibri, Arial; margin-bottom:10px; border-collapse:collapse; empty-cells:show}");
        writer.print("th,td {border:1px solid #12ABDB;padding:.25em .5em}");
        writer.print("th {vertical-align:bottom}");
        writer.print("td {vertical-align:top}");
        writer.print("table a {font-weight:bold}");
        writer.print(".pageHeader {background-image: url(https://www.capgemini.com/pl-pl/wp-content/themes/capgemini-komposite/assets/images/logo.svg); background-repeat: no-repeat; background-position: center left 6px; background-size: 6.5em; background-color: #FFFFFF; color: #12ABDB; height: 36px; text-align: center; vertical-align: middle }"); //Added, header
        writer.print(".totalRow {background-color: #12ABDB; color: #FFFFFF;}");
        writer.print(".stripe td {background-color: #f8f8f7}");
        writer.print(".num {text-align:right}");
        writer.print(".passedodd td {background-color: #FFFFFF}"); //changed colour, added ,.skip
        writer.print(".passedeven td,.stripe {background-color: #FFFFFF}"); //changed colour, added ,.stripe .skip
        writer.print(".skippedodd td {background-color: #f8f8f7}"); //changed colour, added ,.skip
        writer.print(".skippedeven td,.stripe {background-color: #f8f8f7}"); //changed colour, added ,.stripe .skip
        writer.print(".failedodd td,.attn {background-color: #EE364D}"); //changed colour
        writer.print(".failedeven td,.stripe .attn {background-color:  #EE364D}"); //changed colour
        writer.print(".stacktrace {white-space:pre;font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #12ABDB}");
        writer.print("</style>");

        //#F8F8F7
    }

    protected void writeBody() {
        writer.print("<body>");
        writeSuiteSummary();
        writeScenarioSummary();
        writeScenarioDetails();
        writer.print("</body>");
    }

    protected void writeDocumentEnd() {
        writer.print("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        NumberFormat decimalFormat = NumberFormat.getNumberInstance();

        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;

        writer.print("<table>");

        //
        writer.print("<tr><th  class=\"pageHeader\" colspan=\"7\">");
        writer.print("TEST REPORT");
        writer.print("</th></tr>");
        //

        writer.print("<tr>");
        writer.print("<th>Test</th>");
        writer.print("<th>Passed</th>");
        writer.print("<th>Skipped</th>");
        writer.print("<th>Failed</th>");
        //writer.print("<th>Time (ms)</th>"); //commented
        //writer.print("<th>Included Groups</th>"); //commented
        //writer.print("<th>Excluded Groups</th>"); //commented
        writer.print("<th>Start Time</th>"); //Added column
        writer.print("<th>End Time</th>"); //Added column
        writer.print("<th>Test Duration</th>"); //Added column


        writer.print("</tr>");

        int testIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writer.print("<tr><th colspan=\"7\" style=\"color:#12ABDB\">");
            writer.print(Utils.escapeHtml(suiteResult.getSuiteName()));
            writer.print("</th></tr>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();
                long duration = testResult.getDuration();

                //
                //ITestContext overview = testResult.
                //


                writer.print("<tr");
                if ((testIndex % 2) == 1) {
                    writer.print(" class=\"stripe\"");
                }
                writer.print(">");

                buffer.setLength(0);
                writeTableData(buffer.append("<a href=\"#t").append(testIndex).append("\"").append(" style=\"color: #262F36\"")    // put " before style
                        .append(">")
                        .append(Utils.escapeHtml(testResult.getTestName()))
                        .append("</a>").toString());
                writeTableData(integerFormat.format(passedTests),
                        (passedTests > 0 ? "num pass" : "num"));
                writeTableData(integerFormat.format(skippedTests),
                        (skippedTests > 0 ? "num skip" : "num"));
                writeTableData(integerFormat.format(failedTests),
                        (failedTests > 0 ? "num attn" : "num"));

                //writeTableData(decimalFormat.format(duration), "num"); //Commented since we removed this column
                //writeTableData(testResult.getIncludedGroups()); //Commented since we removed this column
                //writeTableData(testResult.getExcludedGroups()); //Commented since we removed this column

                //
                SimpleDateFormat summaryFormat = new SimpleDateFormat("kk:mm:ss");
                writeTableData(summaryFormat.format(testResult.getStartDate()), "num");
                writeTableData(summaryFormat.format(testResult.getEndDate()), "num");
                writeTableData(millisToTimeConversion(duration / 1000), "num"); //Changed to hh:mm:ss
                //

                writer.print("</tr>");

                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                totalDuration += duration;

                testIndex++;
            }
        }

        // Print totals if there was more than one test
        if (testIndex > 1) {

            writer.print("<tr class=\"totalRow\">");
            writer.print("<th>TOTAL</th>");
            writeTableHeader(integerFormat.format(totalPassedTests), "num");
            writeTableHeader(integerFormat.format(totalSkippedTests),
                    (totalSkippedTests > 0 ? "num" : "num"));
            writeTableHeader(integerFormat.format(totalFailedTests),
                    (totalFailedTests > 0 ? "num" : "num"));
            //writeTableHeader(decimalFormat.format(totalDuration), "num"); //Commented since we removed this column
            writer.print("<th colspan=\"2\"></th>");
            writeTableData(millisToTimeConversion(totalDuration / 1000), "num"); //Changed to hh:mm:ss
            writer.print("</tr>");
        }

        writer.print("</table>");
    }

    /**
     * Writes a summary of all the test scenarios.
     */
    protected void writeScenarioSummary() {

        writer.print("<br />");

        writer.print("<table id='summary'>");
        writer.print("<thead>");

        writer.print("<tr><th colspan=\"4\">");
        writer.print("Execution Summary");
        writer.print("</th></tr>");

        writer.print("<tr>");
        writer.print("<th>Test Path</th>");
        writer.print("<th>Test Method</th>");
        writer.print("<th>Start Time</th>");
        writer.print("<th>Duration</th>");
        writer.print("</tr>");
        writer.print("</thead>");

        int testIndex = 0;
        int scenarioIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writer.print("<tbody><tr><th colspan=\"4\">");
            writer.print(Utils.escapeHtml(suiteResult.getSuiteName()));
            writer.print("</th></tr></tbody>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.print("<tbody id=\"t");
                writer.print(testIndex);
                writer.print("\">");

                String testName = Utils.escapeHtml(testResult.getTestName());

                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; failed (configuration methods)",
                        testResult.getFailedConfigurationResults(), "failed",
                        scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; failed", testResult.getFailedTestResults(),
                        "failed", scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; skipped (configuration methods)",
                        testResult.getSkippedConfigurationResults(), "skipped",
                        scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; retried",
                        testResult.getSkippedTestResults(), "skipped",
                        scenarioIndex);
                scenarioIndex += writeScenarioSummary(testName
                                + " &#8212; passed", testResult.getPassedTestResults(),
                        "passed", scenarioIndex);

                writer.print("</tbody>");

                testIndex++;
            }
        }

        writer.print("</table>");
    }

    /**
     * Writes the scenario summary for the results of a given state for a single
     * test.
     */
    private int writeScenarioSummary(String description,
                                     List<ClassResult> classResults, String cssClassPrefix,
                                     int startingScenarioIndex) {
        int scenarioCount = 0;
        if (!classResults.isEmpty()) {
            writer.print("<tr><th colspan=\"4\">");
            writer.print(description);
            writer.print("</th></tr>");

            int scenarioIndex = startingScenarioIndex;
            int classIndex = 0;
            for (ClassResult classResult : classResults) {
                String cssClass = cssClassPrefix
                        + ((classIndex % 2) == 0 ? "even" : "odd");

                buffer.setLength(0);

                int scenariosPerClass = 0;
                int methodIndex = 0;
                for (MethodResult methodResult : classResult.getMethodResults()) {
                    List<ITestResult> results = methodResult.getResults();
                    int resultsCount = results.size();
                    assert resultsCount > 0;

                    ITestResult firstResult = results.iterator().next();
                    String methodName = Utils.escapeHtml(firstResult
                            .getMethod().getMethodName());
                    long startms = firstResult.getStartMillis();
                    String duration = millisToTimeConversion((firstResult.getEndMillis() - startms) / 1000);

                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' kk:mm:ss");
                    String start = sdf.format(startms);

                    // The first method per class shares a row with the class
                    // header
                    if (methodIndex > 0) {
                        buffer.append("<tr class=\"").append(cssClass)
                                .append("\">");

                    }

                    // Write the timing information with the first scenario per
                    // method
                    buffer.append("<td><a href=\"#m").append(scenarioIndex).append("\"").append(" style=\"color: #262F36\"")
                            .append(">").append(methodName)
                            .append("</a></td>").append("<td rowspan=\"")
                            .append(resultsCount).append("\">").append(start)
                            .append("</td>").append("<td rowspan=\"")
                            .append(resultsCount).append("\">")
                            .append(duration).append("</td></tr>");
                    scenarioIndex++;

                    // Write the remaining scenarios for the method
                    for (int i = 1; i < resultsCount; i++) {
                        buffer.append("<tr class=\"").append(cssClass)
                                .append("\">").append("<td><a href=\"#m")
                                .append(scenarioIndex).append("\"").append(" style=\"color: #262F36\"").append(">")
                                .append(methodName).append("</a></td></tr>");
                        scenarioIndex++;
                    }

                    scenariosPerClass += resultsCount;
                    methodIndex++;
                }

                // Write the test results for the class
                writer.print("<tr class=\"");
                writer.print(cssClass);
                writer.print("\">");
                writer.print("<td rowspan=\"");
                writer.print(scenariosPerClass);
                writer.print("\">");
                writer.print(Utils.escapeHtml(classResult.getClassName()));
                writer.print("</td>");
                writer.print(buffer);

                classIndex++;
            }
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }

    /**
     * Writes the details for all test scenarios.
     */
    protected void writeScenarioDetails() {
        int scenarioIndex = 0;


        writer.print("<h1>");
        writer.print("Execution details:");
        writer.print("</h1>");
        writer.print("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");


        for (SuiteResult suiteResult : suiteResults) {
            for (TestResult testResult : suiteResult.getTestResults()) {
                writer.print("<h2>");
                writer.print(Utils.escapeHtml(testResult.getTestName()));
                writer.print("</h2>");

                scenarioIndex += writeScenarioDetails(
                        testResult.getFailedConfigurationResults(),
                        scenarioIndex);
                scenarioIndex += writeScenarioDetails(
                        testResult.getFailedTestResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(
                        testResult.getSkippedConfigurationResults(),
                        scenarioIndex);
                scenarioIndex += writeScenarioDetails(
                        testResult.getSkippedTestResults(), scenarioIndex);
                scenarioIndex += writeScenarioDetails(
                        testResult.getPassedTestResults(), scenarioIndex);
            }
        }
    }

    /**
     * Writes the scenario details for the results of a given state for a single
     * test.
     */
    private int writeScenarioDetails(List<ClassResult> classResults,
                                     int startingScenarioIndex) {
        int scenarioIndex = startingScenarioIndex;
        for (ClassResult classResult : classResults) {
            String className = classResult.getClassName();
            for (MethodResult methodResult : classResult.getMethodResults()) {
                List<ITestResult> results = methodResult.getResults();
                assert !results.isEmpty();

                String label = "";
                //Utils.escapeHtml(className + "#" + results.iterator().next().getMethod().getMethodName());   //removed class path
                for (ITestResult result : results) {
                    writeScenario(scenarioIndex, label, result);
                    scenarioIndex++;
                }
            }
        }

        return scenarioIndex - startingScenarioIndex;
    }

    /**
     * Writes the details for an individual test scenario.
     */
    private void writeScenario(int scenarioIndex, String label,
                               ITestResult result) {
        writer.print("<h3 id=\"m");
        writer.print(scenarioIndex);
        writer.print("\">");
        writer.print(label);
        writer.print("</h3>");

        writer.print("<table class=\"result\">");

        // Write test parameters (if any)
        Object[] parameters = result.getParameters();
        int parameterCount = (parameters == null ? 0 : parameters.length);
        if (parameterCount > 0) {
            writer.print("<tr class=\"param\">");
            for (int i = 1; i <= parameterCount; i++) {
                writer.print("<th>Parameter #");
                writer.print(i);
                writer.print("</th>");
            }
            writer.print("</tr><tr class=\"param stripe\">");
            for (Object parameter : parameters) {
                writer.print("<td>");
                writer.print(Utils.escapeHtml(Utils.toString(parameter)));
                writer.print("</td>");
            }
            writer.print("</tr>");
        }

        // Write reporter messages (if any)
        List<String> reporterMessages = Reporter.getOutput(result);
        if (!reporterMessages.isEmpty()) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">Messages</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");
            writeReporterMessages(reporterMessages);
            writer.print("</td></tr>");
        }

        // Write exception (if any)
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            writer.print("<tr><th");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");
            writer.print((result.getStatus() == ITestResult.SUCCESS ? "Expected Exception"
                    : "Exception"));
            writer.print("</th></tr>");

            writer.print("<tr><td");
            if (parameterCount > 1) {
                writer.print(" colspan=\"");
                writer.print(parameterCount);
                writer.print("\"");
            }
            writer.print(">");
            writeStackTrace(throwable);
            writer.print("</td></tr>");
        }

        writer.print("</table>");
        writer.print("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");
    }

    protected void writeReporterMessages(List<String> reporterMessages) {
        writer.print("<div class=\"messages\">");
        Iterator<String> iterator = reporterMessages.iterator();
        assert iterator.hasNext();
        if (Reporter.getEscapeHtml()) {
            writer.print(Utils.escapeHtml(iterator.next()));
        } else {
            writer.print(iterator.next());
        }
        while (iterator.hasNext()) {
            writer.print("<br/>");
            if (Reporter.getEscapeHtml()) {
                writer.print(Utils.escapeHtml(iterator.next()));
            } else {
                writer.print(iterator.next());
            }
        }
        writer.print("</div>");
    }

    protected void writeStackTrace(Throwable throwable) {
        writer.print("<div class=\"stacktrace\">");
        writer.print(Utils.shortStackTrace(throwable, true));
        writer.print("</div>");
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     *
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     *
     * @param tag        the tag name
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"");
            writer.print(cssClasses);
            writer.print("\"");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }

    private String millisToTimeConversion(long seconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int minutes = (int) (seconds / SECONDS_IN_A_MINUTE);
        seconds -= minutes * SECONDS_IN_A_MINUTE;

        int hours = minutes / MINUTES_IN_AN_HOUR;
        minutes -= hours * MINUTES_IN_AN_HOUR;

        return prefixZeroToDigit(hours) + ":" + prefixZeroToDigit(minutes) + ":" + prefixZeroToDigit((int) seconds);
    }

    private String prefixZeroToDigit(int num) {
        int number = num;
        if (number <= 9) {
            String sNumber = "0" + number;
            return sNumber;
        } else
            return "" + number;

    }


    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {
        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in
         * lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                int result = o1.getTestClass().getName()
                        .compareTo(o2.getTestClass().getName());
                if (result == 0) {
                    result = o1.getMethod().getMethodName()
                            .compareTo(o2.getMethod().getMethodName());
                }
                return result;
            }
        };

        private final String testName;
        private final List<ClassResult> failedConfigurationResults;
        private final List<ClassResult> failedTestResults;
        private final List<ClassResult> skippedConfigurationResults;
        private final List<ClassResult> skippedTestResults;
        private final List<ClassResult> passedTestResults;
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final Date startDate; //Added
        private final Date endDate; //Added
        private final long duration;
        private final String includedGroups;
        private final String excludedGroups;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context
                    .getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests()
                    .getAllResults();
            Set<ITestResult> skippedConfigurations = context
                    .getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests()
                    .getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests()
                    .getAllResults();

            failedConfigurationResults = groupResults(failedConfigurations);
            failedTestResults = groupResults(failedTests);
            skippedConfigurationResults = groupResults(skippedConfigurations);
            skippedTestResults = groupResults(skippedTests);
            passedTestResults = groupResults(passedTests);

            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();

            startDate = context.getStartDate(); //Added
            endDate = context.getEndDate(); //Added
            duration = context.getEndDate().getTime()
                    - context.getStartDate().getTime();

            includedGroups = formatGroups(context.getIncludedGroups());
            excludedGroups = formatGroups(context.getExcludedGroups());
        }

        /**
         * Groups test results by method and then by class.
         */
        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                Collections.sort(resultsList, RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName,
                                resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();

                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName,
                        resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }

        /**
         * @return the results for failed configurations (possibly empty)
         */
        public List<ClassResult> getFailedConfigurationResults() {
            return failedConfigurationResults;
        }

        /**
         * @return the results for failed tests (possibly empty)
         */
        public List<ClassResult> getFailedTestResults() {
            return failedTestResults;
        }

        /**
         * @return the results for skipped configurations (possibly empty)
         */
        public List<ClassResult> getSkippedConfigurationResults() {
            return skippedConfigurationResults;
        }

        /**
         * @return the results for skipped tests (possibly empty)
         */
        public List<ClassResult> getSkippedTestResults() {
            return skippedTestResults;
        }

        /**
         * @return the results for passed tests (possibly empty)
         */
        public List<ClassResult> getPassedTestResults() {
            return passedTestResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public Date getStartDate() { //Added getter
            return startDate;
        }

        public Date getEndDate() { //Added getter
            return endDate;
        }

        public long getDuration() {
            return duration;
        }

        public String getIncludedGroups() {
            return includedGroups;
        }

        public String getExcludedGroups() {
            return excludedGroups;
        }

        /**
         * Formats an array of groups for display.
         */
        protected String formatGroups(String[] groups) {
            if (groups.length == 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(groups[0]);
            for (int i = 1; i < groups.length; i++) {
                builder.append(", ").append(groups[i]);
            }
            return builder.toString();
        }
    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;

        /**
         * @param className     the class name
         * @param methodResults the non-null, non-empty {@link MethodResult} list
         */
        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
        }

        public String getClassName() {
            return className;
        }

        /**
         * @return the non-null, non-empty {@link MethodResult} list
         */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }
    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;

        /**
         * @param results the non-null, non-empty result list
         */
        public MethodResult(List<ITestResult> results) {
            this.results = results;
        }

        /**
         * @return the non-null, non-empty result list
         */
        public List<ITestResult> getResults() {
            return results;
        }
    }
}