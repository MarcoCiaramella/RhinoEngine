package com.outofbound.rhinoengine.gesture;


public interface GLGesture {
	
	void onClick(float x, float y);
	void onRelease(float x, float y);
	void onMove(float x, float y, float velX, float velY);
	void onScale(float scaleFactor);
}
