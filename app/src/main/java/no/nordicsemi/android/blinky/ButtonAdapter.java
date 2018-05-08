package no.nordicsemi.android.blinky;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import no.nordicsemi.android.blinky.database.CorButton;


import java.util.List;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButViewHolder>{

    private List<CorButton> corButtonList;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private int prefNumOfButs;

    Context context;

    public ButtonAdapter(List<CorButton> corButtonList, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.corButtonList = corButtonList;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
   }

    @NonNull
    @Override
    public ButViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.but_item, parent, false);
        return new ButViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ButViewHolder holder, final int position) {

        CorButton corButton = corButtonList.get(position);

        holder.mButText.setText(corButton.getButNum());
        holder.itemView.setTag(corButton);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnLongClickListener(onLongClickListener);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Toast.makeText(context, "Кнопка " + dataSet[position], Toast.LENGTH_SHORT).show();
////                Log.d("myLogs", "Название " + corButtonList.get(position).getButNum());
////                Log.d("myLogs", "Направление " + corButtonList.get(position).getCorDir());
////                Log.d("myLogs", "Значение " + corButtonList.get(position).getCorValue());
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return corButtonList.size();
    }


    class ButViewHolder extends RecyclerView.ViewHolder {
        TextView mButText;

        ButViewHolder(View itemView) {
            super(itemView);
            mButText = itemView.findViewById(R.id.tv_but);
        }
    }

    public void additems(List<CorButton> corButtonList){
        this.corButtonList = corButtonList;
        notifyDataSetChanged();
    }


}
