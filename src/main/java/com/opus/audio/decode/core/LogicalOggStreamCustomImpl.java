package com.opus.audio.decode.core;

import de.jarnbjo.ogg.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LogicalOggStreamCustomImpl {

    public static final String FORMAT_UNKNOWN = "application/octet-stream";

    public static final String FORMAT_VORBIS  = "audio/x-vorbis";
    public static final String FORMAT_FLAC    = "audio/x-flac";
    public static final String FORMAT_THEORA  = "video/x-theora";

    private PhysicalOggStream source;
    private int serialNumber;

    private ArrayList pageNumberMapping=new ArrayList();
    private ArrayList granulePositions=new ArrayList();

    private int pageIndex=0;
    private OggPage currentPage;
    private int currentSegmentIndex;

    private boolean open=true;

    private String format=FORMAT_UNKNOWN;

    public LogicalOggStreamCustomImpl(PhysicalOggStream source, int serialNumber) {
        this.source=source;
        this.serialNumber=serialNumber;
    }

    public void addPageNumberMapping(int physicalPageNumber) {
        pageNumberMapping.add(new Integer(physicalPageNumber));
    }

    public void addGranulePosition(long granulePosition) {
        granulePositions.add(new Long(granulePosition));
    }

    public synchronized void reset() throws OggFormatException, IOException {
        currentPage=null;
        currentSegmentIndex=0;
        pageIndex=0;
    }

    public synchronized OggPage getNextOggPage() throws EndOfOggStreamException, OggFormatException, IOException {
        if(source.isSeekable()) {
            currentPage=source.getOggPage(((Integer)pageNumberMapping.get(pageIndex++)).intValue());
        }
        else {
            currentPage=source.getOggPage(-1);
        }
        return currentPage;
    }

    public synchronized byte[] getNextOggPacket() throws EndOfOggStreamException, OggFormatException, IOException {
        ByteArrayOutputStream res=new ByteArrayOutputStream();
        int segmentLength=0;

        if(currentPage==null) {
            currentPage=getNextOggPage();
        }

        do {
            if(currentSegmentIndex>=currentPage.getSegmentOffsets().length) {
                currentSegmentIndex=0;

                if(!currentPage.isEos()) {
//                    if(source.isSeekable() && pageNumberMapping.size()<=pageIndex) {
//                        while(pageNumberMapping.size()<=pageIndex+10) {
//                            try {
//                                Thread.sleep(1000);
//                            }
//                            catch (InterruptedException ex) {
//                            }
//                        }
//                    }
                    currentPage=getNextOggPage();

                    if(res.size()==0 && currentPage.isContinued()) {
                        boolean done=false;
                        while(!done) {
                            if(currentPage.getSegmentLengths()[currentSegmentIndex++]!=255) {
                                done=true;
                            }
                            if(currentSegmentIndex>currentPage.getSegmentTable().length) {
                                currentPage=source.getOggPage(((Integer)pageNumberMapping.get(pageIndex++)).intValue());
                            }
                        }
                    }
                }
                else {
                    throw new EndOfOggStreamException();
                }
            }
            segmentLength=currentPage.getSegmentLengths()[currentSegmentIndex];
            res.write(currentPage.getData(), currentPage.getSegmentOffsets()[currentSegmentIndex], segmentLength);
            currentSegmentIndex++;
        } while(segmentLength==255);

        return res.toByteArray();
    }

    public boolean isOpen() {
        return open;
    }

    public void close() throws IOException {
        open=false;
    }

    public long getMaximumGranulePosition() {
        Long mgp=(Long)granulePositions.get(granulePositions.size()-1);
        return mgp.longValue();
    }

    public synchronized long getTime() {
        return currentPage!=null?currentPage.getAbsoluteGranulePosition():-1;
    }


    public synchronized void setTime(long granulePosition) throws IOException {
        int page;
        for(page=0; page<granulePositions.size(); page++) {
            Long gp=(Long)granulePositions.get(page);
            if(gp >granulePosition) {
                break;
            }
        }

        pageIndex=page;
        currentPage=source.getOggPage((Integer) pageNumberMapping.get(pageIndex++));
        currentSegmentIndex=0;
        int segmentLength;
        do {
            if(currentSegmentIndex>=currentPage.getSegmentOffsets().length) {
                currentSegmentIndex=0;
                if(pageIndex>=pageNumberMapping.size()) {
                    throw new EndOfOggStreamException();
                }
                currentPage=source.getOggPage((Integer) pageNumberMapping.get(pageIndex++));
            }
            segmentLength=currentPage.getSegmentLengths()[currentSegmentIndex];
            currentSegmentIndex++;
        } while(segmentLength==255);
    }

    public void checkFormat(OggPage page) {
        byte[] data=page.getData();

        if(data.length>=7 &&
                data[1]==0x76 &&
                data[2]==0x6f &&
                data[3]==0x72 &&
                data[4]==0x62 &&
                data[5]==0x69 &&
                data[6]==0x73) {

            format=FORMAT_VORBIS;
        }
        else if(data.length>=7 &&
                data[1]==0x74 &&
                data[2]==0x68 &&
                data[3]==0x65 &&
                data[4]==0x6f &&
                data[5]==0x72 &&
                data[6]==0x61) {

            format=FORMAT_THEORA;
        }
        else if (data.length==4 &&
                data[0]==0x66 &&
                data[1]==0x4c &&
                data[2]==0x61 &&
                data[3]==0x43) {

            format=FORMAT_FLAC;
        }
    }

    public String getFormat() {
        return format;
    }
}
