package com.magenta.mc.client.xmpp.extensions.dataforms;

/**
 * Created 02.03.2010
 *
 * @author Konstantin Pestrikov
 */
public interface Form {
    int append(String text);

    int append(Item formItem);
}
