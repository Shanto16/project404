package mcgyvers.mobitrip.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import mcgyvers.mobitrip.R;
import mcgyvers.mobitrip.dataModels.Member;

/**
 * Created by edson on 14/09/17.
 * adapter for adding members in proper pages
 *
 */

public class MemberData extends RecyclerView.Adapter<MemberData.MyViewHolder> {

    private ArrayList<Member> members;
    private Context context;
    private final onItemClickListener clickListener;

    /**
     * @param members list of member objects
     * @param context application context
     */
    public MemberData(ArrayList<Member> members, Context context, onItemClickListener listener) {
        this.members = members;
        this.context = context;
        this.clickListener = listener;
        System.out.println("Members adapter");

//        System.out.println("first member is : " + members.get(0).getName());
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.members_model, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Integer pos = position;
        final Member member = members.get(pos);
        System.out.println("setting up members adapter");
        holder.memberName.setText(member.getName());
        holder.memberAmount.setText(member.getAmount());
        holder.memberPhone.setText(member.getPhone());

        holder.deleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.callback(pos);
            }
        });


    }

    @Override
    public int getItemCount() {
        return members.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        MaterialEditText memberName;
        MaterialEditText memberAmount;
        MaterialEditText memberPhone;
        ImageView deleteMember;
        public MyViewHolder(View view) {
            super(view);

            memberName = (MaterialEditText) view.findViewById(R.id.model_member_name);
            memberAmount = (MaterialEditText) view.findViewById(R.id.model_member_amount);
            memberPhone = (MaterialEditText) view.findViewById(R.id.model_member_phone);
            deleteMember = (ImageView) view.findViewById(R.id.delete_model);


        }
    }

    public static interface onItemClickListener{

        public void callback(int pos);
    }
}
