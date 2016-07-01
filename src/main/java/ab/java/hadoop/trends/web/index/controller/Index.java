package ab.java.hadoop.trends.web.index.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Index {

    @ResponseBody
    @RequestMapping(value = "/")
    public String helloWorld() {
        return "Hello World";
    }
}