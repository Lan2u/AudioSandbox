package audio.files;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Paul Lancaster on 30/10/2016
 */
class AudioHeader { // Store audio header data
    
    // In order of being read from file
    private String fileHeader;
    private int fileSize;
    private String format;
    
    private int audioFormat;
    private int numberOfChannels;
    private int sampleRate; // Samples per second
    private int byteRate; // sample rate * number of channels * bytes per sample
    private int blockAlign; // The number of bytes for one sample including all channels
    private int bitsPerSample; // divide by 8 for bytes per sample
    private String subChunkStart;
    private int dataSize;
    
    // Calculated
    private double length = -1; // Seconds
    private int bytesPerSample; // Bytes
    
    @Override
    public String toString(){
        return String.format("File Header: %s%n" +
                             "File Size: %s%n" +
                             "File Format: %s%n" +
                             "Audio Format: %d%n"+
                             "Channels: %s%n" +
                             "SampleRate: %s%n" +
                             "Byte Rate: %d%n" +
                             "Block Align: %d%n" +
                             "Bits per sample: %d%n" +
                             "Sub Chunk Start: %s%n" +
                             "Data Size %d%n",
                            fileHeader,
                            fileSize,
                            format,
                            audioFormat,
                            numberOfChannels,
                            sampleRate,
                            byteRate,
                            blockAlign,
                            bitsPerSample,
                            subChunkStart,
                            dataSize);
    }
    
    /** Read the header in from the audio file and use it to set the audio header object values
     * @param in The input file stream from the audio file
     * @throws IOException If there is an exception on in.read(byte[])
     * @see FileInputStream#read(byte[])
     */
    void readHeaderData(FileInputStream in) throws IOException {
        final int HEADER_BYTE_READ_EACH_TIME = 4;
        byte[] headerInputChunk = new byte[HEADER_BYTE_READ_EACH_TIME];
        in.read(headerInputChunk);
        setFileHeader(new String(headerInputChunk));
        in.read(headerInputChunk);
        setFileSize(getInt(headerInputChunk));
        in.read(headerInputChunk);
        setFormat(new String(headerInputChunk));
        in.read(headerInputChunk); // Should be the string "fmt" Marks start of next chunk
        in.read(headerInputChunk);
        int chunkSize = getInt(headerInputChunk);
        int chunkReadSoFar = in.read(headerInputChunk);
        setAudioFormat(getInt(new byte[]{headerInputChunk[0], headerInputChunk[1]})); // Takes the first 2 values of the byte array, gets int equivalent and then set this as the audio format
        setNumberOfChannels(getInt(new byte[]{headerInputChunk[2],headerInputChunk[3]}));
        chunkReadSoFar += in.read(headerInputChunk);
        setSampleRate(getInt(headerInputChunk));
        chunkReadSoFar += in.read(headerInputChunk);
        setByteRate(getInt(headerInputChunk));
        chunkReadSoFar += in.read(headerInputChunk);
        setBlockAlign(getInt(new byte[]{headerInputChunk[0],headerInputChunk[1]}));
        setBitsPerSample(getInt(new byte[]{headerInputChunk[2],headerInputChunk[3]}));
        if (chunkReadSoFar != chunkSize){
            //TODO add support for extra parameters
            System.out.println("Extra header space detected");
        }
        chunkReadSoFar += in.read(headerInputChunk);
        setSubChunkStart(String.valueOf(chunkReadSoFar));
        chunkReadSoFar += in.read(headerInputChunk);
        setDataSize(getInt(headerInputChunk));
    }
    
    private int getInt(byte[] byteRepOfInt) { // MAX 4 BYTES (int's use 4 bytes in java)
        int MAX_BYTES = 4;
        if (byteRepOfInt.length > MAX_BYTES){
            throw new IllegalArgumentException("Size of byte array for int conversion greater than maximum allowed bytes");
        }else {
            ByteBuffer bb = ByteBuffer.allocate(MAX_BYTES);
            for (byte b: byteRepOfInt){
                bb.put(b);
            }
            bb.position(0);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            return bb.getInt();
        }
    }
    
    // Get the length of the audio file in seconds, if the value isn't known then calculate it and store it first
    double getLength(){
        if(length <= 0){
            calcLength();
        }
        return length;
    }
    
    /**
     * Calculate the length in seconds of the audio file
     */
    private void calcLength(){
        length = dataSize / (sampleRate * bytesPerSample);
        length = length/numberOfChannels;
    }
    
    /* Setters and Getters */
    
    void setFileHeader(String fileHeader) {
            this.fileHeader = fileHeader;
        }
    
    void setFormat(String format) {
            this.format = format;
        }
    
    void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }
    
    void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
    
    void setNumberOfChannels(int numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }
    
    void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }
    
    void setByteRate(int byteRate) {
        this.byteRate = byteRate;
    }
    
    void setBlockAlign(int blockAlign) {
        this.blockAlign = blockAlign;
    }
    
    void setBitsPerSample(int bitsPerSample) {
        this.bytesPerSample = (bitsPerSample/8);
        this.bitsPerSample = bitsPerSample;
    }
    
    int getBitsPerSample() {
        return bitsPerSample;
    }
    
    int getSampleRate() {
        return sampleRate;
    }
    
    int getNumberOfChannels() {
        return numberOfChannels;
    }
    
    int getAudioFormat() {
        return audioFormat;
    }
    
    int getByteRate() {
        return byteRate;
    }
    
    int getBlockAlign() {
        return blockAlign;
    }

    int getBytesPerSample() {
        return bytesPerSample;
    }
    
    void setSubChunkStart(String subChunkStart) {
        this.subChunkStart = subChunkStart;
    }
    
    void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
    
    int getDataSize() {
        return dataSize;
    }
}
