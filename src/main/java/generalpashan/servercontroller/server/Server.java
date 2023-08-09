package generalpashan.servercontroller.server;

public interface Server{

    boolean start();

    boolean stop();

    void killProcess();

    State getState();

    ServerInfo getInfo();

}
