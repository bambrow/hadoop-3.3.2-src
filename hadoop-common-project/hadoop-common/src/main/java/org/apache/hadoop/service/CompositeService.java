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

package org.apache.hadoop.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.classification.InterfaceAudience.Public;
import org.apache.hadoop.classification.InterfaceStability.Evolving;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Composition of services.
 */
@Public
@Evolving
public class CompositeService extends AbstractService {

  private static final Logger LOG =
      LoggerFactory.getLogger(CompositeService.class);

  // 此变量决定如下行为：
  // 如true，只停止已经开始的服务（假设服务没有在未开始的时候提供合适的停止方法）
  // 如false，尝试停止所有服务
  // 与该策略无关的一点补充：如果子服务在初始化或开始阶段报错，则子服务会直接调用停止方法进入STOPPED状态
  /**
   * Policy on shutdown: attempt to close everything (purest) or
   * only try to close started services (which assumes
   * that the service implementations may not handle the stop() operation
   * except when started.
   * Irrespective of this policy, if a child service fails during
   * its init() or start() operations, it will have stop() called on it.
   */
  protected static final boolean STOP_ONLY_STARTED_SERVICES = false;

  private final List<Service> serviceList = new ArrayList<Service>();

  public CompositeService(String name) {
    super(name);
  }

  // 返回克隆的服务列表
  /**
   * Get a cloned list of services
   * @return a list of child services at the time of invocation -
   * added services will not be picked up.
   */
  public List<Service> getServices() {
    synchronized (serviceList) {
      return new ArrayList<Service>(serviceList);
    }
  }

  // 向服务列表里增加服务
  /**
   * Add the passed {@link Service} to the list of services managed by this
   * {@link CompositeService}
   * @param service the {@link Service} to be added
   */
  protected void addService(Service service) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Adding service " + service.getName());
    }
    synchronized (serviceList) {
      serviceList.add(service);
    }
  }

  // 检查是否是Service对象，如果是则添加服务
  /**
   * If the passed object is an instance of {@link Service},
   * add it to the list of services managed by this {@link CompositeService}
   * @param object
   * @return true if a service is added, false otherwise.
   */
  protected boolean addIfService(Object object) {
    if (object instanceof Service) {
      addService((Service) object);
      return true;
    } else {
      return false;
    }
  }

  // 在服务列表里删除服务
  protected boolean removeService(Service service) {
    LOG.debug("Removing service {}", service.getName());
    synchronized (serviceList) {
      return serviceList.remove(service);
    }
  }

  // 调用getServices获取服务列表，随后循环初始化所有服务
  protected void serviceInit(Configuration conf) throws Exception {
    List<Service> services = getServices();
    if (LOG.isDebugEnabled()) {
      LOG.debug(getName() + ": initing services, size=" + services.size());
    }
    for (Service service : services) {
      service.init(conf);
    }
    super.serviceInit(conf);
  }

  // 调用getServices获取服务列表，随后循环开启所有服务
  protected void serviceStart() throws Exception {
    List<Service> services = getServices();
    if (LOG.isDebugEnabled()) {
      LOG.debug(getName() + ": starting services, size=" + services.size());
    }
    for (Service service : services) {
      // start the service. If this fails that service
      // will be stopped and an exception raised
      service.start();
    }
    super.serviceStart();
  }

  // 取决于如上定义的停止策略，进行服务停止操作
  protected void serviceStop() throws Exception {
    //stop all services that were started
    int numOfServicesToStop = serviceList.size();
    if (LOG.isDebugEnabled()) {
      LOG.debug(getName() + ": stopping services, size=" + numOfServicesToStop);
    }
    stop(numOfServicesToStop, STOP_ONLY_STARTED_SERVICES);
    super.serviceStop();
  }

  // 实际的停止方法，倒序停止服务
  /**
   * Stop the services in reverse order
   *
   * @param numOfServicesStarted index from where the stop should work
   * @param stopOnlyStartedServices flag to say "only start services that are
   * started, not those that are NOTINITED or INITED.
   * @throws RuntimeException the first exception raised during the
   * stop process -<i>after all services are stopped</i>
   */
  private void stop(int numOfServicesStarted, boolean stopOnlyStartedServices) {
    // stop in reverse order of start
    Exception firstException = null;
    // 使用getServices获取服务列表
    List<Service> services = getServices();
    // 倒序停止服务
    for (int i = numOfServicesStarted - 1; i >= 0; i--) {
      Service service = services.get(i);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Stopping service #" + i + ": " + service);
      }
      // 检查当前服务状态
      STATE state = service.getServiceState();
      // 考虑停止策略，如果停止策略允许，则考虑INITED的服务，否则只考虑STARTED的服务
      //depending on the stop police
      if (state == STATE.STARTED 
         || (!stopOnlyStartedServices && state == STATE.INITED)) {
        // 安静停止任务，如果出错不会抛出异常，而是会记录WARN日志
        Exception ex = ServiceOperations.stopQuietly(LOG, service);
        if (ex != null && firstException == null) {
          // 保存第一个获得的异常
          firstException = ex;
        }
      }
    }
    // 所有服务停止完毕后，抛出第一个获得的异常
    //after stopping all services, rethrow the first exception raised
    if (firstException != null) {
      throw ServiceStateException.convert(firstException);
    }
  }

  /**
   * JVM Shutdown hook for CompositeService which will stop the give
   * CompositeService gracefully in case of JVM shutdown.
   */
  public static class CompositeServiceShutdownHook implements Runnable {

    private CompositeService compositeService;

    public CompositeServiceShutdownHook(CompositeService compositeService) {
      this.compositeService = compositeService;
    }

    @Override
    public void run() {
      ServiceOperations.stopQuietly(compositeService);
    }
  }

}
