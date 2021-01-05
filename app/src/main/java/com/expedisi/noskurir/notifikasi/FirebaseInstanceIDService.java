package com.expedisi.noskurir.notifikasi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    DatabaseReference dr;
    public static SharedPreferences sp;
    private String kotanya;
    Context c;


    public FirebaseInstanceIDService() {
    }

    public FirebaseInstanceIDService(Context c, DatabaseReference dr, SharedPreferences sp) {
        this.dr = dr;
        this.sp = sp;
        this.c = c;
    }

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        registerToken(token);
    }

    private void registerToken(final String token) {
//       dr.child("tk").child(sp.getString("idku","")).addListenerForSingleValueEvent(new ValueEventListener() {
//           @Override
//           public void onDataChange(DataSnapshot dataSnapshot) {
//               if(dataSnapshot.exists()){
//                   String dariserver = dataSnapshot.getValue(String.class);
//                   if(!token.equals(dariserver)){
//                       dr.child("tk").child(sp.getString("idku","")).setValue(token);
//                       Log.e(Fungsi.Tag, "update");
//                   }else{
//                       Log.e(Fungsi.Tag, "sama");
//                   }
//               }else{
//                   dr.child("tk").child(sp.getString("idku","")).setValue(token);
//                   Log.e(Fungsi.Tag, "pertamakali");
//               }
//           }
//
//           @Override
//           public void onCancelled(DatabaseError databaseError) {
//
//           }
//       });
    }
}