package edu.grinnell.kdic.schedule;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;

import edu.grinnell.kdic.Constants;
import edu.grinnell.kdic.NetworkState;

/**
 * Created by [pradhanp] on 10/11/15.
 * Gets the schedule from network location through JSON
 */
public class GetSchedule extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private ScheduleFragment scheduleFragment;
    public static final String TAG = GetSchedule.class.getSimpleName();
    private ProgressDialog dialog;

    public GetSchedule(Context context, ScheduleFragment scheduleFragment) {
        this.context = context;
        this.scheduleFragment = scheduleFragment;
    }

    @Override
    protected void onPreExecute() {
        if (!NetworkState.isOnline(context)) {
            cancel(true);
        } else {
            dialog = new ProgressDialog(context);
            dialog.setTitle("Fetching Schedule");
            dialog.setMessage("Getting the schedule");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();
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
        dialog.dismiss();
        if (success) {
            Log.i(TAG, "Schedule successfully parsed.");
            Toast.makeText(context, "Schedule updated", Toast.LENGTH_LONG).show();
            scheduleFragment.getContent();
        } else {
            // failure message
            Toast.makeText(context, "Failed to Update Schedule", Toast.LENGTH_LONG).show();
        }

        super.onPostExecute(success);
    }

    @Override
    protected void onCancelled() {
        dialog.dismiss();
        // No internet connection
        Toast.makeText(context, "There was an error fetching data.", Toast.LENGTH_LONG).show();
        super.onCancelled();
    }
}
