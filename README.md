# Rhino Engine

[![](https://jitpack.io/v/MarcoCiaramella/RhinoEngine.svg)](https://jitpack.io/#MarcoCiaramella/RhinoEngine)

A graphics engine for Android in OpenGL ES 2.0.

## How to import in your Android project
Add JitPack in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

Add the dependency

```
dependencies {
	        implementation 'com.github.MarcoCiaramella:RhinoEngine:1.2.1'
}
```

## How to use

Add OpengGL ES 2.0 as requirement in your AndroidManifest.xml

```xml
<manifest ..... >

    <uses-feature
        android:glEsVersion="0x00020000"
        android:required="true" />

    <application
	.....
    </application>

</manifest>
```

Add engine to your main activity layout

```xml
<com.your.package.Engine
        android:id="@+id/engine"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

Define your implementation

```java
package com.your.package;

public class Engine extends AbstractEngine {

    private static final Vector3f CAMERA_EYE = new Vector3f(3,2,3);
    private static final Vector3f CAMERA_CENTER = new Vector3f(0,0,0);
    private static final Vector3f CAMERA_UP = new Vector3f(0,1,0);

    public Engine(Context context){
        super(context, new CameraPerspective(CAMERA_EYE, CAMERA_UP, CAMERA_CENTER, 1, 1000), null);
    }

    public Engine(Context context, AttributeSet attrs){
        super(context, attrs, new CameraPerspective(CAMERA_EYE, CAMERA_UP, CAMERA_CENTER, 1, 1000), null);
    }

    @Override
    protected void init() {
        // your code
    }
}
```
