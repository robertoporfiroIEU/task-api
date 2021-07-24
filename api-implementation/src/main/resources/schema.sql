-- MySQL Script generated by MySQL Workbench
-- Sun Jul 25 01:01:19 2021
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema tasksapidb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `tasksapidb` ;

-- -----------------------------------------------------
-- Schema tasksapidb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `tasksapidb` DEFAULT CHARACTER SET utf8 ;
USE `tasksapidb` ;

-- -----------------------------------------------------
-- Table `tasksapidb`.`groups`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`groups` ;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`groups` (
  `name` VARCHAR(50) NOT NULL,
  `description` VARCHAR(250) NULL DEFAULT NULL,
  `applicationUser` VARCHAR(250) NOT NULL,
  `createdAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP(),
  `updatedAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
  PRIMARY KEY (`name`),
  UNIQUE INDEX `applicationUser_UNIQUE` (`applicationUser` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`users` ;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`users` (
  `username` VARCHAR(100) NOT NULL,
  `email` VARCHAR(320) NOT NULL,
  `applicationUser` VARCHAR(250) NOT NULL,
  `createdAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP(),
  PRIMARY KEY (`username`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) )
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`tasks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`tasks` ;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`tasks` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `identifier` CHAR(36) NOT NULL,
  `name` VARCHAR(250) NOT NULL,
  `description` VARCHAR(500) NULL DEFAULT NULL,
  `status` VARCHAR(100) NULL DEFAULT NULL,
  `createdAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP(),
  `updatedAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
  `applicationUser` VARCHAR(250) NOT NULL COMMENT 'This column specifies the application name that used the API',
  `dueDate` DATETIME NULL DEFAULT NULL,
  `users_username` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC) ,
  INDEX `fk_tasks_users1_idx` (`users_username` ASC) ,
  CONSTRAINT `fk_tasks_users1`
    FOREIGN KEY (`users_username`)
    REFERENCES `tasksapidb`.`users` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
AUTO_INCREMENT = 3
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`assigns`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`assigns` ;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`assigns` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `identifier` VARCHAR(36) NOT NULL,
  `groups_name` VARCHAR(50) NULL DEFAULT NULL,
  `tasks_id` BIGINT(20) NULL DEFAULT NULL,
  `users_username` VARCHAR(100) NULL DEFAULT NULL,
  `assignDate` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP(),
  `applicationUser` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC) ,
  UNIQUE INDEX `applicationUser_UNIQUE` (`applicationUser` ASC) ,
  UNIQUE INDEX `fks_UNIQUE` (`groups_name` ASC, `tasks_id` ASC, `users_username` ASC, `identifier` ASC) ,
  INDEX `fk_Assigns_groups1_idx` (`groups_name` ASC) ,
  INDEX `fk_Assigns_tasks1_idx` (`tasks_id` ASC) ,
  INDEX `fk_Assigns_users1_idx` (`users_username` ASC) ,
  CONSTRAINT `fk_Assigns_groups1`
    FOREIGN KEY (`groups_name`)
    REFERENCES `tasksapidb`.`groups` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Assigns_tasks1`
    FOREIGN KEY (`tasks_id`)
    REFERENCES `tasksapidb`.`tasks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Assigns_users1`
    FOREIGN KEY (`users_username`)
    REFERENCES `tasksapidb`.`users` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`comments`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`comments` ;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`comments` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `users_username` VARCHAR(100) NOT NULL,
  `tasks_id` BIGINT(20) NOT NULL,
  `identifier` CHAR(36) NOT NULL,
  `text` TEXT NULL DEFAULT NULL,
  `applicationUser` VARCHAR(100) NOT NULL,
  `createdAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP(),
  `updatedAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
  PRIMARY KEY (`id`),
  UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC) ,
  UNIQUE INDEX `fks_UNIQUE` (`users_username` ASC, `tasks_id` ASC, `identifier` ASC) ,
  UNIQUE INDEX `applicationUser_UNIQUE` (`applicationUser` ASC) ,
  INDEX `fk_comments_users1_idx` (`users_username` ASC) ,
  INDEX `fk_comments_tasks1_idx` (`tasks_id` ASC) ,
  CONSTRAINT `fk_comments_tasks1`
    FOREIGN KEY (`tasks_id`)
    REFERENCES `tasksapidb`.`tasks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comments_users1`
    FOREIGN KEY (`users_username`)
    REFERENCES `tasksapidb`.`users` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`spectators`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`spectators` ;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`spectators` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `identifier` VARCHAR(36) NOT NULL,
  `groups_name` VARCHAR(50) NULL DEFAULT NULL,
  `tasks_id` BIGINT(20) NULL DEFAULT NULL,
  `users_username` VARCHAR(100) NULL DEFAULT NULL,
  `createdAt` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP(),
  `applicationUser` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `identifier_UNIQUE` (`identifier` ASC) ,
  UNIQUE INDEX `applicationUser_UNIQUE` (`applicationUser` ASC) ,
  UNIQUE INDEX `fks_UNIQUE` (`groups_name` ASC, `tasks_id` ASC, `users_username` ASC, `identifier` ASC) ,
  INDEX `fk_Assigns_groups1_idx` (`groups_name` ASC) ,
  INDEX `fk_Assigns_tasks1_idx` (`tasks_id` ASC) ,
  INDEX `fk_Assigns_users1_idx` (`users_username` ASC) ,
  CONSTRAINT `fk_Assigns_groups10`
    FOREIGN KEY (`groups_name`)
    REFERENCES `tasksapidb`.`groups` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Assigns_tasks10`
    FOREIGN KEY (`tasks_id`)
    REFERENCES `tasksapidb`.`tasks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Assigns_users10`
    FOREIGN KEY (`users_username`)
    REFERENCES `tasksapidb`.`users` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `tasksapidb`.`users_has_groups`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tasksapidb`.`users_has_groups` ;

CREATE TABLE IF NOT EXISTS `tasksapidb`.`users_has_groups` (
  `users_username` VARCHAR(100) NOT NULL,
  `groups_name` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`users_username`, `groups_name`),
  INDEX `fk_users_has_groups_groups1_idx` (`groups_name` ASC) ,
  INDEX `fk_users_has_groups_users_idx` (`users_username` ASC) ,
  CONSTRAINT `fk_users_has_groups_groups1`
    FOREIGN KEY (`groups_name`)
    REFERENCES `tasksapidb`.`groups` (`name`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_has_groups_users`
    FOREIGN KEY (`users_username`)
    REFERENCES `tasksapidb`.`users` (`username`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;