
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;

public class PDFSign
{	
	public static void main(String[] args)
	{
		sign( "D:/未加簽/630A5905141002310188UN.PDF","D:/加簽/630A5905141002310188.pdf");
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