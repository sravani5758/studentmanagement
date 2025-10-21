Student Management System – Project Overview

The Student Management System is a Spring Boot backend application that manages students, courses, instructors, and assignments, and calculates CGPA based on student performance.
It simulates a real-world academic system with proper entity relationships and clean REST APIs.

1. Purpose of the Project

Manage students, courses, instructors, and assignments in one system.

Allow students to enroll in courses.

Assign instructors to courses.

Track student performance on assignments.

Calculate CGPA automatically based on grades.

Provide CRUD operations for all entities using REST APIs.

Support soft deletion (records are not permanently removed, just marked inactive).

Enable search with pagination for large datasets.

2. Core Modules
a) Instructor Management

Each instructor is assigned to a course.

Can manage assignments for their course.

Full CRUD operations for instructors.

Soft delete supported — inactive instructors are not removed from database.

b) Course Management

Courses are linked to one instructor.

Students can enroll in courses.

Courses can have multiple assignments.

Tracks enrolled students and their performance.

Soft delete supported — inactive courses cannot have new enrollments.

Search and pagination available for listing courses.

c) Student Management

Students can enroll in multiple courses.

CRUD operations for student records.

CGPA is calculated based on assignment scores.

Soft delete supported — inactive students cannot enroll in new courses.

Search and pagination available for listing students.

d) Assignment Management

Assignments are linked to a specific course.

Grades determine CGPA calculation.

Supports multiple assignments per course.

Soft delete supported — inactive assignments are ignored in CGPA calculation.

3. Workflow
Instructor Assignment

An instructor is assigned to a course.

Student Enrollment

Students enroll in courses of their choice.

Assignment Management

Instructor creates assignments for the course.

Students complete assignments, and grades are recorded.

CGPA Calculation

CGPA is automatically calculated based on the grades of all assignments.

APIs

Expose endpoints for students, instructors, courses, assignments, enrollment, and CGPA retrieval.

Supports soft delete and search with pagination for large data sets.

4. Technical Stack
Technology	Description
Java 17+	Backend language
Spring Boot	Framework for REST APIs and backend logic
Spring Data JPA	Database ORM
MySQL	Relational database
Maven	Build tool
Lombok	Reduce boilerplate code
