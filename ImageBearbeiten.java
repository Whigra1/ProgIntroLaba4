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

    public File reseizX(double sizeX) throws Exception{
        if(sizeX % 1 > 0){
            File newFile = new File(wayOut());
            newFile = doubleX(sizeX, newFile);
            return newFile;
        }
        else {
            File newFile = new File(wayOut());
            int newHeight = Math.abs(height) * (int)(sizeX);
            int newWeight = weight * (int)(sizeX);
            byte[] newwHeight = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(newHeight).array();
            byte[] newwWeight = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(newWeight).array();
            for (short i = 0; i < 4; i++) {
                secondHead[i + 8] = newwHeight[i];
                secondHead[i + 4] = newwWeight[i];
            }
            FileOutputStream output = new FileOutputStream(newFile);
            output.write(firstHead);
            output.write(secondHead);
            if (extaHead != null) {
                output.write(extaHead);
            }
            int start = 0;
            if (height > 0) {
                start = height - 1;
            }
            for (int i = start; i >= 0; i--) {
                for (int l = 0; l < sizeX; l++) {
                    for (int j = 0; j < weight; j++) {
                        for (int k = 0; k < sizeX; k++) {
                            output.write(red[i][j]);
                            output.write(green[i][j]);
                            output.write(blue[i][j]);
                        }
                    }
                    if (newWeight % 4 != 0) {
                        for (int s = 0; s < 4 - (newWeight % 4); s++) {
                            output.write(0);
                        }
                    }
                }
            }
            return newFile;
        }
    }
    private File doubleX(double sizeX, File newFile) throws Exception{
        int newHeight = (int)(Math.abs(height) * sizeX);
        int newWeight = (int)(weight * sizeX);
        byte[][] redNew = new byte[newHeight][newWeight];
        byte[][] greenNew = new byte[newHeight][newWeight];
        byte[][] blueNew = new byte[newHeight][newWeight];
        double kofY = (double) height/(double) newHeight;
        double kofX = (double) weight/(double)newWeight;
        for(int i = 0; i < newHeight; i++){
            for(int l = 0; l < newWeight; l++){
                int realI = (int)(i * kofY);
                int realL = (int)(l * kofX);
                redNew[i][l] = berechnen(realI, realL, i, l, kofX, kofY, red[realI][realL], red[realI][realL + 1], red[realI + 1][realL], red[realI + 1][realL + 1]);
                greenNew[i][l] = berechnen(realI, realL, i, l, kofX, kofY, green[realI][realL], green[realI][realL + 1], green[realI + 1][realL], green[realI + 1][realL + 1]);
                blueNew[i][l] = berechnen(realI, realL, i, l, kofX, kofY, blue[realI][realL], blue[realI][realL + 1], blue[realI + 1][realL], blue[realI + 1][realL + 1]);
            }
        }
        byte[] newwHeight = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(newHeight).array();
        byte[] newwWeight = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(newWeight).array();
        for (short i = 0; i < 4; i++) {
            secondHead[i + 8] = newwHeight[i];
            secondHead[i + 4] = newwWeight[i];
        }
        FileOutputStream output = new FileOutputStream(newFile);
        output.write(firstHead);
        output.write(secondHead);
        if (extaHead != null) {
            output.write(extaHead);
        }
            for (int l = newHeight - 1; l >= 0; l--) {
                for (int j = 0; j < newWeight; j++) {
                        output.write(redNew[l][j]);
                        output.write(greenNew[l][j]);
                        output.write(blueNew[l][j]);
                }
                if (3*newWeight % 4 != 0) {
                    for (int s = 0; s < 4 - (3*newWeight % 4); s++) {
                        output.write(0);
                    }
                }
            }
        return newFile;
    }
    public byte berechnen(int realI, int realL, int i, int l, double kofX, double kofY, byte b1, byte b2, byte b3, byte b4) {
        double r1 = ((double)realL + 1 - l*kofX)/((double)realL + 1 - (double)realL)*(double)b1 + (kofX*l - realL)/((double)realL + 1 - realL)*(double)b2;
        double r2 = ((double)realL + 1 - l*kofX)/((double)realL + 1 - (double)realL)*(double)b3 + (kofX*l - realL)/((double)realL + 1 - realL)*(double)b4;
        double p = ((double)realI + 1 - i*kofY)/((double)realI + 1 - (double)realI)*r1 + (kofY*i - realI)/((double)realI + 1 - (double)realI)*r2;
        return (byte) p;
    }
    private String wayIn(){
        System.out.print("Enter Plase Way to your File: ");
        return a.nextLine();
    }
    private String wayOut(){
        System.out.print("Enter Plase Way where to creat your new File: ");
        return a.nextLine();
    }
    private double enterX(){
        String str = "";
        boolean b;
        do {
            System.out.print("Enter X:");
            str = a.nextLine();
            b = false;
            try {
                Double.valueOf(str);
            } catch (Exception e){
                b = true;
            }
        } while (b);
        return Double.valueOf(str);
    }

}