package com.magenta.mc.client.android.rpc.bin_chunks.random;

public interface RandomTransmitterListener {

    void onEvent(RandomTransmitEvent event);

    class RandomTransmitEvent {

        public static final int STATUS_OK = 0;
        public static final int STATUS_FINISHED = 100;
        public static final int STATUS_ENEXPECTED_END_OF_FILE = -100;
        public static final int STATUS_FILE_NOT_FOUND = -200;

        public String uri;
        public int totalCount;
        public int sentCount;
        public int status;

        public RandomTransmitEvent(String uri, int totalCount, int sentCount, int status) {
            this.uri = uri;
            this.totalCount = totalCount;
            this.sentCount = sentCount;
            this.status = status;
        }

        public RandomTransmitEvent(String uri, int totalCount, int sentCount) {
            this.uri = uri;
            this.totalCount = totalCount;
            this.sentCount = sentCount;
            this.status = STATUS_OK;
        }
    }
}