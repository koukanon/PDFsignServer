
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
			ignoreSsl();  //����SSL
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        // �إ߳s�u
        URL url = new URL(serverUrl); // serverUrl ���A����URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        // �W���ɮ�
        try (OutputStream outputStream = connection.getOutputStream();
             FileInputStream fileInputStream = new FileInputStream(srcFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }
        // �������A���^�Ǫ��ɮ�
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
            System.out.println("�ɮפU�������I");
        } else {
            System.err.println("���~�G���A���^���N�X " + responseCode);
        }
        
        // �����s�u
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
