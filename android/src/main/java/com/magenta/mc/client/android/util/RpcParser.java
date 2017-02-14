package com.magenta.mc.client.android.util;

import com.magenta.mc.client.android.entity.Attribute;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public final class RpcParser {

    private RpcParser() {
    }

    public static Set<Attribute> parseAttributes(final XMLDataBlock block) {
        if (block == null || block.getChildBlocks() == null || block.getChildBlocks().size() == 0) {
            return Collections.emptySet();
        }
        final Vector blockAttributes = block.getChildBlocks();
        final Set<Attribute> attributes = new HashSet<>();
        for (final Object item : blockAttributes) {
            final XMLDataBlock attributeBlock = (XMLDataBlock) item;
            final Attribute attribute = new Attribute(
                    attributeBlock.getChildBlockText("title"),
                    attributeBlock.getChildBlockText("name"),
                    attributeBlock.getChildBlockText("typeName"),
                    attributeBlock.getChildBlockText("value"),
                    attributeBlock.getChildBlockText("unit"));
            attributes.add(attribute);
        }
        return attributes;
    }
}