package study.loginstudy.repository;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.loginstudy.domain.entity.ToDoList;
import study.loginstudy.domain.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {
    List<ToDoList> findAllByUser(User user);
    Optional<ToDoList> findByIdAndUser(Long id, User user);
=======

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.loginstudy.domain.entity.ToDoList;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
}
