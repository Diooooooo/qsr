package com.qsr.sdk.controller.fetcher;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.qsr.sdk.util.Env;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yuan on 2016/4/27.
 */
public class MultipartFetcher extends Fetcher {
    final String saveDirectory;
    final int maxPostSize;

    public MultipartFetcher(Controller controller, String saveDirectory, int maxPostSize) {
        super(controller);
        this.maxPostSize = maxPostSize;
        this.saveDirectory = saveDirectory;
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    @Override
    protected Map<String, Object> buildParameterMap() {
        Map<String, Object> result = new LinkedHashMap<>();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        HttpServletRequest request = this.getController().getRequest();
        int length = request.getContentLength();
        if(length>maxPostSize){
            throw new IllegalStateException("超出上传文件限制大小:"+maxPostSize+",当前大小:"+length);
        }

        try {
            FileItemIterator iter = upload.getItemIterator(request);
            while (iter.hasNext()) {
                FileItemStream item = iter.next();
                String paramName = item.getFieldName();
                OutputStream fileOutputStream = null;
                InputStream stream = null;
                try {
                    stream = item.openStream();
                    if (item.isFormField()) {
                        String value = Streams.asString(stream, Env.getCharset().toString());
                        result.put(paramName, value);
                    } else {
                        String originalfileName = item.getName();
                        String uploadedFileName = System.currentTimeMillis()
                                + originalfileName;
                        String contentType = item.getContentType();
                        File filePath = new File(saveDirectory , uploadedFileName);
                        fileOutputStream = new FileOutputStream(filePath);
                        IOUtils.copy(stream, fileOutputStream);

                        UploadFile uploadFile = new UploadFile(paramName, saveDirectory, uploadedFileName, originalfileName,
                                contentType);
                        result.put(paramName, uploadFile);
                    }
                } finally {
                    IOUtils.closeQuietly(fileOutputStream);
                    IOUtils.closeQuietly(stream);
                }

            }
        } catch (FileUploadException e) {
            throw new IllegalStateException("文件上传异常:"+e.getMessage(),e);
        } catch (IOException e) {
            throw new IllegalStateException("文件上传IO异常:"+e.getMessage(),e);
        }

        return result;
    }
}
