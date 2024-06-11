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
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;

public class pdfsignConversion {
	public static void main(String[] args) throws IOException {
        int port = 8000; // 可自行指定伺服器埠口
        
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new UploadHandler());
        server.setExecutor(null); // 使用預設的執行緒池
        
        System.out.println("伺服器已啟動，正在監聽埠口 " + port + "...");
        server.start();
    }

    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 獲取請求方法
            String requestMethod = exchange.getRequestMethod();
            
            if (exchange.getRequestMethod().equalsIgnoreCase("get")) {
                exchange.sendResponseHeaders(200, 0); // 如果是GET就回200
                return;
            }
            if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(200, 0); // 如果是OPTIONS就回200
                return;
            }
            
            
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                exchange.sendResponseHeaders(405, 0); // 405 Method Not Allowed
                return;
            }
	       	
            if (requestMethod.equalsIgnoreCase("POST")) { // 如果是POST請求             
            	String saveTempFile = "D:/TEMPConvertFilePath/";//暫時存放目錄
            	{
                	File file = new File( saveTempFile  );  
                    if(!file.exists())
                    	file.mkdirs();
            	}
            	String SIGNTempFile = "D:/SIGNFilePath/";//暫時存放目錄
            	{
                	File file = new File( SIGNTempFile  );  
                    if(!file.exists())
                    	file.mkdirs();
            	}
            	
            	//使用呼叫IP和時間建立服務
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
                
                // 獲取輸入串流
                InputStream input = exchange.getRequestBody();
                // 儲存輸入串流中的檔案
                saveFile(input,saveTempFile,filename);
                
                try {
                	PDFSign.dosign(saveTempFile+filename,SIGNTempFile+filename);                	  
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally
				{
					File delData = new File(saveTempFile+filename); //刪除原始傳入的檔案
					delData.delete();
				}
                // 回傳一個檔案
                sendResponse(exchange, SIGNTempFile+filename);
                {
                	File delData = new File(SIGNTempFile+filename);//刪除轉換後檔案
    				delData.delete();
                }
            } else {
                // 如果不是POST請求，回傳405 Method Not Allowed
                exchange.sendResponseHeaders(405, -1);
            }
        }

        private void saveFile(InputStream input,String filePath,String filename) throws IOException {
            // 儲存檔案的路徑
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
                exchange.sendResponseHeaders(404, -1); // 如果檔案不存在，回傳404 Not Found
            }
        }
    }
}
