package be.rvponp.build;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration of CommitViewer
 * User: canas
 * Date: 11/29/13
 * Time: 9:27 AM
 */
public class CommitViewerConfiguration {
    private static final String DEFAULT_CONFIG_FILE = System.getProperty("user.home") + File.separator +
            "CommitViewer.properties";
    private static final String SYSTEM_PROPERTY_NAME = "cv.config";
    private static CommitViewerConfiguration instance;
    private static Properties configFile;
    private static final Logger log = Logger.getLogger(CommitViewerConfiguration.class);

    private CommitViewerConfiguration() {
        if (configFile == null) {
            configFile = new Properties();

            try {
                String property = System.getProperty(SYSTEM_PROPERTY_NAME);
                if (property != null) {
                    log.info("Using system property " + SYSTEM_PROPERTY_NAME + " with value " + property);
                    configFile.load(new FileInputStream(new File(property)));
                } else {
                    log.info("Using default config file " + DEFAULT_CONFIG_FILE);
                    configFile.load(new FileInputStream(new File(DEFAULT_CONFIG_FILE)));
                }
            } catch (IOException e) {
                log.error("Looking for " + SYSTEM_PROPERTY_NAME + "  property. File not found.");
                e.printStackTrace();
            }
        }
    }

    public static CommitViewerConfiguration getInstance() {
        if (instance == null) {
            instance = new CommitViewerConfiguration();
        }
        return instance;
    }

    public String getProperty(String key) {
        if (configFile != null) {
            return configFile.getProperty(key);
        }
        log.error("Config file is null");
        return null;
    }
}
