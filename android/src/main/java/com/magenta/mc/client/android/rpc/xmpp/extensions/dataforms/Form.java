package com.magenta.mc.client.android.rpc.xmpp.extensions.dataforms;

public interface Form {

    int append(String text);

    int append(Item formItem);
}