package org.moon.resources;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;

@Path("/")
@Api(value = "dropwizard practise")
public class FileUploadResource {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FileUploadResource.class);

    private String uploadLocation = "upload";

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Timed
    @ApiOperation(
            value = "upload file",
            response = Response.class
    )
    public Response uploadFile(@QueryParam("name") String newFileName, @QueryParam("type") String fileType, @FormDataParam("file") final InputStream fileInputStream) {
        String filePath = uploadLocation + File.separator + newFileName + "." + fileType;
        saveFile(fileInputStream, filePath);
        String output = "File can be downloaded from the following location : " + filePath;

        return Response.status(200).entity(output).build();
    }

    private void saveFile(InputStream uploadedInputStream,
                          String serverLocation) {
        OutputStream outputStream = null;
        try {
            File dir = new File(uploadLocation);
            if (!dir.exists()) {
                dir.mkdir();
            }

            int read = 0;
            byte[] bytes = new byte[1024];

            outputStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            outputStream.flush();

        } catch (IOException e) {
            logger.warn(e.getMessage());
        }finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }
}
