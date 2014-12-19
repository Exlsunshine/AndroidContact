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
 *这个线程做图像解码的繁重工作
 * @author dswitkin@google.com (Daniel Switkin)
 */
final class DecodeThread extends Thread {

  public static final String BARCODE_BITMAP = "barcode_bitmap";

  private final CaptureActivity activity;
  private final Hashtable<DecodeHintType, Object> hints;//里面放的信息是二维码的格式，字符集以及结果回调接口
  private Handler handler;//Handler是消息传递机制           ？？？？？？它是在什么之间传递数据呢
  private final CountDownLatch handlerInitLatch;//计数锁   目的是当getHandler()的时候，阻塞，当线程Thread被run()的时候才解锁

  
  /**
   * 功能： 将能够识别的二维码的编码格式，如QR code,data matrix等，字符集，结果回调接口 这后三个参数放入到Hashtable<DecodeHintType, Object> hints 中
   * 参数ResultPointCallback是一个接口类型，当一个可能的结果 （如二维码图像中重要的点像角点）被发现是被调用     也属于自动回调*/
  DecodeThread(CaptureActivity activity,
               Vector<BarcodeFormat> decodeFormats,
               String characterSet,
               ResultPointCallback resultPointCallback) {

    this.activity = activity;
    handlerInitLatch = new CountDownLatch(1);
    //CountDownLatch是一个倒数计数的锁， 当当倒数到0时触发事件，也就是开锁，其他人才可以进来；
    //CountDownLatch最重要的方法是countDown()和await()，前者主要是倒数一次，后者是等待倒数到0，如果没有到达0，就只有阻塞等待了
    //把初始的值设置为1，这样在执行一次handlerInitLatch.countDown()时，就可以触发了

    hints = new Hashtable<DecodeHintType, Object>(3);

    if (decodeFormats == null || decodeFormats.isEmpty()) {//？？？？？？？这里设置能够识别的二维码的所有格式
    	 decodeFormats = new Vector<BarcodeFormat>();
    	 decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);//这里的DecodeFormatManager.ONE_D_FORMATS等三个参数都是Vector<BarcodeFormat>类型
    	 decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
    	 decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
    	 
    }
    
    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);//将二维码的可能格式放到 DecodeHintType hints中

    if (characterSet != null) {
      hints.put(DecodeHintType.CHARACTER_SET, characterSet);//这里是放二维码的字符集  推测是？？？字符的编码
    }

    hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);//放入结果回调函数
  }

  Handler getHandler() {
    try {
      handlerInitLatch.await();//等待 handlerInitLatch倒数到0，否则阻塞   当线程run()的时候，不被阻塞
    } catch (InterruptedException ie) {
      // continue?
    }
    return handler;
  }

  @Override
  public void run() {
    Looper.prepare();   //Looper用于封装了android线程中的消息循环，默认情况下一个线程是不存在消息循环（message loop）的，需要调用Looper.prepare()来给线程创建一个消息循环，调用Looper.loop()来使消息循环起作用，从消息队列里取消息，处理消息
    handler = new DecodeHandler(activity, hints);//实例化解码处理程序  并设置了MultiFormatReader能够识别的代码格式hints
    handlerInitLatch.countDown();//打开倒数计数锁，触发执行
    Looper.loop();//使消息循环起作用，从消息队列里取消息，处理消息
  }

}
