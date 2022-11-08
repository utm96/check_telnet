import org.apache.commons.net.telnet.TelnetClient;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class TelnetClientCommon {

    public static void main(String[] args) throws IOException {
//        URL resource = TelnetClientCommon.class.getClassLoader().getResource("listIp.txt");
//        System.out.println(resource.toString());
        try {
            List<TelnetResult> results = Files.lines(Paths.get("/u01/ipms/process/check_telnet/listIp.txt")).collect(Collectors.toList()).parallelStream().map(line -> createInput(line)).map(input -> checkTelnet(input.getIp(), input.getDeviceCode())).collect(Collectors.toList());
            printToFile(results);

        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

    public static TelnetResult checkTelnet(String ip, String deviceCode) {
        System.out.println("Start check: " + ip + " - " + deviceCode);
        TelnetClient telnet = new TelnetClient();
        telnet.setConnectTimeout(5000);
        try {
            telnet.connect(ip, 23);
            disconnect(telnet);
            return new TelnetResult(deviceCode, ip, "OK");
        } catch (Exception e) {
            return new TelnetResult(deviceCode, ip, "NOK");
        }
    }

    public static TelnetResult createInput(String line) {

        String[] input = line.split("\\s+");
        return new TelnetResult(input[0], input[1]);
    }

    public static void disconnect(TelnetClient telnetClient) {
        try {
            telnetClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printToFile(List<TelnetResult> finalResults) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Result");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        Row rowhead = sheet.createRow((short) 0);
        rowhead.createCell(0).setCellValue("Device_code");
        rowhead.createCell(1).setCellValue("Ip");
        rowhead.createCell(2).setCellValue("Status");

        int i = 1;
        for (TelnetResult finalResult : finalResults) {
            Row newRow = sheet.createRow(i);
            newRow.createCell(0).setCellValue(finalResult.getDeviceCode());
            newRow.createCell(1).setCellValue(finalResult.getIp());
            newRow.createCell(2).setCellValue(finalResult.getStatus());
            i++;
        }
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String outputPath = "telnetCheck" + simpleDateFormat.format(new Date()) + ".xlsx";
            FileOutputStream fileOut = new FileOutputStream(outputPath);
            workbook.write(fileOut);
//closing the Stream
            fileOut.close();
//closing the workbook
            workbook.close();
        } catch (Exception e) {

        }
        System.out.println("end");
    }


}