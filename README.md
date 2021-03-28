# Token Cache Revoke Handler - WSO2 API Manager v2.6

Sample handler implementation to revoke the token caches of Gateway component in WSO2 API Manager v2.6.0 using Event-based architecture.

> This handler is designed for WSO2 API Manager v2.6.0 (+1612313472144). The handler needs to be engaged and used with the API Manager v2.6.0 with the latest or after WUM timestamp 1612313472144 to work as expected.

[:construction: Dev in progress]

## How it works?

The provided sample handler will be engaged with `_RevokeAPI_.xml` to revoke the token caches in the Gateway component.

Once receiving a response from the Revoke endpoint, the handler collects the Revoked token metadata and publishes an event to the configured Traffic Manager component. Whereas the Traffic Manager re-routes the received event and publishes it to all subscribed Gateway components to clear the token caches.

## Build & Deploy

### Build

Execute the following maven command to build and create a bundle JAR artifact

```sh
mvn clean package
```

### Deploy

Copy and place the built JAR artifact from `<project>/target` directory to `<gateway>/repository/components/dropins` directory in API Manager server. Once deployed, restart the server to load the artifacts.

Add the following Handler definition and configure the `<apim>/repository/deployment/server/synapse-configs/defualt/api/_RevokeAPI_.xml` as following in the Gateway node to engage the built custom handler

```xml
<!-- _RevokeAPI_.xml -->
...
<handlers>
    <handler class="sample.revoke.handlers.handler.TokenCacheRevokeHandler" />
    <handler class="org.wso2.carbon.apimgt.gateway.handlers.ext.APIManagerCacheExtensionHandler"/>
    <handler class="org.wso2.carbon.apimgt.gateway.handlers.common.SynapsePropertiesHandler"/>
</handlers>
...
```

### Configure API Manager (Gateway)

Configure the Gateway node to publish the data events to the Traffic Manager node as following

> Note: The same following configurations are used to evaluate the Throttling conditions in the API Manager server. Hence, it is not required to re-configure the same if they have been already enabled and configured to publish the events to the respective Traffic Manager nodes

```xml
<!-- api-manager.xml -->
...
<ThrottlingConfigurations>
    <EnableAdvanceThrottling>true</EnableAdvanceThrottling>
    <TrafficManager>
        <Type>Binary</Type>
        <ReceiverUrlGroup>tcp://traffic-manager:9611</ReceiverUrlGroup>
        <AuthUrlGroup>ssl://traffic-manager:9711</AuthUrlGroup>
        <Username>${admin.username}</Username>
        <Password>${admin.password}</Password>
    </TrafficManager>
    <DataPublisher>
        <Enabled>true</Enabled>
        ...
    </DataPublisher>
    ...
</ThrottlingConfigurations>
...
```

## License

[Apache 2.0](LICENSE)
