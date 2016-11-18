package audio.files;

/**
 * Created by Paul Lancaster on 30/10/2016
 */
class AudioHeader { // Store audio header data
    private String fileHeader;
    private String format;
    private int bitsPerSample; // divide by 8 for bytes per sample
    private int numberOfChannels;
    private int audioFormat;
    private int sampleRate;
    private int byteRate; // sample rate * number of channels * bytes per sample
    private int blockAlign; // The number of bytes for one sample including all channels
    private int fileSize;
    public  int bytesPerSample;

    
    @Override
    public String toString(){
        // TODO setup a well formatted string representation of the file header
        return String.format("File Header: %s%nFile Format: %s%nFile Size: %s%nSampleRate: %s%nBytes Per Sample: %s%nChannels: %s%n", fileHeader,format,fileSize,sampleRate,bitsPerSample/8.0,numberOfChannels);
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
    
    public int getSampleRate() {
        return sampleRate;
    }
    
    public int getNumberOfChannels() {
        return numberOfChannels;
    }
    
    public int getAudioFormat() {
        return audioFormat;
    }
    
    public int getByteRate() {
        return byteRate;
    }
    
    public int getBlockAlign() {
        return blockAlign;
    }

    public int getBytesPerSample() {
        return bytesPerSample;
    }
}
