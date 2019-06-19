package main;

import javax.swing.*;
import java.util.*;
import java.awt.*;

public class Screen extends JFrame {
	
	public Screen(int width, int height, String title) {
		this.setTitle(title);
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(true);
		this.setBackground(Color.black);
	}

	
}
