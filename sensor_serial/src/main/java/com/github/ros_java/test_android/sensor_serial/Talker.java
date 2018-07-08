package com.github.ros_java.test_android.sensor_serial;


import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 */
public class Talker extends AbstractNodeMain {
    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava/talker");
    }
    static Publisher<std_msgs.String> publisher;
    static Publisher<std_msgs.String> destPublisher;
    @Override
    public void onStart(final ConnectedNode connectedNode) {

        publisher = connectedNode.newPublisher("wirelessEmergency", std_msgs.String._TYPE);
        destPublisher = connectedNode.newPublisher("destinations", std_msgs.String._TYPE);
        // This CancellableLoop will be canceled automatically when the node shuts
        // down.
//        connectedNode.executeCancellableLoop(new CancellableLoop() {
//            private int sequenceNumber;
//
//            @Override
//            protected void setup() {
//                sequenceNumber = 0;
//            }
//
//            @Override
//            protected void loop() throws InterruptedException {
////                std_msgs.String str = publisher.newMessage();
////                str.setData(sequenceNumber+"");
////                publisher.publish(str);
////                sequenceNumber++;
////                Thread.sleep(1000);
//            }
//        });
    }
}
