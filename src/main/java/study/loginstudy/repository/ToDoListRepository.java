package study.loginstudy.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import study.loginstudy.domain.entity.ToDoList;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {
}
