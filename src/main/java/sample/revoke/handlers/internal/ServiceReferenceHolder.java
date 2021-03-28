package sample.revoke.handlers.internal;

import org.wso2.carbon.apimgt.gateway.throttling.ThrottleDataHolder;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;
import org.wso2.carbon.apimgt.impl.dto.ThrottleProperties;

public class ServiceReferenceHolder {

    private static final ServiceReferenceHolder instance = new ServiceReferenceHolder();

    public ThrottleDataHolder throttleDataHolder;
    private APIManagerConfigurationService amConfigService;

    private ServiceReferenceHolder() {

    }

    public static ServiceReferenceHolder getInstance() {
        return instance;
    }

    public ThrottleProperties getThrottleProperties() {
        if (amConfigService != null) {
            return amConfigService.getAPIManagerConfiguration().getThrottleProperties();
        }
        return null;
    }

    public APIManagerConfigurationService getApiManagerConfigurationService() {
        return amConfigService;
    }

    public void setAPIManagerConfigurationService(APIManagerConfigurationService amConfigService) {
        this.amConfigService = amConfigService;
    }
}
