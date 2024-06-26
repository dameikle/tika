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
package org.apache.tika.parser.tmx;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.io.input.CloseShieldInputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.XHTMLContentHandler;
import org.apache.tika.utils.XMLReaderUtils;

/**
 * Parser for Translation Memory eXchange (TMX) files.
 */
public class TMXParser implements Parser {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 2305588935434276452L;

    /**
     * TMX Mime Type.
     */
    private static final MediaType TMX_CONTENT_TYPE = MediaType.application("x-tmx");

    /**
     * Supported types set.
     */
    private static final Set<MediaType> SUPPORTED_TYPES = Collections.singleton(TMX_CONTENT_TYPE);

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata,
                      ParseContext context) throws IOException, SAXException, TikaException {

        metadata.set(Metadata.CONTENT_TYPE, TMX_CONTENT_TYPE.toString());

        final XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        XMLReaderUtils.parseSAX(CloseShieldInputStream.wrap(stream), new TMXContentHandler(xhtml, metadata), context);

    }

}
