package com.opus.audio.decode.core;

import de.jarnbjo.ogg.EndOfOggStreamException;
import de.jarnbjo.ogg.OggPage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LogicalOggStreamCustomImpl {

    private FileStreamCustom source;

    private ArrayList pageNumberMapping=new ArrayList();
    private ArrayList granulePositions=new ArrayList();

    private int pageIndex=0;
    private OggPage currentPage;
    private int currentSegmentIndex;

    public LogicalOggStreamCustomImpl(FileStreamCustom source) {
        this.source=source;
    }

    public void addPageNumberMapping(int physicalPageNumber) {
        pageNumberMapping.add(physicalPageNumber);
    }

    public void addGranulePosition(long granulePosition) {
        granulePositions.add(granulePosition);
    }

    public synchronized OggPage getNextOggPage() throws IOException {
        if(source.isSeekable()) {
            currentPage=source.getOggPage((Integer) pageNumberMapping.get(pageIndex++));
        } else {
            currentPage=source.getOggPage(-1); }
        return currentPage;
    }

    public synchronized byte[] getNextOggPacket() throws IOException {
        ByteArrayOutputStream res=new ByteArrayOutputStream();
        int segmentLength;

        if(currentPage==null) { currentPage=getNextOggPage(); }

        do {
            if(currentSegmentIndex>=currentPage.getSegmentOffsets().length) {
                currentSegmentIndex=0;

                if(!currentPage.isEos()) {
                    currentPage=getNextOggPage();

                    if(res.size()==0 && currentPage.isContinued()) {
                        boolean done=false;
                        while(!done) {
                            if(currentPage.getSegmentLengths()[currentSegmentIndex++]!=255) {
                                done=true;
                            }
                            if(currentSegmentIndex>currentPage.getSegmentTable().length) {
                                currentPage=source.getOggPage((Integer) pageNumberMapping.get(pageIndex++));
                            }
                        }
                    }
                }
                else { throw new EndOfOggStreamException(); }
            }
            segmentLength=currentPage.getSegmentLengths()[currentSegmentIndex];
            res.write(currentPage.getData(), currentPage.getSegmentOffsets()[currentSegmentIndex], segmentLength);
            currentSegmentIndex++;
        } while(segmentLength==255);

        return res.toByteArray();
    }
}
