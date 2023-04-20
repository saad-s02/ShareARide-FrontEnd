package com.example.sharearide.adapter;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sharearide.R;
import com.example.sharearide.RequestActivity;
import com.google.android.libraries.places.api.model.AutocompletePrediction;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {
    List<AutocompletePrediction> list;
    private LayoutInflater mInflater;
    private EditText mEditText;
    public String startId;
    public String endId;
    public ArrayList<String> aList;

    public RecyclerviewAdapter(Context context, EditText editText, List<AutocompletePrediction> list, ArrayList<String> aList) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        mEditText = editText;
        this.aList = aList;
    }

    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
        AutocompletePrediction item = list.get(position);
        holder.textView.setText(item.getFullText(null));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText(item.getPrimaryText(null));
                Log.d(TAG, item.getPlaceId());
                aList.add(item.getPlaceId());
                Log.d(TAG, aList.toString());
            }
        });
    }

    public ArrayList<String> getList(){
        return aList;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview);
        }
    }
}
