-- MySQL init script for C system (dept_c)
CREATE DATABASE IF NOT EXISTS dept_c DEFAULT CHARACTER SET utf8mb4;
USE dept_c;

DROP TABLE IF EXISTS Choices;
DROP TABLE IF EXISTS Classes;
DROP TABLE IF EXISTS Students;
DROP TABLE IF EXISTS Users;

CREATE TABLE Users (
  username VARCHAR(64) PRIMARY KEY,
  password VARCHAR(64) NOT NULL
);

CREATE TABLE Students (
  Sno VARCHAR(32) PRIMARY KEY,
  Snm VARCHAR(64) NOT NULL,
  Sex VARCHAR(8),
  Sde VARCHAR(64)
);

CREATE TABLE Classes (
  Cno VARCHAR(32) PRIMARY KEY,
  Cnm VARCHAR(128) NOT NULL,
  Ctm VARCHAR(32),
  Cpt VARCHAR(16) NOT NULL,
  Tec VARCHAR(64) NOT NULL,
  Pla VARCHAR(64) NOT NULL,
  Share VARCHAR(8)
);

CREATE TABLE Choices (
  Sno VARCHAR(32) NOT NULL,
  Cno VARCHAR(32) NOT NULL,
  Grd VARCHAR(16),
  PRIMARY KEY (Sno, Cno)
);

-- Minimal seed data for local testing
INSERT INTO Users (username, password) VALUES ('2024001', '123456');

INSERT INTO Students (Sno, Snm, Sex, Sde) VALUES
('2024001', 'Alice', 'F', 'CS');

INSERT INTO Classes (Cno, Cnm, Ctm, Cpt, Tec, Pla, Share) VALUES
('C001', 'Data Integration', '32', '2', 'Dr.Li', 'Room101', 'Y'),
('C002', 'Database Systems', '48', '3', 'Dr.Wang', 'Room102', 'N');
