package audio.files;

/**
 * Created by Paul Lancaster on 30/10/2016
 */
class AudioHeader { // Store audio header data
    private String fileHeader;
    private String format;
    private int bitsPerSample; // divide by 8 for bytes per sample
    private int numberOfChannels; // Integer
    private int audioFormat;
    private int sampleRate; // Samples per second
    private int byteRate; // sample rate * number of channels * bytes per sample
    private int blockAlign; // The number of bytes for one sample including all channels
    private int fileSize;
    private int bytesPerSample; // Bytes
    private String subChunkStart;
    private int dataSize;
    private double length = -1; // Seconds
    
    
    @Override
    public String toString(){
        // TODO setup a well formatted string representation of the file header
        return String.format("File Header: %s%nFile Format: %s%nFile Size: %s%nSampleRate: %s%nBytes Per Sample: %s%nChannels: %s%n", fileHeader,format,fileSize,sampleRate,bitsPerSample/8.0,numberOfChannels);
    }
    
    /**
     * Calculate the length in seconds of the audio file
     */
    private void calcLength(){
        length = dataSize / (sampleRate * bytesPerSample);
        length = length/numberOfChannels;
    }
    
    // Get the length of the audio file in seconds, if the value isn't known then calculate it and store it first
    double getLength(){
        if(length <= 0){
            calcLength();
        }
        return length;
    }
    
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
