package com.jycloud.web.user.support;

import com.jycloud.api.log.constant.LogTypeEnum;
import com.jycloud.api.log.entity.LoginLog;
import com.jycloud.service.auth.dto.AuthDTO;
import com.jycloud.service.auth.vo.AuthInfo;
import com.jycloud.service.user.service.LoginLogService;
import com.jycloud.tool.common.constant.TenantConstant;
import com.jycloud.tool.common.util.AddressUtil;
import com.jycloud.tool.common.util.IpUtil;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import eu.bitwalker.useragentutils.Version;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 登录日志记录器：统一组装登录结果、租户、用户、IP、地点与终端信息。
 *
 * <p>日志写入属于旁路能力，任何采集或保存异常都不得影响登录主流程。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginLogRecorder {

    private static final Pattern WECHAT_VERSION = Pattern.compile("MicroMessenger/([\\d.]+)");
    private static final Pattern EDGE_VERSION = Pattern.compile("Edg(?:e|A|iOS)?/([\\d.]+)");
    private static final Pattern OPERA_VERSION = Pattern.compile("(?:OPR|Opera)/([\\d.]+)");
    private static final Pattern FIREFOX_VERSION = Pattern.compile("(?:Firefox|FxiOS)/([\\d.]+)");
    private static final Pattern CHROME_VERSION = Pattern.compile("(?:Chrome|CriOS)/([\\d.]+)");
    private static final Pattern SAFARI_VERSION = Pattern.compile("Version/([\\d.]+).*Safari");
    private static final Pattern ANDROID_VERSION = Pattern.compile("Android[ /]([\\d.]+)");
    private static final Pattern IOS_VERSION = Pattern.compile("(?:CPU (?:iPhone )?OS|OS) ([\\d_]+)");
    private static final Pattern MAC_VERSION = Pattern.compile("Mac OS X ([\\d_]+)");
    private static final Map<String, String> WINDOWS_VERSIONS = windowsVersions();

    private final LoginLogService loginLogService;
    private final AddressUtil addressUtil;

    /**
     * 记录一次登录结果。
     *
     * @param authDTO  登录请求
     * @param authInfo 登录成功信息，失败时为空
     * @param error    登录失败异常，成功时为空
     */
    public void record(AuthDTO authDTO, AuthInfo authInfo, Throwable error) {
        try {
            loginLogService.save(buildLoginLog(authDTO, authInfo, error));
        } catch (Exception logError) {
            log.warn("登录日志写入失败", logError);
        }
    }

    private LoginLog buildLoginLog(AuthDTO authDTO, AuthInfo authInfo, Throwable error) {
        LoginLog loginLog = new LoginLog()
                .setGrantType(authDTO.getGrantType())
                .setLoginTime(LocalDateTime.now());

        if (authInfo != null && authInfo.getUser() != null) {
            loginLog.setType(LogTypeEnum.SUCCESS.getCode())
                    .setUserId(authInfo.getUser().getUserId())
                    .setUsername(authInfo.getUser().getUsername())
                    .setTenantId(StringUtils.defaultIfBlank(
                            StringUtils.defaultIfBlank(authInfo.getTenantId(), authDTO.getTenantId()),
                            TenantConstant.DEFAULT_TENANT_ID))
                    .setDescription("登录成功");
        } else {
            String errorMessage = error == null ? null : error.getMessage();
            loginLog.setType(LogTypeEnum.FAIL.getCode())
                    .setUserId(0L)
                    .setUsername(authDTO.getUsername())
                    .setTenantId(StringUtils.defaultIfBlank(authDTO.getTenantId(), TenantConstant.DEFAULT_TENANT_ID))
                    .setDescription(StringUtils.substring(
                            StringUtils.defaultIfBlank(errorMessage, "登录失败"), 0, 500));
        }

        HttpServletRequest request = currentRequest();
        String ip = IpUtil.getClientIp(request);
        loginLog.setIp(ip);
        if (StringUtils.isNotBlank(ip)) {
            loginLog.setLocation(StringUtils.trimToNull(addressUtil.getRegion(ip)));
        }

        String ua = request == null ? null
                : StringUtils.substring(request.getHeader(HttpHeaders.USER_AGENT), 0, 500);
        loginLog.setUa(ua);
        fillUserAgent(loginLog, ua);
        return loginLog;
    }

    /**
     * 解析浏览器和操作系统信息。
     */
    private static void fillUserAgent(LoginLog loginLog, String ua) {
        if (StringUtils.isBlank(ua)) {
            return;
        }

        BrowserInfo browserInfo = detectBrowser(ua);
        loginLog.setBrowser(browserInfo.name());
        loginLog.setBrowserVersion(browserInfo.version());
        loginLog.setOperatingSystem(detectOperatingSystem(ua));
    }

    /**
     * 主流浏览器优先使用明确版本规则，未识别时回退 UserAgentUtils。
     */
    private static BrowserInfo detectBrowser(String ua) {
        if (ua.contains("MicroMessenger/")) {
            return browserInfo("微信", WECHAT_VERSION, ua);
        }
        if (ua.contains("Edg/") || ua.contains("Edge/") || ua.contains("EdgiOS/")) {
            return browserInfo("Edge", EDGE_VERSION, ua);
        }
        if (ua.contains("OPR/") || ua.contains("Opera/")) {
            return browserInfo("Opera", OPERA_VERSION, ua);
        }
        if (ua.contains("Firefox/") || ua.contains("FxiOS/")) {
            return browserInfo("Firefox", FIREFOX_VERSION, ua);
        }
        if (ua.contains("Chrome/") || ua.contains("CriOS/")) {
            return browserInfo("Chrome", CHROME_VERSION, ua);
        }
        if (ua.contains("Safari/") && SAFARI_VERSION.matcher(ua).find()) {
            return browserInfo("Safari", SAFARI_VERSION, ua);
        }
        UserAgent userAgent = UserAgent.parseUserAgentString(ua);
        Browser browser = userAgent.getBrowser();
        Version browserVersion = userAgent.getBrowserVersion();
        return new BrowserInfo(
                browser == null ? null : browser.getBrowserType().getName(),
                browserVersion == null ? null : browserVersion.getVersion());
    }

    private static BrowserInfo browserInfo(String name, Pattern versionPattern, String ua) {
        Matcher matcher = versionPattern.matcher(ua);
        return new BrowserInfo(name, matcher.find() ? matcher.group(1) : null);
    }

    /**
     * 解析常见操作系统，无法识别时回退 UserAgentUtils。
     */
    private static String detectOperatingSystem(String ua) {
        for (Map.Entry<String, String> entry : WINDOWS_VERSIONS.entrySet()) {
            if (ua.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        if (ua.contains("Android")) {
            return appendVersion("Android", ANDROID_VERSION, ua);
        }
        if (ua.contains("iPhone") || ua.contains("iPad") || ua.contains("iPod")) {
            return appendVersion("iOS", IOS_VERSION, ua);
        }
        if (ua.contains("Mac OS X")) {
            return appendVersion("macOS", MAC_VERSION, ua);
        }
        if (ua.contains("Linux")) {
            return "Linux";
        }

        UserAgent userAgent = UserAgent.parseUserAgentString(ua);
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        return operatingSystem == null ? null : operatingSystem.getName();
    }

    private static String appendVersion(String name, Pattern versionPattern, String ua) {
        Matcher matcher = versionPattern.matcher(ua);
        return matcher.find() ? name + " " + matcher.group(1).replace('_', '.') : name;
    }

    private static Map<String, String> windowsVersions() {
        Map<String, String> versions = new LinkedHashMap<>();
        versions.put("Windows NT 10.0", "Windows 10/11");
        versions.put("Windows NT 6.3", "Windows 8.1");
        versions.put("Windows NT 6.2", "Windows 8");
        versions.put("Windows NT 6.1", "Windows 7");
        versions.put("Windows NT 6.0", "Windows Vista");
        versions.put("Windows NT 5.1", "Windows XP");
        versions.put("Windows", "Windows");
        return Collections.unmodifiableMap(versions);
    }

    private static HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes servletAttributes ? servletAttributes.getRequest() : null;
    }

    private record BrowserInfo(String name, String version) {
    }
}
