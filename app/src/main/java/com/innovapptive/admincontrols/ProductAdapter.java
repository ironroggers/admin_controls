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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder>{
    Context c;
    ArrayList<Product> productArrayList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public ProductAdapter(Context c, ArrayList<Product> productArrayList) {
        this.c = c;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(c).inflate(R.layout.product_list_item,parent,false);
        return new ProductAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Product product = productArrayList.get(position);
        holder.productTitle.setText(product.getProductTitle());
        holder.mrp.setText("₹"+product.getMrp());
        holder.sp.setText("₹"+product.getSp());
        holder.category.setText(product.getCategory());
        holder.id.setText(product.getId());
        Glide.with(c).load(product.getUrl()).into(holder.productListImage);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setMessage("Are you sure you want delete the item?");
                builder.setTitle("Alert !");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference("products2");
                    Toast.makeText(c, productArrayList.get(position).getProductTitle()+" removed", Toast.LENGTH_SHORT).show();
                    databaseReference.child(productArrayList.get(position).getId()).removeValue();
                    productArrayList.remove(position);
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
        return productArrayList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView productTitle, mrp,sp,category,id;
        ImageButton delete;
        ImageView productListImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.tv_product_title);
            mrp = itemView.findViewById(R.id.price_mrp);
            sp = itemView.findViewById(R.id.price_sp);
            category = itemView.findViewById(R.id.tv_category);
            delete = itemView.findViewById(R.id.delete);
            id = itemView.findViewById(R.id.tv_product_id);
            productListImage = itemView.findViewById(R.id.product_list_image);

        }
    }
}
