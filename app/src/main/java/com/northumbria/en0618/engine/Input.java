package com.northumbria.en0618.engine;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;

public class Input
{
    private static final int FROM_RADS_TO_DEGREES = -57;
    private static final float DEAD_ZONE = 5.0f;

    private static class RotationSensorListener implements SensorEventListener
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if (event.values.length > 4)
            {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                updateDeviceRotation(truncatedRotationVector);
            }
            else
            {
                updateDeviceRotation(event.values);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
        }
    }

    private static float s_screenWidth = 0.0f;
    private static float s_screenHeight = 0.0f;

    private static Sensor s_rotationSensor = null;
    private static SensorEventListener s_rotationSensorEventListener = null;
    private static float s_pitch = 0.0f;
    private static float s_roll = 0.0f;

    private static float s_currentTouchLocationX = 0.0f;
    private static float s_currentTouchLocationY = 0.0f;
    private static float s_lastTouchLocationX = 0.0f;
    private static float s_lastTouchLocationY = 0.0f;

    private static long s_touchStartTime = 0L;
    private static float s_touchDuration = 0.0f;
    private static boolean s_touched = false;
    private static boolean s_touchPressedThisFrame = false;
    private static boolean s_touchReleasedThisFrame = false;

    public static void onTouchEvent(MotionEvent event)
    {
        s_lastTouchLocationX = s_currentTouchLocationX;
        s_lastTouchLocationY = s_currentTouchLocationY;
        s_currentTouchLocationX = event.getX();
        s_currentTouchLocationY = s_screenHeight - event.getY();

        switch (event.getAction())
        {
        case MotionEvent.ACTION_DOWN:
            s_touched = true;
            s_touchPressedThisFrame = true;
            s_touchStartTime = System.nanoTime();
            break;
        case MotionEvent.ACTION_UP:
            s_touched = false;
            s_touchReleasedThisFrame = true;
            s_currentTouchLocationX = 0.0f;
            s_currentTouchLocationY = 0.0f;
            break;
        }
    }

    public static float getScreenWidth()
    {
        return s_screenWidth;
    }

    public static float getScreenHeight()
    {
        return s_screenHeight;
    }

    public static float getTouchDurationSeconds()
    {
        return s_touchDuration;
    }

    public static boolean isTouched()
    {
        return s_touched;
    }

    public static void consumeTouch()
    {
        s_touched = false;
        s_touchPressedThisFrame = false;
        s_touchReleasedThisFrame = false;
    }

    public static boolean getTouchPressed()
    {
        return s_touchPressedThisFrame;
    }

    public static boolean getTouchReleased()
    {
        return s_touchReleasedThisFrame;
    }

    public static float getCurrentTouchX()
    {
        return s_currentTouchLocationX;
    }

    public static float getCurrentTouchY()
    {
        return s_currentTouchLocationY;
    }

    public static float getLastTouchX()
    {
        return s_lastTouchLocationX;
    }

    public static float getLastTouchY()
    {
        return s_lastTouchLocationY;
    }

    // TODO: Use this to decide whether the input method is allowed.
    public static boolean rotationSensorAvailable()
    {
        return s_rotationSensor != null;
    }

    public static float getDevicePitch()
    {
        return s_pitch;
    }

    public static float getDeviceRoll()
    {
        return s_roll;
    }

    public static void initialise(Context context, float screenWidth, float screenHeight, int sensorResolutionMilliseconds)
    {
        s_screenWidth = screenWidth;
        s_screenHeight = screenHeight;

        if (sensorResolutionMilliseconds > 0)
        {
            try
            {
                SensorManager sensorManager = (SensorManager) context.getSystemService(Activity.SENSOR_SERVICE);
                if (sensorManager != null)
                {
                    if (s_rotationSensorEventListener == null)
                    {
                        s_rotationSensorEventListener = new RotationSensorListener();
                    }
                    else
                    {
                        sensorManager.unregisterListener(s_rotationSensorEventListener);
                    }
                    s_rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                    sensorManager.registerListener(s_rotationSensorEventListener, s_rotationSensor, sensorResolutionMilliseconds * 1000);
                }
            }
            catch (Exception exception)
            {
                s_rotationSensor = null;
            }
        }
    }

    private static void updateDeviceRotation(float[] vectors)
    {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        s_pitch = orientation[1] * FROM_RADS_TO_DEGREES;
        s_roll = orientation[2] * -FROM_RADS_TO_DEGREES;

        if (s_pitch < DEAD_ZONE)
        {
            s_pitch = 0.0f;
        }
        if (s_roll < DEAD_ZONE)
        {
            s_roll = 0.0f;
        }
    }

    public static void update()
    {
        s_touchDuration = (System.currentTimeMillis() - s_touchStartTime) / 1000.0f;
        s_touchPressedThisFrame = false;
        s_touchReleasedThisFrame = false;
    }
}
