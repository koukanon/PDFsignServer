/*
 * 
 * Copyright (C) 2024 BSMI/kangdainfo 
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.


提供使用JAVA的程式,使用itextpdf 5.1.3進行PDF的加簽功能, 
itextpdf套件為HomePage http://www.itextpdf.com/ 
注意Licenses為GNU Affero General Public License v3 
http://www.fsf.org/licensing/licenses/agpl-3.0.html

版權所有 (C) 2024 經濟部標準檢驗局/康大資訊股份有限公司
本程式是自由軟體：您可以基於自由軟體基金會發佈的
GNU Affero通用公共許可證的條款下重新分發和/或修改它，
或者本許可證第三版或者（由您選擇）任何後續版本。    

分發本程式是希望它能派上用場，但沒有任何擔保，
甚至也沒有對其適銷性或特定目的適用性的默示擔保。
更多細節請參見“GNU Affero通用公共許可證”。    

您應該已收到本程式隨附的GNU Affero通用公共許可證的副本。
如未收到，請參見：http://www.gnu.org/licenses/ 。

EMAIL   Kh.Li@bsmi.gov.tw

README.md
	
 * 
 * 
 */
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class PDFSIGNUploadClient {
	public static void main(String[] args) throws IOException 
	{
		doUpload("http://localhost:8000/","D:/output2.pdf","D:/1234.pdf");
    }
	
	public static void doUpload(String serverUrl,String srcFile,String saveFile) throws IOException {
		try {
			ignoreSsl();  //忽略SSL
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        // 建立連線
        URL url = new URL(serverUrl); // serverUrl 伺服器的URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        // 上傳檔案
        try (OutputStream outputStream = connection.getOutputStream();
             FileInputStream fileInputStream = new FileInputStream(srcFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
        // 接收伺服器回傳的檔案
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream fileOutputStream = new FileOutputStream(saveFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }
            System.out.println("檔案下載完成！");
        } else {
            System.err.println("錯誤：伺服器回應代碼 " + responseCode);
        }
        
        // 關閉連線
        connection.disconnect();
    }
	
	private static void trustAllHttpsCertificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager tm = new MiTM();
		trustAllCerts[0] = tm;
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}

	static class MiTM implements TrustManager, X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public boolean isServerTrusted(final X509Certificate[] certs) {
			return true;
		}

		public boolean isClientTrusted(final X509Certificate[] certs) {
			return true;
		}

		public void checkServerTrusted(final X509Certificate[] certs, final String authType) throws CertificateException {
			return;
		}

		public void checkClientTrusted(final X509Certificate[] certs, final String authType) throws CertificateException {
			return;
		}
	} 
	
	public static void ignoreSsl() throws Exception {

		HostnameVerifier hv = new HostnameVerifier() {
			public boolean verify(final String urlHostName, final SSLSession session) {
				return true;
			}
		};
		trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
	
}
