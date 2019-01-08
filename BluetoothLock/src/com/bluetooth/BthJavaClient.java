package com.bluetooth;

import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.*;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.ResponseCodes;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;


public class BthJavaClient {

    static final UUID OBEX_FILE_TRANSFER = new UUID(0x1106);

    public static final Vector/*<String>*/ serviceFound = new Vector();

    public static void main(String[] args) throws IOException, InterruptedException {

        // First run RemoteDeviceDiscovery and use discoved device
        RemoteDeviceDiscovery.main(null);

        serviceFound.clear();

        UUID serviceUUID = new UUID("000110100001000800000805F9B34FB", false);
        if ((args != null) && (args.length > 0)) {
            serviceUUID = new UUID(args[0], false);
        }

        final Object serviceSearchCompletedEvent = new Object();

        DiscoveryListener listener = new DiscoveryListener() {

            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
            }

            public void inquiryCompleted(int discType) {
            }

            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
                for (int i = 0; i < servRecord.length; i++) {
                    String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
                    if (url == null) {
                        continue;
                    }
                    serviceFound.add(url);
                    DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
                    if (serviceName != null) {
                        System.out.println("service " + serviceName.getValue() + " found " + url);
                    } else {
                        System.out.println("service found " + url);
                    }
                }
            }

            public void serviceSearchCompleted(int transID, int respCode) {
                System.out.println("service search completed!");
                synchronized(serviceSearchCompletedEvent){
                    serviceSearchCompletedEvent.notifyAll();
                }
            }

        };

        UUID[] searchUuidSet = new UUID[] { serviceUUID };
        int[] attrIDs =  new int[] {
                0x0100 // Service name
        };

        for(Enumeration en = RemoteDeviceDiscovery.devicesDiscovered.elements(); en.hasMoreElements(); ) {
            RemoteDevice btDevice = (RemoteDevice)en.nextElement();

            synchronized(serviceSearchCompletedEvent) {
                System.out.println("search services on " + btDevice.getBluetoothAddress() + " " + btDevice.getFriendlyName(false));
                LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
                serviceSearchCompletedEvent.wait();
            }
        }

    }

}

//public class BthJavaClient {
//    public static final Vector<RemoteDevice> devicesDiscovered = new Vector();
//    public static RemoteDevice device;
//
//    public static void main(String[] args) throws IOException, InterruptedException {
//
//        final Object inquiryCompletedEvent = new Object();
//
//        devicesDiscovered.clear();
//
//        DiscoveryListener listener = new DiscoveryListener() {
//
//            public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
//                System.out.println("Device " + btDevice.getBluetoothAddress() + " found");
//                devicesDiscovered.addElement(btDevice);
//                try {
//                    System.out.println("     name " + btDevice.getFriendlyName(false));
//                    if(btDevice.getFriendlyName(false).equals("小米手机2333")); device = btDevice;
//                } catch (IOException cantGetDeviceName) {
//                }
//            }
//
//            public void inquiryCompleted(int discType) {
//                System.out.println("Device Inquiry completed!");
//                synchronized(inquiryCompletedEvent){
//                    inquiryCompletedEvent.notifyAll();
//                }
//            }
//
//            public void serviceSearchCompleted(int transID, int respCode) {
//                System.out.println("Service Search completed!");
//                synchronized(inquiryCompletedEvent){
//                    inquiryCompletedEvent.notifyAll();
//                }
//            }
//
//            public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
//                System.out.println(transID);
//            }
//        };
//
//        synchronized(inquiryCompletedEvent) {
//            boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
//            if (started) {
//                System.out.println("wait for device inquiry to complete...");
//                inquiryCompletedEvent.wait();
//                System.out.println(devicesDiscovered.size() +  " device(s) found");
//            }
//
//            if(device != null){
////                ClientSession conn = (ClientSession) Connector.open("btspp://" + device.getBluetoothAddress() + ":6");
////                HeaderSet hsConnectReply = conn.connect(null);
////                if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
////                    System.out.println("Failed to connect");
////                    return;
////                }
////                System.out.println("conect");
//            }
//        }
//    }
//}

