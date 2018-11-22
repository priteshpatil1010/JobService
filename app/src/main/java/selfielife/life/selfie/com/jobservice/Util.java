package selfielife.life.selfie.com.jobservice;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class Util {

    private static final String TAG = Util.class.getSimpleName();
    // schedule the start of the service every 10 - 30 seconds

    public static void scheduleJob(Context context) {

        Log.d(TAG,"(scheduleJob) called ");

        ComponentName serviceComponent = new ComponentName(context, TestJobService.class.getName());

        JobInfo.Builder builder = new JobInfo.Builder(1, serviceComponent);
        builder.setMinimumLatency(1 * 20000); // wait at least
        builder.setOverrideDeadline(3 * 1000); // maximum delay

        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        builder.setRequiresDeviceIdle(false); // device should be idle
        builder.setRequiresCharging(false); // we don't care if the device is charging or not
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(builder.build());
    }
}