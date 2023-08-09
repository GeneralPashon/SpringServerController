package generalpashan.servercontroller.server;

import generalpashan.servercontroller.server.minecraft.MinecraftServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import generalpashan.servercontroller.logger.Logger;

public class ServerList{

    private final Map<String, Server> servers;

    public ServerList(){
        servers = new HashMap<>();
    }


    public void loadConfig(String configPath) throws IOException{
        // Parse Configuration => Add Servers to HashMap

        List<String> lines = Files.readAllLines(new File(configPath).toPath());
        for(String line: lines){
            String[] cmd = line.split(":");
            if(cmd.length == 2){
                String[] args = Arrays.stream( cmd[1].trim().split(",") )
                    .map(String::trim).toArray(String[]::new);

                switch(cmd[0].trim()){
                    case "MINECRAFT_SERVER" ->{
                        Logger.getLogger("log_file").info("Configure '" + args[4] + "' Minecraft Server..");

                        servers.put(args[0], new MinecraftServer(args[0], args[1], args[2], args[3], args[4]));
                    }
                   /*
                   case "TERRARIA_SERVER" -> {
                       Logger.getLogger("log_file").info("Configure '" + args[4] + "' Terraria Server..");

                       servers.put(args[0], new TerrariaServer(args[0], args[1], args[2], args[3], args[4]));
                   }
                   */
                }
            }
        }
    }

    public <S extends Server> S getServer(String serverID){
        return (S) servers.get(serverID);
    }

    public Collection<Server> collection(){
        return servers.values();
    }




}
