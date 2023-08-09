package generalpashan.servercontroller.server.minecraft;

import generalpashan.servercontroller.server.ServerInfo;

public class MinecraftServerInfo extends ServerInfo{

    private final String name;
    private final String stopCmd;

    public MinecraftServerInfo(String id, String directory, String name, String stopCmd){
        super(id, directory);

        this.name = name;
        this.stopCmd = stopCmd;
    }


    public String getName(){
        return name;
    }

    public String getStopCmd(){
        return stopCmd;
    }

}
