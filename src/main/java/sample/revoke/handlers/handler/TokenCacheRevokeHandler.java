package sample.revoke.handlers.handler;

import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.rest.AbstractHandler;
import org.wso2.carbon.apimgt.gateway.APIMgtGatewayConstants;

import sample.revoke.handlers.publisher.TokenRevokeDataPublisher;

/**
 * Sample handler implementation to revoke the token caches in Gateway nodes
 * using events.
 * 
 * Once the token revocation is successful, the revoked token data is published
 * to the Traffic Manager node to publish to all subscribed Gateway nodes
 */
public class TokenCacheRevokeHandler extends AbstractHandler {

    private static volatile TokenRevokeDataPublisher revokeDataPublisher;
    private static final Log log = LogFactory.getLog(TokenCacheRevokeHandler.class);

    public static void initThrottleDataPublisher() {
        revokeDataPublisher = new TokenRevokeDataPublisher();
    }

    @Override
    public boolean handleRequest(MessageContext messageContext) {
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext) {
        publishRevocationNotification(messageContext);
        return true;
    }

    private void publishRevocationNotification(MessageContext messageContext) {
        org.apache.axis2.context.MessageContext axisMC = ((Axis2MessageContext) messageContext)
                .getAxis2MessageContext();
        TreeMap transportHeaders = ((TreeMap) axisMC
                .getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS));
        String revokedToken = (String) transportHeaders.get(APIMgtGatewayConstants.REVOKED_ACCESS_TOKEN);

        if (revokedToken != null) {
            if (log.isDebugEnabled()) {
                log.debug("Revoked token: " + revokedToken);
            }

            if (revokeDataPublisher == null) {
                initThrottleDataPublisher();
            }

            revokeDataPublisher = new TokenRevokeDataPublisher();
            revokeDataPublisher.publishTokenRevokeEvent(messageContext, revokedToken);
        }
    }

}
