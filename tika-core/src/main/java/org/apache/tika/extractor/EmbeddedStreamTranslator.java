/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.extractor;

import org.apache.tika.metadata.Metadata;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for different filtering of embedded streams.
 * Specifically, unravel OLE streams in tika-server unpack,
 * and/or handle open containers in TikaInputStream
 *
 * @since Apache Tika 2.0.0
 */
public interface EmbeddedStreamTranslator {

    boolean shouldTranslate(InputStream inputStream, Metadata metadata) throws IOException;

    InputStream translate(InputStream inputStream,
                          Metadata metadata) throws IOException;

}