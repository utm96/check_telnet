//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnmpClient {
    public static final String DEFAULT_PROTOCOL = "udp";
    public static final int DEFAULT_PORT = 161;
    public static final long DEFAULT_TIMEOUT = 5000L;
    public static final int DEFAULT_RETRY = 0;
//    public static final Logger logger = LoggerFactory.getLogger(SnmpClient.class.getName());

    public static CommunityTarget createConfig(String ip, String community, int version) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip + "/" + DEFAULT_PORT);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(version);
        target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
        target.setRetries(DEFAULT_RETRY);
        return target;
    }

    public static Snmp createSnmpClient() throws IOException {
        DefaultUdpTransportMapping transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        snmp.listen();
        return snmp;
    }

    public static PDU snmpGet(CommunityTarget target, Snmp snmp, String oid) {
        PDU response = null;
        try {
            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            response = respEvent.getResponse();
        } catch (Exception e) {
            System.out.println("snmpGet error" + e.getMessage());
        } finally {
//            if (snmp != null) {
//                try {
//                    snmp.close();
//                } catch (IOException ex1) {
//                    snmp = null;
//                }
//            }

        }
        return response;
    }

    public static Map<String, String> snmpGet(CommunityTarget target, Snmp snmp, String... oids) {
        PDU response = null;
        Map<String, String> result = new HashMap<>();
        try {
            PDU pdu = new PDU();
            for (String oid : oids) {
                if (oid != null && !oid.isEmpty()) {
                    pdu.add(new VariableBinding(new OID(oid)));
                }
            }
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            response = respEvent.getResponse();
            if (response != null) {
                for (VariableBinding value : response.getVariableBindings()) {
                    result.put(value.getOid().toString(), value.getVariable().toString());
                }
            }
        } catch (Exception e) {
            System.out.println("snmpGet error: " + oids.toString() + ", community:" + target.getCommunity() + ", ip:" + target.getAddress());
        } finally {
//            if (snmp != null) {
//                try {
//                    snmp.close();
//                } catch (IOException ex1) {
//                    snmp = null;
//                }
//            }

        }
        return result;
    }

    public static Map<String, String> snmpGet(CommunityTarget target, Snmp snmp, List<String> oids) {
        PDU response = null;
        Map<String, String> result = new HashMap<>();
        try {
            PDU pdu = new PDU();
            for (String oid : oids) {
//                if (oid != null && !oid.isEmpty()) {
                pdu.add(new VariableBinding(new OID(oid)));
//                }
            }
            pdu.setType(PDU.GET);
            ResponseEvent respEvent = snmp.send(pdu, target);
            response = respEvent.getResponse();
            for (VariableBinding value : response.getVariableBindings()) {
                result.put(value.getOid().toString(), value.getVariable().toString());
            }

        } catch (Exception e) {
            System.out.println("snmpGet error: " + oids.toString() + ", community:" + target.getCommunity() + ", ip:" + target.getAddress());
        } finally {
//            if (snmp != null) {
//                try {
//                    snmp.close();
//                } catch (IOException ex1) {
//                    snmp = null;
//                }
//            }

        }
        return result;
    }

    public static String getDesrc(Snmp snmp, CommunityTarget target, String targetOid) {
        PDU pduResponse = snmpGet(target, snmp, targetOid);
        if (pduResponse != null) {
            VariableBinding variableBinding = pduResponse.getVariableBindings().get(0);
            if (variableBinding.getVariable() instanceof Null) {
                return null;
            }
            return variableBinding.getVariable().toString();
        } else return null;
    }
//    public static String getDeviceCode(Snmp snmp, CommunityTarget target, String targetOid) {
//        PDU pduResponse = snmpGet(target, snmp, targetOid);
//        if (pduResponse != null) {
//            VariableBinding variableBinding = pduResponse.getVariableBindings().get(0);
//            if (variableBinding.getVariable() instanceof Null) {
//                return null;
//            }
//            return variableBinding.getVariable().toString();
//        } else return null;
//    }

    public static Integer getStatus(Snmp snmp, CommunityTarget target, String targetOid) {
        PDU pduResponse = snmpGet(target, snmp, targetOid);
        if (pduResponse != null) {
            VariableBinding variableBinding = pduResponse.getVariableBindings().get(0);
            return variableBinding.getVariable().toInt();
        } else return null;
    }


//
//    public static CommunityTarget createDefault(String ip, String community) {
//        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + ip
//                + "/" + DEFAULT_PORT);
//        CommunityTarget target = new CommunityTarget();
//        target.setCommunity(new OctetString(community));
//        target.setAddress(address);
//        target.setVersion(DEFAULT_VERSION);
//        target.setTimeout(DEFAULT_TIMEOUT); // milliseconds
//        target.setRetries(DEFAULT_RETRY);
//        return target;
//    }


//    public static void main(String[] args) {
//        String ip = "10.56.32.246";
//        String community = "Viettel2016";
//        String oid = ".1.3.6.1.2.1.31.1.1.1.15";
////        List<String> oids = new ArrayList<>();
//////        String oidval = ".1.3.6.1.2.1.31.1.1.1.18";
//////        String oidval = ".1.3.6.1.2.1.31.1.1?.1.18";
////
////        oids.add(".1.3.6.1.2.1.31.1.1.1.18.4");
////        oids.add(".1.3.6.1.2.1.31.1.1.1.18.1");
////
////
////        List<String> oids1 = new ArrayList<>();
//////        String oidval = ".1.3.6.1.2.1.31.1.1.1.18";
//////        String oidval = ".1.3.6.1.2.1.31.1.1?.1.18";
////
////        oids1.add(".1.3.6.1.2.1.31.1.1.1.18.2");
////        oids1.add(".1.3.6.1.2.1.31.1.1.1.18.3");
//        int number = 10;
//        while (number > 0) {
//            number--;
//            int version = 1;
//            try {
//                Snmp snmp = SnmpClient.createSnmpClient();
//                CommunityTarget target = SnmpClient.createConfig(ip, community, version);
////            System.out.println();
////                Map<String, String> result = SnmpClient.snmpWalk(target, snmp, oid);
//                snmp.close();
//                System.out.println(result.size());
////            System.out.println(count);
//            } catch (IOException e) {
//                System.out.println(e.getMessage());
//            }
//            try {
//                Thread.sleep(30000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }


}