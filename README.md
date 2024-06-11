提供使用JAVA的程式,使用itextpdf 5.1.3進行PDF的加簽功能,
單獨使用Server方式，以免專案使用JAR依賴問題
HomePage	http://www.itextpdf.com/
注意Licenses為GNU Affero General Public License v3
http://www.fsf.org/licensing/licenses/agpl-3.0.html

javac -cp itextpdf-5.1.3.jar;pdfbox-2.0.2.jar PDFSign.java
java -cp .;itextpdf-5.1.3.jar;bcprov-jdk15-1.46.jar;pdfbox-2.0.2.jar;commons-logging.jar;fontbox-2.0.2.jar pd
fsignConversion
