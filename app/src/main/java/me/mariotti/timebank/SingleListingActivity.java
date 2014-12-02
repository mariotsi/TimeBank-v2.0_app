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


public class SingleListingActivity extends Activity {
    Button mClaimButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_listing_activity);
        // TODO hide or show fields based on ownership
        Bundle data= getIntent().getExtras();
        Listing mListing = data.getParcelable(MainActivity.LISTING_OBJECT);
        ((TextView)findViewById(R.id.descriptionText)).setText(mListing.description);
        ((TextView)findViewById(R.id.categoryText)).setText(mListing.categoryName+mListing.categoryId);
        ((TextView)findViewById(R.id.dateText)).setText(Listing.dateFormatter.format(mListing.dateCreation));
        ((CheckBox)findViewById(R.id.checkBox_requested)).setChecked(!mListing.requested);
        mClaimButton = (Button)findViewById(R.id.claimButton);

        mClaimButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                if (true){//TODO replace with isLogged
                    //do request - unrequest
                }
                else{
                    intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGIN);
                }
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (true){//TODO replace with isREquested and i can see that
            mClaimButton.setText(R.string.request_text);

        }
        else{
            mClaimButton.setText(R.string.unrequest_text);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.single_listing_menu, menu);
        MenuItem LogInOut = menu.findItem(R.id.listing_detail_log_in_out);
        Intent intent =(new Intent(this,LoginActivity.class));
        if (User.isLogged){
            LogInOut.setTitle(R.string.log_out);
            intent.putExtra(LoginActivity.ACTION, LoginActivity.LOGOUT);
        }
        else{
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
        if (id == R.id.edit_menu_item) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void editListing(MenuItem item) {
    }

    public void deleteListing(MenuItem item) {
    }
}
