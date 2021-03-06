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
package org.apache.crunch.scrunch

import org.apache.crunch.io.{From => from}

import _root_.org.junit.Test

class CogroupTest extends CrunchSuite {
  lazy val pipeline = Pipeline.mapReduce[CogroupTest](tempDir.getDefaultConfiguration)

  def wordCount(fileName: String) = {
    pipeline.read(from.textFile(fileName))
        .flatMap(_.toLowerCase.split("\\W+")).count
  }

  @Test def cogroup {
    val shakespeare = tempDir.copyResourceFileName("shakes.txt")
    val maugham = tempDir.copyResourceFileName("maugham.txt")
    val diffs = wordCount(shakespeare).cogroup(wordCount(maugham))
        .map((k, v) => (k, (v._1.sum - v._2.sum))).materialize
    assert(diffs.exists(_ == ("the", -11390)))
    pipeline.done
  }
}
