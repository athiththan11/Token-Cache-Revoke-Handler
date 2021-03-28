package sample.revoke.handlers.publisher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.databridge.agent.DataPublisher;

public class TokenRevokeDataPublisherAgent implements Runnable {

    private DataPublisher revokeDataPublisher;
    private String revokedToken;

    private static String streamId = "org.wso2.apimgt.token.revocation.stream:1.0.0";
    private static final Log log = LogFactory.getLog(TokenRevokeDataPublisherAgent.class);

    public TokenRevokeDataPublisherAgent() {
        revokeDataPublisher = getDataPublisher();
    }

    public void setRevokeData(String revokedToken) {
        this.revokedToken = revokedToken;
    }

    public void clearDataReference() {
        this.revokedToken = null;
    }

    @Override
    public void run() {

        if (log.isDebugEnabled()) {
            log.debug("Publishing token revoke event for token: " + revokedToken);
        }

        Object[] objects = new Object[] { this.revokedToken };
        org.wso2.carbon.databridge.commons.Event event = new org.wso2.carbon.databridge.commons.Event(streamId,
                System.currentTimeMillis(), null, null, objects);
        revokeDataPublisher.tryPublish(event);
    }

    protected DataPublisher getDataPublisher() {
        return TokenRevokeDataPublisher.getDataPublisher();
    }
}