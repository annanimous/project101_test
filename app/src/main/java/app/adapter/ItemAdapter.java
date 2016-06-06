package app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.githubdemo.app.R;

import java.util.List;

import app.model.Item;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.login.setText(item.getLogin());
        holder.http_url.setText(item.getHtmlUrl());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void clear() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView login, http_url;

        public MyViewHolder(View view) {
            super(view);
            login = (TextView) view.findViewById(R.id.login);
            http_url = (TextView) view.findViewById(R.id.http_url);
        }
    }
}