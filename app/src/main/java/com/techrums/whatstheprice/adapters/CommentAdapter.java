package com.techrums.whatstheprice.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techrums.whatstheprice.GlideApp;
import com.techrums.whatstheprice.R;
import com.techrums.whatstheprice.models.Comment;
import com.techrums.whatstheprice.models.User;
import com.techrums.whatstheprice.ui.activities.HomeActivity;
import com.techrums.whatstheprice.ui.fragments.HomeFragment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> comments;
    private FirebaseUser mUser;
    public CommentAdapter(Context mContext, List<Comment> comments) {
        this.mContext = mContext;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.row_comment,viewGroup,false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        mUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment=comments.get(i);
        viewHolder.comment.setText(comment.getComment());
        getUserInfo(viewHolder.image_profile,viewHolder.username,comment.getUser().getUser());
        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, HomeActivity.class);
                intent.putExtra("publisherid",comment.getUser().getUser());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView image_profile;
        public TextView username,comment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.iv_comment_owner_display);
            username=itemView.findViewById(R.id.tv_username);
            comment=itemView.findViewById(R.id.tv_commentDisplay);

        }
    }
    private void getUserInfo(final ImageView imageView, final TextView username, String publisherId){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                GlideApp.with(mContext).load(user.getPhotoUrl()).into(imageView);
                username.setText(user.getUser());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
