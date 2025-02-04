package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.Course;
import com.lambdaschool.school.model.ErrorDetail;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.StudentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{
    @Autowired
    private StudentService studentService;

    // Please note there is no way to add students to course yet!
    @ApiOperation(value = "Retrieves list of all students, default 3 students per page", response=Student.class, responseContainer = "List")
    @ApiResponses(value={
            @ApiResponse(code=200, message="List of students found", response = Student.class, responseContainer = "List"),
            @ApiResponse(code=500, message="Internal Server Error", response = ErrorDetail.class)
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", dataType = "integr", paramType = "query", value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(name = "size", dataType = "integer", paramType = "query", value = "Number of records per page."),
            @ApiImplicitParam(name = "sort", allowMultiple = true, dataType = "string", paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). Default sort order is ascending. Multiple sort criteria are supported.")})
    @GetMapping(value = "/students", produces = {"application/json"})
    public ResponseEntity<?> listAllStudents(@PageableDefault(page=0, size=3)Pageable pageable)
    {
        List<Student> myStudents = studentService.findAll(pageable);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }
    @ApiOperation(value = "Retrieves Student with matching id", response=Student.class)
    @ApiResponses(value={
            @ApiResponse(code=200, message="Student with given id found", response = Course.class, responseContainer = "List"),
            @ApiResponse(code=500, message="Internal Server Error", response = ErrorDetail.class),
            @ApiResponse(code=404, message="Student with specified id not found", response = ErrorDetail.class),

    })
    @GetMapping(value = "/Student/{StudentId}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentById(
            @PathVariable
                    Long StudentId)
    {
        Student r = studentService.findStudentById(StudentId);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @ApiOperation(value = "Retrieves  list of all students with name containing the supplied parameter", response=Student.class, responseContainer = "List")
    @ApiResponses(value={
            @ApiResponse(code=200, message="Student/student list ", response = Student.class, responseContainer = "List"),
            @ApiResponse(code=500, message="Internal Server Error", response = ErrorDetail.class),
            @ApiResponse(code=404, message=" not found", response = ErrorDetail.class),

    })
    @GetMapping(value = "/student/namelike/{name}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentByNameContaining(
            @ApiParam(value=" name", required = true, example= "erko") @PathVariable String name)
    {
        List<Student> myStudents = studentService.findStudentByNameLike(name);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }

    @ApiOperation(value = "Creates a new student", response=Student.class)
    @ApiResponses(value={
            @ApiResponse(code=201, message="Student added successfully", response = Student.class, responseContainer = "List"),
            @ApiResponse(code=500, message="Internal Server Error", response = ErrorDetail.class),


    })
    @PostMapping(value = "/Student",
                 consumes = {"application/json"},
                 produces = {"application/json"})
    public ResponseEntity<?> addNewStudent(@Valid
                                           @RequestBody
                                                   Student newStudent) throws URISyntaxException
    {
        newStudent = studentService.save(newStudent);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newStudentURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{Studentid}").buildAndExpand(newStudent.getStudid()).toUri();
        responseHeaders.setLocation(newStudentURI);

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }

    @ApiOperation(value = "Updates student with given id", response=Student.class)
    @ApiResponses(value={
            @ApiResponse(code=202, message="Changes Accepted", response = Student.class, responseContainer = "List"),
            @ApiResponse(code=500, message="Internal Server Error", response = ErrorDetail.class),
            @ApiResponse(code=404, message=" student with givn id not found", response = ErrorDetail.class),

    })
    @PutMapping(value = "/Student/{Studentid}")
    public ResponseEntity<?> updateStudent(
            @ApiParam(value=" Student id", required = true , example ="12")
            @RequestBody
                    Student updateStudent,
            @PathVariable
                    long Studentid)
    {
        studentService.update(updateStudent, Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Deletes student with given id")
    @ApiResponses(value={
            @ApiResponse(code=200, message="deleted successfully", response = Student.class, responseContainer = "List"),
            @ApiResponse(code=500, message="Internal Server Error", response = ErrorDetail.class),
            @ApiResponse(code=404, message=" student with given id not found", response = ErrorDetail.class),

    })
    @DeleteMapping("/Student/{Studentid}")
    public ResponseEntity<?> deleteStudentById(
            @ApiParam(value=" Student id", required = true, example ="12")
            @PathVariable
                    long Studentid)
    {
        studentService.delete(Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
