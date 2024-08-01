package study.loginstudy.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.repository.ToDoListRepository;

@Service
public class ToDoListService {

    @Autowired
    private ToDoListRepository toDoListRepository;

    public List<ToDoList> getAllToDoLists() {
        return toDoListRepository.findAll();
    }

    public Optional<ToDoList> getToDoListById(Long id) {
        return toDoListRepository.findById(id);
    }

    public ToDoList addOrUpdateToDoList(ToDoList toDoList) {
        return toDoListRepository.save(toDoList);
    }

    public void deleteToDoListById(Long id) {
        toDoListRepository.deleteById(id);
    }

    public void markAsCompleted(Long id) {
        Optional<ToDoList> optionalToDoList = toDoListRepository.findById(id);
        if (optionalToDoList.isPresent()) {
            ToDoList toDoList = optionalToDoList.get();
            toDoList.setCompleted("Yes");
            toDoListRepository.save(toDoList);
        }
    }
}
