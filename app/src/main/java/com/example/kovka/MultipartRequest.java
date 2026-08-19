package com.example.kovka;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import java.io.*;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final String boundary = "apiclient-" + System.currentTimeMillis();
    private final String lineEnd = "\r\n";
    private final String twoHyphens = "--";

    private final Map<String, String> params;
    private final Map<String, DataPart> dataParts;
    private final Response.Listener<String> listener;

    public MultipartRequest(String url,
                            Map<String, String> params,
                            Map<String, DataPart> dataParts,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        this.params = params;
        this.dataParts = dataParts;
        this.listener = listener;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // текстовые параметры
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(bos, entry.getKey(), entry.getValue());
                }
            }
            // файлы
            if (dataParts != null && !dataParts.isEmpty()) {
                for (Map.Entry<String, DataPart> entry : dataParts.entrySet()) {
                    buildDataPart(bos, entry.getKey(), entry.getValue());
                }
            }
            // завершающий boundary
            bos.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
        } catch (IOException e) {
            VolleyLog.e("Multipart body error: %s", e.getMessage());
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    private void buildTextPart(ByteArrayOutputStream bos, String name, String value) throws IOException {
        bos.write((twoHyphens + boundary + lineEnd).getBytes());
        bos.write(("Content-Disposition: form-data; name=\"" + name + "\"" + lineEnd).getBytes());
        bos.write(("Content-Type: text/plain; charset=UTF-8" + lineEnd).getBytes());
        bos.write(lineEnd.getBytes());
        bos.write(value.getBytes("UTF-8"));
        bos.write(lineEnd.getBytes());
    }

    private void buildDataPart(ByteArrayOutputStream bos, String name, DataPart dataPart) throws IOException {
        bos.write((twoHyphens + boundary + lineEnd).getBytes());
        bos.write(("Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + dataPart.getFileName() + "\"" + lineEnd).getBytes());
        bos.write(("Content-Type: " + dataPart.getType() + lineEnd).getBytes());
        bos.write(lineEnd.getBytes());
        bos.write(dataPart.getContent());
        bos.write(lineEnd.getBytes());
    }

    public static class DataPart {
        private final byte[] content;
        private final String fileName;
        private final String type;

        public DataPart(byte[] content, String fileName, String type) {
            this.content = content;
            this.fileName = fileName;
            this.type = type;
        }

        public byte[] getContent() { return content; }
        public String getFileName() { return fileName; }
        public String getType() { return type; }
    }
}