package com.wiggin.mangersys.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;

import javax.imageio.ImageIO;


public class ImageUtil {

    public static String IMAGE_TYPE_GIF = "gif";// 图形交换格式
    public static String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
    public static String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
    public static String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
    public static String IMAGE_TYPE_PNG = "png";// 可移植网络图形
    public static String IMAGE_TYPE_PSD = "psd";// Photoshop的专用格式Photoshop


    /**
     * 缩放图片
     * 
     * @param srcImageFile
     * @param result
     * @param scale
     * @param flag
     * @throws Exception
     */
    public static void scale(String srcImageFile, String result, int scale, boolean flag) throws Exception {
        BufferedImage srcImage = ImageIO.read(new File(srcImageFile));
        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        if (flag) {
            width = width * scale;
            height = height * scale;
        } else {
            width = width / scale;
            height = height / scale;
        }
        Image scaledInstance = srcImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);

        BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = tag.getGraphics();
        graphics.drawImage(scaledInstance, 0, 0, null);
        graphics.dispose();
        ImageIO.write(tag, "JPEG", new File(result));// 输出到文件流
    }


    /**
     * 添加水印文字
     * 
     * @param pressText
     * @param srcImageFile
     * @param destImageFile
     * @param fontName
     * @param fontStyle
     * @param color
     * @param fontSize
     * @param x
     * @param y
     * @param alpha
     * @throws Exception
     */
    public static void pressText(String pressText, String srcImageFile, String destImageFile, String fontName, int fontStyle, Color color, int fontSize, int x, int y, float alpha)
            throws Exception {
        File img = new File(srcImageFile);
        Image src = ImageIO.read(img);
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(src, 0, 0, width, height, null);
        g.setColor(color);
        g.setFont(new Font(fontName, fontStyle, fontSize));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        // 在指定坐标绘制水印文字
        g.drawString(pressText, (width - (getLength(pressText) * fontSize)) / 2 + x, (height - fontSize) / 2 + y);
        g.dispose();
        ImageIO.write((BufferedImage) image, "JPEG", new File(destImageFile));// 输出到文件流
    }


    /**
     * 添加水印图片
     * 
     * @param pressImg
     * @param srcImageFile
     * @param destImageFile
     * @param x
     * @param y
     * @param alpha
     * @throws Exception
     */
    public static void pressImage(String pressImg, String srcImageFile, String destImageFile, int x, int y, float alpha) throws Exception {
        File img = new File(srcImageFile);
        Image src = ImageIO.read(img);
        int wideth = src.getWidth(null);
        int height = src.getHeight(null);
        BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(src, 0, 0, wideth, height, null);
        // 水印文件
        Image src_biao = ImageIO.read(new File(pressImg));
        int wideth_biao = src_biao.getWidth(null);
        int height_biao = src_biao.getHeight(null);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
        g.drawImage(src_biao, (wideth - wideth_biao) / 2, (height - height_biao) / 2, wideth_biao, height_biao, null);
        // 水印文件结束
        g.dispose();
        ImageIO.write((BufferedImage) image, "JPEG", new File(destImageFile));
    }


    /**
     * 裁剪图片
     * 
     * @param srcImageFile
     * @param result
     * @param x
     * @param y
     * @param width
     * @param height
     * @throws Exception
     */
    public static void cut(String srcImageFile, String result, int x, int y, int width, int height) throws Exception {
        // 读取源图像
        BufferedImage bi = ImageIO.read(new File(srcImageFile));
        int srcWidth = bi.getHeight(); // 源图宽度
        int srcHeight = bi.getWidth(); // 源图高度
        if (srcWidth > 0 && srcHeight > 0) {
            Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
            // 四个参数分别为图像起点坐标和宽高
            // 即: CropImageFilter(int x,int y,int width,int height)
            ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
            Image img = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), cropFilter));
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(img, 0, 0, width, height, null); // 绘制切割后的图
            g.dispose();
            // 输出为文件
            ImageIO.write(tag, "JPEG", new File(result));
        }
    }


    public final static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (new String(text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }
}
