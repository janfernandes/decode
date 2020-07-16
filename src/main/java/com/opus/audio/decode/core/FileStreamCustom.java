package com.opus.audio.decode.core;

import de.jarnbjo.ogg.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class FileStreamCustom implements PhysicalOggStream{

    private boolean closed=false;
    private RandomAccessFile source;
    private long[] pageOffsets;

    private HashMap logicalStreams=new HashMap();

    public FileStreamCustom(RandomAccessFile source) throws IOException {
        this.source=source;
        ArrayList po=new ArrayList();
        int pageNumber=0;
        try {
            while(true) {
                po.add(new Long(this.source.getFilePointer()));

                // skip data if pageNumber>0
                OggPage op=getNextPage(pageNumber>0);
                if(op==null) {
                    break;
                }

                LogicalOggStreamCustomImpl los=getLogicalStream(op.getStreamSerialNumber());
                if(los==null) {
                    los=new LogicalOggStreamCustomImpl(this, op.getStreamSerialNumber());
                    logicalStreams.put(new Integer(op.getStreamSerialNumber()), los);
                }

                if(pageNumber==0) {
                    los.checkFormat(op);
                }

                los.addPageNumberMapping(pageNumber);
                los.addGranulePosition(op.getAbsoluteGranulePosition());

                if(pageNumber>0) {
                    this.source.seek(this.source.getFilePointer()+op.getTotalLength());
                }

                pageNumber++;
            }
        }
        catch(EndOfOggStreamException e) {
            // ok
        }
        catch(IOException e) {
            throw e;
        }
        //System.out.println("pageNumber: "+pageNumber);
        this.source.seek(0L);
        pageOffsets=new long[po.size()];
        int i=0;
        Iterator iter=po.iterator();
        while(iter.hasNext()) {
            pageOffsets[i++]=((Long)iter.next()).longValue();
        }
    }

    public Collection getLogicalStreams() {
        return logicalStreams.values();
    }

    public boolean isOpen() {
        return !closed;
    }

    public void close() throws IOException {
        closed=true;
        source.close();
    }

    private OggPage getNextPage() throws IOException  {
        return getNextPage(false);
    }

    private OggPage getNextPage(boolean skipData) throws IOException  {
        return OggPage.create(source, skipData);
    }

    public OggPage getOggPage(int index) throws IOException {
        source.seek(pageOffsets[index]);
        return OggPage.create(source);
    }

    private LogicalOggStreamCustomImpl getLogicalStream(int serialNumber) {
        return (LogicalOggStreamCustomImpl)logicalStreams.get(new Integer(serialNumber));
    }

    public void setTime(long granulePosition) throws IOException {
        for(Iterator iter=logicalStreams.values().iterator(); iter.hasNext(); ) {
            LogicalOggStreamCustomImpl los=(LogicalOggStreamCustomImpl)iter.next();
            los.setTime(granulePosition);
        }
    }

    /**
     *  @return always <code>true</code>
     */

    public boolean isSeekable() {
        return true;
    }
}
