package org.apache.tika.parser.indesign;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.ContentHandler;

import java.io.InputStream;

import org.apache.tika.metadata.XMP;

import static org.apache.tika.TikaTest.assertContains;
import static org.junit.Assert.assertEquals;

public class IDMLParserTest {

    private final Parser parser = new IDMLParser();

    @Test
    public void testParser() throws Exception {
        Metadata metadata = new Metadata();
        InputStream stream = getClass().getResourceAsStream("/test-documents/testIndesign.idml");
        ContentHandler handler = new BodyContentHandler();
        parser.parse(stream, handler, metadata, new ParseContext());

        String content = handler.toString();
        assertEquals("3", metadata.get("TotalPageCount"));
        assertEquals("2", metadata.get("MasterSpreadPageCount"));
        assertEquals("1", metadata.get("SpreadPageCount"));
        assertEquals("application/vnd.adobe.indesign-idml-package", metadata.get(Metadata.CONTENT_TYPE));
        assertEquals("2020-09-17T16:44:03Z", metadata.get(XMP.CREATE_DATE));
        assertEquals("2020-09-17T16:44:03Z", metadata.get(XMP.MODIFY_DATE));
        assertEquals("Adobe InDesign CC 14.0 (Windows)", metadata.get(XMP.CREATOR_TOOL));
        assertContains("Lorem ipsum dolor sit amet, consectetur adipiscing elit", content);
    }

}
