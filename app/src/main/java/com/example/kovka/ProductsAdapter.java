package com.example.kovka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>

{
    private final OnStateClickListener onClickListener;



    interface OnStateClickListener{
        void onStateClick(Product state, int position);
    }

    private Context mCtx;
    private List<Product> productList;

    public ProductsAdapter(Context mCtx, List < Product > productList, OnStateClickListener onClickListener) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.onClickListener = onClickListener;
    }

    @Override
    public ProductViewHolder onCreateViewHolder (ViewGroup parent,int viewType){
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.product_list, null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ProductViewHolder holder, @SuppressLint("RecyclerView") int position){
        Product product = productList.get(position);

        //загрузка изображения
        Glide.with(mCtx)
                .load(product.getImage())
                .into(holder.imageView);

        holder.name.setText(product.getTags());
        holder.fail.setText(product.getPath());
        holder.id.setText(product.getId());

        // обработка нажатия
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onStateClick(product, position);
            }
        });
    }

    @Override
    public int getItemCount () {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView name, fail, id;
        ImageView imageView;

        public ProductViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            fail = itemView.findViewById(R.id.fail);
            id = itemView.findViewById(R.id.id);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}

