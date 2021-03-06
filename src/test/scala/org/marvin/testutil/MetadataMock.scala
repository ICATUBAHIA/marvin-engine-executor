/*
 * Copyright [2017] [B2W Digital]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.marvin.testutil

import org.marvin.model.{EngineActionMetadata, EngineMetadata}

object MetadataMock {

  def simpleMockedMetadata(): EngineMetadata = {
    EngineMetadata(
      name = "test",
      actions = List[EngineActionMetadata](
        new EngineActionMetadata(name="predictor", actionType="online", port=777, host="localhost", artifactsToPersist=List(), artifactsToLoad=List("model")),
        new EngineActionMetadata(name="acquisitor", actionType="batch", port=778, host="localhost", artifactsToPersist=List("initial_dataset"), artifactsToLoad=List()),
        new EngineActionMetadata(name="tpreparator", actionType="batch", port=779, host="localhost", artifactsToPersist=List("dataset"), artifactsToLoad=List("initial_dataset")),
        new EngineActionMetadata(name="trainer", actionType="batch", port=780, host="localhost", artifactsToPersist=List("model"), artifactsToLoad=List("dataset")),
        new EngineActionMetadata(name="evaluator", actionType="batch", port=781, host="localhost", artifactsToPersist=List("metrics"), artifactsToLoad=List("dataset", "model")),
        new EngineActionMetadata(name="feedback", actionType="online", port=782, host="localhost", artifactsToPersist=List(), artifactsToLoad=List())
      ),
      artifactsRemotePath = "",
      artifactManagerType = "HDFS",
      s3BucketName = "marvin-artifact-bucket",
      batchActionTimeout = 100,
      engineType = "python",
      hdfsHost = "",
      healthCheckTimeout = 100,
      onlineActionTimeout = 100,
      pipelineActions = List("acquisitor", "tpreparator"),
      reloadStateTimeout = Some(500),
      reloadTimeout = 100,
      version = "1"
    )
  }

}
