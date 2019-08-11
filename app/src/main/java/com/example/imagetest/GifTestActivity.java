package com.example.imagetest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.imagetest.utils.GifAnimationDrawable;
import com.example.imagetest.utils.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GifTestActivity extends AppCompatActivity {
    private static final String TAG = "GifTestActivity";
    private ImageView imageView;
    private static byte[] gColorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif_test);
        imageView = findViewById(R.id.image);
    }

    public void showGifInfo(View view) {
        InputStream fis = null;
        try {
            fis = getAssets().open("boy.gif");
            // 读取Gif头部
            readHeader(fis);
            // 读取逻辑屏幕描述符
            readLogicScreenDescriptor(fis);
            // 读取全局颜色表
            readGlobalColorList(fis);
            // 读取应用扩展块
            readAppExtendPart(fis);
            // 读取图片控制扩展块
            readControlExtendPart(fis);
            // 读取图片描述符
            readImageDescriptor(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
        }
    }

    public void showGifFirstFrame(View view) {
        InputStream fis = null;
        try {
            fis = getAssets().open("boy.gif");
            // 读取Gif头部
            readHeader(fis);
            // 读取逻辑屏幕描述符
            readLogicScreenDescriptor(fis);
            // 读取全局颜色表
            readGlobalColorList(fis);
            // 读取应用扩展块
            readAppExtendPart(fis);
            // 读取图片控制扩展块
            readControlExtendPart(fis);
            // 读取图片描述符
            readImageDescriptor(fis);
            // 读取图片数据
            Bitmap bitmap = readImageData(fis);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
        }
    }

    public void showGifSecondFrame(View view) {
        GifAnimationDrawable gif;
        try {
            gif = new GifAnimationDrawable(getAssets().open("boy.gif"));
            gif.setOneShot(false);
            imageView.setImageDrawable(gif);
            gif.setVisible(true, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Bitmap readImageData(InputStream fis) throws Exception {
        byte[] dataHeader = new byte[2];
        fis.read(dataHeader);
        int minCode = dataHeader[0] & 0xFF;
        int length = (dataHeader[1] & 0xFF);
        Log.e(TAG, "length = " + length);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[length];
        fis.read(data);
        bos.write(data);
        byte[] size = new byte[1];
        fis.read(size);
        while ((size[0] & 0xFF) != 0) {
            byte[] buf = new byte[size[0] & 0xFF];
            fis.read(buf);
            bos.write(buf);
            fis.read(size);
        }
        byte[] frameData = bos.toByteArray();
//        LzwDecompression lzwDecompression = new LzwDecompression();
//        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(frameData);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        lzwDecompression.expand(byteArrayInputStream, byteArrayOutputStream);

//        byte[] realData = byteArrayOutputStream.toByteArray();
        return BitmapFactory.decodeByteArray(frameData, 0, frameData.length);
    }

    private static void readImageDescriptor(InputStream fis) throws Exception {
        byte[] descriptor = new byte[10];
        fis.read(descriptor);
        int blockType = (descriptor[0] & 0xFF);
        boolean dataPart = blockType == 0x2C;
        Log.e(TAG,"dataPart = " + dataPart);

        int lowByte = (descriptor[1] & 0xFF);
        int highByte = (descriptor[2] & 0xFF);
        int left = ((highByte << 8) + lowByte);

        lowByte = (descriptor[3] & 0xFF);
        highByte = (descriptor[4] & 0xFF);
        int top = ((highByte << 8) + lowByte);

        lowByte = (descriptor[5] & 0xFF);
        highByte = (descriptor[6] & 0xFF);
        int width = ((highByte << 8) + lowByte);

        lowByte = (descriptor[7] & 0xFF);
        highByte = (descriptor[8] & 0xFF);
        int height = ((highByte << 8) + lowByte);
        Log.e(TAG,"left = " + left + ", top = " + top + ", width = " + width + ", height = " + height);

        boolean isLocalColorList = (0x80 & descriptor[9]) != 0;
        Log.e(TAG,"isLocalColorList = " + isLocalColorList);

        int localColorSize = (0x07 & descriptor[9]);
        Log.e(TAG,"localColorSize = " + localColorSize);
    }


    /**
     * disposal method
     disposal method占3Bit，能够表示0-7。

     disposal method = 1
     解码器不会清理画布，直接将下一幅图像渲染上一幅图像上。

     disposal method = 2
     解码器会以背景色清理画布，然后渲染下一幅图像。背景色在逻辑屏幕描述符中设置。

     disposal method = 3
     解码器会将画布设置为上之前的状态，然后渲染下一幅图像。

     disposal method = 4-7
     未定义保留值

     delay time
     delay time占两个字节，为无符号整数，控制当前帧的展示时间，单位是10ms
     */
    private static void readControlExtendPart(InputStream fis) throws Exception {
        byte[] extendType = new byte[2];
        fis.read(extendType);
        int blockType = (extendType[0] & 0xFF);
        boolean extendPart = blockType == 0x21;

        Log.e(TAG,"extendPart = " + extendPart);
        int blockUseType = (extendType[1] & 0xFF);
        boolean controlType = blockUseType == 0xF9;
        Log.e(TAG,"controlType = " + controlType);
        byte[] controlExtendData = new byte[6];
        fis.read(controlExtendData);

        System.out.println(Integer.toBinaryString(controlExtendData[1]));
        // 0x01C0 = 0x0001 1100
        int disposalMethod = ((controlExtendData[1] & 0x01C0) >>> 2);
        Log.e(TAG,"disposalMethod = " + disposalMethod);

        int lowByte = (controlExtendData[2] & 0xFF);
        int highByte = (controlExtendData[3] & 0xFF);

        int delayTime = ((highByte << 8) + lowByte) * 10;
        Log.e(TAG,"delayTime = " + delayTime);
    }

    private static void readAppExtendPart(InputStream fis) throws Exception {
        byte[] extendType = new byte[2];
        fis.read(extendType);
        int blockType = (extendType[0] & 0xFF);
        boolean extendPart = blockType == 0x21;

        Log.e(TAG,"extendPart = " + extendPart);
        int blockUseType = (extendType[1] & 0xFF);
        boolean appType = blockUseType == 0xFF;
        Log.e(TAG,"appType = " + appType);
        byte[] appExtendData = new byte[17];
        fis.read(appExtendData);
    }

    private static void readGlobalColorList(InputStream fis) throws Exception {
        // 128 * 7 * 3 / 8 = 16 * 3 * 7
        byte[] colorList = new byte[16 * 3 * 8];
        fis.read(colorList);
        gColorList = colorList;
    }

    private static void readLogicScreenDescriptor(InputStream fis) throws Exception {
        // 逻辑屏幕描述符，7字节
        byte[] logicScreen = new byte[7];
        fis.read(logicScreen);

        // 0, 1字节逻辑屏幕宽度
        int highByte = (logicScreen[1] & 0xFF);
        int lowByte = (logicScreen[0] & 0xFF);
        int width = (highByte << 8) + lowByte;

        // 2, 3字节逻辑屏幕高度
        highByte = (logicScreen[3] & 0xFF);
        lowByte = (logicScreen[2] & 0xFF);
        int height = (highByte << 8) + lowByte;
        Log.e(TAG,"width = " + width + ", height = " + height);

        Log.e(TAG,"logicScreen[4] = " + Integer.toBinaryString(logicScreen[4]));

        // 4字节第1位表示是否包含全局颜色列表 0x80 = 0x1000 0000
        boolean globalColorList = (0x80 & logicScreen[4]) != 0;
        Log.e(TAG,"globalColorList = " + globalColorList);

        // 4字节第2\3\4位表示全局颜色列表 0x70 = 0x0111 0000
        int bitsPerColor = ((0x70 & logicScreen[4]) >>> 4) + 1;
        Log.e(TAG,"bitsPerColor = " + bitsPerColor);

        // 4字节第5位表示颜色列表中颜色排序是否按照出现频率高低出现
        // 0x08 = 0x0000 1000
        boolean sorted = ((0x08 & logicScreen[4])) != 0;
        Log.e(TAG,"sorted = " + sorted);

        // 4字节第5位表示颜色列表中颜色排序是否按照出现频率高低出现
        // 0x07 = 0x0000 0111
        int power = (0x07 & logicScreen[4]);
        int colorListSize = (int) (Math.pow(2, power + 1));
        Log.e(TAG,"power = " + power + ", colorListSize = " + colorListSize);

        int bgIndex = (logicScreen[5] & 0xFF);
        Log.e(TAG,"bgIndex = " + bgIndex);

        // aspectRatio = (aspectFactor + 15) / 64
        int aspectFactor = (logicScreen[6] & 0xFF);
        Log.e(TAG,"aspectFactor = " + aspectFactor);
    }

    private static void readHeader(InputStream fis) throws Exception {
        // GIF文件头部与版本号，6字节
        byte[] header = new byte[6];
        fis.read(header);
        String headerStr = new String(header);
        Log.e(TAG, headerStr); // GIF89a
    }
}
