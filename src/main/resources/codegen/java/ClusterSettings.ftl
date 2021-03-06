################################
# cluster-wide configurations
################################

# currently unsupported
cluster.ssl = false

# maximum size of single operations message (which are actually tiny, other than DIR)
# assuming short host names 4096 would support approximately 90-99 nodes with DIR
cluster.op.buffer.size = 4096

# the interval (in ms) within which the operations inbound stream will be probed
# for available messages
cluster.op.incoming.probe.interval = 100

# maximum size of a single cluster client (tool or application) message
# you may be able to tune this to be much smaller depending on app messages
cluster.app.buffer.size = 10240

# the interval (in ms) within which the application inbound stream will be probed
# for available messages
cluster.app.incoming.probe.interval = 10

# number of polled buffers for outgoing asynchronous operations messages
cluster.op.outgoing.pooled.buffers = 20

# number of polled buffers for outgoing asynchronous operations messages
cluster.app.outgoing.pooled.buffers = 50

# default charset
cluster.msg.charset = UTF-8

# classname of client/application and its stage name
cluster.app.class = io.vlingo.lattice.grid.GridNode
cluster.app.stage = fake.app.stage

# interval at which unconfirmed attribute changes are redistributed
cluster.attributes.redistribution.interval = 1000

# the number of retries for redistributing unconfirmed attribute changes
cluster.attributes.redistribution.retries = 20

# interval at which each health check is scheduled
cluster.health.check.interval = 2000

# after this limit with no pulse from given node, it's considered dead
cluster.live.node.timeout = 20000

# after this limit with too few nodes to constitute a quorum, terminate node
cluster.quorum.timeout = 60000

# currently all active nodes must be listed as seed nodes
<#if (clusterSettings.nodeCount > 1)>
# -- comment the following to disable developer single-node cluster
cluster.seedNodes = ${clusterSettings.oneSeedNode}

# -- uncomment the following to enable all cluster nodes
# cluster.seedNodes = ${clusterSettings.seedNodes}
<#else>
cluster.seedNodes = ${clusterSettings.oneSeedNode}
</#if>

################################
# individual node configurations
################################

<#list clusterSettings.nodes as node>
<#if (node.id > 1)>#</#if>node.${node.name}.id = ${node.id}
<#if (node.id > 1)>#</#if>node.${node.name}.name = ${node.name}
<#if (node.id > 1)>#</#if>node.${node.name}.host = localhost
<#if (node.id > 1)>#</#if>node.${node.name}.op.port = ${node.operationalPort?c}
<#if (node.id > 1)>#</#if>node.${node.name}.app.port = ${node.applicationPort?c}

</#list>