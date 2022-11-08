import org.apache.commons.net.telnet.TelnetClient;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CheckSnmp {

    public static void main(String[] args) throws IOException {
//        URL resource = TelnetClientCommon.class.getClassLoader().getResource("listDeviceSnmpTest.txt");
//        System.out.println(resource.toString());
        String path = "/u01/ipms/process/check_telnet/listDeviceSnmp.txt";
        if (args.length > 0) {
            path = args[0];
        }
        try {
//            List<TelnetResult> results = Files.lines(Paths.get("/u01/ipms/process/check_telnet/listIp.txt")).collect(Collectors.toList()).parallelStream().map(line -> createInput(line)).map(input -> checkTelnet(input.getIp(), input.getDeviceCode())).collect(Collectors.toList());
            List<SnmpInput> results = Files.lines(Paths.get(path)).collect(Collectors.toList()).parallelStream().map(line -> createInput(line))
                    .filter(x -> x != null)
                    .map(input -> checkSnmp(input.getIp(), input.getDeviceCode(), input.getSnmpCommunity())).collect(Collectors.toList());
            printToFile(results);

        } catch (Exception e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

    public static SnmpInput checkSnmp(String ip, String deviceCode, String community) {
        System.out.println("Start check: " + ip + " - " + deviceCode);
        Snmp snmpClient = null;
        try {
            snmpClient = SnmpClient.createSnmpClient();
        } catch (IOException e) {
            return new SnmpInput(deviceCode, ip, community, null);
        }
        CommunityTarget target = SnmpClient.createConfig(ip, community, 1);
//        telnet.setConnectTimeout(5000);
        try {
            String desc = SnmpClient.getDesrc(snmpClient, target, "1.3.6.1.2.1.1.1");
//            disconnect(telnet);
            return new SnmpInput(deviceCode, ip, community, desc);
        } catch (Exception e) {
            return new SnmpInput(deviceCode, ip, community, null);
        } finally {
            if (snmpClient != null) {
                try {
                    snmpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static SnmpInput createInput(String line) {
        try {
            String[] input = line.split("\\s+");
            SnmpInput snmp = new SnmpInput(input[0], input[1], input[2]);
            return snmp;
        } catch (Exception e) {
            return null;
        }
    }

    public static void disconnect(TelnetClient telnetClient) {
        try {
            telnetClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printToFile(List<SnmpInput> finalResults) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Result");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 4000);
        Row rowhead = sheet.createRow((short) 0);
        rowhead.createCell(0).setCellValue("Device_code");
        rowhead.createCell(1).setCellValue("Ip");
        rowhead.createCell(2).setCellValue("Desc");

        int i = 1;
        for (SnmpInput finalResult : finalResults) {
            Row newRow = sheet.createRow(i);
            newRow.createCell(0).setCellValue(finalResult.getDeviceCode());
            newRow.createCell(1).setCellValue(finalResult.getIp());
            newRow.createCell(2).setCellValue(finalResult.getStatus());
            i++;
        }
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String outputPath = "snmpCheck" + simpleDateFormat.format(new Date()) + ".xlsx";
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