package audio.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


//http://soundfile.sapp.org/doc/WaveFormat/

/* The high level audio files (wavefile, mp3file etc. should be the ONLY classes in the package which have anything
* (except stuff that absolutely has to be) visible from outside the package. There should be no direct contact with
* the audio file header or data without going through one of theses higher level sound objects first
*/
public class WaveFile implements AudioFile{
    private AudioHeader header;
    private AudioData data;
    
    /** Create a new WaveFile object which represents a wavefile type audio file
     * @param file The wave file
     * @throws IOException if I/O exception occurs
     * @see FileInputStream#read(byte[])
     */
    public WaveFile(File file) throws IOException { // Creates the wave-file loading the header infromation and data into memory
        header = new AudioHeader();
        FileInputStream in = new FileInputStream(file);
        header.readHeaderData(in);
        data = AudioData.getData(header.getDataSize(), header.getNumberOfChannels());
        data.readData(in,header.getBytesPerSample()); // TODO new not tested
    }
    
    @Override
    public int[] getSamples(double seconds, int channel) {
        return data.getSamples(seconds,header.getSampleRate(),channel,header.getLength());
    }
    
    @Override
    public int getSampleRate() {
        return header.getSampleRate();
    }
    
    @Override
    public int getChannels() {
        if (data instanceof MonoAudioData){
            return 1;
        }
        else if (data instanceof StereoAudioData){
            return 2;
        }
        else{
            return 0; // Unknown number of channels
        }
    }
    
    @Override
    public double getLength() {
        return header.getLength();
    }
    
    @Override
    public int[] getAllSamples(int channel) {
        return data.getSamples(getLength(),header.getSampleRate(),channel,header.getLength());
    }
    
    @Override
    public int getNumberOfSamples() {
        return data.getNumberOfSamples();
    }
    
    @Override
    public short[] getChunk(int samples, int channel) {
        return data.getChunk(samples, channel);
    }
    
    @Override
    public String toString(){
        return header.toString();
    }
}