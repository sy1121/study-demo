package com.example.yishe.audio_demo;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.nio.ByteBuffer;

import static java.lang.Thread.sleep;

public class Playmp4Activity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "Playmp4Activity";

    private String videoPath="";
    private int videoTrackIndex;
    private int audioTrackIndex;
    private MediaCodec videoDecode;
    private MediaCodec audioDecode;
    private MediaExtractor vextractor;
    private MediaExtractor aextractor;
    private AudioTrack audioTrack;
    private static final int TIME_OUT=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SurfaceView surfaceView =new SurfaceView(this);
        surfaceView.getHolder().addCallback(this);
        setContentView(surfaceView);
        videoPath = Environment.getExternalStorageDirectory()+"/new1.mp4";
        Log.i(TAG,"videoPath ="+videoPath);

    }

    private void doPlay(Surface surface){
        Log.i(TAG,"doPlay");
        vextractor = new MediaExtractor();
        aextractor = new MediaExtractor();
        try {
            vextractor.setDataSource(videoPath);
            aextractor.setDataSource(videoPath);
            decodeVideo(surface);
            decodeAudio();
        } catch (IOException e) {
            Log.i(TAG,e.getMessage());
            e.printStackTrace();
        }
    }


    private void decodeVideo(Surface surface){
        for(int i=0;i<vextractor.getTrackCount();i++){
            MediaFormat format=vextractor.getTrackFormat(i);
            String mine=format.getString(MediaFormat.KEY_MIME);
            try{
                if(mine.startsWith("video")){
                    videoTrackIndex = i;
                    videoDecode = MediaCodec.createDecoderByType(mine);
                    videoDecode.configure(format,surface,null,0);
                    videoDecode.start();
                    new Thread(){
                        @Override
                        public void run() {
                            decodeAndPlayVideo();
                        }
                    }.start();
                    return ;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }


    private void decodeAudio(){
        for(int i=0;i<aextractor.getTrackCount();i++){
            MediaFormat format=aextractor.getTrackFormat(i);
            String mine=format.getString(MediaFormat.KEY_MIME);
            try{
               if(mine.startsWith("audio")){
                    audioTrackIndex = i;
                    audioDecode = MediaCodec.createDecoderByType(mine);
                    audioDecode.configure(format,null,null,0);
                    audioDecode.start();
                    initAudioPlay();
                    new Thread(){
                        @Override
                        public void run() {
                            decodeAndPlayAudio();
                        }
                    }.start();
                    return ;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private void decodeAndPlayVideo(){
        Log.i(TAG,"decodeAndPlayVideo");
        ByteBuffer[] inputBuffers=videoDecode.getInputBuffers();
        ByteBuffer[] outputBuffers = videoDecode.getOutputBuffers();
        boolean isEOF=false;
        long startMs = System.currentTimeMillis();
        while(!Thread.interrupted()){
            if(!isEOF){
                int inputBufferIndex = videoDecode.dequeueInputBuffer(TIME_OUT);
                if(inputBufferIndex>-1){
                    ByteBuffer temp = inputBuffers[inputBufferIndex];
                    temp.clear();

                    vextractor.selectTrack(videoTrackIndex);
                    int ret=vextractor.readSampleData(temp,0);
                    if(ret>-1){
                        videoDecode.queueInputBuffer(inputBufferIndex,0,ret,vextractor.getSampleTime(),vextractor.getSampleFlags());
                        vextractor.advance();
                    }else{
                        videoDecode.queueInputBuffer(inputBufferIndex,0,0,vextractor.getSampleTime(),MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                        isEOF = true;
                    }
                }
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferIndex = videoDecode.dequeueOutputBuffer(bufferInfo,TIME_OUT);
            if(outputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER){
                Log.i(TAG,"time out");
            }else if(outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                Log.i(TAG,"output format has changed new format ="+videoDecode.getOutputFormat());
            }else if(outputBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
                outputBuffers =videoDecode.getOutputBuffers();
            }else{
                ByteBuffer buffer = outputBuffers[outputBufferIndex];
             /*   byte[] temp = new byte[bufferInfo.size];
                buffer.get(temp);*/
                while (bufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                videoDecode.releaseOutputBuffer(outputBufferIndex,true);
            }

            if((bufferInfo.flags&MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0) {
                break;
            }

        }
        videoDecode.stop();
        videoDecode.release();
        videoDecode = null;
    }


    private void decodeAndPlayAudio(){
         ByteBuffer[] inputBuffers = audioDecode.getInputBuffers();
         ByteBuffer[] outputBuffers = audioDecode.getOutputBuffers();

         boolean inputEOF=false;
         boolean outputEOF = false;
         while(!outputEOF) {
             if(!inputEOF) {
                 int inputBufferIndex = audioDecode.dequeueInputBuffer(TIME_OUT);
                 if (inputBufferIndex > -1) {
                     ByteBuffer buffer = inputBuffers[inputBufferIndex];
                     aextractor.selectTrack(audioTrackIndex);
                     int size = aextractor.readSampleData(buffer, 0);
                     if (size > 0) {
                         audioDecode.queueInputBuffer(inputBufferIndex, 0, size, aextractor.getSampleTime(), 0);
                         aextractor.advance();
                     } else {
                         inputEOF = true;
                         audioDecode.queueInputBuffer(inputBufferIndex, 0, 0, aextractor.getSampleTime(), MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                     }
                 }
             }
             MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
             int outBufferIndex = audioDecode.dequeueOutputBuffer(info, TIME_OUT);
             if (outBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                 Log.i(TAG, "time out ");
             } else if (outBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                 Log.i(TAG, "format change ");
             } else if (outBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                 outputBuffers = audioDecode.getOutputBuffers();
             } else {
                 ByteBuffer outBuffer = outputBuffers[outBufferIndex];
                 final byte[] temp = new byte[info.size];
                 outBuffer.get(temp);
                 outBuffer.clear();
                 if(temp.length>0){
                     audioTrack.write(temp, info.offset, temp.length);
                 }
                 audioDecode.releaseOutputBuffer(outBufferIndex,false);
             }

             if((info.flags&MediaCodec.BUFFER_FLAG_END_OF_STREAM)!=0){
                 outputEOF=true;
             }
         }

         if(audioDecode!=null){
             audioDecode.stop();
             audioDecode.release();
             audioDecode = null;
         }

    }


    private void initAudioPlay(){
        MediaFormat format =aextractor.getTrackFormat(audioTrackIndex);
        int sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        Log.i(TAG,"initAudioPlay sampleRate ="+sampleRate);
        int channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        int channelConfig = channelCount ==1? AudioFormat.CHANNEL_OUT_MONO:AudioFormat.CHANNEL_OUT_STEREO;;
        int minBuffer = AudioTrack.getMinBufferSize(sampleRate,channelConfig,AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate,channelConfig,
                AudioFormat.ENCODING_PCM_16BIT,minBuffer,AudioTrack.MODE_STREAM);
        audioTrack.play();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG,"surfaceCreated");
        doPlay(holder.getSurface());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG,"surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG,"surfaceDestroyed");
        if(audioTrack!=null){
            audioTrack.stop();
            audioTrack.release();
            audioTrack = null;
        }

        if(aextractor != null){
            aextractor.release();
            aextractor = null;
        }

        if(vextractor != null){
            vextractor.release();
            vextractor = null;
        }
        finish();

    }
}
