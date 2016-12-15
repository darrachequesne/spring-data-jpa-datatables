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

INSERT INTO bill (id, description, amount, hasBeenPayed) VALUES
  (1,  'foo%',  100,  false),
  (2,  'foo_',  200,  true),
  (3,  'foo3',  300,  false),
  (4,  'foo4',  400,  true),
  (5,  'foo5',  500,  false),
  (6,  'foo6',  600,  true),
  (7,  'foo7',  700,  false),
  (8,  'foo8',  800,  true),
  (9,  'foo9',  900,  false),
  (10, 'foo10', 1000, true),
  (11, 'foo11', 1100, false),
  (12, 'foo12', 1200, true),
  (13, 'foo13', 1300, NULL);

INSERT INTO game (id, prize_name) VALUES
  (1,  'prize0'),
  (2,  'prize1'),
  (3,  'prize2'),
  (4,  'prize3'),
  (5,  'prize4'),
  (6,  'prize5'),
  (7,  'prize6'),
  (8,  'prize7'),
  (9,  'prize8'),
  (10, 'prize9'),
  (11, 'prize10'),
  (12, 'prize11');

INSERT INTO home (id, town) VALUES
  (1, 'town0'),
  (2, 'town1'),
  (3, 'town2'),
  (4, 'town3'),
  (5, null),
  (6, 'NULL');

INSERT INTO users (id, username, role, status, id_home, visible) VALUES
  (1,  'john0',  'ADMIN',  'ACTIVE',  null, true),
  (2,  'john1',  'AUTHOR', 'BLOCKED', null, false),
  (3,  'john2',  'USER',   'ACTIVE',  null, true),
  (4,  'john3',  'ADMIN',  'BLOCKED', null, false),
  (5,  'john4',  'AUTHOR', 'ACTIVE',  1,    true),
  (6,  'john5',  'USER',   'BLOCKED', 2,    false),
  (7,  'john6',  'ADMIN',  'ACTIVE',  3,    true),
  (8,  'john7',  'AUTHOR', 'BLOCKED', 4,    false),
  (9,  'john8',  'USER',   'ACTIVE',  1,    true),
  (10, 'john9',  'ADMIN',  'BLOCKED', 2,    false),
  (11, 'john10', 'AUTHOR', 'ACTIVE',  3,    true),
  (12, 'john11', 'USER',   'BLOCKED', 4,    false),
  (13, 'john12', 'ADMIN',  'ACTIVE',  1,    true),
  (14, 'john13', 'AUTHOR', 'BLOCKED', 2,    false),
  (15, 'john14', 'USER',   'ACTIVE',  3,    true),
  (16, 'john15', 'ADMIN',  'BLOCKED', 4,    false),
  (17, 'john16', 'AUTHOR', 'ACTIVE',  1,    true),
  (18, 'john17', 'USER',   'BLOCKED', 2,    false),
  (19, 'john18', 'ADMIN',  'ACTIVE',  3,    true),
  (20, 'john19', 'AUTHOR', 'BLOCKED', 4,    false),
  (21, 'john20', 'USER',   'ACTIVE',  1,    true),
  (22, 'john21', 'ADMIN',  'BLOCKED', 2,    false),
  (23, 'john22', 'AUTHOR', 'ACTIVE',  6,    true),
  (24, 'john23', 'USER',   'BLOCKED', 5,    false);
