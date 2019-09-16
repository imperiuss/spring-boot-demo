package com.pcz.controller;

import cn.hutool.core.lang.Dict;
import com.pcz.property.ApplicationProperty;
import com.pcz.property.DeveloperProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author picongzhi
 */
@RestController
public class PropertyController {
    private final ApplicationProperty applicationProperty;
    private final DeveloperProperty developerProperty;

    @Autowired
    public PropertyController(ApplicationProperty applicationProperty, DeveloperProperty developerProperty) {
        this.applicationProperty = applicationProperty;
        this.developerProperty = developerProperty;
    }

    @GetMapping("/property")
    public Dict index() {
        return Dict.create()
                .set("applicationProperty", applicationProperty)
                .set("developerProperty", developerProperty);
    }
}
