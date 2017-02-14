package com.magenta.mc.client.android.renderer;

import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

public interface ObjectRenderer {

    Object renderFromBlock(XMLDataBlock jobBlock);
}