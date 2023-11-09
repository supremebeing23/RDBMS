package mongodb.mongolabs.api;

import mongodb.mongolabs.persistence.entity.Student;
import mongodb.mongolabs.persistence.repository.StudentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<String> getStudentNames() {
        return studentRepository.findAll().stream().map(Student::getName).collect(Collectors.toList());
    }
}
