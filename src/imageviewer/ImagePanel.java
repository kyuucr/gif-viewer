/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imageviewer;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * 
 * @author umum
 */
public class ImagePanel extends JPanel implements Runnable {

	private ImageData image;
	private int index = 0;
	private boolean isNewImage = false;
	private Thread t;

	public ImagePanel() {

	}

	@Override
	public void paintComponent(Graphics g) {
		if (image != null && image.getPixels(index) != null) {
			int x = (this.getWidth() - image.getWidth()) / 2;
			int y = (this.getHeight() - image.getHeight()) / 2;
			// System.out.println(x + ", " + y);
			Image img = createImage(new MemoryImageSource(image.getWidth(),
					image.getHeight(), image.getPixels(index), 0,
					image.getWidth()));
			g.drawImage(img, (x < 0) ? 0 : x, (y < 0) ? 0 : y, null);
		}
	}

	public void setImage(ImageData i) throws InterruptedException {
		this.image = i;
		this.index = 0;
		isNewImage = true;
		if (t != null)
			t.join();
		t = new Thread(this);
		t.start();
	}

	public void run() {
		isNewImage = false;
		if (image.isAnimated()) {
			while (!isNewImage) {
				this.paintComponent(this.getGraphics());
				if (++index >= image.getImageNum())
					index = 0;
				try {
					Thread.sleep(image.getAnimationDelay(index) * 10);
				} catch (InterruptedException ex) {
					Logger.getLogger(ImagePanel.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		} else
			this.paintComponent(this.getGraphics());
	}

}
