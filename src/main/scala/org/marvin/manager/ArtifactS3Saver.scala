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
package org.marvin.manager

import java.io.File

import akka.Done
import akka.actor.{Actor, ActorLogging}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.GetObjectRequest
import org.apache.hadoop.fs.Path
import org.marvin.manager.ArtifactSaver.{SaveToLocal, SaveToRemote}
import org.marvin.model.EngineMetadata

class ArtifactS3Saver(metadata: EngineMetadata) extends Actor with ActorLogging {
  var S3Client: AmazonS3 = _

  override def preStart() = {
    log.info(s"${this.getClass().getCanonicalName} actor initialized...")

    //Create S3 Client with default credential informations(Environment Variable)
    S3Client = AmazonS3ClientBuilder.standard.withRegion(System.getenv("AWS_DEFAULT_REGION")).build

    log.info("Amazon S3 client initialized...")
  }

  def generatePaths(artifactName: String, protocol: String): Map[String, Path] = {
    var artifactsRemotePath: String = null
    if(metadata.artifactsRemotePath.startsWith("/")){
      artifactsRemotePath = metadata.artifactsRemotePath.substring(1)
    }
    Map(
      "localPath" -> new Path(s"${metadata.artifactsLocalPath}/${metadata.name}/$artifactName"),
      "remotePath" -> new Path(s"${artifactsRemotePath}/${metadata.name}/${metadata.version}/$artifactName/$protocol")
    )
  }

  override def receive: Receive = {
    case SaveToLocal(artifactName, protocol) =>
      log.info("Receive message and starting to working...")
      val uris = generatePaths(artifactName, protocol)
      val localToSave = new File(uris("localPath").toString)

      log.info(s"Copying files from ${metadata.s3BucketName}: ${uris("remotePath")} to ${uris("localPath")}")

      //Get artifact named "uris("remotePath")" from S3 Bucket and save to local
      S3Client.getObject(new GetObjectRequest(metadata.s3BucketName, uris("remotePath").toString), localToSave)

      log.info(s"File ${uris("localPath")} saved!")

      sender ! Done

    case SaveToRemote(artifactName, protocol) =>
      log.info("Receive message and starting to working...")
      val uris = generatePaths(artifactName, protocol)
      val fileToUpload = new File(uris("localPath").toString)

      log.info(s"Copying files from ${uris("localPath")} to ${metadata.s3BucketName}: ${uris("remotePath")}")

      //Get local artifact and save to S3 Bucket with name "uris("remotePath")"
      S3Client.putObject(metadata.s3BucketName, uris("remotePath").toString, fileToUpload)

      log.info(s"File ${uris("localPath")} saved!")

      sender ! Done

    case _ =>
      log.warning("Received a bad format message...")
  }
}
