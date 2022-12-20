package org.mentpeak.swagger.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author lxp
 * @date 2022/05/17 14:47
 **/
@RestController
public class IndexController {
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView saveUser( ModelAndView model) throws Exception {
        model.setViewName("redirect:doc.html");
        return model;
    }
}
