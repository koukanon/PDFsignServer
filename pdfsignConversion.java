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
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class pdfsignConversion {
	public static void main(String[] args) throws IOException {
        int port = 8000; // �i�ۦ���w���A����f
        
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new UploadHandler());
        server.setExecutor(null); // �ϥιw�]���������
        
        System.out.println("���A���w�ҰʡA���b��ť��f " + port + "...");
        server.start();
    }

    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // ����ШD��k
            String requestMethod = exchange.getRequestMethod();
            
            if (exchange.getRequestMethod().equalsIgnoreCase("get")) {
                exchange.sendResponseHeaders(200, 0); // �p�G�OGET�N�^200
                return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(200, 0); // �p�G�OOPTIONS�N�^200
                return;
            }
            
            
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, 0); // 405 Method Not Allowed
                return;
            }
	       	
            if (requestMethod.equalsIgnoreCase("POST")) { // �p�G�OPOST�ШD             
            	String saveTempFile = "D:/TEMPConvertFilePath/";//�Ȯɦs��ؿ�
            	{
                	File file = new File( saveTempFile  );  
                    if(!file.exists())
                    	file.mkdirs();
            	}
            	String SIGNTempFile = "D:/SIGNFilePath/";//�Ȯɦs��ؿ�
            	{
                	File file = new File( SIGNTempFile  );  
                    if(!file.exists())
                    	file.mkdirs();
            	}
            	
            	//�ϥΩI�sIP�M�ɶ��إߪA��
                String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress().replaceAll("\\.", "");
                LocalDateTime now = LocalDateTime.now();
                int year = now.getYear();
                int month = now.getMonthValue();
                int day = now.getDayOfMonth();
                int hour = now.getHour();
                int minute = now.getMinute();
                int second = now.getSecond();
                int millisecond = now.getNano() / 1000000;
                String filename = clientIP+year+month+day+hour+minute+second+millisecond;
                
                // �����J��y
                InputStream input = exchange.getRequestBody();
                // �x�s��J��y�����ɮ�
                saveFile(input,saveTempFile,filename);
                
                try {
                	PDFSign.sign(saveTempFile+filename,SIGNTempFile+filename);                	  
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally
				{
					File delData = new File(saveTempFile+filename); //�R����l�ǤJ���ɮ�
					delData.delete();
				}
                // �^�Ǥ@���ɮ�
                sendResponse(exchange, SIGNTempFile+filename);
                {
                	File delData = new File(SIGNTempFile+filename);//�R���ഫ���ɮ�
    				delData.delete();
                }
            } else {
                // �p�G���OPOST�ШD�A�^��405 Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }

        private void saveFile(InputStream input,String filePath,String filename) throws IOException {
            // �x�s�ɮת����|
            OutputStream output = new FileOutputStream(filePath+filename);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            output.close();
            input.close();
        }

        private void sendResponse(HttpExchange exchange, String fileName) throws IOException {
            File file = new File(fileName);
            if (file.exists()) {
                exchange.sendResponseHeaders(200, file.length());
                OutputStream output = exchange.getResponseBody();
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
                output.close();
            } else {
                exchange.sendResponseHeaders(404, -1); // �p�G�ɮפ��s�b�A�^��404 Not Found
            }
        }
    }
}