/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.ml.util

import java.util.concurrent.ExecutorService

import com.google.common.util.concurrent.MoreExecutors

import org.apache.spark.annotation.Since
import org.apache.spark.ml.param.{IntParam, Params, ParamValidators}
import org.apache.spark.util.ThreadUtils

/**
 * Common parameter for estimators trained in a multithreaded environment.
 */
private[ml] trait ParallelismParam extends Params {

  /**
   * param for the number of processes to use when running parallel one vs. rest
   * The implementation of parallel one vs. rest runs the classification for
   * each class in a separate process.
   * @group param
   */
  @Since("2.3.0")
  val parallelism = new IntParam(this, "parallelism",
    "the number of processes to use when running parallel one vs. rest", ParamValidators.gtEq(1))

  setDefault(
    parallelism -> 1
  )

  /** @group getParam */
  def getParallelism: Int = $(parallelism)

  /** @group setParam */
  @Since("2.3.0")
  def setParallelism(value: Int): this.type = {
    set(parallelism, value)
  }

  def getExecutorService: ExecutorService = {
    if (getParallelism == 1) {
      return MoreExecutors.sameThreadExecutor()
    }
    ThreadUtils
      .newDaemonCachedThreadPool(s"${this.getClass.getSimpleName}-thread-pool", getParallelism)
  }
}
