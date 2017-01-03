package de.hska.lkit.home;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.hska.lkit.redis.model.Post;
import de.hska.lkit.redis.model.User;
import de.hska.lkit.redis.repo.PostRepository;
import de.hska.lkit.redis.repo.UserRepository;
import de.hska.lkit.search.SearchTerm;
import de.hska.lkit.sessions.SessionSecurity;


/**
 * Created by bob on 19/10/2016.
 */


@Controller
public class HomeViewController {

    private final int maxAmountPostsPerPage = 6;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @RequestMapping(value = "/")
    public String showHomeView(Model model) {
        return showHomeOnGlobalPage(0, model);
    }

    @RequestMapping (value= "/global/{page}")
    public String showHomeOnGlobalPage(@PathVariable("page") int page, Model model) {
        return showHomeWithPagination(page, -1, model);
    }

    @RequestMapping(value = "/global/previous/{page}")
    public String showHomeOnGlobalPrevious(@PathVariable("page") int page, Model model) {
        return "redirect:/global/" + (page - 2);
    }

    @RequestMapping (value= "/private/{page}")
    public String showHomeOnPrivatePage(@PathVariable("page") int page, Model model) {
        return showHomeWithPagination(-1, page, model);
    }

    @RequestMapping(value = "/private/previous/{page}")
    public String showHomeOnPrivatePrevious(@PathVariable("page") int page, Model model) {
        return "redirect:/private/" + (page - 2);
    }

    public String showHomeWithPagination(int pageGlobal, int pagePrivate, Model model) {
        //determine which tab is active
        if (pagePrivate == -1 ) {
            model.addAttribute("classTabGlobal", "mdl-tabs__tab is-active");
            model.addAttribute("classTabPrivate", "mdl-tabs__tab");
            model.addAttribute("classPanelPrivate","mdl-tabs__panel" );
            model.addAttribute("classPanelGlobal", "mdl-tabs__panel is-active");
        } else if (pageGlobal == -1){
            model.addAttribute("classTabPrivate", "mdl-tabs__tab is-active");
            model.addAttribute("classTabGlobal", "mdl-tabs__tab");
            model.addAttribute("classPanelGlobal","mdl-tabs__panel" );
            model.addAttribute("classPanelPrivate", "mdl-tabs__panel is-active");
        }

        // Global Stuff to make it work
        User user = userRepository.findUser(SessionSecurity.getName());
        model.addAttribute("user", user);
        model.addAttribute("currentUser", user);
        model.addAttribute("isSelf", true);
        model.addAttribute("searchTerm", new SearchTerm());

        // Followers Count
        model.addAttribute("followingCnt", userRepository.findFollowers(user.getUsername()).size());
        model.addAttribute("followerCnt", userRepository.findFollowing(user.getUsername()).size());


        // Pagination Global
        showGlobalPostsWithPagination(pageGlobal, model);

        // Pagination Private
        showPrivatePostsWithPagination(pagePrivate, model, user);

        return "home";
    }

    private void showGlobalPostsWithPagination(int pageGlobal, Model model) {
        if (pageGlobal == -1) {
            pageGlobal = 0;
        }
        model.addAttribute("nextPageGlobalNo", pageGlobal + 1);
        if (pageGlobal <= 0) {
            // hide previous
            model.addAttribute("displayPrevGlobal", "display:none");
        } else {
            model.addAttribute("displayPrevGlobal", "display:inline");
        }

        Long amountPostsGlobal = postRepository.findAllPostsSize();
        long maxPageNumberGlobal = amountPostsGlobal / maxAmountPostsPerPage;
        if (pageGlobal == maxPageNumberGlobal || amountPostsGlobal == maxAmountPostsPerPage) {
            // hide next
            model.addAttribute("displayNextGlobal", "display:none");
        } else {
            model.addAttribute("displayNextGlobal", "display:inline");
        }

        int firstPostGlobal = pageGlobal * maxAmountPostsPerPage;
        int countGlobal = maxAmountPostsPerPage - 1;
        List<Post> globalPosts = postRepository.findAllPostsPaged(firstPostGlobal, countGlobal);
        model.addAttribute("PostListGlobal", globalPosts);
    }

    private void showPrivatePostsWithPagination(int pagePrivate, Model model, User user) {
        if (pagePrivate == -1) {
            pagePrivate = 0;
        }

        model.addAttribute("nextPagePrivateNo", pagePrivate + 1);
        if (pagePrivate == 0) {
            // hide previous
            model.addAttribute("displayPrevPrivate", "display:none");
        } else {
            model.addAttribute("displayPrevPrivate", "display:inline");
        }

        Long amountPostsPrivate = postRepository.timelineOfUserSize(user.getUsername());
        long maxPageNumberPrivate = amountPostsPrivate / maxAmountPostsPerPage;
        if (pagePrivate == maxPageNumberPrivate || amountPostsPrivate == maxAmountPostsPerPage) {
            // hide next
            model.addAttribute("displayNextPrivate", "display:none");
        } else {
            model.addAttribute("displayNextPrivate", "display:inline");
        }

        int firstPostPrivate = pagePrivate * maxAmountPostsPerPage;
        int countPrivate = maxAmountPostsPerPage - 1;
        List<Post> privatePosts = postRepository.timelineOfUserPaged(user.getUsername(), firstPostPrivate, countPrivate);
        model.addAttribute("PostListPrivate", privatePosts);
    }

}
