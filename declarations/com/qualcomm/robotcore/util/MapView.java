/*
 * Copyright (c) 2014 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.qualcomm.robotcore.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.HashMap;

public class MapView extends View {
  private int width;
  private int height;
  private int lineIntervalX;
  private int lineIntervalY;

  private Paint linePaint;
  private Canvas canvas;
  private Bitmap bCanvas;

  private boolean isSetup = false;
  private boolean isVisible = false;

  MapView mv;

  private int idPool = 1;

  private float scalerX;
  private float scalerY;

  private BitmapDrawable bg;

  private int robotX;
  private int robotY;
  private int robotAngle;
  private boolean haveRobot = false;

  private HashMap<Integer, Marker> markers;
  private Bitmap robotIcon;

  private static final int CIRCLE_RADIUS = 5;
  private static final int CIRCLE_DEGREES = 360;
  private static final float ARROW_SCALER = 0.2f;

  @Override
  protected void onSizeChanged(int x, int y, int oldx, int oldy) {
    // Scaler to convert values in mm to pixels
    scalerX = (float) this.getWidth() / (float) width;
    scalerY = (float) this.getHeight() / (float) height;
    isVisible = true;
    redraw();
    Log.e("MapView", "Size changed");
  }

  public MapView(Context context) {
    super(context);
    init();
  }

  public MapView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MapView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    linePaint = new Paint();
    linePaint.setColor(Color.BLACK);
    linePaint.setStrokeWidth(1f);
    linePaint.setAntiAlias(true);

    mv = this;
    markers = new HashMap<Integer, Marker>();
  }

  // We want certain values to be even
  private int makeEven(int i) {
    if ((i % 2) == 0) {
      return i;
    } else {
      return i + 1;
    }
  }

  /**
   * Call this to setup the MapView and draw the grid
   *
   * @param xMax The max positive width, in mm. Note that this is doubled to give the overall width.
   * @param yMax The max positive height, in mm. Note that this is doubled to give the overall
   *        height.
   * @param numLinesX The number of grid lines to be drawn horizontally
   * @param numLinesY The number of grid lines to be drawn vertically
   */
  public void setup(int xMax, int yMax, int numLinesX, int numLinesY, Bitmap robotIcon) {
    this.width = xMax * 2;
    this.height = yMax * 2;
    this.lineIntervalX = width / makeEven(numLinesX);
    this.lineIntervalY = height / makeEven(numLinesY);
    this.robotIcon = robotIcon;
    isSetup = true;
  }

  private void drawGrid() {
    bCanvas = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
    canvas = new Canvas(bCanvas);

    Paint p = new Paint();
    p.setColor(Color.WHITE);
    p.setAntiAlias(true);
    canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p);

    // Draw horizontal lines
    for (int i = 0; i < height; i += lineIntervalY) {
      float loc = ((float) i) * scalerY;
      canvas.drawLine(0, loc, canvas.getWidth(), loc, linePaint);
    }

    // Draw vertical lines
    for (int i = 0; i < width; i += lineIntervalX) {
      float loc = ((float) i) * scalerX;
      canvas.drawLine(loc, 0, loc, canvas.getHeight(), linePaint);
    }
  }

  private float scaleX(int x) {
    float px = ((float) x) * scalerX;
    px = px + (this.getWidth() / 2);
    return px;
  }

  private float scaleY(int y) {
    float py = ((float) y) * scalerY;
    py = (this.getHeight() / 2) - py;
    return py;
  }

  private int scaleDegrees(int angle) {
    return CIRCLE_DEGREES - angle;
  }

  /**
   * Set the location of the robot
   *
   * @param x The +- mm location horizontally
   * @param y The +- mm location vertically
   * @param angle The angle of the robot, CCW
   */
  public void setRobotLocation(int x, int y, int angle) {
    this.robotX = -x;
    this.robotY = y;
    this.robotAngle = angle;
    this.haveRobot = true;
    // redraw();
  }

  /**
   * Add a marker, represented as a sphere
   *
   * @param x The x coordinate, in +-mm
   * @param y The y coordinate, in +-mm
   * @param color The color value (eg Color.RED).
   * @return The integer id of this circle, which can be passed to `removeMarker`
   */
  public int addMarker(int x, int y, int color) {
    int id = idPool++;

    Marker m = new Marker(id, -x, y, color, true);
    markers.put(id, m);

    // redraw();

    return id;
  }

  /**
   * Removes a marker
   *
   * @param id The marker's id
   * @return True if successful, false otherwise
   */
  public boolean removeMarker(int id) {
    Object o = markers.remove(id);
    // redraw();
    if (o == null) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Adds a drawable resource to the map
   *
   * @param x X coordinate of resource, in +-mm
   * @param y Y coordinate of resource, in +-mm
   * @param resource The int id of theint drawable resource
   * @return The internal id, used to remove the resource
   */
  public int addDrawable(int x, int y, int resource) {
    int id = idPool++;

    Marker m = new Marker(id, -x, y, resource, false);
    markers.put(id, m);

    // redraw();

    return id;
  }

  private void drawMarkers() {
    for (Marker m : markers.values()) {
      float px = scaleX(m.x);
      float py = scaleY(m.y);

      if (m.circle) {
        Paint p = new Paint();
        p.setColor(m.resource);
        canvas.drawCircle(px, py, CIRCLE_RADIUS, p);
      } else {
        Bitmap b = BitmapFactory.decodeResource(getResources(), m.resource);

        // Compensate for size of the marker
        px -= b.getWidth() / 2;
        py -= b.getHeight() / 2;

        canvas.drawBitmap(b, px, py, new Paint());
      }
    }
  }

  private void drawRobot() {
    float px = scaleX(robotX);
    float py = scaleY(robotY);

    int degrees = scaleDegrees(robotAngle);

    // Rotate arrow
    Matrix m = new Matrix();
    m.postRotate(degrees);
    m.postScale(ARROW_SCALER, ARROW_SCALER);

    Bitmap bSrc = robotIcon;

    Bitmap b = Bitmap.createBitmap(bSrc, 0, 0, bSrc.getWidth(), bSrc.getHeight(), m, true);

    // Compensate for size of arrow
    px -= b.getWidth() / 2;
    py -= b.getHeight() / 2;

    canvas.drawBitmap(b, px, py, new Paint());
  }

  @SuppressWarnings("deprecation")
  public void redraw() {
    if (isSetup && isVisible) {
      drawGrid();
      drawMarkers();

      if (haveRobot) {
        drawRobot();
      }
    }
    bg = new BitmapDrawable(getResources(), bCanvas);
    mv.setBackgroundDrawable(bg);
  }

  private class Marker {
    @SuppressWarnings("unused")
    public int id;
    public int x;
    public int y;
    public int resource;
    public boolean circle;

    public Marker(int id, int x, int y, int resource, boolean circle) {
      this.id = id;
      this.x = x;
      this.y = y;
      this.resource = resource;
      this.circle = circle;
    }
  }
}
