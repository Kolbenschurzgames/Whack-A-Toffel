package org.mockito.configuration;

/**
 * Created by alfriedl on 19.09.15.
 */
public class MockitoConfiguration extends DefaultMockitoConfiguration {
    @Override
    public boolean enableClassCache() {
        return false;
    }
}
