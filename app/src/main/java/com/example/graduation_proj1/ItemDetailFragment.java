package com.example.graduation_proj1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.graduation_proj1.models.RecyclerViewItem;
import com.squareup.picasso.Picasso;

public class ItemDetailFragment extends Fragment {
    public static ItemDetailFragment newInstance(RecyclerViewItem item) {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        // 아이템의 정보를 가져옵니다.
        RecyclerViewItem item = getArguments().getParcelable("item");
        Log.d("Title: ", item.getTitle());
        Log.d("ItemType: ", item.getItemType());
        //Log.d("Location: ", item.getLocation());


        // UI에 아이템 정보를 설정합니다.

        TextView itemType_layout = rootView.findViewById(R.id.item_type_layout);
        TextView itemOwner_layout = rootView.findViewById(R.id.item_owner_layout);
        TextView itemPlace_layout = rootView.findViewById(R.id.item_place_layout);
        TextView itemContact_layout = rootView.findViewById(R.id.item_contact_layout);
        TextView itemDate_layout = rootView.findViewById(R.id.item_date_layout);
        TextView itemPlace2_layout = rootView.findViewById(R.id.item_place2_layout);

        ImageView itemImage = rootView.findViewById(R.id.item_detail_image);
        TextView itemTitle = rootView.findViewById(R.id.item_title);
        TextView itemType = rootView.findViewById(R.id.item_type);
        TextView itemOwner = rootView.findViewById(R.id.item_owner);
        TextView itemPlace = rootView.findViewById(R.id.item_place);
        TextView itemContact = rootView.findViewById(R.id.item_contact);
        TextView itemDate = rootView.findViewById(R.id.item_date);
        TextView itemPlace2 = rootView.findViewById(R.id.item_place2);


        Picasso.with(getActivity())
                .load(item.getImageUrl())
                .into(itemImage);

        itemType_layout.setText("물건종류");
        itemOwner_layout.setText("분실자명 (선택)");
        itemPlace_layout.setText("보관 장소");
        itemContact_layout.setText("연락처");
        itemDate_layout.setText("습득 일자");
        itemPlace2_layout.setText("습득 장소");

        itemTitle.setText(item.getTitle());
        itemType.setText(item.getItemType());
        itemOwner.setText(item.getOwner());
        itemPlace.setText(item.getLocation());
        itemContact.setText(item.getContact());
        itemDate.setText(item.getFoundDate());
        itemPlace2.setText(item.getFoundLocation());

        return rootView;
    }
}

