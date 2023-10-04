package com.youkea.furnihaven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FurnitureDetailActivity extends AppCompatActivity {
    Button btnColor0, btnColor1, btnColor2, btnColor3;
    ImageView btnView3D;
    TextView txtFurnitureName, txtFurnitureDescription, txtFurniturePrice;
    ImageView imageView2D;
    int selectedColor = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_furnituredetail);
        statusBarColor(R.color.background, true);

        btnView3D = findViewById(R.id.btnView3D);
        txtFurnitureDescription = findViewById(R.id.txtFurnitureDescription);
        txtFurnitureName = findViewById(R.id.txtFurnitureName);
        txtFurniturePrice = findViewById(R.id.txtFurniturePrice);
        imageView2D = findViewById(R.id.imageView2D);
        btnColor0 = findViewById(R.id.btnColor0);
        btnColor1 = findViewById(R.id.btnColor1);
        btnColor2 = findViewById(R.id.btnColor2);
        btnColor3 = findViewById(R.id.btnColor3);

        // Intent Data Retrieval
        String furnitureId = "CH001";
        String furnitureType = furnitureId.substring(0, 2);
        String furnitureNumber = furnitureId.substring(2, 5);

        // Firebase Things
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        FirebaseFirestore firestoreReference = FirebaseFirestore.getInstance();

        // Get Furniture Data
        firestoreReference.collection("furniture").document(furnitureId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    // Get Furniture Name
                    txtFurnitureName.setText(task.getResult().get("name").toString());
                    // Get Furniture Description
                    txtFurnitureDescription.setText(task.getResult().get("description").toString());
                    // Get Furniture Price
                    txtFurniturePrice.setText(task.getResult().get("price").toString() + " IDR");
                    // Get Furniture Color Option
                    ArrayList<String> arrColor = (ArrayList<String>) task.getResult().get("color");
                    setColorPallete(arrColor, btnColor0, btnColor1, btnColor2, btnColor3);
                }
            }
        });
        storageReference.child("furniture/" + furnitureType + "/" + furnitureNumber + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext())
                        .load(uri.toString())
                        .into(imageView2D);
            }
        });


        btnView3D.setOnClickListener(v -> {
            storageReference.child("furniture/CH/005.gltf").getDownloadUrl().addOnSuccessListener(uri -> {
                viewModel(uri.toString());
            });
        });
    }

    private void setColorPallete(ArrayList<String> arrColor, Button btnColor0, Button btnColor1, Button btnColor2, Button btnColor3){
        if(arrColor.size() > 0){
            btnColor0.setVisibility(View.VISIBLE);
            btnColor0.setBackgroundColor(Color.parseColor(arrColor.get(0).toString()));
            btnColor0.setOnClickListener(v -> {
                // Ganti image jadi warna 0
                Toast.makeText(this, "Warna 0", Toast.LENGTH_SHORT).show();
                selectedColor = 0;
            });
            if(arrColor.size() > 1){
                btnColor1.setVisibility(View.VISIBLE);
                btnColor1.setBackgroundColor(Color.parseColor(arrColor.get(1).toString()));
                btnColor1.setOnClickListener(v -> {
                    // Ganti image jadi warna 1
                    Toast.makeText(this, "Warna 1", Toast.LENGTH_SHORT).show();
                    selectedColor = 1;
                });
                if(arrColor.size() > 2){
                    btnColor2.setVisibility(View.VISIBLE);
                    btnColor2.setBackgroundColor(Color.parseColor(arrColor.get(2).toString()));
                    btnColor2.setOnClickListener(v -> {
                        // Ganti image jadi warna 2
                        Toast.makeText(this, "Warna 2", Toast.LENGTH_SHORT).show();
                        selectedColor = 2;
                    });
                    if(arrColor.size() > 3){
                        btnColor3.setVisibility(View.VISIBLE);
                        btnColor3.setBackgroundColor(Color.parseColor(arrColor.get(3).toString()));
                        btnColor3.setOnClickListener(v -> {
                            // Ganti image jadi warna 3
                            Toast.makeText(this, "Warna 3", Toast.LENGTH_SHORT).show();
                            selectedColor = 3;
                        });
                    }
                }
            }
        }
    }

    private void statusBarColor(int colorResource, boolean isIconDark){
        // Status Bar Customization
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(colorResource));
        }
        if(isIconDark){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }
    private void viewModel(String uri){
        Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
        Uri intentUri =
                Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                        .appendQueryParameter("file", uri)
                        .appendQueryParameter("mode", "3d_preferred")
                        .build();
        sceneViewerIntent.setData(intentUri);
        sceneViewerIntent.setPackage("com.google.ar.core");
        startActivity(sceneViewerIntent);
    }
}