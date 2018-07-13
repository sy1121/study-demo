package com.example.yishe.audio_demo;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

import static android.media.MediaCodec.BUFFER_FLAG_END_OF_STREAM;

public class VideoEncoder {
    private static final String TAG="VideoEncoder";
    private String mime ="video/avc";// H.264 Advanced Video
    private int rate = 25600;
    private int frameRate =24;
    private int frameInterval =5;
    private MediaFormat format;
    private int mWidth,mHeight;
    private MediaCodec vEncoder;
    private boolean vEncoderEnd;
    private long presentationTimeUs;
    private boolean isEncodeLooping;
    private final int TIMEOUT_USEC = 10000;

    private Thread videoDecodeThread;

    private LinkedBlockingQueue<byte[]> videoQueue;

    public VideoEncoder(int width,int height){
        mWidth = width;
        mHeight= height;
        init();
    }

    private void init(){
        format = MediaFormat.createVideoFormat(mime,mWidth,mHeight);
        //选择系统用于编码H264的编码器信息
        MediaCodecInfo vCodecInfo = selectCodec(mime);
        if (vCodecInfo == null) {
            Log.e(TAG, "====zhongjihao=====Unable to find an appropriate codec for " + mime);
            return;
        }

        Log.d(TAG, "======zhongjihao====found video codec: " + vCodecInfo.getName());
        //根据MIME格式,选择颜色格式
        int mColorFormat = selectColorFormat(vCodecInfo, mime);
        rate = (mWidth*mHeight*3/2)*8*frameRate;
        format.setInteger(MediaFormat.KEY_BIT_RATE,rate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE,frameRate);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,frameInterval);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, mColorFormat);

        try {
            vEncoder=MediaCodec.createEncoderByType(mime);
         /*   vEncoder.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            vEncoder.start();*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void pullVideoData(byte[] data){
        try {
            videoQueue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void startEncodeVideo(){
        if(vEncoder == null){
            return ;
        }
        videoDecodeThread =new Thread() {
            @Override
            public void run() {
                vEncoderEnd = false;
                presentationTimeUs =System.currentTimeMillis()*1000;
                vEncoder.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
                vEncoder.start();
                while(isEncodeLooping&&!isInterrupted()){
                    try {
                        byte[] data = videoQueue.take();
                        doEncodeVideo(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                if(vEncoder!=null){
                    vEncoder.stop();
                    vEncoder.release();
                    vEncoder = null;
                }

            }
        };

        isEncodeLooping = true;
        videoDecodeThread.start();
    }

    private void doEncodeVideo(byte[] data){
        try {
            ByteBuffer[] inputBufs=vEncoder.getInputBuffers();
            int index = vEncoder.dequeueInputBuffer(-1);
            long pts=System.currentTimeMillis()*1000-presentationTimeUs;
            if(index>-1){
                ByteBuffer buffer=inputBufs[index];
                buffer.clear();
                buffer.put(data);
                if(vEncoderEnd){
                    vEncoder.queueInputBuffer(index, 0, data.length, pts, BUFFER_FLAG_END_OF_STREAM);
                }else {
                    vEncoder.queueInputBuffer(index, 0, data.length, pts, 0);
                }
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            ByteBuffer[] outBuffers=vEncoder.getOutputBuffers();
            int outBufferIndex = vEncoder.dequeueOutputBuffer(bufferInfo,TIMEOUT_USEC);
            if(outBufferIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED){
                outBuffers = vEncoder.getOutputBuffers();
            }else if(outBufferIndex  == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                Log.i(TAG,"format changed");
            }

            while(outBufferIndex>-1){
                ByteBuffer outBuffer =outBuffers[outBufferIndex];
                byte[] temp =new byte[bufferInfo.size];
                outBuffer.get(temp);
                // 写到文件

                vEncoder.releaseOutputBuffer(outBufferIndex,false);
                outBufferIndex = vEncoder.dequeueOutputBuffer(bufferInfo,TIMEOUT_USEC);
                // 编码结束
                if((bufferInfo.flags&BUFFER_FLAG_END_OF_STREAM)!=0){
                    isEncodeLooping = false;
                    videoDecodeThread.interrupt();
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }

    private int selectColorFormat(MediaCodecInfo codecInfo,
                                  String mimeType) {
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo
                .getCapabilitiesForType(mimeType);
        for (int i = 0; i < capabilities.colorFormats.length; i++) {
            int colorFormat = capabilities.colorFormats[i];
            if (isRecognizedFormat(colorFormat)) {
                return colorFormat;
            }
        }

        Log.d(TAG,
                "==zhongjihao====couldn't find a good color format for " + codecInfo.getName()
                        + " / " + mimeType);
        return 0; // not reached
    }

    private boolean isRecognizedFormat(int colorFormat) {
        switch (colorFormat) {
            // these are the formats we know how to handle for this test
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                return false;
        }
    }



}
