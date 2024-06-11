/*
 * 
 * Copyright (C) 2024 BSMI/kangdainfo 
This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License along with this program.If not, see <https://www.gnu.org/licenses/>.


���Ѩϥ�JAVA���{��,�ϥ�itextpdf 5.1.3�i��PDF���[ñ�\��, 
itextpdf�M��HomePage http://www.itextpdf.com/ 
�`�NLicenses��GNU Affero General Public License v3 
http://www.fsf.org/licensing/licenses/agpl-3.0.html

���v�Ҧ� (C) 2024 �g�ٳ��з����秽/�d�j��T�ѥ��������q
���{���O�ۥѳn��G�z�i�H���ۥѳn�����|�o�G��
GNU Affero�q�Τ��@�\�i�Ҫ����ڤU���s���o�M/�έק復�A
�Ϊ̥��\�i�ҲĤT���Ϊ̡]�ѱz��ܡ^������򪩥��C    

���o���{���O�Ʊ楦�ଣ�W�γ��A���S�������O�A
�Ʀܤ]�S�����A�P�ʩίS�w�ت��A�Ωʪ��q�ܾ�O�C
��h�Ӹ`�аѨ���GNU Affero�q�Τ��@�\�i�ҡ��C    

�z���Ӥw���쥻�{���H����GNU Affero�q�Τ��@�\�i�Ҫ��ƥ��C
�p������A�аѨ��Ghttp://www.gnu.org/licenses/ �C

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
