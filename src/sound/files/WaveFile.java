package sound.files;

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

    public WaveFile(File file) throws IOException { // Creates the wave-file loading the header infromation and data into memory
        header = new AudioHeader();
        FileInputStream in = new FileInputStream(file);
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
        in.read(headerInputChunk); // Skip over the string at the start of the data subchunk that contains "data"
        // Beginning of data sub-chunk
        in.read(headerInputChunk);
        data = new AudioData(getInt(headerInputChunk));
        data.readData(in);
        calcLength();
    }

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
    
    public int getNextSample() {
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

    // TODO getSeconds (number of seconds)
    // TODO Use this to get a certain number of seconds of the file and move the read position up to that point (so it can be called contniuously until end of file)
    @Override
    public int[] getSamples(double seconds){ // Get an array of samples of all the samples within the specified time
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

    /*
    How to normalise the values
    (All amplitude values / Math.mod(Max_amplilitude)) * Maximum display range
     */
}