package me.gfred.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DealActivity extends AppCompatActivity {
    private static int PICTURE_RESULT = 42;
    @BindView(R.id.txtTitle)
    EditText txtTitle;
    @BindView(R.id.txtDescription)
    EditText txtDescription;
    @BindView(R.id.txtPrice)
    EditText txtPrice;
    @BindView(R.id.image)
    ImageView imageView;
    @BindView(R.id.btnImage)
    Button btnImage;

    private DatabaseReference mDatabaseReference;
    private TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal);
        ButterKnife.bind(this);
        mDatabaseReference = FirebaseUtil.mDatabaseReference;

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl());
    }

    @OnClick(R.id.btnImage)
    void uploadImage() {
        Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
        intent1.setType("image/jpeg");
        intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent1, ""), PICTURE_RESULT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        menu.findItem(R.id.delete_menu).setVisible(FirebaseUtil.isAdmin);
        menu.findItem(R.id.save_menu).setVisible(FirebaseUtil.isAdmin);
        enableEditTexts(FirebaseUtil.isAdmin);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDeal() {
        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "Please save deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabaseReference.child(deal.getId()).removeValue();
        Log.d("IMAGE_NAME", deal.getImageName());
        if(deal.getImageName() != null && !deal.getImageName().isEmpty()) {
            StorageReference picRef = FirebaseUtil.mFirebaseStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("Delete Image", "Image successfully deleted");

            }).addOnFailureListener(e -> Log.d("Delete Image", e.getMessage()));
        }
    }

    private void backToList() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    private void enableEditTexts(boolean isEnabled) {
        txtTitle.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        btnImage.setEnabled(isEnabled);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK) {
            Uri imageUri = Objects.requireNonNull(data).getData();
            final StorageReference reference = FirebaseUtil.mStorageReference.child(Objects.requireNonNull(Objects.requireNonNull(imageUri).getLastPathSegment()));
            reference.putFile(imageUri).continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                // Continue with the task to get the download URL
                return reference.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String url = Objects.requireNonNull(downloadUri).toString();
                    String pictureName = downloadUri.getPath();
                    Log.d("IMAGE_URL", url);
                    deal.setImageUrl(url);
                    deal.setImageName(pictureName);
                    showImage(url);
                }
            });
        }
    }

    private void showImage(String url) {
        if (url != null && !url.isEmpty()) {
            Log.d("SHOW_IMAGE", "Image shown " + url);
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width * 2 / 3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}
