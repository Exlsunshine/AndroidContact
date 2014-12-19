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
      Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());//��ʼ��һ��ScheduledExecutorService���� ,����ͨ���˶�����������ִ�е�ʱ��
  private final Activity activity;
  private ScheduledFuture<?> inactivityFuture = null;//ScheduledFuture���Ի�������ִ�н��
  
  /**
   * ���ܣ�ȷ����ɨ�贰�ں�һֱû��ʶ�����ά�������£�ɨ��������300s ֮���Զ�����*/
  public InactivityTimer(Activity activity) {
    this.activity = activity;
    onActivity();
  }

  public void onActivity() {
    cancel();//ȡ�������ŵ�ɨ�贰�ڵ�����
    inactivityFuture = inactivityTimer.schedule(new FinishListener(activity),
                                                INACTIVITY_DELAY_SECONDS,
                                                TimeUnit.SECONDS);
    //schedule() ��������ָ�����ӳٺ�ִ��  ������1.��Ҫִ�е����� 2.�ӳ�ִ�е�ʱ��  3.�ӳٵ�ʱ�䵥λ   
    //������ ��������300��ʱ������ִ�д�����activity

  }

  private void cancel() {
    if (inactivityFuture != null) {
      inactivityFuture.cancel(true);//true ������߳���ִ�У���ô�ʹ�������ִ��
      inactivityFuture = null;//��ʾû�������ִ����  ��������Ϊ�п�����һ��������ִ��ɨ��ʱ����;���������ϴ�ɨ���ʱ��������ִ�е�ʱ����С��300��
    }
  }

  public void shutdown() {
    cancel();
    inactivityTimer.shutdown();
  }

  private static final class DaemonThreadFactory implements ThreadFactory {
    public Thread newThread(Runnable runnable) {//Runnable ����һ�����Ա�ִ�е�������������ڲ�ͬ���߳������д���
      Thread thread = new Thread(runnable);
      thread.setDaemon(true);//������߳�����Ϊ�ػ��߳�  �ػ��߳�����ֻҪ�з��ػ��߳����С������һ�����ػ��߳̽���������ʱ���˳����ⲻ��һ���û������Ӧ�ó���ͨ������صġ�
      return thread;
    }
  }

}
