package sample.revoke.handlers.publisher;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.wso2.carbon.apimgt.impl.dto.ThrottleProperties;
import org.wso2.carbon.databridge.agent.DataPublisher;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAgentConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointAuthenticationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointConfigurationException;
import org.wso2.carbon.databridge.agent.exception.DataEndpointException;
import org.wso2.carbon.databridge.commons.exception.TransportException;

import sample.revoke.handlers.internal.ServiceReferenceHolder;

public class TokenRevokeDataPublisher {

    Executor executor;

    public static TokenRevokeDataPublisherPool dataPublisherPool;
    private static volatile DataPublisher dataPublisher = null;
    private static final Log log = LogFactory.getLog(TokenRevokeDataPublisher.class);

    public TokenRevokeDataPublisher() {
        ThrottleProperties throttleProperties = ServiceReferenceHolder.getInstance().getThrottleProperties();
        if (throttleProperties != null) {
            ThrottleProperties.DataPublisher dataPublisherConfiguration = ServiceReferenceHolder.getInstance()
                    .getThrottleProperties().getDataPublisher();
            if (dataPublisherConfiguration != null && dataPublisherConfiguration.isEnabled()) {
                dataPublisherPool = TokenRevokeDataPublisherPool.getInstance();
                ThrottleProperties.DataPublisherThreadPool dataPublisherThreadPoolConfiguration = ServiceReferenceHolder
                        .getInstance().getThrottleProperties().getDataPublisherThreadPool();

                try {
                    executor = new DataPublisherThreadPoolExecutor(
                            dataPublisherThreadPoolConfiguration.getCorePoolSize(),
                            dataPublisherThreadPoolConfiguration.getMaximumPoolSize(),
                            dataPublisherThreadPoolConfiguration.getKeepAliveTime(), TimeUnit.SECONDS,
                            new LinkedBlockingDeque<Runnable>() {
                            });
                    dataPublisher = new DataPublisher(dataPublisherConfiguration.getType(),
                            dataPublisherConfiguration.getReceiverUrlGroup(),
                            dataPublisherConfiguration.getAuthUrlGroup(), dataPublisherConfiguration.getUsername(),
                            dataPublisherConfiguration.getPassword());

                } catch (DataEndpointAgentConfigurationException e) {
                    log.error(
                            "Error in initializing binary data-publisher to send requests to global throttling engine "
                                    + e.getMessage(),
                            e);
                } catch (DataEndpointException e) {
                    log.error(
                            "Error in initializing binary data-publisher to send requests to global throttling engine "
                                    + e.getMessage(),
                            e);
                } catch (DataEndpointConfigurationException e) {
                    log.error(
                            "Error in initializing binary data-publisher to send requests to global throttling engine "
                                    + e.getMessage(),
                            e);
                } catch (DataEndpointAuthenticationException e) {
                    log.error(
                            "Error in initializing binary data-publisher to send requests to global throttling engine "
                                    + e.getMessage(),
                            e);
                } catch (TransportException e) {
                    log.error(
                            "Error in initializing binary data-publisher to send requests to global throttling engine "
                                    + e.getMessage(),
                            e);
                }
            }
        }
    }

    public static DataPublisher getDataPublisher() {
        return dataPublisher;
    }

    public void publishTokenRevokeEvent(MessageContext messageContext, String revokedToken) {
        try {
            if (dataPublisherPool != null) {
                TokenRevokeDataPublisherAgent agent = dataPublisherPool.get();
                agent.setRevokeData(revokedToken);
                if (log.isDebugEnabled()) {
                    log.debug("Publishing token revocation data from gateway to traffic-manager for: " + revokedToken
                            + " with ID: " + messageContext.getMessageID() + " started" + " at "
                            + new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss,SSS zzz]").format(new Date()));
                }
                executor.execute(agent);
                if (log.isDebugEnabled()) {
                    log.debug("Publishing token revocation data from gateway to traffic-manager for: " + revokedToken
                            + " with ID: " + messageContext.getMessageID() + " ended" + " at "
                            + new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss,SSS zzz]").format(new Date()));
                }
            } else {
                log.debug("Revoke data publisher pool is not initialized.");
            }
        } catch (Exception e) {
            log.error("Error while publishing throttling events to global policy server", e);
        }
    }

    private class DataPublisherThreadPoolExecutor extends ThreadPoolExecutor {
        public DataPublisherThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                LinkedBlockingDeque<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        protected void afterExecute(java.lang.Runnable r, java.lang.Throwable t) {
            try {
                TokenRevokeDataPublisherAgent agent = (TokenRevokeDataPublisherAgent) r;
                TokenRevokeDataPublisher.dataPublisherPool.release(agent);
            } catch (Exception e) {
                log.error("Error while returning Throttle data publishing agent back to pool" + e.getMessage());
            }
        }
    }

}
