package generalpashan.servercontroller;

import generalpashan.servercontroller.logger.Logger;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@ComponentScan
@EnableAutoConfiguration
public class Application{

    public static void main(String[] args) throws IOException{
        // Create Logger
        File logFile = new File("logs/log-" + ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME) + ".txt");
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
        Logger.createLogger("log_file", new Logger(new PrintStream(logFile)));

        // Run
        SpringApplication.run(Application.class, args);
    }


    @Bean
    public ServletWebServerFactory servletContainer(){
        // Enable SSL Trafic
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory(){
            @Override
            protected void postProcessContext(Context context){
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        // Add HTTP to HTTPS redirect
        tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());

        return tomcat;
    }

    /*
    We need to redirect from HTTP to HTTPS. Without SSL, this application used
    port 8082. With SSL it will use port 8443. So, any request for 8082 needs to be
    redirected to HTTPS on 8443.
     */
    private Connector httpToHttpsRedirectConnector(){
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8082);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }

}
