package com.flashlighttouch.FlashLight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.slider.Slider;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.skyfishjy.library.RippleBackground;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private ImageView image1, image2;
    private RippleBackground rippleBackground;
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashOn = false;

    private Timer strobeTimer;
    private TimerTask strobeTask;

    private Slider strobeSlider;

    private Vibrator vibrator;

    private ReviewManager reviewManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CardView cardV1 = findViewById(R.id.cardV1);

        cardV1.setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent1);
            vibrate(15); // Vibrate for 50 milliseconds
        });

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        rippleBackground = findViewById(R.id.content);

        strobeSlider = findViewById(R.id.strobeSlider);
        strobeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                // Update strobe effect based on slider value
                updateStrobeEffect(value);
            }
        });

        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate(20); // Vibrate for 50 milliseconds

                if (!rippleBackground.isRippleAnimationRunning()) {
                    image2.setColorFilter(Color.argb(255, 255, 255, 255));
                    rippleBackground.startRippleAnimation();
                    toggleFlashlightWithStrobe(true);
                } else {
                    image2.setColorFilter(null);
                    rippleBackground.stopRippleAnimation();
                    toggleFlashlightWithStrobe(false);
                }
            }
        };


        image1.setOnClickListener(clickListener);
        image2.setOnClickListener(clickListener);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        strobeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                updateStrobeEffect(value);
                vibrate(10); // Vibrate for 50 milliseconds
            }
        });

        // ...
    }

    private void vibrate(long milliseconds) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(milliseconds);
            }
        }
    }


    private void toggleFlashlightWithStrobe(boolean state) {
        if (strobeTimer != null) {
            strobeTimer.cancel();
            strobeTimer = null;
        }
        if (strobeTask != null) {
            strobeTask.cancel();
            strobeTask = null;
        }

        int strobeInterval = (int) strobeSlider.getValue() + 1; // Add 1 to avoid division by zero

        if (state && strobeInterval > 2) {
            strobeTimer = new Timer();
            strobeTask = new StrobeTask(strobeInterval);
            strobeTimer.schedule(strobeTask, 0, strobeInterval);
        } else {
            toggleFlashlight(state); // Use the provided state to toggle the flashlight
        }
    }

    private class StrobeTask extends TimerTask {
        private boolean isFlashOn = false;
        private int strobeInterval;

        public StrobeTask(int strobeInterval) {
            this.strobeInterval = strobeInterval;
        }

        @Override
        public void run() {
            toggleFlashlight(isFlashOn);
            isFlashOn = !isFlashOn;
        }
    }

    private void toggleFlashlight(boolean state) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, state);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void updateStrobeEffect(float value) {
        if (rippleBackground.isRippleAnimationRunning()) {
            toggleFlashlightWithStrobe(false);
            toggleFlashlightWithStrobe(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rippleBackground.isRippleAnimationRunning()) {
            rippleBackground.stopRippleAnimation();
            toggleFlashlightWithStrobe(false);
        }
    }

    private void requestReview() {
        reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    // We got the ReviewInfo object
                    ReviewInfo reviewInfo = task.getResult();
                    launchReviewFlow(reviewInfo);
                } else {
                    // There was an error
                    // Handle the error gracefully or fallback to a different rating method
                }
            }
        });
    }

    private void launchReviewFlow(ReviewInfo reviewInfo) {
        Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
        flow.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                // The review flow has finished (whether the user submitted a review or not)
                // You can perform any necessary actions after the review flow here
            }
        });
    }
}