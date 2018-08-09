package com.huya.marksman.ipc.server.stub;

import com.huya.marksman.ipc.annotation.ServerUri;
import com.huya.marksman.ipc.server.RemoteIpcServer;

/**
 * Created by charles on 2018/8/9.
 */

@ServerUri(RemoteIpcServer.URI)
public interface RemoteIpcServerStub {
    String switchVideo();
    String updateSetting();
}
