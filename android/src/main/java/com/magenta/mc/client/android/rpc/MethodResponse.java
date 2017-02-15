package com.magenta.mc.client.android.rpc;

import com.magenta.mc.client.android.mc.log.MCLoggerFactory;
import com.magenta.mc.client.android.mc.xml.XMLDataBlock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodResponse extends Parametrized {

    public static final String HANDLER_METHOD_SUFFIX = "Response";
    private Object responseObj;
    private XMLDataBlock methodResponseBlock;
    private String methodName;
    private Long responseId;

    /*
       create MethodResponse from incoming data-block
    */
    public static MethodResponse create(XMLDataBlock methodResponseBlock, String methodName, Long responseId) {
        final MethodResponse response = new MethodResponse();
        response.responseId = responseId;
        response.methodName = methodName;
        response.methodResponseBlock = methodResponseBlock;
        if (methodResponseBlock != null) {
            initParams(methodResponseBlock, response);
        }

        return response;
    }

    protected void initParamInfo() {
        super.initParamInfo();
        if (params.size() > 0) {
            responseObj = params.get(0);
        }
    }

    private Class[] handlerParamClasses() {
        Class[] handlerParamClasses = new Class[params.size() + 1];
        handlerParamClasses[0] = Long.class;
        if (params.size() > 0) {
            System.arraycopy(paramClasses, 0, handlerParamClasses, 1, paramClasses.length);
        }
        return handlerParamClasses;
    }

    private Class[] handlerDataParamClasses() {
        Class[] handlerDataParamClasses = new Class[params.size() + 1];
        handlerDataParamClasses[0] = Long.class;
        if (params.size() > 0) {
            System.arraycopy(dataParamClasses, 0, handlerDataParamClasses, 1, dataParamClasses.length);
        }
        return handlerDataParamClasses;
    }

    private Object[] handlerParams() {
        Object[] handlerParams = new Object[params.size() + 1];
        handlerParams[0] = responseId;
        if (params.size() > 0) {
            System.arraycopy(paramValues, 0, handlerParams, 1, paramValues.length);
        }
        return handlerParams;
    }

    private Object[] handlerDataParams() {
        Object[] handlerDataParams = new Object[params.size() + 1];
        handlerDataParams[0] = responseId;
        if (params.size() > 0) {
            System.arraycopy(paramData, 0, handlerDataParams, 1, paramData.length);
        }
        return handlerDataParams;
    }

    public Object getResponse() {
        return responseObj;
    }

    public void invoke(Object handler) {
        invoke(handler, false);
    }

    public void invoke(Object handler, boolean forceExtended) {
        final Class handlerClass = handler.getClass();
        try {
            if (forceExtended) {
                invokeWithDataParams(handler);
            } else {
                invokeSimple(handler);
            }
        } catch (NoSuchMethodException e) {
            try {
                if (forceExtended) {
                    throw e;
                }
                invokeWithDataParams(handler);
            } catch (NoSuchMethodException e1) {
                MCLoggerFactory.getLogger(getClass()).error("No method " + methodName + HANDLER_METHOD_SUFFIX + " found in class " + handlerClass.getName());
            } catch (Exception e2) {
                handleException(e2, handlerClass);
            }
        } catch (Exception e) {
            handleException(e, handlerClass);
        }
    }

    private void handleException(Exception e, Class listenerClass) {
        final String msg = "Error invoking method " + methodName + HANDLER_METHOD_SUFFIX + " on class " + listenerClass.getName();
        MCLoggerFactory.getLogger(getClass()).error(msg);
        e.printStackTrace();
        throw new RuntimeException(msg, e);
    }

    private String handlerMethodName() {
        return methodName + HANDLER_METHOD_SUFFIX;
    }

    private void invokeSimple(Object listener) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class listenerClass = listener.getClass();
        final Method method = listenerClass.getMethod(handlerMethodName(), handlerParamClasses());
        method.invoke(listener, handlerParams());
    }

    private void invokeWithDataParams(Object listener) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Class listenerClass = listener.getClass();
        final Method method = listenerClass.getMethod(handlerMethodName(), handlerDataParamClasses());
        method.invoke(listener, handlerDataParams());
    }
}
