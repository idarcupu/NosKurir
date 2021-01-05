package com.expedisi.noskurir.notifikasi;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.expedisi.noskurir.Fungsi;
import com.expedisi.noskurir.MainActivity;
import com.expedisi.noskurir.R;
import com.expedisi.noskurir.helper.Ringtonnya;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class TerimaNotif extends FirebaseMessagingService {

    SharedPreferences sp;
    DatabaseReference dr;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Fungsi f = new Fungsi(getApplicationContext());
        Map<String, String> a = remoteMessage.getData();
        sp = getSharedPreferences(f.tag, MODE_PRIVATE);
        dr = FirebaseDatabase.getInstance().getReference();
        sendNotification(a.get("title"),a.get("body"), f);
    }

    private void sendNotification(final String judul, final String isipesan, final Fungsi f) {

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.tingtong);
        String [] part = isipesan.split(Pattern.quote("|"));
        Intent intent = new Intent(TerimaNotif.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("idpesanan", part[1]);
        PendingIntent pendingIntent = PendingIntent.getActivity(TerimaNotif.this,Integer.parseInt("1") /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        long[] pattern = {500,500,500,500,500};
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";
            CharSequence name = getString(R.string.app_name);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Notification notification = new Notification.Builder(TerimaNotif.this)
                    .setContentTitle(judul)
                    .setContentText(part[0])
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setChannelId(CHANNEL_ID)
                    .setAutoCancel(true)
//                    .setSound(soundUri, audioAttributes)
                    .setVibrate(pattern)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(notifyID, notification);

            Ringtonnya.playAudio(TerimaNotif.this, R.raw.tingtong);
        }else{
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(TerimaNotif.this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(judul)
                    .setContentText(part[0])
                    .setSound(soundUri)
                    .setAutoCancel(true)
                    .setVibrate(pattern)
                    .setContentIntent(pendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt("1") /* ID of notification */, notificationBuilder.build());

            Ringtonnya.playAudio(TerimaNotif.this, R.raw.tingtong);
        }
    }
}
