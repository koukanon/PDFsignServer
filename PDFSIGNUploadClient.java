
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
