package com.huya.marksman.ipc.server;

import com.huya.marksman.ipc.server.stub.RemoteIpcServerStub;
import com.huya.marksman.service.MyService;

/**
 * Created by charles on 2018/8/9.
 */

public class RemoteIpcServer extends IpcServer implements RemoteIpcServerStub{
    public static final String URI = "content://com.huya.marksman.ipc.server.RemoteIpcServer";

    private static MyService service = null;

    public static void setMyService(MyService myService) {
        service = myService;
    }

    @Override
    public String switchVideo() {
        return service.switchVideo();
    }

    @Override
    public String updateSetting() {
        return service.updateSetting();
    }
}
