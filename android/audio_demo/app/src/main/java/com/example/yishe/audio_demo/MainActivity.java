package com.example.yishe.audio_demo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements View.OnClickListener{

    private static final String TAG="MainActivity";
    private TextView  viewState;
    private Button start_record,stop_record,start_play,stop_play,wav_play;


    private  RecordTask recorder;
    private PlayTask player;
    private File audioFile;
    private boolean isRecording= true,isPlaying=false;
    private int frequence = 8000;//录制频率，
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private static final int MSG_SAVE_TO_WAV=10000;

    private static final String newAudioName="new.wav";

    private MediaPlayer mediaPlayer;

    private Handler mHanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_SAVE_TO_WAV:
                    new SaveToWavThread().start();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewState = findViewById(R.id.view_state);
        viewState.setText("准备开始");

        start_record = findViewById(R.id.record_start);
        stop_record = findViewById(R.id.record_stop);
        start_play = findViewById(R.id.play_start);
        stop_play = findViewById(R.id.play_stop);
        wav_play = findViewById(R.id.wav_play);

        start_play.setOnClickListener(this);
        stop_record.setOnClickListener(this);
        start_record.setOnClickListener(this);
        stop_record.setOnClickListener(this);
        wav_play.setOnClickListener(this);

        stop_play.setEnabled(false);
        stop_record.setEnabled(false);
        start_play.setEnabled(false);
        //        // 创建一个文件夹用于保存录制内容
        File fpath=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/files");
        fpath.mkdirs();
        try{
            audioFile = File.createTempFile("recording","pcm",fpath);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_start:
                //开始录制
                Log.i(TAG,"开始录制");
                recorder = new RecordTask();
                recorder.execute();
                break;
            case R.id.record_stop:
                // 停止录制
                this.isRecording = false;
                break;
            case R.id.play_start:
                player = new PlayTask();
                player.execute();
                break;
            case R.id.play_stop:
                //完成播放
                this.isPlaying = false;
                recorder=null;
                player = null;
                break;
            case R.id.wav_play:
                playWavMusic();
                break;
        }
    }


    class RecordTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG,"RecordTask doInBackground");
            isRecording = true;
            AudioRecord record=null;
            try{
                //开通输出流到指定文件
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(audioFile)));
                //根据定义到的几个配置，来获取合适的缓冲区大小
                int bufferSize = AudioRecord.getMinBufferSize(frequence,channelConfig,audioEncoding);
                //实例化AudioRecord
                record= new AudioRecord(MediaRecorder.AudioSource.MIC,frequence,channelConfig,audioEncoding,bufferSize);
                //定义缓存区
                short[] buffer = new short[bufferSize];

                //开始录制
                record.startRecording();
                int r =0; //存储录制进度
                // 定义循环，根据isRecording 的值来判断是否继续录制
                while(isRecording){
                    // 从bufferSize中读取字节，返回读取的short个数
                    //这里老师出现buffer overflow不知道什么原因
                    int bufferReadResult = record.read(buffer,0,buffer.length);
                    // 循环将buffer中的音频数据写入到OutputStream中
                    for(int i=0;i<bufferReadResult;i++){
                        dos.writeShort(buffer[i]);
                    }
                    publishProgress(new Integer(r)); //向UI线程中发送进度
                    r++;//自增进度值，
                }
                //录制结束
                record.stop();
                dos.close();
            }catch (Exception e){

            }finally {
                if(record!=null) {
                    record.stop();
                    record.release();//释放资源
                    record = null;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            viewState.setText(values[0].toString());
            Log.i(TAG,"onProgressUpdate");
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG,"onPreExecute");
            start_play.setEnabled(false);
            start_record.setEnabled(false);
            stop_play.setEnabled(false);
            stop_record.setEnabled(true);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG,"onPreExecute");
            start_play.setEnabled(true);
            start_record.setEnabled(false);
            stop_play.setEnabled(false);
            stop_record.setEnabled(false);
            mHanlder.sendEmptyMessage(MSG_SAVE_TO_WAV);
        }
    }


    class PlayTask extends AsyncTask<Void,Integer,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG," PlayTask  onPreExecute");
            isPlaying = true;
            int bufferSize= AudioTrack.getMinBufferSize(frequence,channelConfig,audioEncoding);
            short[] buffer = new short[bufferSize/4];
            try{
                //定义输出流，将音频写入到AudioTrack类中
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(audioFile)));
                //实例 Audiotrack
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,frequence,channelConfig,audioEncoding,bufferSize,AudioTrack.MODE_STREAM);
                //开始播放
                track.play();
                //由于AudioTrack播放的是流， 所以，我们需要一边播放一边读取
                while(isPlaying&&dis.available()>0){
                    int i=0;
                    while(dis.available()>0&&i<buffer.length){
                        buffer[i] =dis.readShort();
                        i++;
                    }
                    //然后将数据写入到AudioTrack中
                    track.write(buffer,0,buffer.length);
                }
                //播放结束
                track.stop();
                dis.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPreExecute() {
            Log.i(TAG," PlayTask  onPreExecute");
            start_play.setEnabled(false);
            start_record.setEnabled(false);
            stop_play.setEnabled(true);
            stop_record.setEnabled(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG," PlayTask  onPostExecute");
            start_play.setEnabled(false);
            start_record.setEnabled(true);
            stop_play.setEnabled(false);
            stop_record.setEnabled(false);
            isPlaying = false;
        }

    }



    // 这里得到可播放的音频文件
    private void copyWaveFile(String inFilename, String outFilename) {
        Log.i(TAG,"inFilename ="+inFilename+",outFileName="+outFilename);
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        int sampleRateInHz = 44100;
        long longSampleRate = sampleRateInHz;
        int channels = 2;
        long byteRate = 16 * sampleRateInHz * channels / 8;
        int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioEncoding);
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
     * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
     * 自己特有的头文件。
     */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

    class SaveToWavThread extends Thread {

        @Override
        public void run() {
            Log.i(TAG,"SaveToWavThread");
            copyWaveFile(audioFile.getAbsolutePath(),Environment.getExternalStorageDirectory()+"/"+newAudioName);
        }
    }

    /**
     * 播放存的wav文件
     */
    private void playWavMusic(){
        try {
            Log.i(TAG,"playWavMusic");
            mediaPlayer = new MediaPlayer();
            File wavFile = new File(Environment.getExternalStorageDirectory()+"/"+newAudioName);
            if(wavFile.exists()){
                Log.i(TAG,"wav file path="+wavFile.getAbsolutePath());
                mediaPlayer.setDataSource(wavFile.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            }else{
                Log.i(TAG,"file not found!");
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e(TAG,"error occur when play wav");
        }finally {
            if(mediaPlayer!=null){
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

}
