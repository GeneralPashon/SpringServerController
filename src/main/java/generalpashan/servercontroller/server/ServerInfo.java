package generalpashan.servercontroller.server;

public abstract class ServerInfo{

    private final String id;
    private final String directory;

    public ServerInfo(String id, String directory){
        this.id = id;
        this.directory = directory;
    }


    public String getID(){
        return id;
    }

    public String getDirectory(){
        return directory;
    }

}
