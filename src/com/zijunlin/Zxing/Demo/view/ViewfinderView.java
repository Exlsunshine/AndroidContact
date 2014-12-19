package com.zijunlin.Zxing.Demo.view;

import com.example.contact.R;
import com.google.zxing.ResultPoint;
import com.zijunlin.Zxing.Demo.camera.CameraManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import java.util.Collection;
import java.util.HashSet;

public final class ViewfinderView extends View {

  private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
  private static final long ANIMATION_DELAY = 100L;
  private static final int OPAQUE = 0xFF;
  private final Paint paint;
  private Bitmap resultBitmap;  
  private final int maskColor;//int 类型的都是组件的颜色值
  private final int resultColor;
  private final int frameColor;
  private final int laserColor;
  private final int resultPointColor;
  private int scannerAlpha;
  private Collection<ResultPoint> possibleResultPoints;
  private Collection<ResultPoint> lastPossibleResultPoints;

  // This constructor is used when the class is built from an XML resource.
  public ViewfinderView(Context context, AttributeSet attrs) {
    super(context, attrs);

    // Initialize these once for performance rather than calling them every time in onDraw().
    paint = new Paint();
    Resources resources = getResources();//获得与视图关联的资源
    maskColor = resources.getColor(R.color.viewfinder_mask);//获得对应资源的颜色值？？？？？？？？？？？？
    resultColor = resources.getColor(R.color.result_view);
    frameColor = resources.getColor(R.color.viewfinder_frame);
    laserColor = resources.getColor(R.color.viewfinder_laser);
    resultPointColor = resources.getColor(R.color.possible_result_points);
    scannerAlpha = 0;
    possibleResultPoints = new HashSet<ResultPoint>(5);
  }

  @Override
  public void onDraw(Canvas canvas) {
	//中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改 
    Rect frame = CameraManager.get().getFramingRect();//Rect是矩形
    if (frame == null) {
      return;
    }
    int width = canvas.getWidth();
    int height = canvas.getHeight();

    // Draw the exterior (i.e. outside the framing rect) darkened
    paint.setColor(resultBitmap != null ? resultColor : maskColor);//结果位图为空与否设置paint的颜色
    

   //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面   
   //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边
    canvas.drawRect(0, 0, width, frame.top, paint);//这四个drawRect函数分别绘制的是扫描二维码的frame区域的 上，左、右、下区域的矩形
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    if (resultBitmap != null) {
      // Draw the opaque result bitmap over the scanning rectangle
      paint.setAlpha(OPAQUE);//设置透明度0xFF
      canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);//在指定点绘制位图  也就是扫描二维码的矩形中
    } else {

      // Draw a two pixel solid black border inside the framing rect
      paint.setColor(frameColor);
      canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);//绘制的是扫面过程中跳出的拍了二维码图像的矩形框
      canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
      canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
      canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

      // Draw a red "laser scanner" line through the middle to show decoding is active
      paint.setColor(laserColor);
      paint.setAlpha(SCANNER_ALPHA[scannerAlpha]);
      scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
      int middle = frame.height() / 2 + frame.top;//计算横线在屏幕中竖直方向的坐标
      canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, paint);//绘制横线

      Collection<ResultPoint> currentPossible = possibleResultPoints;//是hashSet的类型 推断是扫描出来的可能结果
      Collection<ResultPoint> currentLast = lastPossibleResultPoints;
      if (currentPossible.isEmpty()) {
        lastPossibleResultPoints = null;
      } else {
        possibleResultPoints = new HashSet<ResultPoint>(5);//这一快???????????????????????????
        lastPossibleResultPoints = currentPossible;
        paint.setAlpha(OPAQUE);
        paint.setColor(resultPointColor);
        for (ResultPoint point : currentPossible) {
          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 6.0f, paint);//画圆
        }
      }
      if (currentLast != null) {
        paint.setAlpha(OPAQUE / 2);
        paint.setColor(resultPointColor);
        for (ResultPoint point : currentLast) {
        	//遍历currentLast中的值，赋予 point  这里currentLast是一个Collection<ResultPoint>  因为if中已经判定currentLast != null,所以这里遍历不会出现空指针的异常
          canvas.drawCircle(frame.left + point.getX(), frame.top + point.getY(), 3.0f, paint);
        }
      }

      // Request another update at the animation interval, but only repaint the laser line,
      // not the entire viewfinder mask.
      //只刷新扫描框的内容，其他地方不刷新 
      postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
    }
  }

  public void drawViewfinder() {
    resultBitmap = null;
    invalidate();//????????将整个view窗口作废
  }

  /**
   * Draw a bitmap with the result points highlighted instead of the live scanning display.
   *
   * @param barcode An image of the decoded barcode.
   */
  public void drawResultBitmap(Bitmap barcode) {
    resultBitmap = barcode;
    invalidate();//刷新
  }

  /**
   * 这个函数是在找到可能结果的点时，通过回调函数foundPossibleResultPoint()函数调用的
   * 将可能的结果点加入到Collection<ResultPoints>中*/
  public void addPossibleResultPoint(ResultPoint point) {
    possibleResultPoints.add(point);
  }

}
