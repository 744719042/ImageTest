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
    private int[] gColorList;
    private int colorListSize;
    protected int ix, iy;
    protected boolean interlace;
    private int width, height;
    protected short[] prefix;
    protected byte[] suffix;
    protected byte[] pixelStack;
    protected byte[] pixels;
    protected Bitmap image;
    protected byte[] block = new byte[256]; // current data block
    protected static final int MAX_STACK_SIZE = 4096;

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

    private Bitmap readImageData(InputStream fis) throws Exception {
        decodeBitmapData(fis);
        return image;
    }

    protected void decodeBitmapData(InputStream fis) throws Exception {
        int nullCode = -1;
        int npix = width * height;
        int available, clear, code_mask, code_size, end_of_information, in_code, old_code, bits, code, count, i, datum, data_size, first, top, bi, pi;
        if ((pixels == null) || (pixels.length < npix)) {
            pixels = new byte[npix]; // allocate new pixel array
        }
        if (prefix == null) {
            prefix = new short[MAX_STACK_SIZE];
        }
        if (suffix == null) {
            suffix = new byte[MAX_STACK_SIZE];
        }
        if (pixelStack == null) {
            pixelStack = new byte[MAX_STACK_SIZE + 1];
        }
        // Initialize GIF data stream decoder.
        data_size = fis.read();
        clear = 1 << data_size;
        end_of_information = clear + 1;
        available = clear + 2;
        old_code = nullCode;
        code_size = data_size + 1;
        code_mask = (1 << code_size) - 1;
        for (code = 0; code < clear; code++) {
            prefix[code] = 0; // XXX ArrayIndexOutOfBoundsException
            suffix[code] = (byte) code;
        }
        // Decode GIF pixel stream.
        datum = bits = count = first = top = pi = bi = 0;
        for (i = 0; i < npix;) {
            if (top == 0) {
                if (bits < code_size) {
                    // Load bytes until there are enough bits for a code.
                    if (count == 0) {
                        // Read a new data block.
                        count = readBlock(fis);
                        if (count <= 0) {
                            break;
                        }
                        bi = 0;
                    }
                    datum += (((int) block[bi]) & 0xff) << bits;
                    bits += 8;
                    bi++;
                    count--;
                    continue;
                }
                // Get the next code.
                code = datum & code_mask;
                datum >>= code_size;
                bits -= code_size;
                // Interpret the code
                if ((code > available) || (code == end_of_information)) {
                    break;
                }
                if (code == clear) {
                    // Reset decoder.
                    code_size = data_size + 1;
                    code_mask = (1 << code_size) - 1;
                    available = clear + 2;
                    old_code = nullCode;
                    continue;
                }
                if (old_code == nullCode) {
                    pixelStack[top++] = suffix[code];
                    old_code = code;
                    first = code;
                    continue;
                }
                in_code = code;
                if (code == available) {
                    pixelStack[top++] = (byte) first;
                    code = old_code;
                }
                while (code > clear) {
                    pixelStack[top++] = suffix[code];
                    code = prefix[code];
                }
                first = ((int) suffix[code]) & 0xff;
                // Add a new string to the string table,
                if (available >= MAX_STACK_SIZE) {
                    break;
                }
                pixelStack[top++] = (byte) first;
                prefix[available] = (short) old_code;
                suffix[available] = (byte) first;
                available++;
                if (((available & code_mask) == 0) && (available < MAX_STACK_SIZE)) {
                    code_size++;
                    code_mask += available;
                }
                old_code = in_code;
            }
            // Pop a pixel off the pixel stack.
            top--;
            pixels[pi++] = pixelStack[top];
            i++;
        }
        for (i = pi; i < npix; i++) {
            pixels[i] = 0; // clear missing pixels
        }

        setPixels();
    }

    protected void setPixels() {
        // expose destination image's pixels as int array
        int[] dest = new int[width * height];
        // fill in starting image contents based on last image's dispose code
        // copy each source line to the appropriate place in the destination
        int pass = 1;
        int inc = 8;
        int iline = 0;
        for (int i = 0; i < height; i++) {
            int line = i;
            if (interlace) {
                if (iline >= height) {
                    pass++;
                    switch (pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                            break;
                        default:
                            break;
                    }
                }
                line = iline;
                iline += inc;
            }
            line += iy;
            if (line < height) {
                int k = line * width;
                int dx = k + ix; // start of line in dest
                int dlim = dx + width; // end of dest line
                if ((k + width) < dlim) {
                    dlim = k + width; // past dest edge
                }
                int sx = i * width; // start of line in source
                while (dx < dlim) {
                    // map color and insert in destination
                    int index = ((int) pixels[sx++]) & 0xff;
                    int c = gColorList[index];
                    if (c != 0) {
                        dest[dx] = c;
                    }
                    dx++;
                }
            }
        }
        image = Bitmap.createBitmap(dest, width, height, Bitmap.Config.ARGB_4444);
    }

    protected int readBlock(InputStream fis) throws Exception {
        int blockSize = fis.read();
        int n = 0;
        if (blockSize > 0) {
            try {
                int count = 0;
                while (n < blockSize) {
                    count = fis.read(block, n, blockSize - n);
                    if (count == -1) {
                        break;
                    }
                    n += count;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return n;
    }

    private void readImageDescriptor(InputStream fis) throws Exception {
        byte[] descriptor = new byte[10];
        fis.read(descriptor);
        int blockType = (descriptor[0] & 0xFF);
        boolean dataPart = blockType == 0x2C;
        Log.e(TAG,"dataPart = " + dataPart);

        int lowByte = (descriptor[1] & 0xFF);
        int highByte = (descriptor[2] & 0xFF);
        ix = ((highByte << 8) + lowByte);

        lowByte = (descriptor[3] & 0xFF);
        highByte = (descriptor[4] & 0xFF);
        iy = ((highByte << 8) + lowByte);

        lowByte = (descriptor[5] & 0xFF);
        highByte = (descriptor[6] & 0xFF);
        width = ((highByte << 8) + lowByte);

        lowByte = (descriptor[7] & 0xFF);
        highByte = (descriptor[8] & 0xFF);
        height = ((highByte << 8) + lowByte);
        Log.e(TAG,"left = " + ix + ", top = " + iy + ", width = " + width + ", height = " + height);

        interlace = (descriptor[9] & 0x40) != 0;

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
    private void readControlExtendPart(InputStream fis) throws Exception {
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

//        System.out.println(Integer.toBinaryString(controlExtendData[1]));
        // 0x01C0 = 0x0001 1100
        int disposalMethod = ((controlExtendData[1] & 0x01C0) >>> 2);
        disposalMethod = disposalMethod == 0 ? 1 : disposalMethod;
        Log.e(TAG,"disposalMethod = " + disposalMethod);

        int lowByte = (controlExtendData[2] & 0xFF);
        int highByte = (controlExtendData[3] & 0xFF);

        int delayTime = ((highByte << 8) + lowByte) * 10;
        Log.e(TAG,"delayTime = " + delayTime);
    }

    private void readAppExtendPart(InputStream fis) throws Exception {
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

    private void readGlobalColorList(InputStream fis) throws Exception {
        // 128 * 7 * 3 / 8 = 16 * 3 * 7
        byte[] colorList = new byte[16 * 3 * 8];
        fis.read(colorList);
        gColorList = new int[256]; // max size to avoid bounds checks
        int i = 0;
        int j = 0;
        while (i < colorListSize) {
            int r = ((int) colorList[j++]) & 0xff;
            int g = ((int) colorList[j++]) & 0xff;
            int b = ((int) colorList[j++]) & 0xff;
            gColorList[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
        }
    }

    private void readLogicScreenDescriptor(InputStream fis) throws Exception {
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

//        Log.e(TAG,"logicScreen[4] = " + Integer.toBinaryString(logicScreen[4]));

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
        colorListSize = (int) (Math.pow(2, power + 1));
        Log.e(TAG,"power = " + power + ", colorListSize = " + colorListSize);

        int bgIndex = (logicScreen[5] & 0xFF);
        Log.e(TAG,"bgIndex = " + bgIndex);

        // aspectRatio = (aspectFactor + 15) / 64
        int aspectFactor = (logicScreen[6] & 0xFF);
        Log.e(TAG,"aspectFactor = " + aspectFactor);
    }

    private void readHeader(InputStream fis) throws Exception {
        // GIF文件头部与版本号，6字节
        byte[] header = new byte[6];
        fis.read(header);
        String headerStr = new String(header);
        Log.e(TAG, headerStr); // GIF89a
    }
}
