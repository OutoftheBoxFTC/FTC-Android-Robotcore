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

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.Command;

/**
 * Event loop interface
 * <p>
 * Event loops need to implement this interface. Contains methods for managing the life cycle of your robot.
 */
public interface EventLoop {

  /**
   * Init method, this will be called before the first call to loop. You should set up
   * your hardware in this method.
   *
   * @param eventLoopManager event loop manager that is responsible for this event loop
   *
   * @throws RobotCoreException if a RobotCoreException is thrown, it will be handled
   *         by the event loop manager. The manager will report that the robot failed
   *         to start.
   */
  public abstract void init(EventLoopManager eventLoopManager) throws RobotCoreException, InterruptedException;

  /**
   * This method will be repeatedly called by the event loop manager.
   *
   * @throws RobotCoreException if a RobotCoreException is thrown, it will be handled
   *         by the event loop manager. The manager may decide to either stop processing
   *         this iteration of the loop, or it may decide to shut down the robot.
   */
  public abstract void loop() throws RobotCoreException, InterruptedException;

  /**
   * Teardown method, this will be called after the last call to loop. You should place your robot
   * into a safe state before this method exits, since there will be no more changes to communicate
   * with your robot.
   *
   * @throws RobotCoreException if a RobotCoreException is thrown, it will be handled by the event
   *         loop manager. The manager will then attempt to shut down the robot without the benefit
   *         of the teardown method.
   */
  public abstract void teardown() throws RobotCoreException, InterruptedException;

  /**
   * Process command method, this will be called if the event loop manager receives a user defined
   * command. How this command is handled is up to the event loop implementation.
   * @param command command to process
   */
  public abstract void processCommand(Command command);

  public abstract OpModeManager getOpModeManager();
}
