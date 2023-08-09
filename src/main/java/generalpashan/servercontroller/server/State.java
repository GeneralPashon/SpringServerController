package generalpashan.servercontroller.server;

public enum State{

    DISABLED("Disabled"),
    STARTING("Starting.."),
    ENABLED("Enabled"),
    STOPPING("Stopping..");


    public final String text;

    State(String text){
        this.text = text;
    }

}
