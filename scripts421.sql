-- 1.
ALTER TABLE student
    ADD CONSTRAINT check_student_age CHECK (age >= 16);

-- 2.
ALTER TABLE student
    ALTER COLUMN name SET NOT NULL;
ALTER TABLE student
    ADD CONSTRAINT unique_student_name UNIQUE (name);

-- 3.
ALTER TABLE faculty
    ADD CONSTRAINT unique_faculty_name_color UNIQUE (name, color);

-- 4.
ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;