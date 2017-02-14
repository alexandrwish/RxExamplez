package com.magenta.mc.client.android.rpc.xmpp.extensions.rpc;

import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created 03.03.2010
 *
 * @author Konstantin Pestrikov
 */
public class Param {
    private static DateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'") {

        public Date parse(String source) throws ParseException {
            synchronized (this) {
                return super.parse(source);
            }
        }

        public String formatDate(Date date) {
            synchronized (this) {
                return super.format(date);
            }
        }
    };

    static {
        ISO8601_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private Class type;
    private Object value;
    private XMLDataBlock paramBlock;

    private Param() {

    }

    public static Param fromDataBlock(XMLDataBlock paramBlock) {
        final Param param = new Param();
        param.paramBlock = paramBlock;
        XMLDataBlock valueBlock = paramBlock.getChildBlock("value");

        XMLDataBlock typedBlock = (XMLDataBlock) valueBlock.getChildBlocks().get(0);
        final String typeName = typedBlock.getTagName();
        if ("i4".equals(typeName) || "int".equals(typeName)) {
            param.type = Integer.class;
            param.value = new Integer(Integer.parseInt(typedBlock.getText()));
        } else if ("string".equals(typeName)) {
            param.type = String.class;
            param.value = typedBlock.getText();
        } else if ("double".equals(typeName)) {
            param.type = Double.class;
            param.value = new Double(Double.parseDouble(typedBlock.getText()));
        } else if ("Base64".equals(typeName)) {
            param.type = String.class;
            param.value = typedBlock.getText();
        } else if ("boolean".equals(typeName)) {
            param.type = Boolean.class;
            param.value = Boolean.valueOf(typedBlock.getText());
        } else if ("dateTime.iso8601".equals(typeName)) {
            param.type = Date.class;
            try {
                param.value = ISO8601_DATE_FORMAT.parse(typedBlock.getText());
            } catch (ParseException e) {
                throw new RuntimeException("Failed to parse dateTime.iso8601 format: " + typedBlock.getText(), e);
            }
        } else if ("array".equals(typeName)) {
            final XMLDataBlock dataBlock = typedBlock.getChildBlock("data");
            if (dataBlock == null) {
                param.value = null;
            } else {
                List res = new ArrayList();
                Class arrayComponentType = null;
                if (dataBlock.getChildBlocks() != null) {
                    for (int i = 0; i < dataBlock.getChildBlocks().size(); i++) {
                        XMLDataBlock tmpParamElm = new XMLDataBlock("param", null, null);
                        XMLDataBlock valueElm = (XMLDataBlock) dataBlock.getChildBlocks().get(i);
                        tmpParamElm.addChild(valueElm);
                        final Param tmpParam = Param.fromDataBlock(tmpParamElm);
                        res.add(tmpParam.getValue());

                        // determine the component type of array parameter
                        if (arrayComponentType == null) {// first run
                            arrayComponentType = tmpParam.type;
                        } else if (!Object.class.equals(arrayComponentType) // if different from previous and still not Object
                                && !tmpParam.type.equals(arrayComponentType)) {
                            arrayComponentType = Object.class; // only Object[] does for heterogenous array
                        }

                    }
                }
                if (res.size() > 0) {
                    final Object[] resArray = res.toArray();
                    final Object resArrayOfRightType = Array.newInstance(arrayComponentType, resArray.length);
                    for (int i = 0; i < resArray.length; i++) {
                        Object o = resArray[i];
                        Array.set(resArrayOfRightType, i, o);
                    }
                    param.type = resArrayOfRightType.getClass();
                    param.value = resArrayOfRightType;
                } else {
                    param.type = Object[].class;
                    param.value = null;
                }
            }
        } else if ("struct".equals(typeName)) {
            // todo
            throw new IllegalArgumentException("struct not supported");
        }

        return param;
    }

    public static Param createParam(Object arg) {
        final Param param = new Param();
        param.type = arg == null ? Object.class : arg.getClass();
        param.value = arg;
        param.initDataBlock();
        return param;
    }

    private void initDataBlock() {
        paramBlock = new XMLDataBlock("param", null, null);
        final XMLDataBlock valueBlock = new XMLDataBlock("value", paramBlock, null);
        paramBlock.addChild(valueBlock);
        if (Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            valueBlock.addChild(new XMLDataBlock(valueBlock, "int", "" + value));
        } else if (Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            valueBlock.addChild(new XMLDataBlock(valueBlock, "float", "" + value));
        } else if (Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            valueBlock.addChild(new XMLDataBlock(valueBlock, "double", "" + value));
        } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
            valueBlock.addChild(new XMLDataBlock(valueBlock, "boolean", "" + value));
        } else if (Date.class.isAssignableFrom(type)) {
            valueBlock.addChild(new XMLDataBlock(valueBlock, "dateTime.iso8601", ISO8601_DATE_FORMAT.format(value)));
        } else if (type.isArray()) {
            final XMLDataBlock arrayBlock = new XMLDataBlock(valueBlock, "array", "");
            valueBlock.addChild(arrayBlock);
            final XMLDataBlock dataBlock = new XMLDataBlock(valueBlock, "data", "");
            arrayBlock.addChild(dataBlock);
            final int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                final Object nextVal = Array.get(value, i);
                final XMLDataBlock nextValueBlock = Param.createParam(nextVal).getValueBlock();
                dataBlock.addChild(nextValueBlock);
            }
        } else {
            // use string by default
            valueBlock.addChild(new XMLDataBlock(valueBlock, "string", "" + value));
        }

    }


    public Class getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public XMLDataBlock getParamBlock() {
        return paramBlock;
    }

    public XMLDataBlock getValueBlock() {
        return paramBlock.getChildBlock("value");
    }
}
