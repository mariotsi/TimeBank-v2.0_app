package me.mariotti.timebank;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import me.mariotti.timebank.classes.JsonUtils;
import me.mariotti.timebank.classes.RESTCaller;
import me.mariotti.timebank.classes.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

    public static final String ACTION = "me.mariotti.timebank.login_action";
    public static final int LOGIN = 1;
    public static final int LOGOUT = 0;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        action = getIntent().getExtras().getInt(ACTION);
        //If the user is logging out don't create the Activity, just make the call to the API and kill the activity
        if (action == LOGOUT) {
            mAuthTask = new UserLoginTask();
            mAuthTask.execute(action);
            try {
                mAuthTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            finish();
            Toast.makeText(getBaseContext(), getString(R.string.success_logged_out), Toast.LENGTH_SHORT).show();
            return;
        }
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(email)) {
            mUsernameView.setError(getString(R.string.error_invalid_email));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute(action);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 5 && username.length() <= 40;
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                                // Retrieve data rows for the device user's 'profile' contact.
                                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                                                     ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                                // Select only email addresses.
                                ContactsContract.Contacts.Data.MIMETYPE +
                                " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                                                             .CONTENT_ITEM_TYPE},

                                // Show primary email addresses first. Note that there won't be
                                // a primary email address if the user hasn't specified one.
                                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                                         android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsernameView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/logout task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Integer, Void, JSONObject> {

        private static final String TAG = "Login";
        private String mUsername = "";
        private String mPassword = "";
        private String userCredentials;

        UserLoginTask(String email, String password) {
            mUsername = email;
            mPassword = password;
        }

        UserLoginTask() {
        }

        @Override
        protected JSONObject doInBackground(Integer... action) {
            JSONObject mJSONObject = new JSONObject();
            HttpURLConnection urlConnection = null;
            String urlString = RESTCaller.mServerUrl + "users/";
            try {
                userCredentials = mUsername + ":" + mPassword;

                String basicAuth = User.isLogged ? "Basic " + MainActivity.loggedUser.userCredentials :
                        "Basic " + Base64.encodeToString(userCredentials.getBytes(), Base64.DEFAULT);

                if (action[0] == LOGIN) {
                    urlString += "login/";
                } else if (action[0] == LOGOUT) {
                    urlString += "logout/";
                }
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", basicAuth);
                if (action[0] == LOGIN) {
                    urlConnection.setDoOutput(true);
                } else if (action[0] == LOGOUT) {
                    urlConnection.setRequestMethod("DELETE");
                }

                InputStreamReader in = new InputStreamReader(urlConnection.getInputStream(), "UTF-8");
                mJSONObject = JsonUtils.urlResponseToJson(in, urlConnection.getResponseCode(), urlConnection.getResponseMessage());
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    mJSONObject.put("hasErrors", true);
                    mJSONObject.put("errorMessage", e.toString());
                    mJSONObject.put("responseCode", urlConnection != null ? urlConnection.getResponseCode() : -1);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return mJSONObject;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            mAuthTask = null;
            if (mProgressView != null) {
                showProgress(false);
            }
            try {
                // 200 OK login, 410 OK (GONE) logout
                switch (success.getInt("responseCode")) {
                    case 200:
                        User.isLogged = true;
                        JSONObject successBody = success.getJSONObject("responseBody");
                        MainActivity.loggedUser = new User(successBody.getInt("id"), successBody.getString("email"),
                                                           successBody.getString("username"), successBody.getBoolean("is_active"),
                                                           successBody.getBoolean("is_admin"), successBody.getInt("available_hours"),
                                                           successBody.getInt("worked_hours"), successBody.getInt("requested_hours"),
                                                           successBody.getInt("used_hours"), successBody.getString("address"),
                                                           successBody.getInt("city"), successBody.getString("city_name"),
                                                           Base64.encodeToString(userCredentials.getBytes(), Base64.DEFAULT));
                        finish();
                        break;
                    case 410:
                        if (MainActivity.loggedUser != null) {
                            MainActivity.loggedUser = null;
                        }
                        User.isLogged = false;
                        break;
                    case 401:
                        mUsernameView.setError(getString(R.string.error_incorrect_username_or_password));
                        mUsernameView.requestFocus();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), getString(R.string.error_generic_error), Toast.LENGTH_LONG).show();
                        break;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), getString(R.string.error_generic_error), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}



