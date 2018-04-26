package com.ugoodtech.umi.core.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class BaseUtil {
    public static String getRandomString(int number){
        String random = null;
        for (int i=0;i<number;i++){
            Integer ran = (int)(Math.random() * 10);
            if (0<ran && ran<10){
                if (random == null){
                    random = ran.toString();
                }else {
                    random = random + ran;
                }
            }else {
                i--;
            }
        }
        return random;
    }


    public static String priceToString(Float priceFloat) throws Exception {
        Double priceDouble;
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        String priceStr= null;
        if (null!=priceFloat) {
            priceDouble = (double)priceFloat;
            priceStr=decimalFormat.format(priceDouble);
        }
        return priceStr;

    }

    public static String priceToString(Long priceLong) throws Exception {
        Double priceDouble;
        DecimalFormat decimalFormat=new DecimalFormat("0.00");
        String priceStr= null;
        if (null!=priceLong) {
            priceDouble = (double)priceLong/100;
            priceStr=decimalFormat.format(priceDouble);
        }
        return priceStr;

    }

    public static Long priceToLong(String priceStr) throws Exception {
        Double priceDouble;
        Long priceLong=null;
        if (null!=priceStr) {
            priceDouble = (Double.parseDouble(priceStr))*100;
            priceLong=priceDouble.longValue();
        }
        return priceLong;

    }

    public static byte[] fileToByte(String url,String type)throws Exception{
        File file = new File(url);
        FileInputStream fis = new FileInputStream(file);
        byte[] b;
        b = new byte[fis.available()];
        fis.read(b);
        return b;
    }

    public static boolean createFile(File file) throws IOException {
        if(! file.exists()) {
            makeDir(file.getParentFile());
        }
        return file.createNewFile();
    }
    public static void makeDir(File dir) {
        if(! dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }

    public static String IntDivideZero(Double before) {
        if(null!=before){
            int after = (int) before.doubleValue();
            if (before == after) {
                return after + "";
            } else {
                return before + "";
            }
        }else{
            return "/";
        }

    }

    public static String IntDivideZero(float before) {
        int after = (int) before;
        if (before == after) {
            return after + "";
        } else {
            return before + "";
        }
    }

    public static byte[] readBytes(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;

            int readLengthThisTime = 0;

            byte[] message = new byte[contentLen];

            try {

                while (readLen != contentLen) {

                    readLengthThisTime = is.read(message, readLen, contentLen
                            - readLen);

                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }

                    readLen += readLengthThisTime;
                }

                return message;
            } catch (IOException e) {
                // Ignore
                // e.printStackTrace();
            }
        }

        return new byte[] {};
    }
}
