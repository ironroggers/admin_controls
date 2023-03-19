package com.innovapptive.admincontrols;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class CategoryActivity extends AppCompatActivity implements View.OnClickListener {
    Dialog dialog;
    EditText editName, editId;
    ImageView categoryImage;
    Button actionCancel, actionAdd;
    RecyclerView recyclerView;
    ArrayList<Category> categoryArrayList;
    CategoryAdapter categoryAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Bitmap bitmap;
    Uri filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        dialog = new Dialog(this);
        actionAdd = dialog.findViewById(R.id.action_add);
        actionCancel = dialog.findViewById(R.id.action_cancel);

        recyclerView = findViewById(R.id.recycler_view);
        categoryArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryAdapter = new CategoryAdapter(this, categoryArrayList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("categories2");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Category category = dataSnapshot.getValue(Category.class);
                    categoryArrayList.add(category);
                    recyclerView.setAdapter(categoryAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:

                dialog.setContentView(R.layout.category_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
                categoryImage = dialog.findViewById(R.id.category_image);
                actionAdd = dialog.findViewById(R.id.action_add);
                actionCancel = dialog.findViewById(R.id.action_cancel);
                actionCancel.setOnClickListener(this);
                actionAdd.setOnClickListener(this);
                categoryImage.setOnClickListener(this);
                break;

        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onClick(View v) {
        editName = dialog.findViewById(R.id.et_category_name);

        editId = dialog.findViewById(R.id.et_category_id);
        switch (v.getId()) {
            case R.id.action_add:
                String nameVal = editName.getText().toString();
                if (TextUtils.isEmpty(nameVal)) {
                    Toast.makeText(CategoryActivity.this, "Insert Data", Toast.LENGTH_SHORT).show();
                } else {
//                    addDataToFirebase(nameVal);
                    uploadtofirebase();
                }
                dialog.dismiss();
                Intent refresh = new Intent(CategoryActivity.this, CategoryActivity.class);
                startActivity(refresh);
                break;
            case R.id.action_cancel:
                dialog.dismiss();
                break;
            case R.id.category_image:
                Dexter.withActivity(CategoryActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Image File"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
                break;
        }

    }


//    int i=-1;
//    Map<String,Object> map = new HashMap<>();
//    private void addDataToFirebase(String nameVal) {
//        do{
//            i++;
//        }while (map.containsKey(String.valueOf(i)));
//        map.put(String.valueOf(i),nameVal);
//        databaseReference.child("Name").setValue(map);
//        Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();
//    }
    private void uploadtofirebase()
    {
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setTitle("File Uploader");
        dialog.show();

        FirebaseStorage storage=FirebaseStorage.getInstance();
        final StorageReference uploader=storage.getReference("Image1"+editId.getText().toString());

        uploader.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        uploader.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri){

                                dialog.dismiss();
                                FirebaseDatabase db=FirebaseDatabase.getInstance();
                                DatabaseReference root=db.getReference("categories2");

//                                dataholder obj=new dataholder(name.getText().toString(),contact.getText().toString(),course.getText().toString(),uri.toString());
//                                root.child(roll.getText().toString()).setValue(obj);
                                Category cat = new Category(editName.getText().toString(),uri.toString(),editId.getText().toString());
                                root.child(editId.getText().toString()).setValue(cat);
                                editName.setText("");
                                editId.setText("");
                                categoryImage.setImageResource(R.drawable.ic_launcher_background);
                                Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        float percent=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        dialog.setMessage("Uploaded :"+(int)percent+" %");
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode==1  && resultCode==RESULT_OK)
        {
            filepath=data.getData();
            try{
                InputStream inputStream=getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                categoryImage.setImageBitmap(bitmap);
            }catch (Exception ex)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}