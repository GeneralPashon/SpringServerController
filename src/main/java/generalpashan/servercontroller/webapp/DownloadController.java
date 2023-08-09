package generalpashan.servercontroller.webapp;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
public class DownloadController{

    @RequestMapping(value = "/download")
    public ResponseEntity<Object> download() throws IOException{
        System.out.println("DOWNLOADING");

        File file = new File("server_list_configuration.txt");

        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(file.length())
            .body(new InputStreamResource(new FileInputStream(file)));
    }

}
