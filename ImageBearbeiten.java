package oop4;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Scanner;

public class ImageBearbeiten {
    private File oldFile;
    private int weight;
    private int height;
    private int startImage;
    private byte[] startPicture;
    private InputStream in;
    private byte[] buf;
    private byte[] firstHead;
    private byte[] secondHead;
    private byte[] extaHead;
    private byte[][] red;
    private byte[][] green;
    private byte[][] blue;

private void allDataRead()throws Exception{
        in.read(firstHead);
        in.read(secondHead);
        byte[] heightt = new byte[4];
        byte[] weightt = new byte[4];
        for(short i = 0; i < 4; i++){
            startPicture[i] = firstHead[i + 10];
            weightt[i] = secondHead[i + 4];
            heightt[i] = secondHead[i + 8];
        }
        ByteBuffer bF = ByteBuffer.wrap(startPicture);
        bF.order(ByteOrder.LITTLE_ENDIAN);
        startImage = bF.getInt();

        bF = ByteBuffer.wrap(weightt);
        bF.order(ByteOrder.LITTLE_ENDIAN);
        weight = bF.getInt();

        bF = ByteBuffer.wrap(heightt);
        bF.order(ByteOrder.LITTLE_ENDIAN);
        height = bF.getInt();

        if((weight*3)%4 == 0) {
            buf = new byte[weight * 3];
        }
        else{
            int i = 4 - (weight*3)%4;
            buf = new byte[weight*3 +  i];
        }
    }

    private void creatPixelMatrix() throws Exception {
      if(firstHead.length + secondHead.length < startImage){
            extaHead = new byte[startImage - firstHead.length + secondHead.length];
            in.read(extaHead);
        }
        red = new byte[Math.abs(height) + 1][weight + 1];
        green = new byte[Math.abs(height) + 1][weight + 1];
        blue = new byte[Math.abs(height) + 1][weight + 1];
        int realSize = weight * 3;
        int start = 0;
        if (height > 0) {
            start = Math.abs(height) - 1;
        }
        for (int i = start; i >= 0; i--) {
            in.read(buf);
            int iterator = 0;
            for (int l = 0; l < realSize; l += 3) {
                red[i][iterator] = buf[l];
                green[i][iterator] = buf[l + 1];
                blue[i][iterator] = buf[l + 2];
                iterator++;
            }
        }
    }
}