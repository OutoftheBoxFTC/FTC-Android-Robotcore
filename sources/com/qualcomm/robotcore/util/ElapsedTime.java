/* Copyright (c) 2014 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.robotcore.util;


/**
 * Measure elapsed time
 * <p>
 * Does not measure deep sleep. Nanosecond accuracy.
 */
public class ElapsedTime {

  private static final double SECOND_IN_NANO = 1000000000;

  private long startTime = 0;

  /**
   * Constructor
   * <p>
   * Starts the timer
   */
  public ElapsedTime() {
    reset();
  }

  /**
   * Constructor
   * <p>
   * Starts timer with a pre-set time
   * @param startTime pre set time
   */
  public ElapsedTime(long startTime) {
    this.startTime = startTime;
  }

  /**
   * Reset the start time to now
   */
  public void reset() {
    startTime = System.nanoTime();
  }

  /**
   * Get the relative start time
   * @return relative start time
   */
  public double startTime() {
    return startTime / SECOND_IN_NANO;
  }

  /**
   * How many seconds since the start time. Nanosecond accuracy.
   * @return time
   */
  public double time() {
    return (System.nanoTime() - startTime) / SECOND_IN_NANO;
  }

  /**
   * Log a message stating how long the timer has been running
   */
  public void log(String label) {
    RobotLog.v(String.format("TIMER: %20s - %1.3f", label, time()));
  }

  /**
   * Return a string stating the number of seconds that have passed
   */
  @Override
  public String toString() {
    return String.format("%1.4f seconds", time());
  }
}
