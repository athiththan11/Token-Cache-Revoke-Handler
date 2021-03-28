package sample.revoke.handlers.publisher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.StackObjectPool;

public class TokenRevokeDataPublisherPool {

    private ObjectPool clientPool;
    private static final Log log = LogFactory.getLog(TokenRevokeDataPublisherPool.class);

    private TokenRevokeDataPublisherPool() {
        clientPool = new StackObjectPool(new BasePoolableObjectFactory() {
            @Override
            public Object makeObject() throws Exception {
                if (log.isDebugEnabled()) {
                    log.debug("Initializing new TokenRevokeDataPublisher instance");
                }
                return new TokenRevokeDataPublisherAgent();
            }
        }, 1000, 200);
    }

    private static class RevokeDataPublisherPoolHolder {
        private static final TokenRevokeDataPublisherPool INSTANCE = new TokenRevokeDataPublisherPool();

        private RevokeDataPublisherPoolHolder() {
        }
    }

    public static TokenRevokeDataPublisherPool getInstance() {
        return RevokeDataPublisherPoolHolder.INSTANCE;
    }

    public TokenRevokeDataPublisherAgent get() throws Exception {
        return (TokenRevokeDataPublisherAgent) clientPool.borrowObject();
    }

    public void release(TokenRevokeDataPublisherAgent client) throws Exception {
        client.clearDataReference();
        clientPool.returnObject(client);
    }

    public void cleanup() {
        try {
            clientPool.close();
        } catch (Exception e) {
            log.warn("Error while cleaning up the object pool", e);
        }
    }
}
