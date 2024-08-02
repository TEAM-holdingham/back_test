package study.loginstudy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.domain.entity.User;
import study.loginstudy.service.ToDoListService;
import study.loginstudy.service.UserService;

import java.util.List;

@Controller
public class ToDoListController {

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
        model.addAttribute("listToDoLists", listToDoLists);
        ToDoList toDoList = new ToDoList();
        model.addAttribute("toDoList", toDoList);
        return "todolist";
    }

    @PostMapping("/save")
    public String saveToDoList(@ModelAttribute("toDoList") ToDoList toDoList) {
        User user = userService.getCurrentUser();  // 현재 사용자 가져오기
        toDoList.setUser(user);  // 현재 사용자와 할 일 연결
        toDoListService.addOrUpdateToDoList(toDoList);
        return "redirect:/todolist";
    }

    @GetMapping("/delete/{id}")
    public String deleteToDoList(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        toDoListService.deleteToDoListByIdAndUser(id, user);
        return "redirect:/todolist";
    }

    @GetMapping("/complete/{id}")
    public String completeToDoList(@PathVariable("id") Long id) {
        User user = userService.getCurrentUser();
        toDoListService.markAsCompletedByUser(id, user);
        return "redirect:/todolist";
    }
}
