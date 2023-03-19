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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Map;
import java.util.Random;

public class ProductActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Dialog dialog;
    EditText editTitle, editMrp, editSp, editId;
    ImageView productImage;
    Spinner editCategorySpinner;
    Button actionCancel, actionAdd;
    RecyclerView recyclerView;
    ArrayAdapter ad;
    String editCategory;
    ArrayList<Product> productArrayList;
    ProductAdapter productAdapter;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,categoryReferance;
    ArrayList<String> categories;
    Uri filepath;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        dialog = new Dialog(this);

        actionAdd = dialog.findViewById(R.id.action_add);
        actionCancel = dialog.findViewById(R.id.action_cancel);
        recyclerView = findViewById(R.id.recycler_view);
        productArrayList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productArrayList);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("products2");
        categoryReferance = firebaseDatabase.getReference("categories2");
        categories = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    productArrayList.add(product);
                    recyclerView.setAdapter(productAdapter);
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
                dialog.setContentView(R.layout.product_dialog);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();
                editId = dialog.findViewById(R.id.et_id);
                productImage = dialog.findViewById(R.id.product_image);
                editCategorySpinner = dialog.findViewById(R.id.et_category);
                categoryReferance.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Category cat = dataSnapshot.getValue(Category.class);
                            categories.add(cat.getName());
                            editCategorySpinner.setAdapter(ad);
                            ad.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                editCategorySpinner.setOnItemSelectedListener(this);
                ad  = new ArrayAdapter(this,android.R.layout.simple_spinner_item,categories);
                ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                actionAdd = dialog.findViewById(R.id.action_add);
                actionCancel = dialog.findViewById(R.id.action_cancel);
                actionCancel.setOnClickListener(this);
                actionAdd.setOnClickListener(this);
                productImage.setOnClickListener(this);
                break;

        }
        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onClick(View v) {
        editTitle = dialog.findViewById(R.id.et_title);
        editCategorySpinner = dialog.findViewById(R.id.et_category);
        editMrp = dialog.findViewById(R.id.et_mrp);
        editSp = dialog.findViewById(R.id.et_sp);
        switch (v.getId()){
            case R.id.action_add:
                String titleVal = editTitle.getText().toString();
                String categoryVal = editCategory;
                String mrpVal = editMrp.getText().toString();
                String spVal = editSp.getText().toString();

                if(TextUtils.isEmpty(titleVal) && TextUtils.isEmpty(mrpVal) && TextUtils.isEmpty(spVal)){
                    Toast.makeText(ProductActivity.this, "Insert Data", Toast.LENGTH_SHORT).show();
                }else{
                    uploadtofirebase();
//                    addDataToFirebase(titleVal,categoryVal,mrpVal,spVal);

                }
                dialog.dismiss();
                Intent refresh = new Intent(ProductActivity.this, ProductActivity.class);
                startActivity(refresh);
                break;
            case R.id.action_cancel:
                dialog.dismiss();
                break;
            case R.id.product_image:
                Dexter.withActivity(ProductActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response)
                            {
                                Intent intent=new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent,"Select Image File"),1);
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        editCategory = categories.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        editCategory = "No Category Selected";
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                productImage.setImageBitmap(bitmap);
            }catch (Exception ex)
            {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

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
                                DatabaseReference root=db.getReference("products2");

//                                dataholder obj=new dataholder(name.getText().toString(),contact.getText().toString(),course.getText().toString(),uri.toString());
//                                root.child(roll.getText().toString()).setValue(obj);
                                Product product = new Product(editTitle.getText().toString(),editCategory,editMrp.getText().toString(),editSp.getText().toString(),editId.getText().toString(),uri.toString());
                                root.child(editId.getText().toString()).setValue(product);
                                editTitle.setText("");editCategory="";editId.setText("");editMrp.setText("");editSp.setText("");
                                productImage.setImageResource(R.drawable.ic_launcher_background);
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
}