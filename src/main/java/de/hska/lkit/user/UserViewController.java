package de.hska.lkit.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class UserViewController {

    @RequestMapping(value = "/user")
    public String showUserView() {
        return "user";
    }
}

