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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter; 

public class PDFSign
{	
	public static void main(String[] args)
	{
		sign( "D:/未加簽/630A5905141002310188UN.PDF","D:/加簽/630A5905141002310188.pdf");
	}
 
	
	public static void dosign(String writeFileName,String writeSignFileName)//加簽
	{
		int pageSize =pdfToImgMerage(writeFileName);//pdf轉為圖片
	    File[] listFile = new File[pageSize-1];			//
	    
	    for(int index=1;index<(pageSize);index++)
	    {
	 	    listFile[index-1]=(new File(writeFileName+"_"+index+".jpg"));
	    }
	    try {
			imgMerageToPdf(listFile, new File(writeFileName+"TEMP.pdf"));
			sign(writeFileName+"TEMP.pdf",writeSignFileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	 public static int pdfToImgMerage(String writeFileName) {
	        int count = 1; // Count variable used to separate each image file

	        PDDocument doc = null;
	        try {
	            doc = PDDocument.load(new File(writeFileName));

	            // Create a PDFRenderer to render the document pages
	            PDFRenderer pdfRenderer = new PDFRenderer(doc);

	            System.out.println("Please wait...");
	            // Loop through each page and convert it to an image
	            for (int i = 0; i < doc.getNumberOfPages(); i++) {
	                BufferedImage bi = pdfRenderer.renderImageWithDPI(i, 170, ImageType.RGB);
	                ImageIO.write(bi, "jpg", new File(writeFileName + "_" + (count++) + ".jpg"));
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (doc != null) {
	                try {
	                    doc.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	        return count;
	    }
	
	//圖片轉pdf
	public static boolean imgMerageToPdf(File[] list, File file)throws Exception {
		Map<Integer,File> mif = new TreeMap<Integer,File>();
		for(int index=0;index<list.length;index++)
		{
			File f=list[index];
			mif.put(index, f);
		}
		//2：拿第一個IMG的寬高為此PDF的格式
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048*3);
		InputStream is = new FileInputStream(mif.get(0));
		for(int len;(len=is.read())!=-1;)
			baos.write(len);
		baos.flush();
		Image image = Image.getInstance(baos.toByteArray());
		float width = image.getWidth();
		float height = image.getHeight();
		baos.close();
		Document document = new Document(new Rectangle(width,height));
		PdfWriter pdfWr = PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		 
		for(Entry<Integer,File> eif : mif.entrySet())
		{
			baos = new ByteArrayOutputStream(2048*3);
			is = new FileInputStream(eif.getValue());
			for(int len;(len=is.read())!=-1;)
				baos.write(len);
			baos.flush();
			image = Image.getInstance(baos.toByteArray());
			Image.getInstance(baos.toByteArray());
			image.setAbsolutePosition(0.0f, 0.0f);
			document.add(image);
			document.newPage();
			baos.close();
		}
		document.close();
		pdfWr.close();
		return true;
	}

	
	public static void sign(String writeFileName,String writeSignFileName)//加簽
	{
	  try {//證書加簽及加浮水印

			FileOutputStream fout = new FileOutputStream(writeSignFileName); //簽章後文件
	  
				KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
				ks.load(new FileInputStream("你的.keystore位置"), "你的keystore密碼".toCharArray());  //
				String alias = "你定義的alias"; 
				PrivateKey key = (PrivateKey) ks.getKey(alias, "你的keystore密碼".toCharArray());						 
		        java.security.cert.Certificate[] chain = ks.getCertificateChain(alias);  
				PdfReader reader = new PdfReader(writeFileName); // 原始文件
				PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0');
				PdfSignatureAppearance sap = stp.getSignatureAppearance();
				sap.setCrypto(key, chain, null, PdfSignatureAppearance.VERISIGN_SIGNED);
				
				sap.setReason("簽名的名稱");//可以是空的
				sap.setLocation("位置訊息"); // 增加位置訊息，可以是空的
				sap.setContact("位置訊息聯絡方式");//可以是空的
				stp.getWriter().setCompressionLevel(5);
				if (stp != null) {
					stp.close();
				}
				if (fout != null) {
					fout.close();
				}
				if (reader != null) {
					reader.close();
				} 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
