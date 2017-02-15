package com.magenta.mc.client.android.rpc;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodCall extends Parametrized {

    private String methodName;
    private XMLDataBlock methodCallBlock;

    private MethodCall() {

    }

    /*
        create MethodCall from incoming data-block
     */
    public static MethodCall create(XMLDataBlock methodCallBlock) {
        final MethodCall call = new MethodCall();
        call.methodCallBlock = methodCallBlock;
        call.methodName = methodCallBlock.getChildBlock("methodName").getText();

        initParams(methodCallBlock, call);

        return call;
    }

    /*
    create MethodCall from method name and parameter objects
     */
    public static MethodCall create(String methodName, Object[] args) {
        final MethodCall call = new MethodCall();
        call.methodName = methodName;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            call.params.add(Param.createParam(arg));
        }

        call.initParamInfo(); // actually, seems like info of these parameters is not going to be used in this case

        call.initDataBlock();

        return call;
    }

    public String getMethodName() {
        return methodName;
    }

    public void invoke(Object listener) {
        invoke(listener, false);
    }

    public void invoke(Object listener, boolean forceExtended) {
        final Class listenerClass = listener.getClass();
        try {
            if (forceExtended) {
                invokeWithDataParams(listener);
            } else {
                invokeSimple(listener);
            }
        } catch (NoSuchMethodException e) {
            try {
                if (forceExtended) {
                    throw e;
                }
                invokeWithDataParams(listener);
            } catch (NoSuchMethodException e1) {
                MCLoggerFactory.getLogger(getClass()).error("No method " + methodName + " found in class " + listenerClass.getName());
            } catch (Exception e2) {
                handleException(e2, listenerClass);
            }
        } catch (Exception e) {
            handleException(e, listenerClass);
        }
    }

    private void handleException(Exception e, Class listenerClass) {
        final String msg = "Error invoking method " + methodName + " on class " + listenerClass.getName();
        MCLoggerFactory.getLogger(getClass()).error(msg);
        e.printStackTrace();
        throw new RuntimeException(msg, e);
    }

    private void invokeSimple(Object listener) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class listenerClass = listener.getClass();
        final Method method = listenerClass.getMethod(methodName, paramClasses);
        method.invoke(listener, paramValues);
    }

    private void invokeWithDataParams(Object listener) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class listenerClass = listener.getClass();
        final Method method = listenerClass.getMethod(methodName, dataParamClasses);
        method.invoke(listener, paramData);
    }

    private void initDataBlock() {
        methodCallBlock = new XMLDataBlock("methodCall", null, null);
        methodCallBlock.addChild("methodName", methodName);
        initParamsBlock(methodCallBlock);
    }

    public XMLDataBlock getDataBlock() {
        return methodCallBlock;
    }
}
