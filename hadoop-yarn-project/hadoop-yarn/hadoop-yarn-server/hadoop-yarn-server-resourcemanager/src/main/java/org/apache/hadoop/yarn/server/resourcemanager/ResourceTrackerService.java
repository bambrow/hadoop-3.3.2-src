/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.thirdparty.com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.net.Node;
import org.apache.hadoop.security.authorize.PolicyProvider;
import org.apache.hadoop.service.AbstractService;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.VersionUtil;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.NodeAttribute;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.ipc.YarnRPC;
import org.apache.hadoop.yarn.nodelabels.NodeLabelUtil;
import org.apache.hadoop.yarn.server.api.ResourceTracker;
import org.apache.hadoop.yarn.server.api.protocolrecords.NMContainerStatus;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.NodeHeartbeatResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.RegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.UnRegisterNodeManagerResponse;
import org.apache.hadoop.yarn.server.api.records.AppCollectorData;
import org.apache.hadoop.yarn.server.api.records.MasterKey;
import org.apache.hadoop.yarn.server.api.records.NodeAction;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.NodeLabelsUtils;
import org.apache.hadoop.yarn.server.resourcemanager.resource.DynamicResourceConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.event.RMAppAttemptContainerFinishedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeReconnectEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStartedEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.security.NMTokenSecretManagerInRM;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMContainerTokenSecretManager;
import org.apache.hadoop.yarn.server.resourcemanager.security.authorize.RMPolicyProvider;
import org.apache.hadoop.yarn.server.utils.YarnServerBuilderUtils;
import org.apache.hadoop.yarn.util.RackResolver;
import org.apache.hadoop.yarn.util.YarnVersionInfo;

import org.apache.hadoop.thirdparty.com.google.common.annotations.VisibleForTesting;

