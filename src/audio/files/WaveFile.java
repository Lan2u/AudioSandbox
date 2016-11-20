package audio.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
//http://soundfile.sapp.org/doc/WaveFormat/
public class WaveFile implements AudioFile{
    private static final int HEADER_BYTE_READ_EACH_TIME = 4;
    private AudioHeader header;
    private AudioData data;
    private double length; // Length of file in seconds
    
    /** Create a new WaveFile object which represents a wavefile type audio file
     * @param file The wave file
     * @throws IOException if I/O exception occurs
     * @see FileInputStream#read(byte[])
     */
    public WaveFile(File file) throws IOException { // Creates the wave-file loading the header infromation and data into memory
        header = new AudioHeader();
        FileInputStream in = new FileInputStream(file);
        readHeaderData(in);
        readAllAudioData(in);
        calcLength();
    }
    
    /** Read the audio data in in one big chunk
     * @param in The input file stream from the audio file
     * @throws IOException If there is an exception on in.read(byte[])
     * @see FileInputStream#read(byte[])
     */
    private void readAllAudioData(FileInputStream in) throws IOException{
        byte[] inputChunk = new byte[HEADER_BYTE_READ_EACH_TIME];
        
        int bytesRead = 0;
        bytesRead +=in.read(inputChunk); // Skip over the string at the start of the data subchunk that contains "data"
        // Beginning of data sub-chunk
        bytesRead +=in.read(inputChunk);
        
        data = new AudioData(getInt(inputChunk));
        data.readData(in);
    }
    
    /** Read the header in from the audio file and use it to set the audio header object values
     * @param in The input file stream from the audio file
     * @throws IOException If there is an exception on in.read(byte[])
     * @see FileInputStream#read(byte[])
     */
    private void readHeaderData(FileInputStream in) throws IOException {
        byte[] headerInputChunk = new byte[HEADER_BYTE_READ_EACH_TIME];
        in.read(headerInputChunk);
        header.setFileHeader(new String(headerInputChunk));
        in.read(headerInputChunk);
        header.setFileSize(getInt(headerInputChunk));
        in.read(headerInputChunk);
        header.setFormat(new String(headerInputChunk));
        in.read(headerInputChunk); // Should be the string "fmt" Marks start of next chunk
        in.read(headerInputChunk);
        int chunkSize = getInt(headerInputChunk);
        int chunkReadSoFar = in.read(headerInputChunk);
        header.setAudioFormat(getInt(new byte[]{headerInputChunk[0], headerInputChunk[1]})); // Takes the first 2 values of the byte array, gets int equivalent and then set this as the audio format
        header.setNumberOfChannels(getInt(new byte[]{headerInputChunk[2],headerInputChunk[3]}));
        chunkReadSoFar += in.read(headerInputChunk);
        header.setSampleRate(getInt(headerInputChunk));
        chunkReadSoFar += in.read(headerInputChunk);
        header.setByteRate(getInt(headerInputChunk));
        chunkReadSoFar += in.read(headerInputChunk);
        header.setBlockAlign(getInt(new byte[]{headerInputChunk[0],headerInputChunk[1]}));
        header.setBitsPerSample(getInt(new byte[]{headerInputChunk[2],headerInputChunk[3]}));
        if (chunkReadSoFar != chunkSize){
            //TODO add support for extra parameters
            System.out.println("Extra header space detected");
        }
    }
    
    /**
     * Calculate the length in seconds of the audio file
     */
    private void calcLength(){
        length = data.getDataSize() / (header.getSampleRate() * header.getBytesPerSample());
        length = length/header.getNumberOfChannels();
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
    
    @Override
    public String toString(){
        return String.format("Header:%n%s%nLength (s) : %s%nData:%n%s",header.toString(),length,data.toString());
    }
    
    private int getNextSample() {
        int bytes = header.getBytesPerSample();
        byte[] sample = data.nextChunk(bytes);
        return getAmplitude(sample);
    }

    private int getAmplitude(byte[] sample){
        // FIXME this only supports 2 bytes per sample
        return (sample[1] << 8 | sample[0] & 0xFF);
    }
    
    @Override
    public int getNumberOfSamples(){
        return (int)(data.getDataSize() / ((header.getBitsPerSample()/8.0) * 2)); //TODO workout exactly why it is *2 and why this works (it does?)
    }
    
    @Override
    public int[] getChunk(int chunk_size) {
        int[] chunk = new int[chunk_size];
        for (int i = 0; i < chunk_size; i++) {
            chunk[i] = getSample();
        }
        return chunk;
    }
    
    private short getShort(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getShort();
    }
    
    public boolean hasNextSample(){
        return data.hasNextSample();
    }
    
    @Override
    public int getSampleRate() {
        return header.getSampleRate();
    }
    
    @Override
    public int getChannels() {
        return header.getNumberOfChannels();
    }
    
    /**
     * Get an array of samples of all the samples within the specified time
     * @param seconds The number of seconds of audio data to get
     * @return The audio data (array of samples)
     */
    @Override
    public int[] getSamples(double seconds){
        double secondsLeft = length - (data.getPosition()/getSampleRate());
        int samplesToRead;
        if (seconds < secondsLeft){
            samplesToRead = (int)(seconds * getSampleRate() * getChannels());
        }else{
            samplesToRead = (int)(getSampleRate() * secondsLeft * getChannels());
        }
        if (samplesToRead == 0){
            System.out.println("No more file to read");
            return null;
        }

        System.out.println("Samples to read " + samplesToRead);

        int[] samples = new int[samplesToRead]; // Store the audio data
        for (int i = 0; i < samplesToRead ; i++) {
            samples[i] = getNextSample();
        }
        return samples;
    }
    
    @Override
    public double getLength() {
        return length;
    }
    
    @Override
    public int[] getAllSamples() {
        return getSamples(getLength());
    }
    
    public int getSample() {
        byte[] sample = data.nextChunk(header.bytesPerSample);
        if (sample == null){
            return -1;
        }else {
            return getAmplitude(sample);
        }
    }

    /*
        How to normalise the values
       (All amplitude values / Math.mod(Max_amplilitude)) * Maximum display range
        Alec's suggestion
     */
}