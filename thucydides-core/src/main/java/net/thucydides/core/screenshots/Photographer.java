package net.thucydides.core.screenshots;

import com.google.common.base.Optional;
import net.thucydides.core.guice.Injectors;
import net.thucydides.core.webdriver.WebDriverFacade;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The photographer takes and stores screenshots during the test.
 * The actual screenshots are taken using the specified web driver,
 * and are stored in the specified target directory. Screenshots
 * are numbered sequentially.
 *
 * @author johnsmart
 */
public class Photographer {

    private static final int MESSAGE_DIGEST_MASK = 0xFF;
    private static final int PNG_SUFFIX_LENGTH = ".png".length();
    private final WebDriver driver;
    private final File targetDirectory;
    private final ScreenshotSequence screenshotSequence;
    private final MessageDigest digest;

    private final Logger logger = LoggerFactory.getLogger(Photographer.class);
    private ScreenshotProcessor screenshotProcessor;

    protected Logger getLogger() {
        return logger;
    }

    private static final ScreenshotSequence DEFAULT_SCREENSHOT_SEQUENCE = new ScreenshotSequence();

    public Photographer(final WebDriver driver, final File targetDirectory) {
        this(driver, targetDirectory, Injectors.getInjector().getInstance(ScreenshotProcessor.class));
    }

    public Photographer(final WebDriver driver, final File targetDirectory, final ScreenshotProcessor screenshotProcessor) {
        this.driver = driver;
        this.targetDirectory = targetDirectory;
        this.screenshotProcessor = screenshotProcessor;
        this.screenshotSequence = DEFAULT_SCREENSHOT_SEQUENCE;
        this.digest = getMd5Digest();
    }

    private MessageDigest getMd5Digest() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            getLogger().error("Failed to create digest for screenshot name.", e);
        }
        return md;
    }

    protected long nextScreenshotNumber() {
        return screenshotSequence.next();
    }

    private String nextScreenshotName(final String prefix) {
        long nextScreenshotNumber = nextScreenshotNumber();
        return "screenshot-" + getMD5DigestFrom(prefix) + nextScreenshotNumber + ".png";
    }

    private String getMD5DigestFrom(final String value) {
        byte[] messageDigest = digest.digest(value.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : messageDigest) {
            hexString.append(Integer.toHexString(MESSAGE_DIGEST_MASK & aMessageDigest));
        }
        return hexString.toString();
    }

    /**
     * Take a screenshot of the current browser and store it in the output directory.
     */
    public File takeScreenshot(final String prefix) {
        if (driverCanTakeSnapshots()) {
            try {
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                File savedScreenshot = targetScreenshot(prefix);
                screenshotProcessor.queueScreenshot(new QueuedScreenshot(screenshotFile, savedScreenshot));
                savePageSourceFor(savedScreenshot.getAbsolutePath());
                return savedScreenshot;
            } catch (Throwable e) {
                getLogger().warn("Failed to write screenshot (possibly an out of memory error): " + e.getMessage());
            }
        }
        return null;
    }


    private File targetScreenshot(String prefix) {
        targetDirectory.mkdirs();
        return new File(targetDirectory, nextScreenshotName(prefix));
    }

    protected boolean driverCanTakeSnapshots() {
        if (driver == null) {
            return false;
        } else if (driver instanceof WebDriverFacade) {
            return ((WebDriverFacade) driver).canTakeScreenshots()
                    && (((WebDriverFacade) driver).getProxiedDriver() != null);
        } else {
            return TakesScreenshot.class.isAssignableFrom(driver.getClass());
        }
    }

    private void savePageSourceFor(final String screenshotFile) throws IOException {
        try {
            WebDriver webdriver = driver;
            String pageSource = webdriver.getPageSource();

            File savedSource = new File(sourceCodeFileFor(screenshotFile));
            FileUtils.writeStringToFile(savedSource, pageSource);
        } catch (WebDriverException e) {
            getLogger().warn("Failed to save screen source code", e);
        }
    }


    private String sourceCodeFileFor(final String screenshotFile) {
        String rootFilename = screenshotFile.substring(0, screenshotFile.length() - PNG_SUFFIX_LENGTH);
        return rootFilename + ".html";
    }

    public File getMatchingSourceCodeFor(final File screenshot) {
        if (screenshot != null) {
            return new File(sourceCodeFileFor(screenshot.getAbsolutePath()));
        } else {
            return null;
        }
    }

    public void setScreenshotProcessor(ScreenshotProcessor screenshotProcessor) {
        this.screenshotProcessor = screenshotProcessor;
    }

    protected ScreenshotProcessor getScreenshotProcessor() {
        return screenshotProcessor;
    }
}