public class ResourceTrackerService extends AbstractService implements
    ResourceTracker {

  private static final Logger LOG =
      LoggerFactory.getLogger(ResourceTrackerService.class);

  private static final RecordFactory recordFactory = 
    RecordFactoryProvider.getRecordFactory(null);

  private final RMContext rmContext;
  private final NodesListManager nodesListManager;
  private final NMLivelinessMonitor nmLivelinessMonitor;
  private final RMContainerTokenSecretManager containerTokenSecretManager;
  private final NMTokenSecretManagerInRM nmTokenSecretManager;

  private final ReadLock readLock;
  private final WriteLock writeLock;

  private long nextHeartBeatInterval;
  private boolean heartBeatIntervalScalingEnable;
  private long heartBeatIntervalMin;
  private long heartBeatIntervalMax;
  private float heartBeatIntervalSpeedupFactor;
  private float heartBeatIntervalSlowdownFactor;


  private Server server;
  private InetSocketAddress resourceTrackerAddress;
  private String minimumNodeManagerVersion;

  private int minAllocMb;
  private int minAllocVcores;

  private DecommissioningNodesWatcher decommissioningWatcher;

  private boolean isDistributedNodeLabelsConf;
  private boolean isDelegatedCentralizedNodeLabelsConf;
  private DynamicResourceConfiguration drConf;

  private final AtomicLong timelineCollectorVersion = new AtomicLong(0);
  private boolean checkIpHostnameInRegistration;
  private boolean timelineServiceV2Enabled;

  public ResourceTrackerService(RMContext rmContext,
      NodesListManager nodesListManager,
      NMLivelinessMonitor nmLivelinessMonitor,
      RMContainerTokenSecretManager containerTokenSecretManager,
      NMTokenSecretManagerInRM nmTokenSecretManager) {
    super(ResourceTrackerService.class.getName());
    this.rmContext = rmContext;
    this.nodesListManager = nodesListManager;
    this.nmLivelinessMonitor = nmLivelinessMonitor;
    this.containerTokenSecretManager = containerTokenSecretManager;
    this.nmTokenSecretManager = nmTokenSecretManager;
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();
    this.decommissioningWatcher = new DecommissioningNodesWatcher(rmContext);
  }

  // 从配置中读取所需信息
  @Override
  protected void serviceInit(Configuration conf) throws Exception {
    resourceTrackerAddress = conf.getSocketAddr(
        YarnConfiguration.RM_BIND_HOST,
        YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS,
        YarnConfiguration.DEFAULT_RM_RESOURCE_TRACKER_ADDRESS,
        YarnConfiguration.DEFAULT_RM_RESOURCE_TRACKER_PORT);

    RackResolver.init(conf);

    checkIpHostnameInRegistration = conf.getBoolean(
        YarnConfiguration.RM_NM_REGISTRATION_IP_HOSTNAME_CHECK_KEY,
        YarnConfiguration.DEFAULT_RM_NM_REGISTRATION_IP_HOSTNAME_CHECK_KEY);
    minAllocMb = conf.getInt(
        YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB,
        YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB);
    minAllocVcores = conf.getInt(
        YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES,
        YarnConfiguration.DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES);

    minimumNodeManagerVersion = conf.get(
        YarnConfiguration.RM_NODEMANAGER_MINIMUM_VERSION,
        YarnConfiguration.DEFAULT_RM_NODEMANAGER_MINIMUM_VERSION);
    timelineServiceV2Enabled =  YarnConfiguration.
        timelineServiceV2Enabled(conf);

    if (YarnConfiguration.areNodeLabelsEnabled(conf)) {
      isDistributedNodeLabelsConf =
          YarnConfiguration.isDistributedNodeLabelConfiguration(conf);
      isDelegatedCentralizedNodeLabelsConf =
          YarnConfiguration.isDelegatedCentralizedNodeLabelConfiguration(conf);
    }
    updateHeartBeatConfiguration(conf);
    loadDynamicResourceConfiguration(conf);
    decommissioningWatcher.init(conf);
    super.serviceInit(conf);
  }

  /**
   * Load DynamicResourceConfiguration from dynamic-resources.xml.
   * @param conf
   * @throws IOException
   */
  public void loadDynamicResourceConfiguration(Configuration conf)
      throws IOException {
    try {
      // load dynamic-resources.xml
      InputStream drInputStream = this.rmContext.getConfigurationProvider()
          .getConfigurationInputStream(conf,
          YarnConfiguration.DR_CONFIGURATION_FILE);
      // write lock here on drConfig is unnecessary as here get called at
      // ResourceTrackerService get initiated and other read and write
      // operations haven't started yet.
      if (drInputStream != null) {
        this.drConf = new DynamicResourceConfiguration(conf, drInputStream);
      } else {
        this.drConf = new DynamicResourceConfiguration(conf);
      }
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  /**
   * Update DynamicResourceConfiguration with new configuration.
   * @param conf
   */
  public void updateDynamicResourceConfiguration(
      DynamicResourceConfiguration conf) {
    this.writeLock.lock();
    try {
      this.drConf = conf;
    } finally {
      this.writeLock.unlock();
    }
  }

  /**
   * Update HearBeatConfiguration with new configuration.
   * @param conf Yarn Configuration
   */
  public void updateHeartBeatConfiguration(Configuration conf) {
    this.writeLock.lock();
    try {
      nextHeartBeatInterval =
          conf.getLong(YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_MS,
              YarnConfiguration.DEFAULT_RM_NM_HEARTBEAT_INTERVAL_MS);
      heartBeatIntervalScalingEnable =
          conf.getBoolean(
              YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_SCALING_ENABLE,
              YarnConfiguration.
                  DEFAULT_RM_NM_HEARTBEAT_INTERVAL_SCALING_ENABLE);
      heartBeatIntervalMin =
          conf.getLong(YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_MIN_MS,
              YarnConfiguration.DEFAULT_RM_NM_HEARTBEAT_INTERVAL_MIN_MS);
      heartBeatIntervalMax =
          conf.getLong(YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_MAX_MS,
              YarnConfiguration.DEFAULT_RM_NM_HEARTBEAT_INTERVAL_MAX_MS);
      heartBeatIntervalSpeedupFactor =
          conf.getFloat(
              YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_SPEEDUP_FACTOR,
              YarnConfiguration.
                  DEFAULT_RM_NM_HEARTBEAT_INTERVAL_SPEEDUP_FACTOR);
      heartBeatIntervalSlowdownFactor =
          conf.getFloat(
              YarnConfiguration.RM_NM_HEARTBEAT_INTERVAL_SLOWDOWN_FACTOR,
              YarnConfiguration.
                  DEFAULT_RM_NM_HEARTBEAT_INTERVAL_SLOWDOWN_FACTOR);

      if (nextHeartBeatInterval <= 0) {
        LOG.warn("HeartBeat interval: " + nextHeartBeatInterval
            + " must be greater than 0, using default.");
        nextHeartBeatInterval =
            YarnConfiguration.DEFAULT_RM_NM_HEARTBEAT_INTERVAL_MS;
      }

      if (heartBeatIntervalScalingEnable) {
        if (heartBeatIntervalMin <= 0
            || heartBeatIntervalMin > heartBeatIntervalMax
            || nextHeartBeatInterval < heartBeatIntervalMin
            || nextHeartBeatInterval > heartBeatIntervalMax) {
          LOG.warn("Invalid NM Heartbeat Configuration. "
              + "Required: 0 < minimum <= interval <= maximum. Got: 0 < "
              + heartBeatIntervalMin + " <= "
              + nextHeartBeatInterval + " <= "
              + heartBeatIntervalMax
              + " Setting min and max to configured interval.");
          heartBeatIntervalMin = nextHeartBeatInterval;
          heartBeatIntervalMax = nextHeartBeatInterval;
        }
        if (heartBeatIntervalSpeedupFactor < 0
            || heartBeatIntervalSlowdownFactor < 0) {
          LOG.warn(
              "Heartbeat scaling factors must be >= 0 "
                  + " SpeedupFactor:" + heartBeatIntervalSpeedupFactor
                  + " SlowdownFactor:" + heartBeatIntervalSlowdownFactor
                  + ". Using Defaults");
          heartBeatIntervalSlowdownFactor =
              YarnConfiguration.
                  DEFAULT_RM_NM_HEARTBEAT_INTERVAL_SLOWDOWN_FACTOR;
          heartBeatIntervalSpeedupFactor =
              YarnConfiguration.DEFAULT_RM_NM_HEARTBEAT_INTERVAL_SPEEDUP_FACTOR;
        }
        LOG.info("Heartbeat Scaling Configuration: "
            + " defaultInterval:" + nextHeartBeatInterval
            + " minimumInterval:" + heartBeatIntervalMin
            + " maximumInterval:" + heartBeatIntervalMax
            + " speedupFactor:" + heartBeatIntervalSpeedupFactor
            + " slowdownFactor:" + heartBeatIntervalSlowdownFactor);
      }
    } finally {
      this.writeLock.unlock();
    }
  }

  // 服务启动代码
  @Override
  protected void serviceStart() throws Exception {
    super.serviceStart();
    // ResourceTrackerServer authenticates NodeManager via Kerberos if
    // security is enabled, so no secretManager.
    Configuration conf = getConfig();
    // 新建RPC Server
    YarnRPC rpc = YarnRPC.create(conf);
    this.server = rpc.getServer(
        ResourceTracker.class, this, resourceTrackerAddress, conf, null,
        conf.getInt(YarnConfiguration.RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT,
            YarnConfiguration.DEFAULT_RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT)); // 默认50

    // 启动服务授权？
    // Enable service authorization?
    if (conf.getBoolean(
        CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHORIZATION,
        false)) {
      InputStream inputStream =
          this.rmContext.getConfigurationProvider()
              .getConfigurationInputStream(conf,
                  YarnConfiguration.HADOOP_POLICY_CONFIGURATION_FILE);
      if (inputStream != null) {
        conf.addResource(inputStream);
      }
      refreshServiceAcls(conf, RMPolicyProvider.getInstance());
    }

    // 启动RPC Server并更新相应配置
    this.server.start();
    conf.updateConnectAddr(YarnConfiguration.RM_BIND_HOST,
        YarnConfiguration.RM_RESOURCE_TRACKER_ADDRESS,
        YarnConfiguration.DEFAULT_RM_RESOURCE_TRACKER_ADDRESS,
        server.getListenerAddress());
  }

  @Override
  protected void serviceStop() throws Exception {
    decommissioningWatcher.stop();
    if (this.server != null) {
      this.server.stop();
    }

    super.serviceStop();
  }

  // 处理获取的ContainerStatus
  // 如果ContainerStatus对应着managed AM的master-container的完成状态，则进行RMAppAttemptContainerFinishedEvent处理
  /**
   * Helper method to handle received ContainerStatus. If this corresponds to
   * the completion of a master-container of a managed AM,
   * we call the handler for RMAppAttemptContainerFinishedEvent.
   */
  @SuppressWarnings("unchecked")
  @VisibleForTesting
  void handleNMContainerStatus(NMContainerStatus containerStatus, NodeId nodeId) {
    ApplicationAttemptId appAttemptId =
        containerStatus.getContainerId().getApplicationAttemptId();
    RMApp rmApp =
        rmContext.getRMApps().get(appAttemptId.getApplicationId());
    // 如果未找到对应的App，直接忽略
    if (rmApp == null) {
      LOG.error("Received finished container : "
          + containerStatus.getContainerId()
          + " for unknown application " + appAttemptId.getApplicationId()
          + " Skipping.");
      return;
    }

    // 忽略unmanaged AM的container完成状态
    if (rmApp.getApplicationSubmissionContext().getUnmanagedAM()) {
      LOG.debug("Ignoring container completion status for unmanaged AM {}",
          rmApp.getApplicationId());
      return;
    }

    // 找不到对应Attempt，直接忽略
    RMAppAttempt rmAppAttempt = rmApp.getRMAppAttempt(appAttemptId);
    if (rmAppAttempt == null) {
      LOG.info("Ignoring not found attempt " + appAttemptId);
      return;
    }

    // 获取master-container
    Container masterContainer = rmAppAttempt.getMasterContainer();
    // 如果是master-container并且对应的状态为完成
    if (masterContainer.getId().equals(containerStatus.getContainerId())
        && containerStatus.getContainerState() == ContainerState.COMPLETE) {
      ContainerStatus status =
          ContainerStatus.newInstance(containerStatus.getContainerId(),
            containerStatus.getContainerState(), containerStatus.getDiagnostics(),
            containerStatus.getContainerExitStatus());
      // 发送RMAppAttemptContainerFinishedEvent
      // sending master container finished event.
      RMAppAttemptContainerFinishedEvent evt =
          new RMAppAttemptContainerFinishedEvent(appAttemptId, status,
              nodeId);
      rmContext.getDispatcher().getEventHandler().handle(evt);
    }
  }

  // 注册NM的服务端代码
  @SuppressWarnings("unchecked")
  @Override
  public RegisterNodeManagerResponse registerNodeManager(
      RegisterNodeManagerRequest request) throws YarnException,
      IOException {
    // 获取必要参数
    NodeId nodeId = request.getNodeId();
    String host = nodeId.getHost();
    int cmPort = nodeId.getPort();
    int httpPort = request.getHttpPort();
    Resource capability = request.getResource();
    String nodeManagerVersion = request.getNMVersion();
    Resource physicalResource = request.getPhysicalResource();
    NodeStatus nodeStatus = request.getNodeStatus();

    // 新建Response对象
    RegisterNodeManagerResponse response = recordFactory
        .newRecordInstance(RegisterNodeManagerResponse.class);

    // 检查NM版本是否匹配，默认为NONE，不做版本检查
    // yarn.resourcemanager.nodemanager.minimum.version
    if (!minimumNodeManagerVersion.equals("NONE")) {
      if (minimumNodeManagerVersion.equals("EqualToRM")) {
        minimumNodeManagerVersion = YarnVersionInfo.getVersion();
      }
      // 版本不匹配，直接shutdown
      if ((nodeManagerVersion == null) ||
          (VersionUtil.compareVersions(nodeManagerVersion,minimumNodeManagerVersion)) < 0) {
        String message =
            "Disallowed NodeManager Version " + nodeManagerVersion
                + ", is less than the minimum version "
                + minimumNodeManagerVersion + " sending SHUTDOWN signal to "
                + "NodeManager.";
        LOG.info(message);
        response.setDiagnosticsMessage(message);
        response.setNodeAction(NodeAction.SHUTDOWN);
        return response;
      }
    }

    // 是否检查ip-hostname，默认不检查
    // yarn.resourcemanager.resource-tracker.nm.ip-hostname-check
    if (checkIpHostnameInRegistration) {
      InetSocketAddress nmAddress =
          NetUtils.createSocketAddrForHost(host, cmPort);
      InetAddress inetAddress = Server.getRemoteIp();
      // 无法解析hostname，直接shutdown
      if (inetAddress != null && nmAddress.isUnresolved()) {
        // Reject registration of unresolved nm to prevent resourcemanager
        // getting stuck at allocations.
        final String message =
            "hostname cannot be resolved (ip=" + inetAddress.getHostAddress()
                + ", hostname=" + host + ")";
        LOG.warn("Unresolved nodemanager registration: " + message);
        response.setDiagnosticsMessage(message);
        response.setNodeAction(NodeAction.SHUTDOWN);
        return response;
      }
    }

    // 检查是否是可用的节点，并且不在退役中
    // 检查可用的步骤：1. 白名单为空，并且不在黑名单中；2. 在白名单中，并且不在黑名单中
    // Check if this node is a 'valid' node
    if (!this.nodesListManager.isValidNode(host) &&
        !isNodeInDecommissioning(nodeId)) {
      String message =
          "Disallowed NodeManager from  " + host
              + ", Sending SHUTDOWN signal to the NodeManager.";
      LOG.info(message);
      response.setDiagnosticsMessage(message);
      response.setNodeAction(NodeAction.SHUTDOWN);
      return response;
    }

    // 检查是否从dynamic-resources.xml里读取该节点资源数据
    // check if node's capacity is load from dynamic-resources.xml
    String nid = nodeId.toString();

    Resource dynamicLoadCapability = loadNodeResourceFromDRConfiguration(nid);
    if (dynamicLoadCapability != null) {
      LOG.debug("Resource for node: {} is adjusted from: {} to: {} due to"
          + " settings in dynamic-resources.xml.", nid, capability,
          dynamicLoadCapability);
      capability = dynamicLoadCapability;
      // sync back with new resource.
      response.setResource(capability);
    }

    // 检查节点资源是否满足最小资源分配值
    // Check if this node has minimum allocations
    if (capability.getMemorySize() < minAllocMb
        || capability.getVirtualCores() < minAllocVcores) {
      String message = "NodeManager from  " + host
          + " doesn't satisfy minimum allocations, Sending SHUTDOWN"
          + " signal to the NodeManager. Node capabilities are " + capability
          + "; minimums are " + minAllocMb + "mb and " + minAllocVcores
          + " vcores";
      LOG.info(message);
      response.setDiagnosticsMessage(message);
      response.setNodeAction(NodeAction.SHUTDOWN);
      return response;
    }

    // 设置token
    response.setContainerTokenMasterKey(containerTokenSecretManager
        .getCurrentKey());
    response.setNMTokenMasterKey(nmTokenSecretManager
        .getCurrentKey());

    // 创建新的RMNode对象，并替换旧对象
    RMNode rmNode = new RMNodeImpl(nodeId, rmContext, host, cmPort, httpPort,
        resolve(host), capability, nodeManagerVersion, physicalResource);
    // 获取旧的RMNode对象
    RMNode oldNode = this.rmContext.getRMNodes().putIfAbsent(nodeId, rmNode);
    if (oldNode == null) {
      // 如果旧对象为null，证明为新注册的NM，新建RMNodeStartedEvent并进行处理
      RMNodeStartedEvent startEvent = new RMNodeStartedEvent(nodeId,
          request.getNMContainerStatuses(),
          request.getRunningApplications(), nodeStatus);
      if (request.getLogAggregationReportsForApps() != null
          && !request.getLogAggregationReportsForApps().isEmpty()) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Found the number of previous cached log aggregation "
              + "status from nodemanager:" + nodeId + " is :"
              + request.getLogAggregationReportsForApps().size());
        }
        startEvent.setLogAggregationReportsForApps(request
            .getLogAggregationReportsForApps());
      }
      this.rmContext.getDispatcher().getEventHandler().handle(
          startEvent);
    } else {
      // 如果旧对象存在，证明为重新连接的NM
      LOG.info("Reconnect from the node at: " + host);
      // 临时取消该NM的心跳监控
      this.nmLivelinessMonitor.unregister(nodeId);

      if (CollectionUtils.isEmpty(request.getRunningApplications())
          && rmNode.getState() != NodeState.DECOMMISSIONING
          && rmNode.getHttpPort() != oldNode.getHttpPort()) {
        // 新的NM与旧的NM不是同一个节点，需要进行替换操作
        // Reconnected node differs, so replace old node and start new node
        // 修改metrics信息
        switch (rmNode.getState()) {
        case RUNNING:
          ClusterMetrics.getMetrics().decrNumActiveNodes();
          break;
        case UNHEALTHY:
          ClusterMetrics.getMetrics().decrNumUnhealthyNMs();
          break;
        default:
          LOG.debug("Unexpected Rmnode state");
        }
        // 新建NodeRemovedSchedulerEvent并进行处理
        this.rmContext.getDispatcher().getEventHandler()
            .handle(new NodeRemovedSchedulerEvent(rmNode));
        // 将新的Node写入RMContext，并新建RMNodeStartedEvent进行处理
        this.rmContext.getRMNodes().put(nodeId, rmNode);
        this.rmContext.getDispatcher().getEventHandler()
            .handle(new RMNodeStartedEvent(nodeId, null, null, nodeStatus));
      } else {
        // 新的NM与旧的NM是同一个节点，重置心跳
        // Reset heartbeat ID since node just restarted.
        oldNode.resetLastNodeHeartBeatResponse();
        // 新建RMNodeReconnectEvent并进行处理
        this.rmContext.getDispatcher().getEventHandler()
            .handle(new RMNodeReconnectEvent(nodeId, rmNode,
                request.getRunningApplications(),
                request.getNMContainerStatuses()));
      }
    }
    // 清除节点的NMToken
    // On every node manager register we will be clearing NMToken keys if
    // present for any running application.
    this.nmTokenSecretManager.removeNodeKey(nodeId);
    // 将节点加入心跳监控
    this.nmLivelinessMonitor.register(nodeId);

    // 如果没有开启RM的工作保留重启机制，额外处理NMContainerStatus
    // Handle received container status, this should be processed after new
    // RMNode inserted
    if (!rmContext.isWorkPreservingRecoveryEnabled()) {
      if (!request.getNMContainerStatuses().isEmpty()) {
        LOG.info("received container statuses on node manager register :"
            + request.getNMContainerStatuses());
        for (NMContainerStatus status : request.getNMContainerStatuses()) {
          handleNMContainerStatus(status, nodeId);
        }
      }
    }

    // 处理节点标签，更新到RM的NodeLabelManager
    // Update node's labels to RM's NodeLabelManager.
    Set<String> nodeLabels = NodeLabelsUtils.convertToStringSet(
        request.getNodeLabels());
    if (isDistributedNodeLabelsConf && nodeLabels != null) {
      // yarn.node-labels.configuration-type=distributed，默认为centralized
      // 同时在Response里加入更新回复
      try {
        updateNodeLabelsFromNMReport(nodeLabels, nodeId);
        response.setAreNodeLabelsAcceptedByRM(true);
      } catch (IOException ex) {
        // Ensure the exception is captured in the response
        response.setDiagnosticsMessage(ex.getMessage());
        response.setAreNodeLabelsAcceptedByRM(false);
      }
    } else if (isDelegatedCentralizedNodeLabelsConf) {
      // yarn.node-labels.configuration-type=delegated-centralized，默认为centralized
      this.rmContext.getRMDelegatedNodeLabelsUpdater().updateNodeLabels(nodeId);
    }

    // 处理节点属性，更新到RM的NodeAttributesManager
    // Update node's attributes to RM's NodeAttributesManager.
    if (request.getNodeAttributes() != null) {
      try {
        // 如果有必要，更新节点属性，并且在Response里加入更新回复
        // update node attributes if necessary then update heartbeat response
        updateNodeAttributesIfNecessary(nodeId, request.getNodeAttributes());
        response.setAreNodeAttributesAcceptedByRM(true);
      } catch (IOException ex) {
        //ensure the error message is captured and sent across in response
        String errorMsg = response.getDiagnosticsMessage() == null ?
            ex.getMessage() :
            response.getDiagnosticsMessage() + "\n" + ex.getMessage();
        response.setDiagnosticsMessage(errorMsg);
        response.setAreNodeAttributesAcceptedByRM(false);
      }
    }

    // 构建日志message，包括host/port/capability/nodeId/nodeLabel/nodeAttribute信息
    StringBuilder message = new StringBuilder();
    message.append("NodeManager from node ").append(host).append("(cmPort: ")
        .append(cmPort).append(" httpPort: ");
    message.append(httpPort).append(") ")
        .append("registered with capability: ").append(capability);
    message.append(", assigned nodeId ").append(nodeId);
    if (response.getAreNodeLabelsAcceptedByRM()) {
      message.append(", node labels { ").append(
          StringUtils.join(",", nodeLabels) + " } ");
    }
    if (response.getAreNodeAttributesAcceptedByRM()) {
      message.append(", node attributes { ")
          .append(request.getNodeAttributes() + " } ");
    }

    LOG.info(message.toString());
    // 写入剩余的Response信息，NM注册完毕
    response.setNodeAction(NodeAction.NORMAL);
    response.setRMIdentifier(ResourceManager.getClusterTimeStamp());
    response.setRMVersion(YarnVersionInfo.getVersion());
    return response;
  }

  // 处理NM心跳的服务端代码
  @SuppressWarnings("unchecked")
  @Override
  public NodeHeartbeatResponse nodeHeartbeat(NodeHeartbeatRequest request)
      throws YarnException, IOException {

    // 获取当前NodeStatus
    NodeStatus remoteNodeStatus = request.getNodeStatus();
    // 处理心跳的步骤
    // 1. 检查是否为可用的节点（没有被加入黑名单）
    // 2. 检查是否是注册过的节点
    // 3. 检查是否是全新的心跳（而不是重复的心跳）
    // 4. 为RMNode发送healthStatus健康状态
    // 5. 如果分布式节点标签的功能开启，则更新节点标签
    /**
     * Here is the node heartbeat sequence...
     * 1. Check if it's a valid (i.e. not excluded) node
     * 2. Check if it's a registered node
     * 3. Check if it's a 'fresh' heartbeat i.e. not duplicate heartbeat
     * 4. Send healthStatus to RMNode
     * 5. Update node's labels if distributed Node Labels configuration is enabled
     */
    NodeId nodeId = remoteNodeStatus.getNodeId();

    // 1. 检查是否为可用的节点（没有被加入黑名单），并且确认不是退役节点
    //    这里与NM注册的逻辑类似，如果不可用，直接shutdown
    // 1. Check if it's a valid (i.e. not excluded) node, if not, see if it is
    // in decommissioning.
    if (!this.nodesListManager.isValidNode(nodeId.getHost())
        && !isNodeInDecommissioning(nodeId)) {
      String message =
          "Disallowed NodeManager nodeId: " + nodeId + " hostname: "
              + nodeId.getHost();
      LOG.info(message);
      return YarnServerBuilderUtils.newNodeHeartbeatResponse(
          NodeAction.SHUTDOWN, message);
    }

    // 2. 检查是否是注册过的节点
    // 2. Check if it's a registered node
    RMNode rmNode = this.rmContext.getRMNodes().get(nodeId);
    if (rmNode == null) {
      // 发现节点未被注册过，返回resync指令
      /* node does not exist */
      String message = "Node not found resyncing " + remoteNodeStatus.getNodeId();
      LOG.info(message);
      return YarnServerBuilderUtils.newNodeHeartbeatResponse(NodeAction.RESYNC,
          message);
    }

    // 更新NMLivelinessMonitor中的ping时间，更新DecommissioningNodesWatcher
    // Send ping
    this.nmLivelinessMonitor.receivedPing(nodeId);
    this.decommissioningWatcher.update(rmNode, remoteNodeStatus);

    // 3. 检查是否是全新的心跳（而不是重复的心跳）
    // 3. Check if it's a 'fresh' heartbeat i.e. not duplicate heartbeat
    // 获取到上次的心跳信息
    NodeHeartbeatResponse lastNodeHeartbeatResponse = rmNode.getLastNodeHeartBeatResponse();
    // 对比ResponseID（注意本次ResponseID需要先+1），如果相同，则为重复，直接返回上次的Response
    if (getNextResponseId(
        remoteNodeStatus.getResponseId()) == lastNodeHeartbeatResponse
            .getResponseId()) {
      LOG.info("Received duplicate heartbeat from node "
          + rmNode.getNodeAddress()+ " responseId=" + remoteNodeStatus.getResponseId());
      return lastNodeHeartbeatResponse;
    } else if (remoteNodeStatus.getResponseId() != lastNodeHeartbeatResponse
        .getResponseId()) {
      // ResponseID对不上（不为上次+1），则心跳出现了错乱，直接重启
      String message =
          "Too far behind rm response id:"
              + lastNodeHeartbeatResponse.getResponseId() + " nm response id:"
              + remoteNodeStatus.getResponseId();
      LOG.info(message);
      // 这里设置了一个todo，以后可能会有优化
      // TODO: Just sending reboot is not enough. Think more.
      this.rmContext.getDispatcher().getEventHandler().handle(
          new RMNodeEvent(nodeId, RMNodeEventType.REBOOTING));
      // 随后返回resync指令
      return YarnServerBuilderUtils.newNodeHeartbeatResponse(NodeAction.RESYNC,
          message);
    }

    // 检测退役中节点是否已经准备好完成退役
    // Evaluate whether a DECOMMISSIONING node is ready to be DECOMMISSIONED.
    if (rmNode.getState() == NodeState.DECOMMISSIONING &&
        decommissioningWatcher.checkReadyToBeDecommissioned(
            rmNode.getNodeID())) {
      String message = "DECOMMISSIONING " + nodeId +
          " is ready to be decommissioned";
      LOG.info(message);
      // 发送退役事件，取消注册心跳，并且发送shutdown指令
      this.rmContext.getDispatcher().getEventHandler().handle(
          new RMNodeEvent(nodeId, RMNodeEventType.DECOMMISSION));
      this.nmLivelinessMonitor.unregister(nodeId);
      return YarnServerBuilderUtils.newNodeHeartbeatResponse(
          NodeAction.SHUTDOWN, message);
    }

    // 如果开启了Timeline v2，更新collectors信息
    // yarn.timeline-service.version
    if (timelineServiceV2Enabled) {
      // Check & update collectors info from request.
      updateAppCollectorsMap(request);
    }

    // 心跳Response
    // Heartbeat response
    // 获取下一次心跳间隔，可以在配置里进行静态或动态配置，默认1000ms
    long newInterval = nextHeartBeatInterval;
    // 如果开启了动态规划心跳，则进行心跳间隔计算
    // yarn.resourcemanager.nodemanagers.heartbeat-interval-scaling-enable，默认为false
    if (heartBeatIntervalScalingEnable) {
      newInterval = rmNode.calculateHeartBeatInterval(
          nextHeartBeatInterval, heartBeatIntervalMin,
          heartBeatIntervalMax, heartBeatIntervalSpeedupFactor,
          heartBeatIntervalSlowdownFactor);
    }
    // 创建心跳Response对象
    NodeHeartbeatResponse nodeHeartBeatResponse =
        YarnServerBuilderUtils.newNodeHeartbeatResponse(
            getNextResponseId(lastNodeHeartbeatResponse.getResponseId()), // 将ResponseID+1
            NodeAction.NORMAL, null, null, null, null, newInterval);
    rmNode.setAndUpdateNodeHeartbeatResponse(nodeHeartBeatResponse);

    // Token相关
    populateKeys(request, nodeHeartBeatResponse);

    populateTokenSequenceNo(request, nodeHeartBeatResponse);

    // 如果开启了Timeline v2，更新collectors信息
    // yarn.timeline-service.version
    if (timelineServiceV2Enabled) {
      // Return collectors' map that NM needs to know
      setAppCollectorsMapToResponse(rmNode.getRunningApps(),
          nodeHeartBeatResponse);
    }

    // 4. 将心跳状态发送到RMNode，保存最新Response
    // 4. Send status to RMNode, saving the latest response.
    // 构建RMNodeStatusEvent对象并进行处理
    RMNodeStatusEvent nodeStatusEvent =
        new RMNodeStatusEvent(nodeId, remoteNodeStatus);
    if (request.getLogAggregationReportsForApps() != null
        && !request.getLogAggregationReportsForApps().isEmpty()) {
      nodeStatusEvent.setLogAggregationReportsForApps(request
        .getLogAggregationReportsForApps());
    }
    this.rmContext.getDispatcher().getEventHandler().handle(nodeStatusEvent);

    // 5. 向NodeLabelManager更新节点标签
    // 5. Update node's labels to RM's NodeLabelManager.
    if (isDistributedNodeLabelsConf && request.getNodeLabels() != null) {
      // yarn.node-labels.configuration-type=distributed，默认为centralized
      // 同时在Response里加入更新回复
      try {
        updateNodeLabelsFromNMReport(
            NodeLabelsUtils.convertToStringSet(request.getNodeLabels()),
            nodeId);
        nodeHeartBeatResponse.setAreNodeLabelsAcceptedByRM(true);
      } catch (IOException ex) {
        //ensure the error message is captured and sent across in response
        nodeHeartBeatResponse.setDiagnosticsMessage(ex.getMessage());
        nodeHeartBeatResponse.setAreNodeLabelsAcceptedByRM(false);
      }
    }

    // 6. 检查是否从dynamic-resources.xml里读取该节点资源数据，如是，在Response里添加更新信息
    // 6. check if node's capacity is load from dynamic-resources.xml
    // if so, send updated resource back to NM.
    String nid = nodeId.toString();
    Resource capability = loadNodeResourceFromDRConfiguration(nid);
    // sync back with new resource if not null.
    if (capability != null) {
      nodeHeartBeatResponse.setResource(capability);
    }
    // 检查是否有来自AdminService的资源更新
    // Check if we got an event (AdminService) that updated the resources
    if (rmNode.isUpdatedCapability()) {
      nodeHeartBeatResponse.setResource(rmNode.getTotalCapability());
      rmNode.resetUpdatedCapability();
    }

    // 7. 将container队列限制发送到节点，节点利用此信息处理排队的containers
    // 7. Send Container Queuing Limits back to the Node. This will be used by
    // the node to truncate the number of Containers queued for execution.
    if (this.rmContext.getNodeManagerQueueLimitCalculator() != null) {
      nodeHeartBeatResponse.setContainerQueuingLimit(
          this.rmContext.getNodeManagerQueueLimitCalculator()
              .createContainerQueuingLimit());
    }

    // 8. 获取节点属性，更新到NodeAttributesManager
    // 8. Get node's attributes and update node-to-attributes mapping
    // in RMNodeAttributeManager.
    if (request.getNodeAttributes() != null) {
      try {
        // 如果有必要，更新节点属性，并且在Response里加入更新回复
        // update node attributes if necessary then update heartbeat response
        updateNodeAttributesIfNecessary(nodeId, request.getNodeAttributes());
        nodeHeartBeatResponse.setAreNodeAttributesAcceptedByRM(true);
      } catch (IOException ex) {
        //ensure the error message is captured and sent across in response
        String errorMsg =
            nodeHeartBeatResponse.getDiagnosticsMessage() == null ?
                ex.getMessage() :
                nodeHeartBeatResponse.getDiagnosticsMessage() + "\n" + ex
                    .getMessage();
        nodeHeartBeatResponse.setDiagnosticsMessage(errorMsg);
        nodeHeartBeatResponse.setAreNodeAttributesAcceptedByRM(false);
      }
    }

    return nodeHeartBeatResponse;
  }

  /**
   * Update node attributes if necessary.
   * @param nodeId - node id
   * @param nodeAttributes - node attributes
   * @return true if updated
   * @throws IOException if prefix type is not distributed
   */
  private void updateNodeAttributesIfNecessary(NodeId nodeId,
      Set<NodeAttribute> nodeAttributes) throws IOException {
    if (LOG.isDebugEnabled()) {
      nodeAttributes.forEach(nodeAttribute -> LOG.debug(
          nodeId.toString() + " ATTRIBUTE : " + nodeAttribute.toString()));
    }

    // Validate attributes
    if (!nodeAttributes.stream().allMatch(
        nodeAttribute -> NodeAttribute.PREFIX_DISTRIBUTED
            .equals(nodeAttribute.getAttributeKey().getAttributePrefix()))) {
      // All attributes must be in same prefix: nm.yarn.io.
      // Since we have the checks in NM to make sure attributes reported
      // in HB are with correct prefix, so it should not reach here.
      throw new IOException("Reject invalid node attributes from host: "
          + nodeId.toString() + ", attributes in HB must have prefix "
          + NodeAttribute.PREFIX_DISTRIBUTED);
    }
    // Replace all distributed node attributes associated with this host
    // with the new reported attributes in node attribute manager.
    Set<NodeAttribute> currentNodeAttributes =
        this.rmContext.getNodeAttributesManager()
            .getAttributesForNode(nodeId.getHost()).keySet();
    if (!currentNodeAttributes.isEmpty()) {
      currentNodeAttributes = NodeLabelUtil
          .filterAttributesByPrefix(currentNodeAttributes,
              NodeAttribute.PREFIX_DISTRIBUTED);
    }
    if (!NodeLabelUtil
        .isNodeAttributesEquals(nodeAttributes, currentNodeAttributes)) {
      this.rmContext.getNodeAttributesManager()
          .replaceNodeAttributes(NodeAttribute.PREFIX_DISTRIBUTED,
              ImmutableMap.of(nodeId.getHost(), nodeAttributes));
    } else {
      LOG.debug("Skip updating node attributes since there is no change"
          +" for {} : {}", nodeId, nodeAttributes);
    }
  }

  private int getNextResponseId(int responseId) {
    // Loop between 0 and Integer.MAX_VALUE
    return (responseId + 1) & Integer.MAX_VALUE;
  }

  private void setAppCollectorsMapToResponse(
      List<ApplicationId> runningApps, NodeHeartbeatResponse response) {
    Map<ApplicationId, AppCollectorData> liveAppCollectorsMap = new
        HashMap<>();
    Map<ApplicationId, RMApp> rmApps = rmContext.getRMApps();
    // Set collectors for all running apps on this node.
    for (ApplicationId appId : runningApps) {
      RMApp app = rmApps.get(appId);
      if (app != null) {
        AppCollectorData appCollectorData = rmApps.get(appId)
            .getCollectorData();
        if (appCollectorData != null) {
          liveAppCollectorsMap.put(appId, appCollectorData);
        } else {
          LOG.debug("Collector for applicaton: {} hasn't registered yet!",
              appId);
        }
      }
    }
    response.setAppCollectors(liveAppCollectorsMap);
  }

  private void updateAppCollectorsMap(NodeHeartbeatRequest request) {
    Map<ApplicationId, AppCollectorData> registeringCollectorsMap =
        request.getRegisteringCollectors();
    if (registeringCollectorsMap != null
        && !registeringCollectorsMap.isEmpty()) {
      Map<ApplicationId, RMApp> rmApps = rmContext.getRMApps();
      for (Map.Entry<ApplicationId, AppCollectorData> entry:
          registeringCollectorsMap.entrySet()) {
        ApplicationId appId = entry.getKey();
        AppCollectorData collectorData = entry.getValue();
        if (collectorData != null) {
          if (!collectorData.isStamped()) {
            // Stamp the collector if we have not done so
            collectorData.setRMIdentifier(
                ResourceManager.getClusterTimeStamp());
            collectorData.setVersion(
                timelineCollectorVersion.getAndIncrement());
          }
          RMApp rmApp = rmApps.get(appId);
          if (rmApp == null) {
            LOG.warn("Cannot update collector info because application ID: " +
                appId + " is not found in RMContext!");
          } else {
            synchronized (rmApp) {
              AppCollectorData previousCollectorData = rmApp.getCollectorData();
              if (AppCollectorData.happensBefore(previousCollectorData,
                  collectorData)) {
                // Sending collector update event.
                // Note: RM has to store the newly received collector data
                // synchronously. Otherwise, the RM may send out stale collector
                // data before this update is done, and the RM then crashes, the
                // newly updated collector data will get lost.
                LOG.info("Update collector information for application " + appId
                    + " with new address: " + collectorData.getCollectorAddr()
                    + " timestamp: " + collectorData.getRMIdentifier()
                    + ", " + collectorData.getVersion());
                ((RMAppImpl) rmApp).setCollectorData(collectorData);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Check if node in decommissioning state.
   * @param nodeId
   */
  private boolean isNodeInDecommissioning(NodeId nodeId) {
    RMNode rmNode = this.rmContext.getRMNodes().get(nodeId);

    if (rmNode != null) {
      NodeState state = rmNode.getState();

      if (state == NodeState.DECOMMISSIONING ||
          (state == NodeState.RUNNING &&
          this.nodesListManager.isGracefullyDecommissionableNode(rmNode))) {
        return true;
      }
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UnRegisterNodeManagerResponse unRegisterNodeManager(
      UnRegisterNodeManagerRequest request) throws YarnException, IOException {
    UnRegisterNodeManagerResponse response = recordFactory
        .newRecordInstance(UnRegisterNodeManagerResponse.class);
    NodeId nodeId = request.getNodeId();
    RMNode rmNode = this.rmContext.getRMNodes().get(nodeId);
    if (rmNode == null) {
      LOG.info("Node not found, ignoring the unregister from node id : "
          + nodeId);
      return response;
    }
    LOG.info("Node with node id : " + nodeId
        + " has shutdown, hence unregistering the node.");
    this.nmLivelinessMonitor.unregister(nodeId);
    this.rmContext.getDispatcher().getEventHandler()
        .handle(new RMNodeEvent(nodeId, RMNodeEventType.SHUTDOWN));
    return response;
  }

  private void updateNodeLabelsFromNMReport(Set<String> nodeLabels,
      NodeId nodeId) throws IOException {
    try {
      Map<NodeId, Set<String>> labelsUpdate =
          new HashMap<NodeId, Set<String>>();
      labelsUpdate.put(nodeId, nodeLabels);
      this.rmContext.getNodeLabelManager().replaceLabelsOnNode(labelsUpdate);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Node Labels {" + StringUtils.join(",", nodeLabels)
            + "} from Node " + nodeId + " were Accepted from RM");
      }
    } catch (IOException ex) {
      StringBuilder errorMessage = new StringBuilder();
      errorMessage.append("Node Labels {")
          .append(StringUtils.join(",", nodeLabels))
          .append("} reported from NM with ID ").append(nodeId)
          .append(" was rejected from RM with exception message as : ")
          .append(ex.getMessage());
      LOG.error(errorMessage.toString(), ex);
      throw new IOException(errorMessage.toString(), ex);
    }
  }

  private void populateKeys(NodeHeartbeatRequest request,
      NodeHeartbeatResponse nodeHeartBeatResponse) {

    // Check if node's masterKey needs to be updated and if the currentKey has
    // roller over, send it across

    // ContainerTokenMasterKey

    MasterKey nextMasterKeyForNode =
        this.containerTokenSecretManager.getNextKey();
    if (nextMasterKeyForNode != null
        && (request.getLastKnownContainerTokenMasterKey().getKeyId()
            != nextMasterKeyForNode.getKeyId())) {
      nodeHeartBeatResponse.setContainerTokenMasterKey(nextMasterKeyForNode);
    }

    // NMTokenMasterKey

    nextMasterKeyForNode = this.nmTokenSecretManager.getNextKey();
    if (nextMasterKeyForNode != null
        && (request.getLastKnownNMTokenMasterKey().getKeyId() 
            != nextMasterKeyForNode.getKeyId())) {
      nodeHeartBeatResponse.setNMTokenMasterKey(nextMasterKeyForNode);
    }
  }

  private Resource loadNodeResourceFromDRConfiguration(String nodeId) {
    // check if node's capacity is loaded from dynamic-resources.xml
    this.readLock.lock();
    try {
      String[] nodes = this.drConf.getNodes();
      if (nodes != null && Arrays.asList(nodes).contains(nodeId)) {
        return Resource.newInstance(this.drConf.getMemoryPerNode(nodeId),
            this.drConf.getVcoresPerNode(nodeId));
      } else {
        return null;
      }
    } finally {
      this.readLock.unlock();
    }
  }

  /**
   * resolving the network topology.
   * @param hostName the hostname of this node.
   * @return the resolved {@link Node} for this nodemanager.
   */
  public static Node resolve(String hostName) {
    return RackResolver.resolve(hostName);
  }

  void refreshServiceAcls(Configuration configuration, 
      PolicyProvider policyProvider) {
    this.server.refreshServiceAclWithLoadedConfiguration(configuration,
        policyProvider);
  }

  @VisibleForTesting
  public Server getServer() {
    return this.server;
  }

  private void populateTokenSequenceNo(NodeHeartbeatRequest request,
      NodeHeartbeatResponse nodeHeartBeatResponse) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Token sequence no received from heartbeat request: "
          + request.getTokenSequenceNo() + ". Current token sequeunce no: "
          + this.rmContext.getTokenSequenceNo()
          + ". System credentials for apps size: "
          + rmContext.getSystemCredentialsForApps().size());
    }
    if(request.getTokenSequenceNo() != this.rmContext.getTokenSequenceNo()) {
      if (!rmContext.getSystemCredentialsForApps().isEmpty()) {
        LOG.debug("Sending System credentials for apps as part of"
            + " NodeHeartbeat response.");
        nodeHeartBeatResponse
            .setSystemCredentialsForApps(
                rmContext.getSystemCredentialsForApps().values());
      }
    }
    nodeHeartBeatResponse.setTokenSequenceNo(
        this.rmContext.getTokenSequenceNo());
  }
}
