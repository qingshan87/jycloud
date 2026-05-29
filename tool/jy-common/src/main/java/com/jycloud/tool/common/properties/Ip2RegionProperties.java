package com.jycloud.tool.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Lqs
 * @date 2025/3/7 11:14
 */
@Data
@Component
@ConfigurationProperties(prefix = "sys.ip2region")
public class Ip2RegionProperties {

    private String path;


}
