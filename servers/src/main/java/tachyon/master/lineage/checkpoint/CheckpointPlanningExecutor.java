/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.master.lineage.checkpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import tachyon.Constants;
import tachyon.HeartbeatExecutor;
import tachyon.conf.TachyonConf;

/**
 * Executes a checkpoint manager.
 */
public final class CheckpointPlanningExecutor implements HeartbeatExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

  private final TachyonConf mTachyonConf;
  private final CheckpointManager mCheckpointManager;
  private final CheckpointScheduler mScheduler;

  public CheckpointPlanningExecutor(TachyonConf conf, CheckpointManager checkpointManager) {
    mCheckpointManager = Preconditions.checkNotNull(checkpointManager);
    mTachyonConf = Preconditions.checkNotNull(conf);
    mScheduler = CheckpointScheduler.Factory.createScheduler(mTachyonConf,
        mCheckpointManager.getLineageStoreView());
  }

  @Override
  public void heartbeat() {
    CheckpointPlan plan = mScheduler.schedule(mCheckpointManager.getLineageStoreView());
    if (!plan.isEmtpy()) {
      LOG.info("Checkpoint scheduler created the plan: " + plan);
    }
    mCheckpointManager.acceptPlan(plan);
  }
}