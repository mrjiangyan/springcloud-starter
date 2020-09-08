//package com..web.starter.configuration;
//
//
//import javax.servlet.ReadListener;
//import javax.servlet.ServletInputStream;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequestWrapper;
//import java.io.*;
//
//import static java.nio.charset.StandardCharsets.UTF_8;
//
///**
// * @author 流的方式获取JSON数据
// */
//public class RequestWrapper extends HttpServletRequestWrapper {
//
//    /**
//     * 存放JSON数据主体
//     */
//    private final String body;
//
//    public RequestWrapper(HttpServletRequest request) throws IOException {
//        super(request);
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader bufferedReader = null;
//        try {
//            InputStream inputStream = request.getInputStream();
//            if (inputStream != null) {
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                char[] charBuffer = new char[128];
//                int bytesRead;
//                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
//                    stringBuilder.append(charBuffer, 0, bytesRead);
//                }
//            }
//        } finally {
//            if (bufferedReader != null) {
//                bufferedReader.close();
//            }
//        }
//        body = stringBuilder.toString();
//    }
//
//    @Override
//    public ServletInputStream getInputStream() {
//        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes(UTF_8));
//        return new ServletInputStream() {
//            @Override
//            public boolean isFinished() {
//                return false;
//            }
//
//            @Override
//            public boolean isReady() {
//                return false;
//            }
//
//            @Override
//            public void setReadListener(ReadListener readListener) {
//
//            }
//
//            @Override
//            public int read() {
//                return byteArrayInputStream.read();
//            }
//        };
//    }
//
//    @Override
//    public BufferedReader getReader() {
//        return new BufferedReader(new InputStreamReader(this.getInputStream()));
//    }
//
//    public String getBody() {
//        return this.body;
//    }
//}