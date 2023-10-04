package com.example.graduation_proj1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graduation_proj1.models.RecyclerViewItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LostItemAdapter extends RecyclerView.Adapter<LostItemAdapter.ViewHolder> {
    private List<RecyclerViewItem> mItemList=null;
    private Context mContext; // Context 추가

    private OnItemClickListener listener;

    public LostItemAdapter(Context context, List<RecyclerViewItem> itemList) {
        mItemList = itemList;
        mContext = context; // Context 설정
    }


    public interface OnItemClickListener {
        void onItemClick(RecyclerViewItem item);
    }

    public LostItemAdapter(Context context, List<RecyclerViewItem> itemList, OnItemClickListener listener) {
        mItemList = itemList;
        mContext = context; // Context 설정
        this.listener = listener;
    }


    // onCreateViewHolder, onBindViewHolder, getItemCount 메서드 추가

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        LostItemAdapter.ViewHolder vh = new LostItemAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerViewItem item = mItemList.get(position);

        //holder.imageView.setBackground(item.getImageUrl());
        holder.mainText.setText(item.getTitle());
        holder.subText.setText(item.getItemType());

        // 이미지 URL을 가져와서 Picasso를 사용하여 ImageView에 이미지 설정
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(mContext)
                    .load(imageUrl)
                    .into(holder.imageView);
        }

        // 아이템을 클릭했을 때 리스너 호출
        holder.itemView.setOnClickListener(view -> {
            // 새로운 Fragment로 이동
            ((MainMenuActivity)mContext).replaceFragment(ItemDetailFragment.newInstance(item));
        });
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void setItems(List<RecyclerViewItem> items) {
        mItemList = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder 내부의 UI 요소 선언 및 초기화
        ImageView imageView;
        TextView mainText;
        TextView subText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // UI 요소 초기화
            imageView = itemView.findViewById(R.id.item_image);
            mainText = itemView.findViewById(R.id.icon_main_text);
            subText = itemView.findViewById(R.id.icon_sub_text);
        }
    }
}