package org.apache.rocketmq.client.route;

import apache.rocketmq.v1.Resource;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.rocketmq.client.constant.Permission;
import org.apache.rocketmq.client.remoting.Address;
import org.apache.rocketmq.client.remoting.Endpoints;

@Getter
@ToString
@EqualsAndHashCode
public class Partition {
    private final Topic topic;
    private final Broker broker;
    private final int id;

    private final Permission permission;

    public Partition(apache.rocketmq.v1.Partition partition) {
        final Resource topicResource = partition.getTopic();
        this.topic = new Topic(topicResource.getArn(), topicResource.getName());
        this.id = partition.getId();
        final apache.rocketmq.v1.Permission perm = partition.getPermission();
        switch (perm) {
            case READ:
                this.permission = Permission.READ;
                break;
            case WRITE:
                this.permission = Permission.WRITE;
                break;
            case READ_WRITE:
                this.permission = Permission.READ_WRITE;
                break;
            case NONE:
            default:
                this.permission = Permission.NONE;
                break;
        }

        final String brokerName = partition.getBroker().getName();
        final int brokerId = partition.getBroker().getId();

        final apache.rocketmq.v1.Endpoints endpoints = partition.getBroker().getEndpoints();
        final apache.rocketmq.v1.AddressScheme scheme = endpoints.getScheme();
        AddressScheme targetAddressScheme;

        switch (scheme) {
            case IPv4:
                targetAddressScheme = AddressScheme.IPv4;
                break;
            case IPv6:
                targetAddressScheme = AddressScheme.IPv6;
                break;
            case DOMAIN_NAME:
            default:
                targetAddressScheme = AddressScheme.DOMAIN_NAME;
        }
        List<Address> addresses = new ArrayList<Address>();
        for (apache.rocketmq.v1.Address address : endpoints.getAddressesList()) {
            addresses.add(new Address(address));
        }
        this.broker = new Broker(brokerName, brokerId, new Endpoints(targetAddressScheme, addresses));
    }
}
