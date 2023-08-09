package generalpashan.servercontroller.server.minecraft;


import generalpashan.servercontroller.logger.Logger;
import generalpashan.servercontroller.server.Server;
import generalpashan.servercontroller.server.State;

import java.io.*;

import static generalpashan.servercontroller.server.State.*;

public class MinecraftServer implements Server{

    private final MinecraftServerInfo info;
    private volatile State state;

    protected Process process;
    private BufferedReader reader;
    private final String[] command;
    protected final Thread thread;

    public MinecraftServer(String id, String directory, String jarFileName, String stopCmd, String name){
        this.info = new MinecraftServerInfo(id, directory, name, stopCmd);

        command = new String[]{ "java", "-jar", jarFileName, "--nogui" };
        state = DISABLED;

        thread = new Thread(()->{
            try{
                while(!Thread.interrupted()){
                    if(!reader.ready())
                        continue;

                    String line = reader.readLine();
                    if(line == null)
                        continue;

                    processServerOut(line);
                }
                reader.close();

            }catch(IOException e){
                Logger.getGlobal().warn(e.getMessage());
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setDaemon(true);
    }

    private void processServerOut(String line){
        if(line.endsWith(" INFO]: Timings Reset") || line.contains(" INFO]: Listening on /"))
            updateState(ENABLED);

        else if(line.endsWith(" INFO]: Closing Thread Pool") || line.endsWith(" INFO]: Thank you and goodbye"))
            updateState(DISABLED);

        else if(line.equals("io.netty.channel.unix.Errors$NativeIoException: bind(..) failed: Address already in use") || line.endsWith(" WARN]: Perhaps a server is already running on that port?")){
            process.destroy();
            state = DISABLED;
            Logger.getLogger("log_file").info("Start failed (Address already in use)");
        }else if(line.startsWith("net.minecraft.util.SessionLock$ExceptionWorldConflict: ")){
            state = DISABLED;
            Logger.getLogger("log_file").info("Start failed ('session.lock' already locked)");
        }

        System.out.println("(" + this.info.getName() + ") " + line);
    }

    @Override
    public boolean start(){
        if(state != DISABLED)
            return false;

        try{
            process = Runtime.getRuntime().exec(command, null, new File(info.getDirectory()));
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            updateState(STARTING);
            if(thread.getState() == Thread.State.NEW)
                thread.start();

            return true;

        }catch(IOException e){
            Logger.getLogger("log_file").warn("Start failed (No such file or directory)");

            return false;
        }
    }

    @Override
    public boolean stop(){
        if(state != ENABLED)
            return false;

        updateState(STOPPING);

        try{
            OutputStream out = process.getOutputStream();
            out.write(info.getStopCmd().getBytes());
            out.close();

            return true;
        }catch(Exception e){
            Logger.getGlobal().warn(e.getMessage());

            return false;
        }
    }

    @Override
    public void killProcess(){
        if(state == DISABLED)
            return;

        process.destroy();
        state = DISABLED;
        Logger.getLogger("log_file").info("Interrupted");
    }

    private synchronized void updateState(State state){
        this.state = state;
        Logger.getLogger("log_file").info(state.text);
    }

    @Override
    public State getState(){
        return state;
    }

    @Override
    public MinecraftServerInfo getInfo(){
        return info;
    }

}
