package programfirebase.comdasd.example.javad.progland;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by javad on 28.02.2018.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context mContext;

    public MyAdapter(List<ListItem> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
    }
    // public MyAdapter(List<ListItem> listItems, Context context) {
     //   this.listItems = listItems;
       // this.mContext = context;
    //}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final   ListItem listItem = listItems.get(position);


        holder.textViewHead.setText(listItem.getHead());
        holder.textViewDesc.setText(listItem.getDesc());
        holder.textViewInf.setText(listItem.getInformation());



        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullInf.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Head",  listItem.getHead());
                intent.putExtra("Desc",  listItem.getDesc());
                intent.putExtra("Inf",   listItem.getInformation());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewHead;
        public TextView textViewDesc;
        public TextView textViewInf;
        public LinearLayout linearLayout;



        public ViewHolder(View itemView) {
            super(itemView);

            textViewHead= (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc= (TextView) itemView.findViewById(R.id.textViewDesc);
            textViewInf = (TextView) itemView.findViewById(R.id.textViewInf);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.linerLayout);
        }
    }
}
