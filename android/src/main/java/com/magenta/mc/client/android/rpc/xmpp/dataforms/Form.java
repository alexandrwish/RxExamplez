package com.magenta.mc.client.android.rpc.xmpp.dataforms;

public interface Form {

    int append(String text);

    int append(Item formItem);
}