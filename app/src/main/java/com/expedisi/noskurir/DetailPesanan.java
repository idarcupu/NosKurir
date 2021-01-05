package com.expedisi.noskurir;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.expedisi.noskurir.adapter.AdapterDetilBarang;
import com.expedisi.noskurir.adapter.AdapterGudang;
import com.expedisi.noskurir.model.ModelGudang;
import com.expedisi.noskurir.model.ModelTransaksi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.ncorti.slidetoact.SlideToActView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class DetailPesanan extends AppCompatActivity {

    LinearLayout lsamakota, llainkota, ldetail, lpilihgudang, lisilainkota;
    TextView alamatjemput, namapengguna, alamatantar, mapjemput, mapantar, alamatantarlain, mapantarlain;
    CircleImageView fotopengguna;
    CardView cvpilihgudang;
    String noresi;
    Uri picUri;
    SlideToActView geser;
    RecyclerView rvgudang;
    ProgressDialog pd;
    Fungsi f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_pesanan);

        f = new Fungsi(this, this);
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Silahkan Tunggu...");

        noresi = getIntent().getExtras().getString("noresi");

        ldetail = findViewById(R.id.ldetail);
        lsamakota = findViewById(R.id.lsamakota);
        llainkota = findViewById(R.id.llainkota);
        alamatantar = findViewById(R.id.alamatantar);
        alamatantarlain = findViewById(R.id.alamatantarlain);
        mapantarlain = findViewById(R.id.mapantarlain);
        lpilihgudang = findViewById(R.id.lpilihgudang);
        lisilainkota = findViewById(R.id.lisilainkota);
        fotopengguna = findViewById(R.id.fotopengguna);
        namapengguna = findViewById(R.id.namapengguna);
        cvpilihgudang = findViewById(R.id.cvpilihgudang);
        alamatjemput = findViewById(R.id.alamatjemput);

        mapjemput = findViewById(R.id.mapjemput);
        mapantar = findViewById(R.id.mapantar);

        geser = findViewById(R.id.geser);
        geser.setSliderIcon(R.mipmap.ic_launcher);

        f.dr.child("transaksi").child(noresi).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot transaksi) {
                ModelTransaksi m = transaksi.getValue(ModelTransaksi.class);

                if(m.getStatus().equals("Menunggu")){
                    geser.setText("Jemput Barang");
                    geser.setOnSlideCompleteListener(slideToActView -> {
                        if(m.getKotaasal().equals(m.getKotatujuan())){
                            HashMap<String, Object> mapnya = new HashMap();
                            mapnya.put("transaksi/"+noresi+"/status", "Menuju");
                            mapnya.put("tracking/"+noresi+"/"+f.dr.push().getKey(), "[Kurir]Menuju penjemputan paket ke [Pelanggan]|"+f.tglSekarang("eropa")+"|"+f.jamSekarang());
                            f.dr.updateChildren(mapnya);
                            geser.resetSlider();
                            geser.setText("Antar Barang");
                        }else{
                            if(!m.getAgen().equals("kosong")){
                                HashMap<String, Object> mapnya = new HashMap();
                                mapnya.put("transaksi/"+noresi+"/status", "Menuju");
                                mapnya.put("tracking/"+noresi+"/"+f.dr.push().getKey(), "[Kurir]Menuju penjemputan paket ke [Pelanggan]|"+f.tglSekarang("eropa")+"|"+f.jamSekarang());
                                f.dr.updateChildren(mapnya);
                                geser.resetSlider();
                                geser.setText("Antar Barang");
                            }else{
                                geser.resetSlider();
                                Toast.makeText(DetailPesanan.this, "Anda belum memilih gudang Nos.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else if(m.getStatus().equals("Menuju")){
                    String kemana = "";
                    if(m.getAgen().equals("kosong")){
                        kemana = "Penerima";
                    }else{
                        String[] gudangsplit1 = m.getAgen().split(Pattern.quote("_"));
                        kemana = gudangsplit1[0];
                    }
                    geser.setText("Antar Barang");
                    String finalKemana = kemana;
                    geser.setOnSlideCompleteListener(slideToActView -> {
                        HashMap<String, Object> mapnya = new HashMap();
                        mapnya.put("transaksi/"+noresi+"/status", "Antar");
                        mapnya.put("tracking/"+noresi+"/"+f.dr.push().getKey(), "[Kurir]Paket Dikirim ke ["+ finalKemana +"]|"+f.tglSekarang("eropa")+"|"+f.jamSekarang());
                        f.dr.updateChildren(mapnya);
                        geser.resetSlider();
                        geser.setText("Selesaikan");
                        geser.setReversed(true);
                    });
                }else if(m.getStatus().equals("Antar")){
                    geser.setText("Selesaikan");
                    geser.setReversed(true);
                    geser.setOnSlideCompleteListener(slideToActView -> {
                        String st = m.getKotaasal().equals(m.getKotatujuan()) ? "Sukses" : "SampaiAgen";
                        if(st.equals("Sukses")){
                            cekPermission(Fungsi.kodenya);
                            geser.setReversed(true);
                            geser.resetSlider();
                        }else {
                            String[] gudangsplit = m.getAgen().split(Pattern.quote("_"));
                            geser.setReversed(true);
                            geser.resetSlider();
                            // status, gudang1, idkurir1
                            bsbqrcode(noresi, st, gudangsplit[0]+"_darikurir", f.kurir("idku")+"_selesai");
                        }
                    });
                }else if(m.getStatus().equals("SampaiAgen")){
                    geser.setVisibility(View.GONE);
                }

                ldetail.setOnClickListener(v -> {
                    bsbDetailBarang(transaksi.child("z_kat"));
                });

                f.dr.child("user").child(m.getIdpemesan()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot user) {
                        String nama = user.child("nama").getValue(String.class);
                        namapengguna.setText(nama);
                        alamatjemput.setText(m.getAddr1());
                        mapjemput.setOnClickListener(v -> f.panggilMap(m.getLat1(), m.getLon1()));

                        if(m.getKotaasal().equals(m.getKotatujuan())){
                            lsamakota.setVisibility(View.VISIBLE);
                            llainkota.setVisibility(View.GONE);
                            lisilainkota.setVisibility(View.GONE);
                            alamatantar.setText(m.getAddr2());
                            mapantar.setOnClickListener(v -> f.panggilMap(m.getLat2(), m.getLon2()));
                        }else{
                            lsamakota.setVisibility(View.GONE);
                            llainkota.setVisibility(View.VISIBLE);
                            if(!m.getAgen().equals("kosong")){
                                cvpilihgudang.setVisibility(View.GONE);
                                lisilainkota.setVisibility(View.VISIBLE);
                                String[] gudangsplit = m.getAgen().split(Pattern.quote("_"));
                                f.dr.child("gudang").child(m.getKotaasal()).child(gudangsplit[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot gudang) {
                                        ModelGudang mg = gudang.getValue(ModelGudang.class);
                                        alamatantarlain.setText(mg.getAlamat());
                                        mapantarlain.setOnClickListener(v -> f.panggilMap(mg.getLat(), mg.getLon()));
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }else{
                                cvpilihgudang.setVisibility(View.VISIBLE);
                                lisilainkota.setVisibility(View.GONE);
                            }
                            lpilihgudang.setOnClickListener(v -> bsbgudang(m.getNoresi(), m.getKotaasal()));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bsbselesai(Uri uri) {
        final BottomSheetDialog bsd = new BottomSheetDialog(DetailPesanan.this);
        View v = getLayoutInflater().inflate(R.layout.bsbselesai, null);
        String[] Item = {"Diterima langsung oleh penerima","Diterima oleh keluarga penerima","Diterima oleh tetangga penerima","Diterima oleh satpam/keamanan","Diterima oleh resepsionis"};

        ImageView ft = v.findViewById(R.id.ft);
        Picasso.with(DetailPesanan.this).load(uri).into(ft);
        Spinner spinner = v.findViewById(R.id.spinner);
        Button selesaikan = v.findViewById(R.id.selesaikan);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,Item);
        spinner.setAdapter(adapter);

        selesaikan.setOnClickListener(v1 -> {
            String spinteks = spinner.getSelectedItem().toString();
            if(!TextUtils.isEmpty(spinteks)){
                bsd.cancel();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pd.show();
                    }
                },500);
                upload(uri,spinteks);
            }
        });

        bsd.setCancelable(false);
        bsd.setCanceledOnTouchOutside(false);
        bsd.setContentView(v);
        bsd.show();
    }

    private void bsbDetailBarang(DataSnapshot datanyax) {
        final BottomSheetDialog bsd = new BottomSheetDialog(DetailPesanan.this);
        View v = getLayoutInflater().inflate(R.layout.bsbdetailbarang, null);
        ArrayList<String> listdetilx = new ArrayList<>();
        RecyclerView rvditel = v.findViewById(R.id.rvditel);
        rvditel.setLayoutManager(new GridLayoutManager(DetailPesanan.this, 1));
        rvditel.setHasFixedSize(true);
        AdapterDetilBarang adb = new AdapterDetilBarang(DetailPesanan.this, DetailPesanan.this, listdetilx);
        rvditel.setAdapter(adb);
        f.dr.child("transaksi").child(noresi).child("z_kat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot tr) {
                for(DataSnapshot dskat: tr.getChildren()){
                    String hasil = dskat.getValue(String.class);
                    listdetilx.add(hasil);
                }
                adb.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bsd.setContentView(v);
        bsd.show();
    }

    private void bsbqrcode(String noresi, String status, String gudang, String idkurir) {
        final BottomSheetDialog bsd = new BottomSheetDialog(DetailPesanan.this);
        View v = getLayoutInflater().inflate(R.layout.bsb_qrcode, null);
        String teks = noresi+"|"+status+"|"+gudang+"|"+idkurir;
        ImageView qrcode = v.findViewById(R.id.qrcode);

        try {
            Bitmap bitmap = buatKode(teks);
            qrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        bsd.setContentView(v);
        bsd.show();
    }

    Bitmap buatKode(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 150, 150, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }

        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private void bsbgudang(String noresi, String kota){
        final BottomSheetDialog bsd = new BottomSheetDialog(DetailPesanan.this);
        View v = getLayoutInflater().inflate(R.layout.bsbgudang, null);
        ArrayList<ModelGudang> listgudang = new ArrayList<>();
        rvgudang = v.findViewById(R.id.rvgudang);
        rvgudang.setLayoutManager(new GridLayoutManager(this, 1));
        rvgudang.setHasFixedSize(true);
        final AdapterGudang ag = new AdapterGudang(this, this, listgudang, bsd);
        rvgudang.setAdapter(ag);

        f.dr.child("gudang").child(kota).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot gudang) {
                f.dr.child("kurirLatLon").child(f.kurir("idku")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot kurirlatlon) {
                        String lat = kurirlatlon.child("lat").getValue(String.class);
                        String lon = kurirlatlon.child("lon").getValue(String.class);
                        listgudang.clear();
                        for(DataSnapshot dsgudang: gudang.getChildren()){
                            ModelGudang mg = dsgudang.getValue(ModelGudang.class);
                            mg.setNoresi(noresi);
                            mg.setLatkurir(lat);
                            mg.setLonkurir(lon);
                            listgudang.add(mg);
                        }
                        ag.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        bsd.setContentView(v);
        bsd.show();
    }

    private void cekPermission(final int kode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(DetailPesanan.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                ambilFoto(kode);
            } else {
                if (ContextCompat.checkSelfPermission(DetailPesanan.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DetailPesanan.this,
                            android.Manifest.permission.CAMERA)) {
                        new AlertDialog.Builder(DetailPesanan.this)
                                .setTitle("Nusantara Online Services")
                                .setMessage("Izinkan aplikasi mengakses kamera.")
                                .setCancelable(false)
                                .setPositiveButton("Izinkan", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ActivityCompat.requestPermissions(DetailPesanan.this,
                                                new String[]{android.Manifest.permission.CAMERA},
                                                Fungsi.kodenya);
                                        dialogInterface.cancel();
                                        ambilFoto(kode);
                                    }
                                })
                                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, final int id) {
                                        dialog.cancel();
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        ActivityCompat.requestPermissions(DetailPesanan.this,
                                new String[]{android.Manifest.permission.CAMERA},
                                Fungsi.kodenya);
                    }
                }
            }
        } else {
            ambilFoto(kode);
        }
    }

    private void ambilFoto(int kode) {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = getOutputMediaFile(1);
            if (photoFile != null) {
                Uri photoURI = anu(photoFile);
                picUri = photoURI;
                f.dr.child("tempFotoPenerima").child(noresi).setValue(Uri.fromFile(photoFile).getPath());
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                pictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(pictureIntent, kode);
            }
        }
    }

    public Uri anu(File file) {
        Uri uri;
        uri = FileProvider.getUriForFile(DetailPesanan.this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        return uri;
    }

    private Bitmap getBitmap(String path) {
        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 50000;
            in = getContentResolver().openInputStream(uri);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;

                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                int height = b.getHeight();
                int width = b.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = "";
        if(android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.P){
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        }else{
            path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        }
        return Uri.parse(path);
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "strukshowroom_lajur");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + f.dr.push().getKey() + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 8910: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    ambilFoto(8910);
                } else {

                    Toast.makeText(DetailPesanan.this, "Anda belum mengizinkan aplikasi untuk mengambil foto / gambar pada ponsel.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Fungsi.kodenya:
                    bsbselesai(picUri);
                    break;
            }
        }
    }

    private void upload(final Uri uri, final String keterangan) {
        f.dr.child("tempFotoPenerima").child(noresi).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ambilnamafoto) {
                if (ambilnamafoto.exists()) {
                    String imagePath = ambilnamafoto.getValue(String.class);
                    try {
                        getContentResolver().notifyChange(uri, null);
                    } catch (NullPointerException e) {

                    }

                    String namaFolder = null;
                    String namakode = null;

                    namaFolder = "fotoPenerima";
                    namakode = noresi;

                    Bitmap reducedSizeBitmap = getBitmap(imagePath);
                    Uri uri2 = getImageUri(getApplicationContext(), reducedSizeBitmap);
                    StorageReference filepath = f.sr.child(namaFolder).child(namakode + "_" + f.kurir("idku"));
                    final String finalNamaFolder = namaFolder;
                    final String finalNamakode = namakode;
                    filepath.putFile(uri2).addOnSuccessListener(taskSnapshot -> {
                        pd.cancel();
                        Uri downloadURL = taskSnapshot.getDownloadUrl();
                        HashMap<String, Object> mp = new HashMap();
                        mp.put("dokumenPenerima/" + noresi+"/foto", downloadURL.toString());
                        mp.put("dokumenPenerima/" + noresi+"/keterangan", keterangan);
                        mp.put("tempFotoPenerima/"+noresi, null);

                        mp.put("transaksi/"+noresi+"/status", "Sukses");
                        mp.put("transaksi/"+noresi+"/idkurir", f.kurir("idku")+"_selesai");
                        mp.put("tracking/"+noresi+"/"+f.dr.push().getKey(), "[Kurir]Paket Telah Sampai["+keterangan+"]|"+f.tglSekarang("eropa")+"|"+f.jamSekarang());
                        f.dr.updateChildren(mp);
                        AlertDialog.Builder ab = new AlertDialog.Builder(DetailPesanan.this);
                        ab.setCancelable(false);
                        ab.setMessage("Transaksi Berhasil");
                        ab.setPositiveButton("Tutup", (dialog, which) -> {
                            dialog.cancel();
                            Intent intent = new Intent(DetailPesanan.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
                        ab.show();
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}