package ru.sir.richard.boss.api.market;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.junit.Test;

public class SititekApiTest {
	
	private static final int BUFFER_SIZE = 4096;
	 
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String saveDir) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");        
        String loginPassword = "dealer:fLR7MRrI";
        String base64LoginPassword = Base64.getEncoder().encodeToString(loginPassword.getBytes());
        httpConn.setRequestProperty("authorization", "Basic " + base64LoginPassword);
        
        httpConn.setRequestProperty("Content-type", "application/vnd.ms-excel");
        httpConn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"); 
        httpConn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        httpConn.setRequestProperty("accept-language", "ru,en;q=0.9");

        int responseCode = httpConn.getResponseCode();
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                            disposition.length() - 1);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }
	
	
	
	//@Test
	public void testLoadPrice() throws IOException {
		

		downloadFile("https://www.sititek.ru/dealer/Price.xls", "c:\\zero\\20\\");
		
		
		/*
		Request URL: https://www.sititek.ru/dealer/Price.xls
			Request Method: GET
			Status Code: 200 
			Remote Address: 62.109.17.68:443
			Referrer Policy: strict-origin-when-cross-origin
			accept-ranges: bytes
			content-length: 401408
			content-type: application/vnd.ms-excel
			date: Tue, 28 Sep 2021 14:29:43 GMT
			etag: "6152f5d5-62000"
			last-modified: Tue, 28 Sep 2021 11:00:37 GMT
			server: nginx
			strict-transport-security: max-age=15768000
			:authority: www.sititek.ru
			:method: GET
			:path: /dealer/Price.xls
			:scheme: https
			*/
			// accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9
		/*
			accept-encoding: gzip, deflate, br
			accept-language: ru,en;q=0.9
			authorization: Basic ZGVhbGVyOmZMUjdNUnJJ
			cookie: _ga=GA1.2.849166857.1621793611; _ym_d=1621793611; _ym_uid=1621793611286051593; view-style=320f92ba9942f59cddb93ecd91c329d555a8fcdf5bbf970199f9dd51b74a8d6fa%3A2%3A%7Bi%3A0%3Bs%3A10%3A%22view-style%22%3Bi%3A1%3Bs%3A4%3A%22list%22%3B%7D; _gcl_au=1.1.1375738897.1623864667; PHPSESSID=jlkf5pcdacuvgfkavu3bfvauiu; enterUrl=e3f0c53e7965ff1d72de4732158b38bd5d316813c67652362aad5854da8d43dea%3A2%3A%7Bi%3A0%3Bs%3A8%3A%22enterUrl%22%3Bi%3A1%3Bs%3A313%3A%22%23enterUrlhttps%3A%2F%2Fwww.sititek.ru%2Fsearch%3Fsearchid%3D2292132%26text%3D%25D0%25AD%25D0%25BB%25D0%25B5%25D0%25BA%25D1%2582%25D1%2580%25D0%25BE%25D0%25BD%25D0%25BD%25D1%258B%25D0%25B9%2520%25D0%25BE%25D1%2582%25D0%25BF%25D1%2583%25D0%25B3%25D0%25B8%25D0%25B2%25D0%25B0%25D1%2582%25D0%25B5%25D0%25BB%25D1%258C%2520%25D0%25BF%25D1%2582%25D0%25B8%25D1%2586%2520%28%25D0%2593%25D1%2580%25D0%25BE%25D0%25BC%25D0%25BF%25D1%2583%25D1%2588%25D0%25BA%25D0%25B0%29%2520%2522Zon%2520EL08%2522%26web%3D0%22%3B%7D; _csrf=0f02786cac024c5f4cbe898bdaa150bb170a2225b96271121bf5d44881c82006a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22Cv8Rmty8ssYW30RfRYR5TgYOGqGIcTX3%22%3B%7D; _gid=GA1.2.1982506642.1632839337; _gat_gtag_UA_155148922_1=1; _ym_isad=2; _ym_visorc=w; ZCallbackWidgetTmpId=e5e4220181b3765461102faea518f4b6
			if-modified-since: Mon, 27 Sep 2021 04:00:17 GMT
			if-none-match: "615141d1-62000"
			referer: https://www.sititek.ru/optom/otpugivateli-ptic-ultrazvukovye/Zon-EL08.html
			sec-ch-ua: "Chromium";v="94", "Google Chrome";v="94", ";Not A Brand";v="99"
			sec-ch-ua-mobile: ?0
			sec-ch-ua-platform: "Windows"
			sec-fetch-dest: document
			sec-fetch-mode: navigate
			sec-fetch-site: same-origin
			sec-fetch-user: ?1
			upgrade-insecure-requests: 1
			user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36
			*/
		
		
	}

}
