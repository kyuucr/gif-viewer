/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imageviewer;

import java.util.ArrayList;

/**
 *
 * @author umum
 */
public class ImageData {
    
    private int width = 0;
    private int height = 0;
    private int imageNum = 0;
    private ArrayList<int[]> pixels = new ArrayList<int[]>();
    private ArrayList<int[]> startPoint = new ArrayList<int[]>();
    private ArrayList<Integer> animationDelay = new ArrayList<Integer>();

    public ImageData() {

    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @param int index
     * @return the pixels
     */
    public int[] getPixels(int index) {
        return pixels.get(index);
    }

    /**
     * @param the pixels to set, int index to set
     */
    public void setPixels(int[] pixels,int index) {
        this.imageNum++;
        this.pixels.set(index, pixels);
    }

    /**
     * Automatically push pixel array
     * @param the pixels to set
     */
    public void setPixels(int[] pixels) {
        this.imageNum++;
        this.pixels.add(pixels);
    }

    /**
     * @param int index
     * @return int start point of image
     */
    public int[] getStartPoint(int index) {
        return startPoint.get(index);
    }

    /**
     * @param int start point to set, int index
     */
    public void setStartPoint(int[] pixels,int index) {
        this.startPoint.set(index, pixels);
    }

    /**
     * @param int start point to set
     */
    public void setStartPoint(int[] pixels) {
        this.startPoint.add(pixels);
    }

    /**
     * @return the size
     */
    public int getSize() {
        return width * height;
    }

    /**
     * @return is the image animated
     */
    public boolean isAnimated() {
        return pixels.size() > 1;
    }

    /**
     * @param int index
     * @return the animationDelay
     */
    public int getAnimationDelay(int index) {
        return animationDelay.get(index);
    }

    /**
     * @param int the animationDelay to set
     */
    public void setAnimationDelay(int animationDelay) {
        this.animationDelay.add(animationDelay);
    }

    /**
     * @param int the animationDelay to set, int index
     */
    public void setAnimationDelay(int animationDelay, int index) {
        this.animationDelay.set(animationDelay, index);
    }

    /**
     * @return int number of images
     */
    public int getImageNum() {
        return imageNum;
    }

}
