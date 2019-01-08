package com.main;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel
{
	private static final long serialVersionUID = 8657137422032250507L;
	private Image image = null;
 
	public BackgroundPanel(Image image)
	{
		this.image = image;
	}
	protected void paintComponent(Graphics g)
	{
		g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
	}
}
