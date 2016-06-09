INSERT INTO course_type (id, name) VALUES
  (1, 'CourseTypeA'),
  (2, 'CourseTypeB'),
  (3, 'CourseTypeC');

INSERT INTO course (id, name, course_type_id) VALUES
  (1, 'CourseA-1', 1),
  (2, 'CourseA-2', 1),
  (3, 'CourseA-3', 1),
  (4, 'CourseB-1', 2);

INSERT INTO lesson (id, name, course_id) VALUES
  (1, 'LessonA-1-a', 1),
  (2, 'LessonA-1-b', 1),
  (3, 'LessonA-1-c', 1),
  (4, 'LessonA-2-a', 2),
  (5, 'LessonA-2-b', 2),
  (6, 'LessonB-1-a', 4),
  (7, 'LessonB-1-b', 4);
