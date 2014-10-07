package org.openepics.discs.conf.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

/**
 * Implementation of {@link AppProperties} that is suitable for non-Glassfish App. Servers (JBoss, Wildfly...)
 *
 * @author Miroslav Pavleski
 */
@Alternative
@ApplicationScoped
public class AppPropertiesJBoss implements AppProperties {
    private static final String PREFIX = "org.openepics.discs.conf.props.";

    /**
     * @see AppProperties#getProperty(String)
     */
    @Override
    public String getProperty(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        return System.getProperty(PREFIX + name);
    }
}
