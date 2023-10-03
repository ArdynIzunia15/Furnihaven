package com.youkea.furnihaven;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    ImageView btnView3D;
    TextView txtFurnitureName, txtFurnitureDescription;
    ImageView imageView2D;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusBarColor(R.color.background, true);

        btnView3D = findViewById(R.id.btnView3D);
        txtFurnitureDescription = findViewById(R.id.txtFurnitureDescription);
        txtFurnitureName = findViewById(R.id.txtFurnitureName);
        imageView2D = findViewById(R.id.imageView2D);

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
                    txtFurnitureName.setText(task.getResult().get("name").toString());
                    txtFurnitureDescription.setText(task.getResult().get("description").toString());
                    task.getResult().get("price");
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