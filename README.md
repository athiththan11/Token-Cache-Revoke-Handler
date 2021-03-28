# Token Cache Revoke Handler - WSO2 API Manager v2.6

Sample handler implementation to revoke the token caches of Gateway component in WSO2 API Manager v2.6.0 using Event-based architecture.

> This handler is designed for WSO2 API Manager v2.6.0 (+1612313472144). The handler needs to be engaged and used with the API Manager v2.6.0 with the latest or after WUM timestamp 1612313472144 to work as expected.

[:construction: Dev in progress]

## How it works?

The provided sample handler will be engaged with `_RevokeAPI_.xml` to revoke the token caches in the Gateway component.

Once receiving a response from the Revoke endpoint, the handler collects the Revoked token metadata and publishes an event to the configured Traffic Manager component. Whereas the Traffic Manager re-routes the received event and publishes it to all subscribed Gateway components to clear the token caches.
