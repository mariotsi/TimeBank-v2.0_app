package me.mariotti.timebank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import me.mariotti.timebank.classes.Listing;
import me.mariotti.timebank.classes.User;


public class ListingDetailActivity extends Activity {
    private Button mRequestButton;
    private Menu mOptionsMenu;
    private Listing mListing;
    private CheckBox mCheckBox;
    private TextView mRequestedLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_listing_activity);
        Bundle data = getIntent().getExtras();
        mListing = data.getParcelable(MainActivity.LISTING_OBJECT);
        ((TextView) findViewById(R.id.descriptionText)).setText(mListing.description);
        ((TextView) findViewById(R.id.categoryText)).setText(mListing.categoryName);
        ((TextView) findViewById(R.id.dateText)).setText(Listing.dateFormatter.format(mListing.dateCreation));
        (mCheckBox = (CheckBox) findViewById(R.id.checkBox_requested)).setChecked(!mListing.requested);
        mRequestedLabel = (TextView) findViewById(R.id.requested_label);
        mRequestButton = (Button) findViewById(R.id.claimButton);
        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClickEvent();
            }
        });

    }

    private void handleClickEvent() {
        if (mListing.iHaveRequestedThis()) {
            //TODO unrequest
            Toast.makeText(getBaseContext(), "UNREQUEST IT!", Toast.LENGTH_SHORT).show();
        } else {
            //TODO request
            Toast.makeText(getBaseContext(), "REQUEST IT!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
        if (mOptionsMenu != null) {
            MenuItem logInOut = mOptionsMenu.findItem(R.id.menu__listing_detail__log_in_out);
            Intent intent = (new Intent(this, LoginActivity.class));
            if (User.isLogged) {
                logInOut.setTitle(R.string.log_out);
                intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGOUT);
            } else {
                logInOut.setTitle(R.string.log_in);
                intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGIN);
            }
            logInOut.setIntent(intent);

        }
        mRequestButton.setVisibility(!mListing.imOwner() && User.isLogged ? View.VISIBLE : View.INVISIBLE);
        if (mListing.imOwner()) {
            mCheckBox.setVisibility(View.VISIBLE);
            mRequestedLabel.setVisibility(View.VISIBLE);
        } else {
            mCheckBox.setVisibility(View.INVISIBLE);
            mRequestedLabel.setVisibility(View.INVISIBLE);

            if (mListing.iHaveRequestedThis()) {
                mRequestButton.setText(getString(R.string.unrequest_text));
            } else {
                mRequestButton.setText(getString(R.string.request_text));
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem profile = mOptionsMenu.findItem(R.id.menu__listing_detail__profile);
        if (User.isLogged) {
            profile.setVisible(true);
        } else {
            profile.setVisible(false);
        }
        if (!mListing.imOwner()) {
            mOptionsMenu.findItem(R.id.menu__listing_detail__edit).setVisible(false);
            mOptionsMenu.findItem(R.id.menu__listing_detail__delete).setVisible(false);
        } else {
            mOptionsMenu.findItem(R.id.menu__listing_detail__edit).setVisible(true);
            mOptionsMenu.findItem(R.id.menu__listing_detail__delete).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_listing_detail, menu);
        MenuItem LogInOut = mOptionsMenu.findItem(R.id.menu__listing_detail__log_in_out);
        Intent intent = (new Intent(this, LoginActivity.class));
        if (User.isLogged) {
            LogInOut.setTitle(R.string.log_out);
            intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGOUT);
        } else {
            LogInOut.setTitle(R.string.log_in);
            intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGIN);
        }
        LogInOut.setIntent(intent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu__listing_detail__edit) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void editListing(MenuItem item) {
    }

    public void deleteListing(MenuItem item) {
    }
}
