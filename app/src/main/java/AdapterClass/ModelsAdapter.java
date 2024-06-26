package AdapterClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ziac.wheelzcustomer.R;

import java.util.List;

import Fragments.VehicleDetailFragment;
import ModelClasses.Global;
import ModelClasses.CommonClass;

public class ModelsAdapter extends RecyclerView.Adapter<ModelsAdapter.ViewHolder> {

    private final List<CommonClass> commonClassList;
    private final Context context;
    public ModelsAdapter(List<CommonClass> commonClassList, Context context) {
        this.context = context;
        this.commonClassList = commonClassList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.models_layout, parent, false);

        ModelsAdapter.ViewHolder viewHolder = new ModelsAdapter.ViewHolder(view);
        return viewHolder;

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Global.loadWithPicasso(context, holder.Veh_image, Global.modelsimageurl + commonClassList.get(position).getImage_path());

        holder.Manufacturer.setText(commonClassList.get(position).getManufacture());

        String ccString = commonClassList.get(position).getCc();
        float cc = Float.parseFloat(ccString);
        int ccInteger = (int) cc;

        holder.Model_name.setText(commonClassList.get(position).getModel_name() + " - " + ccInteger);
        holder.Modelscardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonClass svel = new CommonClass();

                svel.setImage_path(Global.allleadslist.get(position).getImage_path());
                svel.setCategory(Global.allleadslist.get(position).getCategory());
                svel.setManufacture(Global.allleadslist.get(position).getManufacture());
                svel.setModel_name(Global.allleadslist.get(position).getModel_name());
                svel.setCc(Global.allleadslist.get(position).getCc());
                svel.setBhp(Global.allleadslist.get(position).getBhp());
                svel.setTopspeed(Global.allleadslist.get(position).getTopspeed());
                svel.setBodytype(Global.allleadslist.get(position).getBodytype());
                svel.setFuelname(Global.allleadslist.get(position).getFuelname());
                svel.setModel_name(Global.allleadslist.get(position).getModel_name());
                svel.setChargingtime(Global.allleadslist.get(position).getChargingtime());
                svel.setSaleprice(Global.allleadslist.get(position).getSaleprice());

                Global.modellist = svel;

                VehicleDetailFragment vehicleDetailFragment = new VehicleDetailFragment();
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, vehicleDetailFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return commonClassList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Veh_image;
        TextView Manufacturer,Model_name,Category,CC,BHP;
        CardView Modelscardview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Veh_image=itemView.findViewById(R.id.veh_image);
            Manufacturer=itemView.findViewById(R.id.manufacture_name);
            Model_name=itemView.findViewById(R.id.model_name);
            Modelscardview=itemView.findViewById(R.id.modelscardview);


            // CC=itemView.findViewById(R.id.veh_cc);

          /*  Category=itemView.findViewById(R.id.veh_category);
            BHP=itemView.findViewById(R.id.veh_bhp);*/
        }
    }

}
