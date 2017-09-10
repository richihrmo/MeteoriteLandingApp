package com.developers.team100k.meteoritelandingapp.JobServices;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Richard Hrmo.
 */

public class MyJobService extends JobService {

  private EventBus mEventBus = EventBus.getDefault();

  @Override
  public boolean onStartJob(JobParameters job) {
    mEventBus.post("download");
    return false;
  }

  @Override
  public boolean onStopJob(JobParameters job) {
    return false;
  }
}
