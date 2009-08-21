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
		ApplicationFactory appFactory = new ApplicationFactory();
		appFactory.buildApp();
		appFactory.startApp();
	}

}
