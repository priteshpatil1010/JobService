package selfielife.life.selfie.com.jobservice;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class TestJobService extends JobService {

    private static final String TAG = TestJobService.class.getSimpleName() ;

    @Override
    public boolean onStartJob(JobParameters params) {

        Intent service = new Intent(getApplicationContext(), MainActivity.class);
        getApplicationContext().startService(service);
        Toast.makeText(this, "onStartJob called", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Pritesh Notification");
        Util.scheduleJob(getApplicationContext()); // reschedule the job
        return true;

    }

    @Override
    public boolean onStopJob(JobParameters params) {
       // Util.scheduleJob(getApplicationContext());

        //true to indicate to the JobManager whether you'd like to reschedule this job based on the retry criteria provided at job creation-time;
        // or false to end the job entirely. Regardless of the value returned, your job must stop executing.

        return true;
    }




}