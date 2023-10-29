package dev.mayuna.sakuyabridge.server.networking.tcp.listeners;

import dev.mayuna.sakuyabridge.commons.login.LoginMethod;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListener;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.Packets;
import lombok.NonNull;

public class LoginMethodsRequestListener extends TimeStopListener<Packets.LoginMethodsRequest> {

    public LoginMethodsRequestListener() {
        super(Packets.LoginMethodsRequest.class, 100);
    }

    @Override
    public void process(@NonNull Context context, Packets.@NonNull LoginMethodsRequest message) {
        // TODO: Allowed Login Methods config
        context.getConnection().sendTCP(new Packets.LoginMethodsResponse(LoginMethod.values()));
    }
}
