package com.example.sellwastepaper;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sellwastepaper.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Post> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;

    FirebaseFirestore db;
    CollectionReference placeCollection;

    private boolean isLogin = false;

    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        placeCollection = db.collection("post");




        recyclerView = findViewById(R.id.recycler_view);
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_progress);
        dialog.setCancelable(false);
        dialog.show();




        postAdapter = new PostAdapter(postList,this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(postAdapter);

        prepareDivision();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Post post = postList.get(position);
                //Toast.makeText(getApplicationContext(), " is selected!", Toast.LENGTH_SHORT).show();
                Intent goToPlaceDetailsActivity = new Intent(getApplicationContext(),PostDetailsActivity.class);
                goToPlaceDetailsActivity.putExtra("post", post);
                startActivity(goToPlaceDetailsActivity);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void prepareDivision() {
        //Toast.makeText(getApplicationContext(),"ene",Toast.LENGTH_SHORT).show();
        //Place a = new Place("hello","asfd","asdfasdf","sdfasfd","asdf");
        //placeList.add(a);
        placeCollection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(),"suc",Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //Log.d("collect data", document.getId() + " => " + document.get("about")+"\n");
                                /*Post post = new Post(
                                        document.getId(),
                                        (String)document.get("url"),
                                        (String)document.get("about"),
                                        (String)document.get("address"),
                                        (String)document.get("visitHour")

                                );*/

                                Post post = document.toObject(Post.class);
                                postList.add(post);

                                //Log.d("collect data", document.getId() + " => " + document.getData() + "dedddd "+place.getAbout());
                                /*Log.d("collect data", document.getId() + " => " + place.getName()+"\n");
                                Log.d("collect data", document.getId() + " => " + place.getAbout()+"\n");
                                Log.d("collect data", document.getId() + " => " + place.getAddress()+"\n");
                                Log.d("collect data", document.getId() + " => " + place.getImageUrl()+"\n");
                                Log.d("collect data", document.getId() + " => " + place.getVisitingHour()+"\n");*/
                            }
                            postAdapter.notifyDataSetChanged();
                            dialog.hide();

                            if (postList.isEmpty()){
                                Toast.makeText(getApplicationContext(),"Empty",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            //Toast.makeText(getApplicationContext(),"fal",Toast.LENGTH_SHORT).show();
                            //Log.d("collect data", "Error getting documents: ", task.getException());
                        }
                    }
                });


        //placeAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem shareItem = menu.findItem(R.id.action_favorite);

        // show the button when some condition is true
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            shareItem.setVisible(true);
        }else{
            shareItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            //Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            if (isLogin){
                Intent intent = new Intent(getApplicationContext(),AddActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }


            return true;
        }
        else if (id == R.id.logout){
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            isLogin = true;
        }
    }

}
