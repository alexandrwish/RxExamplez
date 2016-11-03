package com.magenta.maxunits.mobile.renderer;

import com.magenta.mc.client.xml.XMLDataBlock;

/**
 * User: smirnitsky
 * Date: 07.02.11
 * Time: 14:56
 */
public interface ObjectRenderer {
    Object renderFromBlock(XMLDataBlock jobBlock);
}
