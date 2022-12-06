package com.geostat.census_2024.ui.addressing.adapter;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.geostat.census_2024.R;
import com.geostat.census_2024.data.local.realtions.AddressingWithHolders;
import com.geostat.census_2024.architecture.inter.handler.IndexAdapterItemClickHandlers;
import com.geostat.census_2024.architecture.inter.handler.IndexAdapterItemClickHandlersWithMap;
import com.geostat.census_2024.utility.InterDate;

import java.util.List;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class AddressingAdapter extends RecyclerView.Adapter<AddressingAdapter.AddressingHolder> {

    private static String caller;
    private static Integer houseStatus;
    private final List<AddressingWithHolders> response;
    private final IndexAdapterItemClickHandlers addressingItemClickHandler;

    public AddressingAdapter(List<AddressingWithHolders> response, IndexAdapterItemClickHandlers addressingItemClickHandler, String caller, Integer houseStatus) {
        AddressingAdapter.caller = caller;
        this.response = response;
        AddressingAdapter.houseStatus = houseStatus;
        this.addressingItemClickHandler = addressingItemClickHandler;
    }

    public List<AddressingWithHolders> getResponse() {
        return response;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void start(List<AddressingWithHolders> response) {
        this.response.clear();
        this.response.addAll(response);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void remove(int index) {
        this.response.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, response.size());

    }

    public void updateItem(int index, AddressingWithHolders updated) {
        this.response.set(index, updated);
        notifyItemChanged(index);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void add(AddressingWithHolders addressingHolder) {
        if (response.parallelStream().noneMatch(addressingWithHolders -> addressingWithHolders.inquireV1Entity.getUuid().equals(addressingHolder.inquireV1Entity.getUuid()))){
            this.response.add(0, addressingHolder);
            notifyItemInserted(0);
        }
        notifyItemRangeChanged(0, response.size());
    }

    @NonNull
    @Override
    public AddressingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addressing_item_layout, parent, false);
        return new AddressingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressingHolder holder, int index) {
        AddressingHolder.bind(holder, this.response.get(index), index, addressingItemClickHandler);
    }

    @Override
    public int getItemCount() {
        return this.response.size();
    }

    public static class AddressingHolder extends RecyclerView.ViewHolder {

        View binding;

        public AddressingHolder(@NonNull View itemView) {
            super(itemView);
            binding = itemView;
        }

        @SuppressLint("SetTextI18n")
        public static void bind(AddressingHolder holder, AddressingWithHolders addressing, int index, IndexAdapterItemClickHandlers addressingItemClickHandler) {
            TextView textView = holder.binding.findViewById(R.id.id);
            textView.setText(addressing.inquireV1Entity.getIndex().toString());

            ImageView statusBtn = holder.binding.findViewById(R.id.status_icon);
            if (addressing.inquireV1Entity.getStatus().equals(1)) {
                statusBtn.setImageResource(R.drawable.ic_baseline_check_circle_24_3);
            } else if (addressing.inquireV1Entity.getStatus().equals(2)) {
                statusBtn.setImageResource(R.drawable.ic_baseline_check_circle_24);
            }

            TextView nameView = holder.binding.findViewById(R.id.name);
            if (addressing.inquireV1HolderEntity.size() > 0) {
                String firstName = addressing.inquireV1HolderEntity.get(0).getFirstName();
                String lastName =  addressing.inquireV1HolderEntity.get(0).getLastName();

                if (firstName != null && lastName != null) {
                    String nameText = firstName + " " + lastName;
                    nameView.setText(nameText);
                }
            } else {
                nameView.setVisibility(View.GONE);
            }

            TextView dateView = holder.binding.findViewById(R.id.date);
            TextView timeView = holder.binding.findViewById(R.id.time);

            String[] dateTime = InterDate.lDate(addressing.inquireV1Entity.getCreatedAt());
            dateView.setText(dateTime[0]);
            timeView.setText(dateTime[1]);

            TextView buildingTypeView = holder.binding.findViewById(R.id.building_type);
            String buildingTypeText = addressing.buildingTypeEntity.getName();
            buildingTypeView.setText(buildingTypeText);


            Button button = holder.binding.findViewById(R.id.button);

            if (caller.equals("addressing")) {
                button.setOnClickListener(view -> addressingItemClickHandler.btnClickListener(addressing.inquireV1Entity.getId(), holder.getLayoutPosition()));
            } else if (caller.equals("rollback")) {
                button.setOnClickListener(view -> ((IndexAdapterItemClickHandlersWithMap) addressingItemClickHandler).btnClickListener(addressing.inquireV1Entity.getId(), holder.getLayoutPosition(), addressing.inquireV1Entity.getHouseCode()));
            }

            ImageView remove = holder.binding.findViewById(R.id.remove);
            if (houseStatus.equals(2) || addressing.inquireV1Entity.getStatus().equals(2)) {
                remove.setEnabled(false);
                remove.setImageResource(R.drawable.ic_baseline_delete_24_alter);
            } else if (caller.equals("addressing")) {
                remove.setOnClickListener(view -> addressingItemClickHandler.removeClickListener(addressing.inquireV1Entity.getId(), index));
            } else if (caller.equals("rollback")) {
                remove.setVisibility(View.GONE);
            }


            ImageView email = holder.binding.findViewById(R.id.email);
            if (caller.equals("rollback") && addressing.inquireV1Entity.getRollbackComment() != null && !addressing.inquireV1Entity.getRollbackComment().isEmpty()) {
                email.setOnClickListener(view ->
                        new SimpleTooltip.Builder(button.getContext()).anchorView(view).text(addressing.inquireV1Entity.getRollbackComment()).gravity(Gravity.TOP).build().show());
            } else if(caller.equals("addressing") && addressing.inquireV1Entity.getComment() != null && !addressing.inquireV1Entity.getComment().isEmpty()) {
                email.setOnClickListener(view ->
                        new SimpleTooltip.Builder(button.getContext()).anchorView(view).text(addressing.inquireV1Entity.getComment()).gravity(Gravity.TOP).build().show());
            } else {
                email.setImageResource(R.drawable.ic_baseline_email_24_alt);
            }


            ImageView btnMap = holder.binding.findViewById(R.id.btn_map);
            if (caller.equals("addressing")) {
                btnMap.setImageResource(R.drawable.ic_baseline_add_location_24_alt);;
            } else if (caller.equals("rollback")) {
                btnMap.setOnClickListener(view -> ((IndexAdapterItemClickHandlersWithMap) addressingItemClickHandler).mapClickListener(addressing.inquireV1Entity.getHouseCode()));
            }
        }
    }
}
