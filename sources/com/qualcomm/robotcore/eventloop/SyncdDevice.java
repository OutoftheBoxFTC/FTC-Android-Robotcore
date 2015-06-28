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

package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.exception.RobotCoreException;

/**
 * SyncdDevice is for a device that wants to be in sync with the event loop. If there is sync'd
 * device registered with the event loop manager then the event loop manager will run the event
 * loop in this manor:
 * <p>
 * 1. wait until all sync'd device have returned from blockUtilReady() <br />
 * 2. run EventLoop.loop() <br />
 * 3. call startBlockingWork() on all sync'd device <br />
 * <p>
 * Sync'd devices need to register themselves with the event loop manager
 */
public interface SyncdDevice {

  /**
   * This method should block until it is ready for the event loop to run, Once this method has
   * returned, subsequent calls should return immediately until startBlockingWork() has been called.
   * @throws InterruptedException
   */
  public void blockUntilReady() throws RobotCoreException, InterruptedException;

  /**
   * This method will be called to let the sync'd device know that it's ok to enter a blocking
   * state.
   * <p>
   * Before this method returns, the sync'd device should put blockUntilReady() into a blocking
   * state. blockUntilReady() should remain in a blocking state until the device is ready for the
   * event loop to run. Once blockUntilReady() returns, it should not block again until
   * startBlockingWork() has been called.
   */
  public void startBlockingWork();
}
