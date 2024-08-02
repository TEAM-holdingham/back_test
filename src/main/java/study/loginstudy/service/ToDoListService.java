package study.loginstudy.service;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.domain.entity.User;
import study.loginstudy.repository.ToDoListRepository;
=======
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13

import java.util.List;
import java.util.Optional;

<<<<<<< HEAD
@Service
public class ToDoListService {

    private final ToDoListRepository toDoListRepository;

    @Autowired
    public ToDoListService(ToDoListRepository toDoListRepository) {
        this.toDoListRepository = toDoListRepository;
    }

    public List<ToDoList> getAllToDoListsByUser(User user) {
        return toDoListRepository.findAllByUser(user);
    }

    public void addOrUpdateToDoList(ToDoList toDoList) {
        toDoListRepository.save(toDoList);
    }

    public void deleteToDoListByIdAndUser(Long id, User user) {
        Optional<ToDoList> toDoList = toDoListRepository.findByIdAndUser(id, user);
        toDoList.ifPresent(toDoListRepository::delete);
    }

    public void markAsCompletedByUser(Long id, User user) {
        Optional<ToDoList> toDoList = toDoListRepository.findByIdAndUser(id, user);
        toDoList.ifPresent(list -> {
            list.setCompleted("true");
            toDoListRepository.save(list);
        });
=======
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
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
    }
}
