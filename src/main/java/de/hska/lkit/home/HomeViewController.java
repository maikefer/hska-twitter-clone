package de.hska.lkit.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class HomeViewController {

    @RequestMapping(value = "/")
    public String showHomeView() {
        return "home";
    }
}

