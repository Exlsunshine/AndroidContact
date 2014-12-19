/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zijunlin.Zxing.Demo.decoding;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import android.app.Activity;

/**
 * Finishes an activity after a period of inactivity.
 */
public final class InactivityTimer {

  private static final int INACTIVITY_DELAY_SECONDS = 5 * 60;

  private final ScheduledExecutorService inactivityTimer =
      Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());//初始化一个ScheduledExecutorService对象 ,可以通过此对象设置任务执行的时间
  private final Activity activity;
  private ScheduledFuture<?> inactivityFuture = null;//ScheduledFuture可以获得任务的执行结果
  
  /**
   * 功能：确保打开扫描窗口后，一直没有识别出二维码的情况下，扫描任务在300s 之后自动结束*/
  public InactivityTimer(Activity activity) {
    this.activity = activity;
    onActivity();
  }

  public void onActivity() {
    cancel();//取消运行着的扫描窗口的任务
    inactivityFuture = inactivityTimer.schedule(new FinishListener(activity),
                                                INACTIVITY_DELAY_SECONDS,
                                                TimeUnit.SECONDS);
    //schedule() 函数是在指定的延迟后执行  参数：1.将要执行的任务 2.延迟执行的时间  3.延迟的时间单位   
    //在这里 功能是在300秒时间后结束执行传来的activity

  }

  private void cancel() {
    if (inactivityFuture != null) {
      inactivityFuture.cancel(true);//true 是如果线程在执行，那么就打断任务的执行
      inactivityFuture = null;//表示没有任务的执行了  这里是因为有可能上一次我们在执行扫描时，中途放弃，而上次扫描的时间与现在执行的时间间隔小于300秒
    }
  }

  public void shutdown() {
    cancel();
    inactivityTimer.shutdown();
  }

  private static final class DaemonThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable runnable) {//Runnable 代表一个可以被执行的命令，常常用作在不同的线程中运行代码
      Thread thread = new Thread(runnable);
      thread.setDaemon(true);//将这个线程设置为守护线程  守护线程运行只要有非守护线程运行。当最后一个非守护线程结束，运行时将退出。这不是一个用户界面的应用程序通常是相关的。
      return thread;
    }
  }

}
