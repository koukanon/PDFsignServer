提供使用JAVA的程式,使用itextpdf 5.1.3進行PDF的加簽功能,
單獨使用Server方式，與專案程式分離，以免專案使用JAR依賴問題
本PDF加簽程式使用
itextpdf-5.1.3.jar
bcprov-jdk15-1.46.jar
pdfbox-2.0.2.jar
fontbox-2.0.2.jar
commons-logging.jar
PDFSign.JAVA為主要加簽程式
pdfsignConversion.JAVA為單獨使用Server
PDFSIGNUploadClient為執行範例

HomePage	http://www.itextpdf.com/
注意itextpdf使用的授權版本為Licenses為GNU Affero General Public License v3
http://www.fsf.org/licensing/licenses/agpl-3.0.html

JAVA編譯及執行方式如下
javac -cp itextpdf-5.1.3.jar;pdfbox-2.0.2.jar PDFSign.java
java -cp .;itextpdf-5.1.3.jar;bcprov-jdk15-1.46.jar;pdfbox-2.0.2.jar;commons-logging.jar;fontbox-2.0.2.jar pd
fsignConversion
