package com.rockbb.sms.commons;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InetAddressUtil {
    private static final String IP_ADDRESS_REGEX = "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})";
    private static final String PRIVATE_IP_ADDRESS_REGEX = "(^127\\.0\\.0\\.1)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)";

    private static final String HOSTNAME_REGEX = "([\\w-]+\\.[\\w-]+\\.[\\w-]+)";

    private static Pattern IP_ADDRESS_PATTERN = Pattern.compile(IP_ADDRESS_REGEX);
    private static Pattern PRIVATE_IP_ADDRESS_PATTERN = Pattern.compile(PRIVATE_IP_ADDRESS_REGEX);
    private static Pattern HOSTNAME_PATTERN = Pattern.compile(IP_ADDRESS_REGEX);

    public static String getAddressFromRequest(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String xRealIp = request.getHeader("X-Real-IP");
        //logger.debug("X-Forwarded-For: {}   X-Real-IP: {}", forwardedFor, xRealIp);
        if (forwardedFor != null && (forwardedFor = findNonPrivateIpAddress(forwardedFor)) != null)
            return forwardedFor;
        return request.getRemoteAddr();
    }

    public static String getServerNameFromRequest(HttpServletRequest request) {
        String forwardHost = request.getHeader("X-Forwarded-Host");
        if (forwardHost != null) {
            String[] hosts = forwardHost.split("\\s*,\\s*");
            return hosts[hosts.length - 1];
        }
        return request.getRemoteAddr();
    }

    public static String getHostnameFromRequest(HttpServletRequest request) {
        String addr = getAddressFromRequest(request);
        try {
            return Inet4Address.getByName(addr).getHostName();
        } catch (Exception e) {
        }
        return addr;
    }

    public static InetAddress getInet4AddressFromRequest(HttpServletRequest request) throws UnknownHostException {
        return Inet4Address.getByName(getAddressFromRequest(request));
    }

    private static String findNonPrivateIpAddress(String s) {
        Matcher matcher = IP_ADDRESS_PATTERN.matcher(s);
        while (matcher.find()) {
            if (!PRIVATE_IP_ADDRESS_PATTERN.matcher(matcher.group(0)).find())
                return matcher.group(0);
            matcher.region(matcher.end(), s.length());
        }
        return null;
    }
}
