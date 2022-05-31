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

import org.apache.hadoop.yarn.ams.ApplicationMasterServiceContext;
import org.apache.hadoop.yarn.ams.ApplicationMasterServiceProcessor;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This maintains a chain of {@link ApplicationMasterServiceProcessor}s.
 */
class AMSProcessingChain implements ApplicationMasterServiceProcessor {

  private static final Logger LOG =
      LoggerFactory.getLogger(AMSProcessingChain.class);

  private ApplicationMasterServiceProcessor head;
  private RMContext rmContext;

  // 必须有根处理程序，否则报错
  /**
   * This has to be initialized with at-least 1 Processor.
   * @param rootProcessor Root processor.
   */
  AMSProcessingChain(ApplicationMasterServiceProcessor rootProcessor) {
    if (rootProcessor == null) {
      throw new YarnRuntimeException("No root ApplicationMasterService" +
          "Processor specified for the processing chain..");
    }
    this.head = rootProcessor;
  }

  // 初始化方法，目前并没有使用链条的下一个处理程序，显式将下一个程序设置为null
  @Override
  public void init(ApplicationMasterServiceContext amsContext,
      ApplicationMasterServiceProcessor nextProcessor) {
    LOG.info("Initializing AMS Processing chain. Root Processor=["
        + this.head.getClass().getName() + "].");
    this.rmContext = (RMContext)amsContext;
    // The head is initialized with a null 'next' processor
    this.head.init(amsContext, null);
  }

  // 将一个处理程序放在链条头部
  /**
   * Add an processor to the top of the chain.
   * @param processor ApplicationMasterServiceProcessor
   */
  public synchronized void addProcessor(
      ApplicationMasterServiceProcessor processor) {
    LOG.info("Adding [" + processor.getClass().getName() + "] tp top of" +
        " AMS Processing chain. ");
    processor.init(this.rmContext, this.head);
    this.head = processor;
  }

  @Override
  public void registerApplicationMaster(
      ApplicationAttemptId applicationAttemptId,
      RegisterApplicationMasterRequest request,
      RegisterApplicationMasterResponse resp) throws IOException, YarnException {
    this.head.registerApplicationMaster(applicationAttemptId, request, resp);
  }

  @Override
  public void allocate(ApplicationAttemptId appAttemptId,
      AllocateRequest request, AllocateResponse response) throws YarnException {
    this.head.allocate(appAttemptId, request, response);
  }

  @Override
  public void finishApplicationMaster(
      ApplicationAttemptId applicationAttemptId,
      FinishApplicationMasterRequest request,
      FinishApplicationMasterResponse response) {
    this.head.finishApplicationMaster(applicationAttemptId, request, response);
  }
}
