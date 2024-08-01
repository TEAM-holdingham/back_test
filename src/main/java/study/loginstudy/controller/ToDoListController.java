package study.loginstudy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.service.ToDoListService;


import java.util.List;

@Controller
public class ToDoListController {

    @Autowired
    private ToDoListService toDoListService;

    @GetMapping("/todolist")
    public String viewHomePage(Model model) {
        List<ToDoList> listToDoLists = toDoListService.getAllToDoLists();
        model.addAttribute("listToDoLists", listToDoLists);
        ToDoList toDoList = new ToDoList();
        model.addAttribute("toDoList", toDoList);
        return "todolist";
    }

    @PostMapping("/save")
    public String saveToDoList(@ModelAttribute("toDoList") ToDoList toDoList) {
        toDoListService.addOrUpdateToDoList(toDoList);
        return "redirect:/todolist";
    }

    @GetMapping("/delete/{id}")
    public String deleteToDoList(@PathVariable("id") Long id) {
        toDoListService.deleteToDoListById(id);
        return "redirect:/todolist";
    }

    @GetMapping("/complete/{id}")
    public String completeToDoList(@PathVariable("id") Long id) {
        toDoListService.markAsCompleted(id);
        return "redirect:/todolist";
    }
}
