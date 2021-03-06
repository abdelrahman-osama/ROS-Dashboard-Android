/*
 * Copyright (C) 2014 Jamie Cho.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.ros_java.test_android.sensor_serial;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import sensor_msgs.Image;
import std_msgs.Int32;

/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class Listener extends AbstractNodeMain {

  static Subscriber<std_msgs.Float32> subscriber;
  static Subscriber<std_msgs.Int32> errorSub;

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("rosjava/listener");
  }

  @Override
  public void onStart(ConnectedNode connectedNode) {
    final Log log = connectedNode.getLog();
    subscriber = connectedNode.newSubscriber("estimated_vel_kmph", std_msgs.Float32._TYPE);
    errorSub = connectedNode.newSubscriber("diagnostic_system", Int32._TYPE );
//    subscriber.addMessageListener(new MessageListener<std_msgs.String>() {
//      @Override
//      public void onNewMessage(std_msgs.String message) {
//        log.info("I heard: \"" + message.getData() + "\"");
//      }
//    });

  }
}

