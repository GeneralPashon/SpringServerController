package generalpashan.servercontroller.webapp;

import generalpashan.servercontroller.server.Server;
import generalpashan.servercontroller.server.ServerInfo;
import generalpashan.servercontroller.server.ServerList;
import jakarta.annotation.PreDestroy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class ServerController{

    private final ServerList serverList;

    public ServerController() throws IOException{
        serverList = new ServerList();
        serverList.loadConfig("server_list_configuration.txt");
    }


    @RequestMapping("/server/list")
    public List<ServerInfo> serverList(){
        return serverList.collection()
            .stream().map(Server::getInfo).toList();
    }


    @RequestMapping("/server/start/{serverID}")
    public ResponseEntity<String> startServer(@PathVariable("serverID") String serverID){
        Server server = serverList.getServer(serverID);
        if(server == null)
            return ResponseEntity.internalServerError().body("Server start failed");

        if(server.start())
            return ResponseEntity.ok("Server started successfully");

        return ResponseEntity.internalServerError().body("Server ID '" + serverID + "' is invalid");
    }

    @RequestMapping("/server/stop/{serverID}")
    public ResponseEntity<String> stopServer(@PathVariable("serverID") String serverID){
        Server server = serverList.getServer(serverID);
        if(server == null)
            return ResponseEntity.internalServerError().body("Server ID '" + serverID + "' is invalid");

        if(server.stop())
            return ResponseEntity.ok("Server stopped successfully");

        return ResponseEntity.internalServerError().body("Server stop failed");
    }

    @RequestMapping("/server/kill/{serverID}")
    public ResponseEntity<String> forceStopServer(@PathVariable("serverID") String serverID){
        Server server = serverList.getServer(serverID);
        if(server == null)
            return ResponseEntity.internalServerError().body("Server ID '" + serverID + "' is invalid");

        server.killProcess();
        return ResponseEntity.ok("Server stopped successfully");
    }


    @PreDestroy
    public void preDestroy(){
        for(Server server: serverList.collection()){
            System.out.println("Stopping " + server.getInfo().getID());
            server.stop();
        }
    }

}
