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
package org.apache.tika.parser.indesign.xmp;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.XMPSchemaBasic;
import org.apache.jempbox.xmp.XMPSchemaPagedText;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.XMP;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Basic XMP Metadata Extractor using Jempbox.
 */
public class XMPMetadataExtractor {

    /**
     * Empty Parse Context.
     */
    private static final ParseContext EMPTY_PARSE_CONTEXT = new ParseContext();

    /**
     * Paged Text Name Space
     */
    public static final String XAP_1_0_T_PG = "http://ns.adobe.com/xap/1.0/t/pg/";

    /**
     * XMP Packet Scanner.
     */
    private final XMPPacketScanner scanner = new XMPPacketScanner();

    /**
     * Parse the XMP Packets.
     *
     * @param stream the stream to parser.
     * @param metadata the metadata collection to update
     * @throws IOException on any IO error.
     * @throws TikaException on any Tika error.
     */
    public void parse(InputStream stream, Metadata metadata) throws IOException, TikaException {
        ByteArrayOutputStream xmpraw = new ByteArrayOutputStream();
        if (!scanner.parse(stream, xmpraw)) {
            return;
        }

        XMPMetadata xmp = null;
        try (InputStream decoded =
                     new ByteArrayInputStream(xmpraw.toByteArray())
        ) {
            Document dom = XMLReaderUtils.buildDOM(decoded, EMPTY_PARSE_CONTEXT);
            if (dom != null) {
                xmp = new XMPMetadata(dom);
            }
        } catch (IOException| SAXException e) {
            //
        }
        extractXMPBasicSchema(xmp, metadata);
        extractXMPPagedText(xmp, metadata);
    }

    /**
     * Extracts basic schema metadata from XMP.
     *
     * Silently swallows exceptions.
     * @param xmp the XMP Metadata object.
     * @param metadata the metadata map
     */
    public static void extractXMPBasicSchema(XMPMetadata xmp, Metadata metadata) throws IOException {
        if (xmp == null) {
            return;
        }
        XMPSchemaBasic schemaBasic = null;
        try {
            schemaBasic = xmp.getBasicSchema();
        } catch (IOException e) {
            //swallow
            return;
        }
        if (schemaBasic != null) {
            addMetadata(metadata, XMP.CREATOR_TOOL, schemaBasic.getCreatorTool());
            addMetadata(metadata, XMP.CREATE_DATE, schemaBasic.getCreateDate().getTime());
            addMetadata(metadata, XMP.MODIFY_DATE, schemaBasic.getModifyDate().getTime());
        }
    }

    /**
     * Extracts paged text metadata from XMP.
     *
     * Silently swallows exceptions.
     * @param xmp the XMP Metadata object.
     * @param metadata the metadata map
     */
    public static void extractXMPPagedText(XMPMetadata xmp, Metadata metadata) throws IOException {
        if (xmp == null) {
            return;
        }
        XMPSchemaPagedText schemaPagedText = null;
        try {
            schemaPagedText = xmp.getPagedTextSchema();
        } catch (IOException e) {
            //swallow
            return;
        }
        if (schemaPagedText != null) {
            NodeList list = schemaPagedText.getElement().getElementsByTagNameNS(XAP_1_0_T_PG, "PageNumber");
            addMetadata(metadata, Property.externalText("ThumbnailCount"), Integer.toString(list.getLength()));
        }
    }

    /**
     * Add value to the metadata map.
     *
     * @param metadata the metadata map to update.
     * @param property the property to add.
     * @param value the value to add.
     */
    private static void addMetadata(Metadata metadata, Property property, String value) {
        if (value != null) {
            if (property.isMultiValuePermitted() || metadata.get(property) == null) {
                metadata.set(property, value);
            }
        }
    }

    /**
     * Add value to the metadata map.
     *
     * @param metadata the metadata map to update.
     * @param property the property to add.
     * @param value the value to add.
     */
    private static void addMetadata(Metadata metadata, Property property, Date value) {
        if (value != null) {
            if (property.isMultiValuePermitted() || metadata.get(property) == null) {
                metadata.set(property, value);
            }
        }
    }

}
