/*
 * *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.runtime;

import org.apache.hadoop.thirdparty.com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.protocol.datatransfer.IOStreamPair;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.api.records.LocalResource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.container.Container;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged.PrivilegedOperationExecutor;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerExecutionException;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerRuntime;
import org.apache.hadoop.yarn.server.nodemanager.containermanager.runtime.ContainerRuntimeContext;
import org.apache.hadoop.yarn.server.nodemanager.executor.ContainerExecContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// 本类代理所有关于LinuxContainerRuntime的操作
// 包括但不限于：DefaultLinuxContainerRuntime，DockerLinuxContainerRuntime，JavaSandboxLinuxContainerRuntime
// 或者自定义的Runtime实例
/**
 * This class is a {@link ContainerRuntime} implementation that delegates all
 * operations to a {@link DefaultLinuxContainerRuntime} instance, a
 * {@link DockerLinuxContainerRuntime} instance, a
 * {@link JavaSandboxLinuxContainerRuntime} instance, or a custom instance
 * depending on whether each instance believes the operation to be within its
 * scope.
 *
 * @see LinuxContainerRuntime#isRuntimeRequested
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable
public class DelegatingLinuxContainerRuntime implements LinuxContainerRuntime {
  private static final Logger LOG =
      LoggerFactory.getLogger(DelegatingLinuxContainerRuntime.class);
  private DefaultLinuxContainerRuntime defaultLinuxContainerRuntime;
  private DockerLinuxContainerRuntime dockerLinuxContainerRuntime;
  private RuncContainerRuntime runcContainerRuntime;
  private JavaSandboxLinuxContainerRuntime javaSandboxLinuxContainerRuntime;
  private Set<String> allowedRuntimes = new HashSet<>();
  private List<LinuxContainerRuntime> pluggableRuntimes = new ArrayList<>();

  @Override
  public void initialize(Configuration conf, Context nmContext)
      throws ContainerExecutionException {
    String[] configuredRuntimes = conf.getTrimmedStrings(
        YarnConfiguration.LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES,
        YarnConfiguration.DEFAULT_LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES);
    for (String configuredRuntime : configuredRuntimes) {
      String normRuntime = configuredRuntime.toUpperCase();
      allowedRuntimes.add(normRuntime);
      if (isPluggableRuntime(normRuntime)) {
        LinuxContainerRuntime runtime = createPluggableRuntime(conf,
            configuredRuntime);
        runtime.initialize(conf, nmContext);
        pluggableRuntimes.add(runtime);
      }
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.JAVASANDBOX.name())) {
      javaSandboxLinuxContainerRuntime = new JavaSandboxLinuxContainerRuntime(
          PrivilegedOperationExecutor.getInstance(conf));
      javaSandboxLinuxContainerRuntime.initialize(conf, nmContext);
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.DOCKER.name())) {
      dockerLinuxContainerRuntime = new DockerLinuxContainerRuntime(
          PrivilegedOperationExecutor.getInstance(conf));
      dockerLinuxContainerRuntime.initialize(conf, nmContext);
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.RUNC.name())) {
      runcContainerRuntime = new RuncContainerRuntime(
          PrivilegedOperationExecutor.getInstance(conf));
      runcContainerRuntime.initialize(conf, nmContext);
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.DEFAULT.name())) {
      defaultLinuxContainerRuntime = new DefaultLinuxContainerRuntime(
          PrivilegedOperationExecutor.getInstance(conf));
      defaultLinuxContainerRuntime.initialize(conf, nmContext);
    }
  }

  @Override
  public boolean isRuntimeRequested(Map<String, String> env) {
    return true;
  }

  @VisibleForTesting
  LinuxContainerRuntime pickContainerRuntime(
      Map<String, String> environment) throws ContainerExecutionException {
    LinuxContainerRuntime runtime;
    // 先检查Java沙箱，避免DockerRuntime被优先选择
    //Sandbox checked first to ensure DockerRuntime doesn't circumvent controls
    if (javaSandboxLinuxContainerRuntime != null &&
        javaSandboxLinuxContainerRuntime.isRuntimeRequested(environment)){
      runtime = javaSandboxLinuxContainerRuntime;
    } else if (dockerLinuxContainerRuntime != null &&
        dockerLinuxContainerRuntime.isRuntimeRequested(environment)) {
      runtime = dockerLinuxContainerRuntime;
    } else if (runcContainerRuntime != null &&
        runcContainerRuntime.isRuntimeRequested(environment)) {
      runtime = runcContainerRuntime;
    } else {
      LinuxContainerRuntime pluggableRuntime = pickPluggableRuntime(
          environment);
      if (pluggableRuntime != null) {
        runtime = pluggableRuntime;
      } else if (defaultLinuxContainerRuntime != null &&
          defaultLinuxContainerRuntime.isRuntimeRequested(environment)) {
        runtime = defaultLinuxContainerRuntime;
      } else {
        throw new ContainerExecutionException("Requested runtime not allowed.");
      }
    }

    LOG.debug("Using container runtime: {}", runtime.getClass()
          .getSimpleName());

    return runtime;
  }

  private LinuxContainerRuntime pickPluggableRuntime(
      Map<String, String> environment) {
    for (LinuxContainerRuntime runtime : pluggableRuntimes) {
      if (runtime.isRuntimeRequested(environment)) {
        return runtime;
      }
    }
    return null;
  }

  private LinuxContainerRuntime pickContainerRuntime(Container container)
      throws ContainerExecutionException {
    return pickContainerRuntime(container.getLaunchContext().getEnvironment());
  }

  @Override
  public void prepareContainer(ContainerRuntimeContext ctx)
      throws ContainerExecutionException {
    LinuxContainerRuntime runtime = pickContainerRuntime(ctx.getContainer());
    runtime.prepareContainer(ctx);
  }

  @Override
  public void launchContainer(ContainerRuntimeContext ctx)
      throws ContainerExecutionException {
    Container container = ctx.getContainer();
    LinuxContainerRuntime runtime = pickContainerRuntime(container);

    runtime.launchContainer(ctx);
  }

  @Override
  public void relaunchContainer(ContainerRuntimeContext ctx)
      throws ContainerExecutionException {
    Container container = ctx.getContainer();
    LinuxContainerRuntime runtime = pickContainerRuntime(container);

    runtime.relaunchContainer(ctx);
  }

  @Override
  public void signalContainer(ContainerRuntimeContext ctx)
      throws ContainerExecutionException {
    Container container = ctx.getContainer();
    LinuxContainerRuntime runtime = pickContainerRuntime(container);

    runtime.signalContainer(ctx);
  }

  @Override
  public void reapContainer(ContainerRuntimeContext ctx)
      throws ContainerExecutionException {
    Container container = ctx.getContainer();
    LinuxContainerRuntime runtime = pickContainerRuntime(container);

    runtime.reapContainer(ctx);
  }

  @Override
  public String[] getIpAndHost(Container container)
      throws ContainerExecutionException {
    LinuxContainerRuntime runtime = pickContainerRuntime(container);
    return runtime.getIpAndHost(container);
  }

  @Override
  public String getExposedPorts(Container container)
      throws ContainerExecutionException {
    LinuxContainerRuntime runtime = pickContainerRuntime(container);
    return runtime.getExposedPorts(container);
  }

  private boolean isPluggableRuntime(String runtimeType) {
    for (LinuxContainerRuntimeConstants.RuntimeType type :
        LinuxContainerRuntimeConstants.RuntimeType.values()) {
      if (type.name().equalsIgnoreCase(runtimeType)) {
        return false;
      }
    }
    return true;
  }

  private LinuxContainerRuntime createPluggableRuntime(Configuration conf,
      String runtimeType) throws ContainerExecutionException {
    String confKey = String.format(
        YarnConfiguration.LINUX_CONTAINER_RUNTIME_CLASS_FMT, runtimeType);
    Class<? extends LinuxContainerRuntime> clazz = conf.getClass(
        confKey, null, LinuxContainerRuntime.class);
    if (clazz == null) {
      throw new ContainerExecutionException("Invalid runtime set in "
          + YarnConfiguration.LINUX_CONTAINER_RUNTIME_ALLOWED_RUNTIMES + " : "
          + runtimeType + " : Missing configuration " + confKey);
    }
    return ReflectionUtils.newInstance(clazz, conf);
  }

  @VisibleForTesting
  boolean isRuntimeAllowed(String runtimeType) {
    return runtimeType != null && allowedRuntimes.contains(
        runtimeType.toUpperCase());
  }

  @Override
  public IOStreamPair execContainer(ContainerExecContext ctx)
      throws ContainerExecutionException {
    Container container = ctx.getContainer();
    LinuxContainerRuntime runtime = pickContainerRuntime(container);
    return runtime.execContainer(ctx);
  }


  @Override
  public Map<String, LocalResource> getLocalResources(Container container)
      throws IOException {
    try {
      LinuxContainerRuntime runtime = pickContainerRuntime(container);
      return runtime.getLocalResources(container);
    } catch (ContainerExecutionException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void start() {
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.JAVASANDBOX.name())) {
      javaSandboxLinuxContainerRuntime.start();
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.DOCKER.name())) {
      dockerLinuxContainerRuntime.start();
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.RUNC.name())) {
      runcContainerRuntime.start();
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.DEFAULT.name())) {
      defaultLinuxContainerRuntime.start();
    }

  }

  @Override
  public void stop() {
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.JAVASANDBOX.name())) {
      javaSandboxLinuxContainerRuntime.stop();
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.DOCKER.name())) {
      dockerLinuxContainerRuntime.stop();
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.RUNC.name())) {
      runcContainerRuntime.stop();
    }
    if (isRuntimeAllowed(
        LinuxContainerRuntimeConstants.RuntimeType.DEFAULT.name())) {
      defaultLinuxContainerRuntime.stop();
    }

  }

}