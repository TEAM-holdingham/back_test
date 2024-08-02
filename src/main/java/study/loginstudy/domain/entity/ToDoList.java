package study.loginstudy.domain.entity;

import javax.persistence.*;

@Entity
public class ToDoList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    // 변경된 부분: completed를 String 타입으로 수정
    @Column(name = "completed")
    private String completed;

    @Column(name = "study_time")
    private Integer studyTime;

<<<<<<< HEAD
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // User와의 관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

=======
>>>>>>> cc1c588b353a0198d845dbaa746966af04791a13
    // Getter와 Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public Integer getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(Integer studyTime) {
        this.studyTime = studyTime;
    }
}
