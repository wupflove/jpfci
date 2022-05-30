/** 
* @author 吴平福 
* E-mail:421722623@qq.com 
* @version 创建时间：2016年5月16日 上午10:13:20 
* 类说明 
*/ 

package org.jpf.unittests.utrpt;

/**
 * 
 */
public class UnitTestInfo {
    private final String strJavaTestName="Running";
    /**
     * 
     */
    public UnitTestInfo(String strTestName,String strResult) {
        // TODO Auto-generated constructor stub
        testName=strTestName.substring(strJavaTestName.length(),strTestName.length()).trim();
        String[] strResults=strResult.split(",");
        if (5==strResults.length)
        {
            tests=getIntResult(strResults[0]);
            failures=getIntResult(strResults[1]);
            errors=getIntResult(strResults[2]);
            skips=getIntResult(strResults[3]);
            timeElapsed=getDoubleResult(strResults[4]);
        }
    }
    private Double getDoubleResult(String strInput)
    {
        String[] strInputs=  strInput.split(":");
        if (strInputs.length==2)
        {
            if (strInputs[1].endsWith("sec"))
            {
                strInputs[1]=strInputs[1].substring(0, strInputs[1].length()-3);
            }
            return Double.parseDouble(strInputs[1].trim());
        }
        return Double.valueOf(-1);
    }
    private int getIntResult(String strInput)
    {
        String[] strInputs=  strInput.split(":");
        if (strInputs.length==2)
        {

            return Integer.parseInt(strInputs[1].trim());
        }
        return -1;
    }
    /**
     * @return the testName
     */
    public String getTestName() {
        return testName;
    }
    /**
     * @param testName the testName to set
     */
    public void setTestName(String testName) {
        this.testName = testName;
    }
    /**
     * @return the tests
     */
    public int getTests() {
        return tests;
    }
    /**
     * @param tests the tests to set
     */
    public void setTests(int tests) {
        this.tests = tests;
    }
    /**
     * @return the failures
     */
    public int getFailures() {
        return failures;
    }
    /**
     * @param failures the failures to set
     */
    public void setFailures(int failures) {
        this.failures = failures;
    }
    /**
     * @return the errors
     */
    public int getErrors() {
        return errors;
    }
    /**
     * @param errors the errors to set
     */
    public void setErrors(int errors) {
        this.errors = errors;
    }
    /**
     * @return the skips
     */
    public int getSkips() {
        return skips;
    }
    /**
     * @param skips the skips to set
     */
    public void setSkips(int skips) {
        this.skips = skips;
    }
    /**
     * @return the timeElapsed
     */
    public double getTimeElapsed() {
        return timeElapsed;
    }
    /**
     * @param timeElapsed the timeElapsed to set
     */
    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
    private String testName;
    private int tests;
    private int failures;
    private int errors;
    private int skips;
    private double timeElapsed;
}
