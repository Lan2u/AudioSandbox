package audio.files;

import java.io.FileInputStream;
import java.io.IOException;

class AudioData{ // Store audio data
    
    private byte[] data;
    private int readPosition = 0; // Position reached so far through the data array in samples
    
    AudioData(int size){
        data = new byte[size];
    }
    
    void readData(FileInputStream in) throws IOException { // Read all the data into an array
        in.read(data);
        // TODO replace this so that the audio is read bit by bit rather than all at once if the file is large
    }
    
    @Override
    public String toString(){
        return "Data length " + data.length + " Bytes";
    }
    
    byte[] nextChunk(int length){ // Get the next chunk of data (this is the raw data which will make up 1 sample)
        byte[] chunk = new byte[length];
        if (readPosition >= data.length){
            return null;
        }
        System.arraycopy(data, readPosition, chunk, 0, length);
        readPosition = readPosition + length; //FIXME +1 might be required
        return chunk;
    }
    
    boolean hasNextSample() { // Is there another same? (true if so)
        return readPosition < (data.length - 1);
    }
    
    void resetPosition(int newPos){ // Move the read position marker to a new position (0 = start)
        if (newPos > (data.length -1)){
            throw new IllegalArgumentException("pos is greater than the number of bytes of data");
        }
        this.readPosition = newPos;
    }

    public int getPosition() {
        return readPosition;
    }
    
    int getDataSize(){ // Get the number of bytes of raw data
        return data.length;
    }
    
    byte[] getRawData(){
        return data.clone();
    }
}
