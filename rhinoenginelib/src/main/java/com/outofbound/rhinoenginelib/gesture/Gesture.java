package com.outofbound.rhinoenginelib.gesture;


/**
 * The gesture interface.
 */
public interface Gesture {

	/**
	 * Called on a click on the screen.
	 * @param x the x position of the click
	 * @param y the y position of the click
	 */
	void onClick(float x, float y);

	/**
	 * Called when the click on the screen is released.
	 * @param x the x position of the release
	 * @param y the y position of the release
	 */
	void onRelease(float x, float y);

	/**
	 * Callend then finger is moving over the screen.
	 * @param x the current x position on the screen
	 * @param y the current y position on the screen
	 * @param velX the x velocity of the moving
	 * @param velY the y velocity of the moving
	 */
	void onMove(float x, float y, float velX, float velY);

	/**
	 * Called when a scale gesture is detected.
	 * @param scaleFactor the scale factor detected
	 */
	void onScale(float scaleFactor);
}
