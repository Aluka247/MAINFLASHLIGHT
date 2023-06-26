package com.stemmaflashlight.FlashLight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial aSwitch;
    private boolean isNightModeOn;
    private  ImageView backArrow;

    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        aSwitch = findViewById(R.id.aSwitch);
       aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (isChecked){
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                   vibrate(15); // Vibrate for 50 milliseconds

                   buttonView.setText("Night Mode");
               }else {
                   AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                   buttonView.setText("Light Mode");
                   vibrate(15); // Vibrate for 50 milliseconds
               }
           }
       });

       boolean isNightModeOn = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
       aSwitch.setChecked(isNightModeOn);
       if (isNightModeOn){
           isNightModeOn = false;
           aSwitch.setText("Night Mode");
       }else {
           aSwitch.setText("Light Mode");
           isNightModeOn = true;
       }



        // Set the toolbar as the activity's action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Customize the toolbar image
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.getNavigationIcon().setTint(getResources().getColor(android.R.color.white));


        // Set click listener for the button
        CardView clearCache = findViewById(R.id.clear_cache);
        CardView moreApps = findViewById(R.id.more_apps);
        CardView rateUs = findViewById(R.id.rate_us);
        CardView shareApp = findViewById(R.id.share_app);
        CardView appVersion = findViewById(R.id.app_version);
        TextView owner = findViewById(R.id.owner);


        moreApps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15); // Vibrate for 50 milliseconds
                Toast.makeText(SettingsActivity.this,"You made a good decision",Toast.LENGTH_SHORT).show();
            }
        });

        rateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15); // Vibrate for 50 milliseconds
                Toast.makeText(SettingsActivity.this,"Thank you",Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.stemmaflashlight.FlashLight");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }
        });


        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15); // Vibrate for 50 milliseconds
                Toast.makeText(SettingsActivity.this,"Thank you",Toast.LENGTH_SHORT).show();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey, Download,Test, and share your testimony.\n" +
                        "Don't forget to share to friends and family.\n" +
                        "Remember, health is wealth. By using this FlashLight app, your vision is 100% secured " +
                        "and you can use this app without glasses \uD83D\uDE0E: " +
                        " https://play.google.com/store/apps/details?id=com.stemmaflashlight.FlashLight");
                startActivity(shareIntent);
            }
        });

        appVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15); // Vibrate for 50 milliseconds
                Toast.makeText(SettingsActivity.this,"Version: 1.0",Toast.LENGTH_SHORT).show();
            }
        });

        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(20); // Vibrate for 50 milliseconds
                Toast.makeText(SettingsActivity.this,"Hello",Toast.LENGTH_SHORT).show();
            }
        });


        clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate(15); // Vibrate for 50 milliseconds
                clearCache(SettingsActivity.this);
            }
        });
    }


    private void clearCache(Context context) {
        // Get the cache directory
        File cacheDir = context.getCacheDir();

        // Get the initial cache size
        long initialCacheSize = getDirectorySize(cacheDir);

        if (initialCacheSize > 0) {
            // Clear the application cache
            deleteDirectory(cacheDir);

            // Get the final cache size
            long finalCacheSize = getDirectorySize(cacheDir);

            // Calculate the size difference
            long cacheClearedSize = initialCacheSize - finalCacheSize;

            // Display a toast message with the cache cleared size
            Toast.makeText(context, "Cache cleared: " + formatFileSize(cacheClearedSize), Toast.LENGTH_SHORT).show();
        } else {
            // Display a toast message when there is nothing to clear
            Toast.makeText(context, "Nothing to clear", Toast.LENGTH_SHORT).show();
        }
    }

    private long getDirectorySize(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return 0;
        }

        long size = 0;
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += getDirectorySize(file);
                }
            }
        }
        return size;
    }

    private void deleteDirectory(File directory) {
        if (directory == null || !directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    deleteDirectory(file);
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatFileSize(long size) {
        if (size <= 0) {
            return "0 B";
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }


    // Handle the arrow click event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            vibrate(15); // Vibrate for 50 milliseconds
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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


}