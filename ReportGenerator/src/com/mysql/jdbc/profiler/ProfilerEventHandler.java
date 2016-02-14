package com.mysql.jdbc.profiler;

import com.mysql.jdbc.Extension;

public abstract interface ProfilerEventHandler extends Extension
{
  public abstract void consumeEvent(ProfilerEvent paramProfilerEvent);
}

/* Location:           C:\xampp\htdocs\SuiteCRM\SuiteCRM-7.5.1\custom\modules\AOS_Contracts\ReportGenerator.jar
 * Qualified Name:     com.mysql.jdbc.profiler.ProfilerEventHandler
 * JD-Core Version:    0.6.0
 */