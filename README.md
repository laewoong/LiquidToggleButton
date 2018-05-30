# LiquidToggleButton

![](https://laewoong.github.io/assets/img/2018_03_18_cover.gif?raw=true)


LiquidToggleButton using Canvas api in android.
You can see [the original design](https://dribbble.com/shots/2853092-Liquid-Toggle-Button) by Apostol Voicu.

See How to implement this in [blog](https://laewoong.github.io/Liquid-Toggle-Button-by-ApostolVoicu/)


## How to use

If you want use this library, you only add the project as a library in your android project settings.

### Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

```xml
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
```

### Step 2. Add the dependency

```xml
dependencies {
    implementation 'com.github.laewoong:LiquidToggleButton:1.0.1'
}
```

This components have custom attributes, if you want use them, you must add this line in your xml file in the first component:

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    ...
    >
</RelativeLayout>
```

You can set two attributes: buttonColor, uncheckedColor, checkedColor.

```xml
<com.laewoong.apostolvoicu.liquidtogglebutton.LiquidToggleButton
        android:id="@+id/btn"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:buttonColor="#414141"
        app:uncheckedColor="#d7d7d7"
        app:checkedColor="#ffb84c"
        android:layout_width="100dp"
        android:layout_height="50dp" />
```

You can check button status using `isChecked()` method.

```xml
btn = (LiquidToggleButton)findViewById(R.id.btn);
btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Log.i("fff", "OnClicked!!!! : " + btn.isChecked());
    }
});
```

## Demo

Check the video in [youtube](https://youtu.be/0YN6adajz-U)

