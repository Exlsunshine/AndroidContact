/*
 * Copyright (C) 2008 ZXing authors
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

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;
import com.zijunlin.Zxing.Demo.CaptureActivity;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * This thread does all the heavy lifting of decoding the images.
 *����߳���ͼ�����ķ��ع���
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";

  private final CaptureActivity activity;
  private final Hashtable<DecodeHintType, Object> hints;//����ŵ���Ϣ�Ƕ�ά��ĸ�ʽ���ַ����Լ�����ص��ӿ�
  private Handler handler;//Handler����Ϣ���ݻ���           ������������������ʲô֮�䴫��������
  private final CountDownLatch handlerInitLatch;//������   Ŀ���ǵ�getHandler()��ʱ�����������߳�Thread��run()��ʱ��Ž���

  
  /**
   * ���ܣ� ���ܹ�ʶ��Ķ�ά��ı����ʽ����QR code,data matrix�ȣ��ַ���������ص��ӿ� ��������������뵽Hashtable<DecodeHintType, Object> hints ��
   * ����ResultPointCallback��һ���ӿ����ͣ���һ�����ܵĽ�� �����ά��ͼ������Ҫ�ĵ���ǵ㣩�������Ǳ�����     Ҳ�����Զ��ص�*/
  DecodeThread(CaptureActivity activity,
               Vector<BarcodeFormat> decodeFormats,
               String characterSet,
               ResultPointCallback resultPointCallback) {

    this.activity = activity;
    handlerInitLatch = new CountDownLatch(1);
    //CountDownLatch��һ���������������� ����������0ʱ�����¼���Ҳ���ǿ����������˲ſ��Խ�����
    //CountDownLatch����Ҫ�ķ�����countDown()��await()��ǰ����Ҫ�ǵ���һ�Σ������ǵȴ�������0�����û�е���0����ֻ�������ȴ���
    //�ѳ�ʼ��ֵ����Ϊ1��������ִ��һ��handlerInitLatch.countDown()ʱ���Ϳ��Դ�����

    hints = new Hashtable<DecodeHintType, Object>(3);

    if (decodeFormats == null || decodeFormats.isEmpty()) {//�����������������������ܹ�ʶ��Ķ�ά������и�ʽ
    	 decodeFormats = new Vector<BarcodeFormat>();
    	 decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);//�����DecodeFormatManager.ONE_D_FORMATS��������������Vector<BarcodeFormat>����
    	 decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
    	 decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
    	 
    }
    
    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);//����ά��Ŀ��ܸ�ʽ�ŵ� DecodeHintType hints��

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);//�����ǷŶ�ά����ַ���  �Ʋ��ǣ������ַ��ı���
    }

    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);//�������ص�����
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();//�ȴ� handlerInitLatch������0����������   ���߳�run()��ʱ�򣬲�������
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();   //Looper���ڷ�װ��android�߳��е���Ϣѭ����Ĭ�������һ���߳��ǲ�������Ϣѭ����message loop���ģ���Ҫ����Looper.prepare()�����̴߳���һ����Ϣѭ��������Looper.loop()��ʹ��Ϣѭ�������ã�����Ϣ������ȡ��Ϣ��������Ϣ
    handler = new DecodeHandler(activity, hints);//ʵ�������봦�����  ��������MultiFormatReader�ܹ�ʶ��Ĵ����ʽhints
    handlerInitLatch.countDown();//�򿪵���������������ִ��
    Looper.loop();//ʹ��Ϣѭ�������ã�����Ϣ������ȡ��Ϣ��������Ϣ
  }

}
