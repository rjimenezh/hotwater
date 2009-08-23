/**
 * 
 */
package com.modelesis.hotwater.app;

/**
 * The application's entry point.
 * 
 * @author ramon
 */
public class HotWater {
	
	public static void main(String[] args) {
		ApplicationBuilder appFactory = new ApplicationBuilder();
		appFactory.buildApp();
		appFactory.startApp();
	}

}
