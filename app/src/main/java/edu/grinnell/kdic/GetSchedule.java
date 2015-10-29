package edu.grinnell.kdic;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by [pradhanp] on 10/11/15.
 * Gets the schedule from network location through JSON
 */
public class GetSchedule extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    public static final String TAG = GetSchedule.class.getSimpleName();

    private OnScheduleParsed listener;

    public GetSchedule(Context context, OnScheduleParsed listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        if (!NetworkState.isOnline(context)) {
            cancel(true);
        }
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.SCHEDULE_URL)
                .build();
        try {
            Response response = client.newCall(request).execute();

            Schedule schedule = new Schedule(context);
            // Parse JSON and add it to SQLite DB
            schedule.updateSchedule(response.body().string());

            return true; // successfully updated schedule
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false; // failed to update schedule
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            listener.onScheduleParsed();
            Log.i(TAG, "Schedule successfully parsed.");
            Toast.makeText(context, "Schedule updated", Toast.LENGTH_LONG).show();
        } else {
            // failure messege
            Toast.makeText(context, "Failed to Update Schedule", Toast.LENGTH_LONG).show();
        }
        super.onPostExecute(success);
    }

    @Override
    protected void onCancelled() {
        // No internet connection
        // failure messege
        super.onCancelled();
    }
}
