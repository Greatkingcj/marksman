package com.huya.marksman.ipc.proxy;

import android.net.Uri;
import android.os.Bundle;

import com.huya.marksman.MarkApplication;
import com.huya.marksman.ipc.IpcConstant;
import com.huya.marksman.ipc.annotation.ServerUri;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by charles on 2018/8/9.
 */

public class IpcServerStubProxy {
    public static <T> T create(Class<T> stubClass) {
        String serverUriString = "";
        ServerUri serverUri = stubClass.getAnnotation(ServerUri.class);
        if (serverUri != null) {
            serverUriString = serverUri.value();
        }
        return (T) Proxy.newProxyInstance(stubClass.getClassLoader(),
                new Class[] {stubClass}, new IpcInvocationHandler(serverUriString));
    }

    private static class IpcInvocationHandler implements InvocationHandler {
        private Uri serverUri;
        public IpcInvocationHandler(String serverUri) {
            this.serverUri = Uri.parse(serverUri);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (proxy == null || method == null) {
                return null;
            }

            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            String ipcMethodName = method.getName();
            Bundle ipcExtras = new Bundle();
            if (args != null) {
                Class[] paramTypes = method.getParameterTypes();
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (paramTypes[i] == String.class) {
                        ipcExtras.putString(IpcConstant.ARG_KEYS[i], arg instanceof String ? (String) arg : "");
                    }
                    else if (paramTypes[i] == Boolean.class || paramTypes[i] == boolean.class) {
                        ipcExtras.putBoolean(IpcConstant.ARG_KEYS[i], arg instanceof Boolean ? (Boolean) arg : false);
                    }
                    else if (paramTypes[i] == Integer.class || paramTypes[i] == int.class) {
                        ipcExtras.putInt(IpcConstant.ARG_KEYS[i], arg instanceof Integer ? (Integer) arg : 0);
                    }
                    else if (paramTypes[i] == Long.class || paramTypes[i] == long.class) {
                        ipcExtras.putLong(IpcConstant.ARG_KEYS[i], arg instanceof Long ? (Long) arg : 0);
                    }
                    else {
                        ipcExtras.putString(IpcConstant.ARG_KEYS[i], null);
                    }
                }
                ipcExtras.putInt(IpcConstant.ARG_COUNT, args.length);
            }
            Class returnType = method.getReturnType();
            return ipcCall(returnType, ipcMethodName, ipcExtras);
        }

        private Object ipcCall(Class returnType, String methodName, Bundle extras) {
            Bundle returnBundle = MarkApplication.getApplication().getContentResolver().call(serverUri, methodName, null, extras);
            String result = returnBundle != null ? returnBundle.getString(IpcConstant.RETURN_VALUE) : null;

            if (returnType == Boolean.class || returnType == boolean.class) {
                return Boolean.parseBoolean(result);
            }
            if (returnType == Integer.class || returnType == int.class) {
                return Integer.parseInt(result);
            }
            if (returnType == Long.class || returnType == long.class) {
                return Long.parseLong(result);
            }
            return result;
        }
    }
}
