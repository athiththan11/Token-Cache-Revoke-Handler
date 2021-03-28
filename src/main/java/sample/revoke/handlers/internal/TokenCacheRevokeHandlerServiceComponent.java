package sample.revoke.handlers.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.apimgt.impl.APIManagerConfigurationService;

/**
 * @scr.component name="sample.revoke.handlers" immediate="true"
 * @scr.reference name="api.manager.config.service"
 *                interface="org.wso2.carbon.apimgt.impl.APIManagerConfigurationService"
 *                cardinality="1..1" policy="dynamic"
 *                bind="setAPIManagerConfigurationService"
 *                unbind="unsetAPIManagerConfigurationService"
 */
public class TokenCacheRevokeHandlerServiceComponent {

    private static final Log log = LogFactory.getLog(TokenCacheRevokeHandlerServiceComponent.class);

    @Activate
    protected void activate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Token cache revoke handlers component activated");
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Token cache revoke handlers component deactivated");
        }
    }

    protected void setAPIManagerConfigurationService(APIManagerConfigurationService amcService) {
        if (log.isDebugEnabled()) {
            log.debug("API manager configuration service bound to the API handlers");
        }
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(amcService);
    }

    protected void unsetAPIManagerConfigurationService(APIManagerConfigurationService amcService) {
        if (log.isDebugEnabled()) {
            log.debug("API manager configuration service unbound from the API handlers");
        }
        ServiceReferenceHolder.getInstance().setAPIManagerConfigurationService(null);
    }
}
