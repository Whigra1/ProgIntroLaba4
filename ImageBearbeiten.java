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
}