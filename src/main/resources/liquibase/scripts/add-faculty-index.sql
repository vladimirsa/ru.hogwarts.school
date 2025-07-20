-- liquibase formatted sql
-- changeset vladimirs.anufrijevs:1
CREATE INDEX idx_faculty_name_color ON faculty(name, color);