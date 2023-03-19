package com.innovapptive.admincontrols;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder>{
    Context c;
    ArrayList<Category> categoryArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public CategoryAdapter(Context c, ArrayList<Category> categoryArrayList) {
        this.c = c;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.category_list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categoryArrayList.get(position);
        holder.name.setText(category.getName());
        Glide.with(c).load(category.getUrl()).into(holder.categoryListImage);
        holder.deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage("Are you sure you want delete the item?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("categories2");
                    Toast.makeText(c, categoryArrayList.get(position).getName()+" removed", Toast.LENGTH_SHORT).show();
                    databaseReference.child(categoryArrayList.get(position).getId()).removeValue();
                    categoryArrayList.remove(position);
                    notifyDataSetChanged();

                    dialog.cancel();
                });
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView categoryListImage;
        ImageButton deleteCategory;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            categoryListImage = itemView.findViewById(R.id.category_list_image);
            deleteCategory = itemView.findViewById(R.id.delete_category);
        }
    }
}
