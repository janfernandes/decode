package com.opus.audio.decode.core;

import de.jarnbjo.ogg.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class FileStreamCustom {

    private boolean closed = false;
    private RandomAccessFile source;
    private long[] pageOffsets;

    private HashMap logicalStreams = new HashMap();

    public FileStreamCustom(RandomAccessFile source) throws IOException {
        this.source = source;
        ArrayList po = new ArrayList();
        int pageNumber = 0;
        try {
            while (true) {
                po.add(new Long(this.source.getFilePointer()));

                // skip data if pageNumber>0
                OggPage op = getNextPage(pageNumber > 0);
                if (op == null) {
                    break;
                }

                LogicalOggStreamCustomImpl los = getLogicalStream(op.getStreamSerialNumber());
                if (los == null) {
                    los = new LogicalOggStreamCustomImpl(this);
                    logicalStreams.put(op.getStreamSerialNumber(), los);
                }

                los.addPageNumberMapping(pageNumber);
                los.addGranulePosition(op.getAbsoluteGranulePosition());

                if (pageNumber > 0) {
                    this.source.seek(this.source.getFilePointer() + op.getTotalLength());
                }

                pageNumber++;
            }
        } catch (EndOfOggStreamException e) {
            // ok
        }
        this.source.seek(0L);
        pageOffsets = new long[po.size()];
        int i = 0;
        for (Object o : po) {
            pageOffsets[i++] = (Long) o;
        }
    }

    public Collection getLogicalStreams() {
        return logicalStreams.values();
    }

    public boolean isOpen() {
        return !closed;
    }

    public void close() throws IOException {
        closed = true;
        source.close();
    }

    private OggPage getNextPage() throws IOException {
        return getNextPage(false);
    }

    private OggPage getNextPage(boolean skipData) throws IOException {
        return OggPage.create(source, skipData);
    }

    public OggPage getOggPage(int index) throws IOException {
        source.seek(pageOffsets[index]);
        return OggPage.create(source);
    }

    private LogicalOggStreamCustomImpl getLogicalStream(int serialNumber) {
        return (LogicalOggStreamCustomImpl) logicalStreams.get(new Integer(serialNumber));
    }

    /**
     * @return always <code>true</code>
     */

    public boolean isSeekable() {
        return true;
    }
}
