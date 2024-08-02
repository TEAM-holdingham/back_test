package study.loginstudy.controller;

<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.entity.ToDoList;
<<<<<<< HEAD
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.ToDoListService;
import study.loginstudy.service.UserService;
=======
import study.loginstudy.service.ToDoListService;

>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13

import java.util.List;

@Controller
public class ToDoListController {

<<<<<<< HEAD
    private final ToDoListService toDoListService;
    private final UserService userService; // UserService 주입

    public ToDoListController(ToDoListService toDoListService, UserService userService) {
        this.toDoListService = toDoListService;
        this.userService = userService;
    }

    @GetMapping("/todolist")
    public String viewHomePage(Model model) {
        User user = userService.getCurrentUser();  // 현재 사용자 가져오기
        List<ToDoList> listToDoLists = toDoListService.getAllToDoListsByUser(user);
=======
    @Autowired
    private ToDoListService toDoListService;

    @GetMapping("/todolist")
    public String viewHomePage(Model model) {
        List<ToDoList> listToDoLists = toDoListService.getAllToDoLists();
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
        model.addAttribute("listToDoLists", listToDoLists);
        ToDoList toDoList = new ToDoList();
        model.addAttribute("toDoList", toDoList);
        return "todolist";
    }

    @PostMapping("/save")
    public String saveToDoList(@ModelAttribute("toDoList") ToDoList toDoList) {
<<<<<<< HEAD
        User user = userService.getCurrentUser();  // 현재 사용자 가져오기
        toDoList.setUser(user);  // 현재 사용자와 할 일 연결
=======
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
        toDoListService.addOrUpdateToDoList(toDoList);
        return "redirect:/todolist";
    }

    @GetMapping("/delete/{id}")
    public String deleteToDoList(@PathVariable("id") Long id) {
<<<<<<< HEAD
        User user = userService.getCurrentUser();
        toDoListService.deleteToDoListByIdAndUser(id, user);
=======
        toDoListService.deleteToDoListById(id);
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
        return "redirect:/todolist";
    }

    @GetMapping("/complete/{id}")
    public String completeToDoList(@PathVariable("id") Long id) {
<<<<<<< HEAD
        User user = userService.getCurrentUser();
        toDoListService.markAsCompletedByUser(id, user);
=======
        toDoListService.markAsCompleted(id);
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
        return "redirect:/todolist";
    }
}
