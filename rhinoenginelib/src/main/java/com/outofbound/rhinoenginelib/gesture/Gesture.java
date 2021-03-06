package com.outofbound.rhinoenginelib.gesture;


public interface Gesture {
	
	void onClick(float x, float y);
	void onRelease(float x, float y);
	void onMove(float x, float y, float velX, float velY);
	void onScale(float scaleFactor);
}
