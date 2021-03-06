package com.example.mourad.navigationandroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class WaysAdapter extends RecyclerView.Adapter<WaysAdapter.ProductViewHolder> {

    private List<Rider_Ways> list;
    private Context context;
    //getting the context and product list with constructor
    WaysAdapter(List<Rider_Ways> list, Context context) {
        this.list=list;
        this.context=context;
        setHasStableIds(true);
    }



        @NonNull
        @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_ways, parent,false);
        return new WaysAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
       //  Rider_Ways mylist = list.get(position);
        //binding the data with the viewholder views

        final int poss=holder.getAdapterPosition();
        final Rider_Ways rd=list.get(position);
        isFavorite(holder.getAdapterPosition(),holder);
        holder.tvFullName.setText(list.get(position).getFull_Name());
        holder.tvSource.setText(list.get(position).getSource());
        holder.tvDestination.setText(list.get(position).getDestination());
        holder.tvDate.setText(list.get(position).getDate());
        holder.tvTime.setText(list.get(position).getTime());
        holder.tvPhone.setText(list.get(position).getPhone());
        holder.tvCarId.setText(list.get(position).getCarId());
        holder.UID=list.get(position).getUID();
       holder.favoriteButton.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
            @Override
            public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                if (favorite){
                    addFavorite(rd);
                }else {
                    removeFavorite(rd);

                }

            }
        });

        holder.imageWtsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("smsto:" + list.get(poss).getPhone());
                Intent i = new Intent(Intent.ACTION_SENDTO,uri);
                i.setPackage("com.whatsapp");
                context.startActivity(i);
            }
        });
        holder.imagePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+list.get(poss).getPhone()));
                context.startActivity(intent);
            }
        });

        holder.imageShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody="Hey check out our app at: https://play.google.com/store/apps/details?id=com.google.android.apps.plus";
                myIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                context.startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });

        if (list.get(position).getUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.imageDelete.setVisibility(View.VISIBLE);
            holder.favoriteButton.setVisibility(View.GONE);
        }else {
            holder.imageDelete.setVisibility(View.GONE);
        }
        final Rider_Ways rider=list.get(poss);
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert_delete(rider);
            }
        });


        Glide.with(context)
           .load(list.get(position).getImage_ways())
           .into(holder.imageProfile);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageWtsp,imageShare,imagePhone,imageDelete;
        TextView tvFullName, tvSource, tvDestination, tvDate, tvTime,tvPhone, tvCarId;
        MaterialFavoriteButton favoriteButton;
        String UID;
        CircleImageView imageProfile;

        ProductViewHolder(final View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPhone =itemView.findViewById(R.id.tvPhone);
            tvCarId = itemView.findViewById(R.id.tvCarId);
            favoriteButton=itemView.findViewById(R.id.fav);
            imageWtsp=itemView.findViewById(R.id.wtsp);
            imageShare = itemView.findViewById(R.id.share);
            imagePhone = itemView.findViewById(R.id.imgPhone);
            imageDelete = itemView.findViewById(R.id.imageDelete);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("NewApi")
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context,OffreInformation.class);
            intent.putExtra("image",list.get(getLayoutPosition()).getImage_ways());
            intent.putExtra("full Name",list.get(getLayoutPosition()).getFull_Name());
            intent.putExtra("source",list.get(getLayoutPosition()).getSource());
            intent.putExtra("destination",list.get(getLayoutPosition()).getDestination());
            intent.putExtra("date",list.get(getLayoutPosition()).getDate());
            intent.putExtra("time",list.get(getLayoutPosition()).getTime());
            intent.putExtra("phone",list.get(getLayoutPosition()).getPhone());
            intent.putExtra("carid",list.get(getLayoutPosition()).getCarId());
            intent.putExtra("LatLngSrc",list.get(getLayoutPosition()).getLatLngSrc());
            intent.putExtra("LatLngDes",list.get(getLayoutPosition()).getLatLngDes());
            intent.putExtra("UID",UID);

            /*Pair[] pairs = new Pair[8];
            pairs[0]=new Pair<View,String>(imageProfile,"imageTransition");
            pairs[1]=new Pair<View,String>(tvFullName,"nameTransition");
            pairs[2]=new Pair<View,String>(tvSource,"sourceTransition");
            pairs[3]=new Pair<View,String>(tvDestination,"destinationTransition");
            pairs[4]=new Pair<View,String>(tvDate,"dateTransition");
            pairs[5]=new Pair<View,String>(tvTime,"timeTransition");
            pairs[6]=new Pair<View,String>(tvPhone,"phoneTransition");
            pairs[7]=new Pair<View,String>(tvCarId,"carIdTransition");*/



            // @SuppressLint({"NewApi", "LocalSuppress"})
                 //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,pairs);
                context.startActivity(intent /*,options.toBundle()*/);



        }
    }

    private void addFavorite(Rider_Ways rider){
        FirebaseDatabase database_user = FirebaseDatabase.getInstance();
        DatabaseReference Users = database_user.getReference("Users");
        Users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Favorites")
                .child(rider.getUID())
                .setValue(rider.getUID(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });
    }
    private void removeFavorite(Rider_Ways rider){
        FirebaseDatabase database_user = FirebaseDatabase.getInstance();
        DatabaseReference Users = database_user.getReference("Users");

        Users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Favorites")
                .child(rider.getUID())
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });
    }
    private void isFavorite(int pos, final ProductViewHolder holder){
        final String UID=list.get(pos).getUID();
        FirebaseDatabase database_user = FirebaseDatabase.getInstance();
        DatabaseReference Users = database_user.getReference("Users");

        Users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Favorites")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){
                    String UIDFav = dataSnapshot1.getValue(String.class);
                    if (UID.equals(UIDFav)){
                        holder.favoriteButton.setFavorite(true,false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }
    private void removeAt(Rider_Ways rid) {
        int poss=list.indexOf(rid);
        list.remove(poss);
        notifyDataSetChanged();
        notifyItemRemoved(poss);
    }
    public void alert_delete(final Rider_Ways rider)
    {
        AlertDialog.Builder alertDialog2 = new
                AlertDialog.Builder(
                context);

        // Setting Dialog Title
        alertDialog2.setTitle("Confirm Delete");

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure you want to Delete?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog;
                        removeAt(rider);
                        FirebaseDatabase database_user = FirebaseDatabase.getInstance();
                        DatabaseReference Ways = database_user.getReference("Ways");
                        Ways.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .removeValue();
                        Toast.makeText(context,"Delete success",Toast.LENGTH_LONG).show();
                    }
                });

        // Setting Negative "NO" Btn
        alertDialog2.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });

        // Showing Alert Dialog
        alertDialog2.show();


    }


}
