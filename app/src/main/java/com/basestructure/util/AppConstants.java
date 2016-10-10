package com.basestructure.util;

public class AppConstants {

    public AppConstants(){}

    public static final int REQUEST_CODE_PICK_FROM_CAMERA=3;
    public static final int REQUEST_CODE_PICK_FROM_FILE=4;



    public enum UserDeviceStatus{
        ACTIVE(1), INACTIVE(2), PURGED(3);

        private int deviceStatus;

        private UserDeviceStatus(int deviceStatus) {
            this.deviceStatus = deviceStatus;
        }

        public int getDeviceStatus() {
            return deviceStatus;
        }

    }

    public enum userVerifiedEnum {
        UNVERIFIED(0), VERIFIED(1);

        private int status;
        private userVerifiedEnum(int i) {
            status = i;
        }

        public int getStatus() {
            return status;
        }
    }

}
